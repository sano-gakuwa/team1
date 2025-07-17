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

public class EmployeeInfoDeletion implements Runnable {
    private ArrayList<String> selected;
    private ArrayList<EmployeeInformation> backupEmployeeList;
    private final EmployeeManager MANAGER = new EmployeeManager();
    private ThreadsManager threadsManager=new ThreadsManager();
    private static ReentrantLock deletionLock = new ReentrantLock();
    private ViewDialog dialog = new ViewDialog();

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
        MANAGER.printInfoLog("社員情報削除の開始");
        threadsManager.startUsing(Thread.currentThread());
        // 削除処理
        try {
            // 社員情報リストから選択した社員情報を削除
            backupEmployeeList = EmployeeManager.employeeList;
            deletingOfList();
            // 社員情報保存CSVから選択した社員情報を削除
            File originalFile = EmployeeManager.EMPLOYEE_CSV;
            File backupFile = new File("CSV/employee_data_backup.csv");
            FileLock lock = null;
            try {
                createbackupFile(originalFile, backupFile);
                csvWriting(originalFile,lock);
            } catch (Exception e) {
                tryUnLock(lock);
                MANAGER.printExceptionLog(e, "削除後の社員情報リストの保存に失敗しました");
                revertingOriginalFile(originalFile, backupFile);
                MANAGER.printInfoLog("社員情報保存CSVファイルを削除処理前に戻しました");
                dialog.viewErrorDialog("削除後の社員情報リストの保存に失敗しました");
                return;
            }
            Files.deleteIfExists(backupFile.toPath());
        } catch (Exception e) {
            // 社員情報保存CSV＆社員情報リスト以外で例外が発生した場合 (メモリがいっぱいなど)
            MANAGER.printExceptionLog(e, "社員情報の削除に失敗しました");
            dialog.viewErrorDialog("社員情報の削除に失敗しました");
            return;
        }finally{
            deletionLock.unlock();
            threadsManager.endUsing(Thread.currentThread());
        }
        MANAGER.printInfoLog("社員情報削除の完了");
    }

    /**
     * 
     */
    private void deletingOfList() {
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
                MANAGER.printInfoLog("社員情報リストを削除処理前に戻しました");
                dialog.viewErrorDialog("選択された社員情報の削除に失敗しました");
                return;
            }
        }
    }

    private void createbackupFile(File originalFile, File backupFile) {
        try {
            // バックアップファイル作成
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            MANAGER.printExceptionLog(e, "バックアップファイルの作成に失敗しました");
            dialog.viewErrorDialog("バックアップファイルの作成に失敗しました");
            return;
        }
    }

    private void csvWriting(File originalFile, FileLock lock) {
        try (PrintWriter printWriter = new PrintWriter(setBufferedWriter(originalFile, lock));) {
            // 社員情報保存CSVファイルの1行目に項目名を記載
            printWriter.println(String.join(",", EmployeeManager.EMPLOYEE_CATEGORY));
            // 社員情報リストの内容を社員情報保存CSVに上書き保存
            for (EmployeeInformation employee : EmployeeManager.employeeList) {
                printWriter.println(MANAGER.convertToCSV(employee));
            }
        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "社員情報保存CSVファイルに書き込み失敗");
        } finally {
            tryUnLock(lock);
        }
    }

    private BufferedWriter setBufferedWriter(File originalFile, FileLock lock) {
        BufferedWriter bufferedWriter = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(originalFile);
            FileChannel channel = fileOutputStream.getChannel();
            lock = channel.lock(); // CSVファイルの排他ロック（同時書き込み防止）
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "Shift-JIS");
            bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "BufferedWriterの設定に失敗");
        }
        return bufferedWriter;
    }

    private void tryUnLock(FileLock lock) {
        try {
            if (lock != null && lock.isValid()) {
                lock.release(); // エラーでもロック解除
                MANAGER.printInfoLog("社員情報保存CSVファイルロック解除成功");
            }
        } catch (IOException e) {
            MANAGER.printExceptionLog(e, "社員情報保存CSVファイルのロック解除失敗");
        }
    }

    private void revertingOriginalFile(File originalFile, File backupFile) {
        if (originalFile.exists() && backupFile.exists()) {
            try {
                Files.deleteIfExists(originalFile.toPath());
            } catch (Exception e) {
                // オリジナルの社員情報保存CSVファイルが削除に失敗した場合
                MANAGER.printExceptionLog(e, "オリジナルの社員情報保存CSVファイルが削除できませんでした");
                dialog.viewErrorDialog("オリジナルの社員情報保存CSVファイルが削除できませんでした");
                return;
            }
            backupFile.renameTo(originalFile);
        }
    }
}
