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

public class EmployeeInfoUpdate implements Runnable {
    private EmployeeInformation updatedEmployee;
    private final EmployeeManager MANAGER = new EmployeeManager();
    private static ReentrantLock updateLock = new ReentrantLock();

    /**
     * 社員情報削除のロックを取得
     * 
     * @return ロックの状態 true:ロック中, false:ロックされていない
     */
    public boolean validateUpdateLock() {
        return updateLock.isLocked();
    }

    public void update(EmployeeInformation updatedEmployee) {
        this.updatedEmployee = updatedEmployee;
    }

    public void run() {
        updateLock.lock(); // ロックを取得
        // --- 入力内容のチェック（必須項目が空ならエラーダイアログ表示） ---
        if (!validateEmployee(updatedEmployee)) {
            showErrorDialog("必須項目が入力されていません");
            return;
        }

        // --- 社員リストの中から該当する社員IDのデータを検索し、差し替える ---
        boolean found = false;
        for (int i = 0; i < EmployeeManager.employeeList.size(); i++) {
            EmployeeInformation current = EmployeeManager.employeeList.get(i);
            if (current.getEmployeeID().equals(updatedEmployee.getEmployeeID())) {
                EmployeeManager.employeeList.set(i, updatedEmployee); // 上書き
                found = true;
                break;
            }
        }

        // --- 該当する社員が見つからなかった場合はエラー終了 ---
        if (!found) {
            showErrorDialog("指定された社員情報が見つかりません");
            return;
        }
        MANAGER.LOGGER.info("社員情報更新の開始");
        // --- CSVファイル更新処理（安全性のためバックアップ＋排他ロックを使用） ---
        File originalFile = EmployeeManager.EMPLOYEE_CSV; // 元のCSVファイル
        File backupFile = new File("CSV/employee_data_backup.csv"); // バックアップファイル
        FileLock lock = null; // ファイルロック用
        FileOutputStream fos = null; // 出力ストリーム

        try {
            // --- 元のCSVファイルをバックアップ ---
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // --- CSVファイル書き込みの準備（ロック取得） ---
            fos = new FileOutputStream(originalFile); // 上書きモードで開く
            FileChannel channel = fos.getChannel();
            lock = channel.lock(); // 排他ロック

            // --- CSVファイルにヘッダと社員情報を書き出し ---
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos, "Shift-JIS")));

            // カテゴリ（1行目）出力
            for (String category : EmployeeManager.EMPLOYEE_CATEGORY) {
                pw.print(category + ",");
            }
            pw.println(); // 改行

            // 社員リスト全件をCSV形式で出力
            for (EmployeeInformation employee : EmployeeManager.employeeList) {
                pw.println(MANAGER.convertToCSV(employee));
            }

            pw.close();
            MANAGER.LOGGER.info("社員情報更新成功（社員ID: " + updatedEmployee.getEmployeeID() + "）");

        } catch (IOException e) {
            // --- 書き込みエラー時：ロールバック処理（元に戻す） ---
            MANAGER.printErrorLog(e, "社員情報更新失敗");
            try {
                Files.deleteIfExists(originalFile.toPath()); // 壊れたファイル削除
                Files.move(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING); // バックアップ復元
            } catch (IOException ex) {
                MANAGER.printErrorLog(ex, "CSVロールバック失敗");
            }
            showErrorDialog("社員情報の保存に失敗しました");
            return;
        } finally {
            // --- ファイルロックやリソースの後始末 ---
            try {
                if (lock != null && lock.isValid())
                    lock.release(); // ロック解除
                if (fos != null)
                    fos.close(); // ストリームクローズ
                Files.deleteIfExists(backupFile.toPath()); // バックアップ削除
            } catch (IOException e) {
                MANAGER.printErrorLog(e, "リソースの解放に失敗しました");
            }
        }
        updateLock.unlock(); // ロック解除
        MANAGER.LOGGER.info("社員情報更新完了");
    }

    /**
     * 社員情報の形式が正しいかを検証 必須項目がすべて入力されているかを確認
     *
     * @param e 検証する社員情報
     * @return 形式が正しい場合はtrue、そうでない場合はfalse
     * @author nishiyama
     */
    private boolean validateEmployee(EmployeeInformation e) {
        boolean validate = true;
        if (e.getEmployeeID() == null || e.getEmployeeID().isEmpty()) {
            MANAGER.LOGGER.warning("社員ID欄が空欄です");
            validate = false;
        }
        if (e.getLastName() == null || e.getLastName().isEmpty()) {
            MANAGER.LOGGER.warning("名字欄が空欄です");
            validate = false;
        }
        if (e.getFirstname() == null || e.getFirstname().isEmpty()) {
            MANAGER.LOGGER.warning("名前欄が空欄です");
            validate = false;
        }
        if (e.getRubyLastName() == null || e.getRubyLastName().isEmpty()) {
            MANAGER.LOGGER.warning("名字のフリガナ欄が空欄です");
            validate = false;
        }
        if (e.getRubyFirstname() == null || e.getRubyFirstname().isEmpty()) {
            MANAGER.LOGGER.warning("名前のフリガナ欄が空欄です");
            validate = false;
        }
        if (e.getBirthday() == null) {
            MANAGER.LOGGER.warning("誕生日欄が空欄です");
            validate = false;
        }
        if (e.getJoiningDate() == null) {
            MANAGER.LOGGER.warning("入社年月欄が空欄です");
            validate = false;
        }
        if (e.getSkillPoint() == null) {
            MANAGER.LOGGER.warning("技術欄が空欄です");
            validate = false;
        }
        if (e.getCommunicationPoint() == null) {
            MANAGER.LOGGER.warning("コミュニケーション能力欄が空欄です");
            validate = false;
        }
        if (e.getAttitudePoint() == null) {
            MANAGER.LOGGER.warning("受講態度欄が空欄です");
            validate = false;
        }
        if (e.getLeadershipPoint() == null) {
            MANAGER.LOGGER.warning("リーダーシップ欄が空欄です");
            validate = false;
        }
        return validate;
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
