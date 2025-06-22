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
    private static ReentrantLock additionLock = new ReentrantLock();

    /**
     * CSV読み込みのロックを取得
     * 
     * @return ロックの状態 true:ロック中, false:ロックされていない
     */
    public boolean validateAdditionLock() {
        return additionLock.isLocked();
    }

    public boolean validateNullEmployee() {
        return validateEmployee(newEmployee);
    }

    public boolean validateOverlappingEmployee() {
        // 重複チェック：既に同じ社員IDが存在していないか
        for (EmployeeInformation existing : EmployeeManager.employeeList) {
            if (existing.getEmployeeID().equals(newEmployee.getEmployeeID())) {
                return true;
            }
        }
        return false;
    }

    public void addition(EmployeeInformation newEmployee) {
        this.newEmployee = newEmployee;
    }

    public void run() {
        additionLock.lock(); // ロックを取得
        MANAGER.LOGGER.info("社員情報追加の開始");
        // バックアップファイル作成
        File originalFile = EmployeeManager.EMPLOYEE_CSV;
        File backupFile = new File("employee_data_backup.csv");
        try {
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MANAGER.LOGGER.info("CSVバックアップ作成成功");
        } catch (IOException e) {
            MANAGER.printErrorLog(e, "CSVバックアップ作成失敗");
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
            MANAGER.LOGGER.info("CSVファイルに社員情報を追記成功（社員ID: " + newEmployee.getEmployeeID() + "）");
            pw.close();
            MANAGER.LOGGER.info("ファイルロック解除成功");
        } catch (IOException e) {
            MANAGER.printErrorLog(e, "CSVファイル新規追加失敗しました");
            // 追記中エラー時のロールバック処理を追加
            try {
                if (lock != null && lock.isValid()) {
                    lock.release(); // エラーでもロック解除
                    MANAGER.LOGGER.info("ファイルロック解除成功");
                }
            } catch (IOException ex) {
                MANAGER.printErrorLog(e, "ファイルロック解除失敗");
            }
            try {
                Files.deleteIfExists(originalFile.toPath()); // 失敗時にCSVファイルを削除
                Files.move(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING); // バックアップを復元
            } catch (IOException ex) {
                MANAGER.printErrorLog(ex, "CSVファイルのロールバック処理に失敗");
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
                        MANAGER.LOGGER.info("バックアップファイル削除成功");
                    } else {
                        MANAGER.LOGGER.warning("バックアップファイル削除失敗");
                    }
                }
                if (lock != null && lock.isValid()) {
                    lock.release();
                    MANAGER.LOGGER.info("ファイルロック解除成功");
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    MANAGER.LOGGER.info("ファイル出力ストリームクローズ成功");
                }
            } catch (IOException e) {
                MANAGER.printErrorLog(e, "finally句での後処理に失敗");
            }
        }
        // 社員情報リストに新規データを追加
        try {
            EmployeeManager.employeeList.add(newEmployee);
            MANAGER.LOGGER.info("社員リストに新規データを追加成功（社員ID: " + newEmployee.getEmployeeID() + "）");
        } catch (Exception e) {
            MANAGER.printErrorLog(e, "社員リストに新規データを追加失敗（社員ID: " + newEmployee.getEmployeeID() + "）");
        }
        additionLock.unlock(); // ロックを解放
        MANAGER.LOGGER.info("社員情報追加の終了");
    }

    /**
     * 社員情報の形式が正しいかを検証 必須項目がすべて入力されているかを確認
     *
     * @param e 検証する社員情報
     * @return 形式が正しい場合はtrue、そうでない場合はfalse
     * @author nishiyama
     */
    private boolean validateEmployee(EmployeeInformation e) {
        boolean validate = false;
        if (e.getEmployeeID() == null || e.getEmployeeID().isEmpty()) {
            MANAGER.LOGGER.warning("社員ID欄が空欄です");
            validate = true;
        }
        if (e.getLastName() == null || e.getLastName().isEmpty()) {
            MANAGER.LOGGER.warning("名字欄が空欄です");
            validate = true;
        }
        if (e.getFirstname() == null || e.getFirstname().isEmpty()) {
            MANAGER.LOGGER.warning("名前欄が空欄です");
            validate = true;
        }
        if (e.getRubyLastName() == null || e.getRubyLastName().isEmpty()) {
            MANAGER.LOGGER.warning("名字のフリガナ欄が空欄です");
            validate = true;
        }
        if (e.getRubyFirstname() == null || e.getRubyFirstname().isEmpty()) {
            MANAGER.LOGGER.warning("名前のフリガナ欄が空欄です");
            validate = true;
        }
        if (e.getBirthday() == null) {
            MANAGER.LOGGER.warning("誕生日欄が空欄です");
            validate = true;
        }
        if (e.getJoiningDate() == null) {
            MANAGER.LOGGER.warning("入社年月欄が空欄です");
            validate = true;
        }
        if (e.getSkillPoint() == null) {
            MANAGER.LOGGER.warning("技術欄が空欄です");
            validate = true;
        }
        if (e.getCommunicationPoint() == null) {
            MANAGER.LOGGER.warning("コミュニケーション能力欄が空欄です");
            validate = true;
        }
        if (e.getAttitudePoint() == null) {
            MANAGER.LOGGER.warning("受講態度欄が空欄です");
            validate = true;
        }
        if (e.getLeadershipPoint() == null) {
            MANAGER.LOGGER.warning("リーダーシップ欄が空欄です");
            validate = true;
        }
        return validate;
    }

    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }
}
