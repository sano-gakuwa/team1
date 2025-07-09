import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

public class EmployeeInfoDeletion implements Runnable {
    private ArrayList<String> selected;
    private final EmployeeManager MANAGER = new EmployeeManager();
    private static ReentrantLock deletionLock = new ReentrantLock();

    /**
     * 社員情報削除のロックを取得
     * 
     * @return ロックの状態 true:ロック中, false:ロックされていない
     */
    public boolean validateDeleteLock() {
        return deletionLock.isLocked();
    }

    public void delete(ArrayList<String> selected) {
        this.selected = selected;
    }

    public void run() {
        deletionLock.lock();
        MANAGER.LOGGER.info("社員情報削除の開始");
        // 削除処理
        try {
            // 社員情報リストから選択した社員情報を削除
            ArrayList<EmployeeInformation> backupEmployeeList;
            backupEmployeeList = EmployeeManager.employeeList;
            synchronized (EmployeeManager.employeeList) {
                try {
                    for (Iterator<EmployeeInformation> employeeIterator = EmployeeManager.employeeList
                            .iterator(); employeeIterator.hasNext();) {
                        EmployeeInformation employee = employeeIterator.next();
                        // 選択された社員情報と合致したら削除
                        if (selected.contains(employee.getEmployeeID()) == true) {
                            employeeIterator.remove();
                        }
                    }
                } catch (Exception e) {
                    MANAGER.printExceptionLog(e, "選択された社員情報の削除に失敗しました");
                    // employeeListにbackupEmployeeListをコピー
                    EmployeeManager.employeeList.clear();
                    EmployeeManager.employeeList = backupEmployeeList;
                    MANAGER.LOGGER.info("社員情報リストを削除処理前に戻しました");
                    showErrorDialog("選択された社員情報の削除に失敗しました");
                    return;
                }
            }
            // 社員情報保存CSVから選択した社員情報を削除
            File originalFile = EmployeeManager.EMPLOYEE_CSV;
            File backupFile = new File("CSV/employee_data_backup.csv");
            FileLock lock = null;
            try {
                try {
                    // バックアップファイル作成
                    Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    MANAGER.printExceptionLog(e, "バックアップファイルの作成に失敗しました");
                    showErrorDialog("バックアップファイルの作成に失敗しました");
                    return;
                }
                FileOutputStream fileOutputStream = new FileOutputStream(originalFile);
                FileChannel channel = fileOutputStream.getChannel();
                lock = channel.lock(); // CSVファイルの排他ロック（同時書き込み防止）
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "Shift-JIS");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
                // 社員情報保存CSVファイルの1行目に項目名を記載
                printWriter.println(String.join(",", EmployeeManager.EMPLOYEE_CATEGORY));
                // 社員情報リストの内容を社員情報保存CSVに上書き保存
                for (EmployeeInformation employee : EmployeeManager.employeeList) {
                    printWriter.println(MANAGER.convertToCSV(employee));
                }
                printWriter.close();
                try {
                    if (lock != null && lock.isValid()) {
                        lock.release(); // エラーでもロック解除
                        MANAGER.LOGGER.info("社員情報保存CSVファイルロック解除成功");
                    }
                } catch (IOException ex) {
                    MANAGER.LOGGER.info("社員情報保存CSVファイルのロック解除失敗");
                }
            } catch (Exception e) {
                // 追記中エラー時のロールバック処理を追加
                try {
                    if (lock != null && lock.isValid()) {
                        lock.release(); // エラーでもロック解除
                        MANAGER.LOGGER.info("社員情報保存CSVファイルロック解除成功");
                    }
                } catch (IOException ex) {
                    MANAGER.LOGGER.info("社員情報保存CSVファイルロック解除失敗");
                }
                MANAGER.printExceptionLog(e, "削除後の社員情報リストの保存に失敗しました");
                if (originalFile.exists() && backupFile.exists()) {
                    try {
                        Files.deleteIfExists(originalFile.toPath());
                    } catch (Exception ex) {
                        // オリジナルの社員情報保存CSVファイルが削除に失敗した場合
                        MANAGER.printExceptionLog(ex, "オリジナルの社員情報保存CSVファイルが削除できませんでした");
                        showErrorDialog("オリジナルの社員情報保存CSVファイルが削除できませんでした");
                        return;
                    }
                    backupFile.renameTo(originalFile);
                }
                MANAGER.LOGGER.info("社員情報保存CSVファイルを削除処理前に戻しました");
                showErrorDialog("削除後の社員情報リストの保存に失敗しました");
                return;
            }
            Files.deleteIfExists(backupFile.toPath());
        } catch (Exception e) {
            // 社員情報保存CSV＆社員情報リスト以外で例外が発生した場合 (メモリがいっぱいなど)
            MANAGER.printExceptionLog(e, "社員情報の削除に失敗しました");
            showErrorDialog("社員情報の削除に失敗しました");
            return;
        }
        deletionLock.unlock();
        MANAGER.LOGGER.info("社員情報削除の完了");
    }

    /**
     * エラー表示用に用意したダイアログに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }
}
