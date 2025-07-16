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
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

public class EmployeeInfoAddition implements Runnable {
    private EmployeeInformation newEmployee;
    private final EmployeeManager MANAGER = new EmployeeManager();
    private final ThreadsManager THREAD_MANAGER = new ThreadsManager();
    private static ReentrantLock additionLock = new ReentrantLock();

    /**
     * CSV読み込みのロックを取得
     * 
     * @return ロックの状態 true:ロック中, false:ロックされていない
     */
    public boolean validateAdditionLock() {
        return additionLock.isLocked();
    }

    public void addition(EmployeeInformation newEmployee) {
        this.newEmployee = newEmployee;
    }

    public void run() {
        MANAGER.printInfoLog("社員情報追加の開始");
        THREAD_MANAGER.startUsing(Thread.currentThread());
        additionLock.lock(); // ロックを取得
        
        try {
            csvAddition();
            listAddition();
            if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
                showEndDialog("社員情報の追加機能終了");
            }
        } catch (Exception e) {
            if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
                showValidationError("社員情報の追加に失敗しました。");
            }
        } finally {
            additionLock.unlock(); // ロックを解放
            THREAD_MANAGER.endUsing(Thread.currentThread());
            MANAGER.printInfoLog("社員情報追加の終了");
        }
    }

    private void csvAddition() {
        // バックアップファイル作成
        File originalFile = EmployeeManager.EMPLOYEE_CSV;
        File backupFile = new File("employee_data_backup.csv");
        try {
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MANAGER.printInfoLog("CSVバックアップ作成成功");
        } catch (IOException e) {
            MANAGER.printExceptionLog(e, "CSVバックアップ作成失敗");
            return;
        }
        // CSVファイルのロック処理
        FileLock lock = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(originalFile, true);
            FileChannel channel = fileOutputStream.getChannel();
            lock = channel.lock(); // CSVファイルの排他ロック（同時書き込み防止）
            PrintWriter pw = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(fileOutputStream, "Shift-JIS")));
            // 追加情報を記述
            pw.println(MANAGER.convertToCSV(newEmployee));
            MANAGER.printInfoLog("CSVファイルに社員情報を追記成功（社員ID: " + newEmployee.getEmployeeID() + "）");
            pw.close();
            MANAGER.printInfoLog("ファイルロック解除成功");
        } catch (IOException e) {
            MANAGER.printExceptionLog(e, "CSVファイル新規追加失敗しました");
            // 追記中エラー時のロールバック処理を追加
            try {
                if (lock != null && lock.isValid()) {
                    lock.release(); // エラーでもロック解除
                    MANAGER.printInfoLog("ファイルロック解除成功");
                }
            } catch (IOException ex) {
                MANAGER.printExceptionLog(e, "ファイルロック解除失敗");
            }
            try {
                Files.deleteIfExists(originalFile.toPath()); // 失敗時にCSVファイルを削除
                Files.move(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING); // バックアップを復元
            } catch (IOException ex) {
                MANAGER.printExceptionLog(ex, "CSVファイルのロールバック処理に失敗");
            }
            javax.swing.SwingUtilities.invokeLater(() -> {
                showValidationError("CSVファイルへの追加に失敗しました。"); // UIclassでエラーメッセージを表示
            });
            return; // スレッド終了（deactivateサブ）
            // ファイル操作終了時、必ず対象ファイルに対するアクセス制御（ロック）や、開いたファイルのリソースを解放する
        } finally {
            try {
                if (backupFile.exists()) {
                    if (backupFile.delete()) {
                        MANAGER.printInfoLog("バックアップファイル削除成功");
                    } else {
                        MANAGER.printErrorLog("バックアップファイル削除失敗");
                    }
                }
                if (lock != null && lock.isValid()) {
                    lock.release();
                    MANAGER.printInfoLog("ファイルロック解除成功");
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    MANAGER.printInfoLog("ファイル出力ストリームクローズ成功");
                }
            } catch (IOException e) {
                MANAGER.printExceptionLog(e, "finally句での後処理に失敗");
            }
        }
    }

    private void listAddition() {
        // 社員情報リストに新規データを追加
        try {
            EmployeeManager.employeeList.add(newEmployee);
            MANAGER.printInfoLog("社員リストに新規データを追加成功（社員ID: " + newEmployee.getEmployeeID() + "）");
        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "社員リストに新規データを追加失敗（社員ID: " + newEmployee.getEmployeeID() + "）");
        }
    }

    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    private void showEndDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "完了通知", JOptionPane.INFORMATION_MESSAGE);
    }
}
