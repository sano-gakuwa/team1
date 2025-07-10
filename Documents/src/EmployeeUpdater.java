
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class EmployeeUpdater extends Thread {

    private final EmployeeManager MANAGER = new EmployeeManager();

    public EmployeeUpdater() {
    }

    /**
     * 社員情報を追加するメソッド。 入力内容のバリデーションを行い、重複する社員IDがない場合に限り、CSVファイルと社員リストへ情報を追加。
     * 処理中にエラーが発生した場合は、CSVのロールバック処理を行い、画面へ適切なエラーメッセージを表示。
     * 同時実行防止のため、ファイルとリスト操作はロック制御を行う。
     *
     * @param newEmployee 追加対象となる社員情報オブジェクト（EmployeeInformation）
     * @author nishiyama
     */
    public void addition(EmployeeInformation newEmployee) {
        ViewAdditionScreen callScreen = new ViewAdditionScreen();

        try {
            // 必須項目の空欄チェック
            if (!validateEmployee(newEmployee)) {
                showErrorDialog("必須項目が入力されていません");
                return;
            }

            // 社員IDの重複チェック
            for (EmployeeInformation existing : EmployeeManager.employeeList) {
                if (existing.getEmployeeID().equals(newEmployee.getEmployeeID())) {
                    SwingUtilities.invokeLater(() -> {
                        callScreen.showValidationError("社員IDが既に存在します。別のIDを入力してください。");
                        MANAGER.printErrorLog("重複する社員IDが存在（ID: " + newEmployee.getEmployeeID() + "）");
                    });
                    return;
                }
            }

            File originalFile = EmployeeManager.EMPLOYEE_CSV;
            File backupFile = new File("employee_data_backup.csv");

            // バックアップファイル作成
            if (!backupCsv(originalFile, backupFile)) {
                return;
            }

            // CSVに情報を追記
            if (!appendToCsv(originalFile, newEmployee)) {
                rollbackCsv(originalFile, backupFile, new Exception("CSV追記失敗"));
                SwingUtilities.invokeLater(() -> {
                    callScreen.showErrorMessageOnPanel("CSVファイルへの追加に失敗しました。");
                    callScreen.refreshUI();
                });
                return;
            }

            // backup削除
            if (!backupFile.delete()) {
                MANAGER.printErrorLog("バックアップファイル削除に失敗"); // ここはerrorログの方が良い
            }

            // リストに追加
            EmployeeManager.employeeList.add(newEmployee);
            MANAGER.printInfoLog("社員リストに新規データを追加成功（社員ID: " + newEmployee.getEmployeeID() + "）");

            // 成功通知＆画面更新
            SwingUtilities.invokeLater(() -> {
                callScreen.showSuccessDialog("社員情報の追加に成功しました。");
                callScreen.refreshUI();
            });

        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "社員追加処理で例外発生");
            showErrorDialog("社員の追加処理に失敗しました");
        }
    }

    /**
     * 指定されたCSVファイルに新しい社員情報を追記する。
     * 追記時にはファイルロックを取得して排他制御を行い、Shift-JISエンコーディングで出力します。
     * 処理中に例外が発生した場合はエラーログを出力し、falseを返す。
     *
     * @param file 追記対象のCSVファイル
     * @param newEmployee 追記する社員情報
     * @return trueまたはfalseで返す
     *
     * @author nishiyama
     */
    private boolean appendToCsv(File originalFile, EmployeeInformation newEmployee) {
        FileLock lock = null;

        try (
                    FileOutputStream fos = new FileOutputStream(originalFile, true);
        FileChannel channel = fos.getChannel();
        OutputStreamWriter osw = new OutputStreamWriter(fos, "Shift-JIS");
        BufferedWriter bw = new BufferedWriter(osw);
        PrintWriter pw = new PrintWriter(bw)
        ){
            lock = channel.lock();

            pw.println(convertToCSV(newEmployee));
            MANAGER.printInfoLog("CSVファイルに社員情報を追記成功（ID: " + newEmployee.getEmployeeID() + "）");
            
            return true;

        } catch (IOException e) {
            MANAGER.printExceptionLog(e, "CSVファイルへの追記中にエラーが発生（ID: " + newEmployee.getEmployeeID() + "）");
            return false;

        } finally {
            try {
                if (lock != null && lock.isValid()) {
                    lock.release();
                    MANAGER.printInfoLog("CSVファイルロック解除成功");
                }
            } catch (IOException ex) {
                MANAGER.printExceptionLog(ex, "ファイルストリームやロックの後処理で例外発生");
            }
        }
    }

    /**
     * 社員情報の形式が正しいかを検証 必須項目がすべて入力されているかを確認
     *
     * @param e 検証する社員情報
     * @return 形式が正しい場合はtrue、そうでない場合はfalse
     * @author nishiyama & shimomura
     */
    private boolean validateEmployee(EmployeeInformation e) {
        boolean isValid = true;

        isValid &= checkNotBlank(e.getEmployeeID(), "社員ID欄が空欄です");
        isValid &= checkNotBlank(e.getLastName(), "名字欄が空欄です");
        isValid &= checkNotBlank(e.getFirstname(), "名前欄が空欄です");
        isValid &= checkNotBlank(e.getRubyLastName(), "名字フリガナ欄が空欄です");
        isValid &= checkNotBlank(e.getRubyFirstname(), "名前フリガナ欄が空欄です");
        isValid &= checkNotNull(e.getBirthday(), "生年月日欄が空欄です");
        isValid &= checkNotNull(e.getJoiningDate(), "入社年月欄が空欄です");
        isValid &= checkNotNull(e.getSkillPoint(), "技術欄が空欄です");
        isValid &= checkNotNull(e.getCommunicationPoint(), "コミュニケーション能力欄が空欄です");
        isValid &= checkNotNull(e.getAttitudePoint(), "受講態度欄が空欄です");
        isValid &= checkNotNull(e.getLeadershipPoint(), "リーダーシップ欄が空欄です");

        return isValid;
    }

    /**
     * 記入欄が未入力でないか検証 必須項目がすべて入力されているかを確認
     *
     * @param value 社員情報
     * @param message 未入力時の警告文
     * @return 形式が正しい場合はtrue、そうでない場合はfalse
     * @author nishiyama
     */
    private boolean checkNotBlank(String value, String message) {
        if (value == null || value.isEmpty()) {
            MANAGER.printErrorLog(message);
            return false;
        }
        return true;
    }

    /**
     * 選択欄が未選択でないか検証 必須項目がすべて入力されているかを確認
     *
     * @param value 社員情報
     * @param message 未選択時の警告文
     * @return 形式が正しい場合はtrue、そうでない場合はfalse
     * @author nishiyama
     */
    private boolean checkNotNull(Object value, String message) {
        if (value == null) {
            MANAGER.printErrorLog(message);
            return false;
        }
        return true;
    }

    /**
     * 社員情報をCSV形式の文字列に変換
     *
     * @param e 変換する社員情報
     * @return CSV形式の文字列
     * @author nishiyama
     */
    private String convertToCSV(EmployeeInformation employee) {
        return String.join(",",
                employee.getEmployeeID(),
                employee.getLastName(),
                employee.getFirstname(),
                employee.getRubyLastName(),
                employee.getRubyFirstname(),
                employee.formatDate(employee.getBirthday()),
                employee.formatDate(employee.getJoiningDate()),
                String.valueOf(employee.getEngineerDate()),
                employee.getAvailableLanguages(),
                employee.getCareerDate(),
                employee.getTrainingDate(),
                String.valueOf(employee.getSkillPoint()),
                String.valueOf(employee.getAttitudePoint()),
                String.valueOf(employee.getCommunicationPoint()),
                String.valueOf(employee.getLeadershipPoint()),
                employee.getRemarks(),
                employee.formatDate(employee.getUpdatedDay())
        );
    }

    /**
     * 選択された社員情報を削除し、CSVファイルに反映する。
     * <p>
     * 指定された社員IDリストに基づき社員情報を削除し、削除処理中は排他ロックを取得する。
     * 削除前に元のCSVファイルをバックアップし、書き込みに失敗した場合はバックアップからロールバックを行う。
     * 処理の各段階でログを出力し、例外発生時にはエラーダイアログを表示する。
     * </p>
     *
     * @param selected 削除対象の社員IDのリスト
     * @author shimomura & nishiyama
     */
    public void delete(ArrayList<String> selected) {

        try {
            List<EmployeeInformation> backupList = new ArrayList<>(EmployeeManager.employeeList);

            // 情報削除処理
            if (!removeSelectedEmployees(selected, backupList)) {
                return;
            }

            // fileBackup処理
            File originalFile = EmployeeManager.EMPLOYEE_CSV;
            File backupFile = new File("CSV/employee_data_backup.CSV");

            if (!backupCsv(originalFile, backupFile)) {
                return;
            }

            // CSVに書き込み処理
            try {
                writeCSV(originalFile);
                Files.deleteIfExists(backupFile.toPath());
            } catch (Exception e) {
                rollbackCsv(originalFile, backupFile, e); // rollBack処理
            }

        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "社員情報の削除に失敗しました");
            showErrorDialog("社員情報の削除に失敗しました");
        }
    }

    /**
     * 社員情報を更新する処理
     * 必須項目のバリデーション
     * 該当する社員の情報をリスト内で置換
     * バックアップを取りつつCSVファイルへ保存
     * 失敗時はロールバック処理実施
     *
     * @param updatedEmployee 更新対象の社員情報
     * @author nishiyama & sano
     */
    public void update(EmployeeInformation updatedEmployee) {
        // --- 入力内容のチェック（必須項目が空ならエラーダイアログ表示） ---
        if (!validateEmployee(updatedEmployee)) {
            showErrorDialog("必須項目が入力されていません");
            return;
        }

        // --- 社員リストの中から該当する社員IDのデータを検索し、差し替える ---
        boolean isfound = false;
        synchronized (EmployeeManager.employeeList) {
            for (int i = 0; i < EmployeeManager.employeeList.size(); i++) {
                EmployeeInformation current = EmployeeManager.employeeList.get(i);
                if (current.getEmployeeID().equals(updatedEmployee.getEmployeeID())) {
                    EmployeeManager.employeeList.set(i, updatedEmployee); // 上書き
                    isfound = true;
                    break;
                }
            }
        }
        // --- 該当する社員が見つからなかった場合はエラー終了 ---
        if (!isfound) {
            showErrorDialog("指定された社員情報が見つかりません");
            return;
        }
        // --- CSVファイル更新処理（安全性のためバックアップ＋排他ロックを使用） ---
        File originalFile = EmployeeManager.EMPLOYEE_CSV;
        File backupFile = new File("CSV/employee_data_backup.csv");

        if (!backupCsv(originalFile, backupFile)) {
            return;
        }

        try {
            writeCSV(originalFile);
            Files.deleteIfExists(backupFile.toPath());
            MANAGER.printInfoLog("社員情報更新成功（社員ID: " + updatedEmployee.getEmployeeID() + "）");
        } catch (Exception e) {
            rollbackCsv(originalFile, backupFile, e);
            showErrorDialog("社員情報の保存に失敗しました");
        }
    }

    /**
     * 選択された社員IDに該当する社員情報を社員リストから削除する。
     * <p>
     * 削除処理は同期化されており、例外発生時には社員リストをバックアップ内容に復元する。 処理失敗時にはエラーログ出力とダイアログ表示を行う。
     * </p>
     *
     * @param selected 削除対象の社員IDリスト
     * @param backup 削除前の社員情報リストのバックアップ
     * @return 削除処理が成功した場合はtrue、失敗した場合はfalse
     * @author nishiyama
     */
    private boolean removeSelectedEmployees(List<String> selected, List<EmployeeInformation> backup) {
        try {
            synchronized (EmployeeManager.employeeList) {
                EmployeeManager.employeeList.removeIf(emp -> selected.contains(emp.getEmployeeID()));
            }
            MANAGER.printInfoLog("選択された社員情報の削除に成功しました");
            return true;
        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "選択された社員情報の削除に失敗しました");
            EmployeeManager.employeeList.clear();
            EmployeeManager.employeeList.addAll(backup);
            MANAGER.printInfoLog("社員情報リストを削除処理前に戻しました");
            showErrorDialog("選択された社員情報の削除に失敗しました");
            return false;
        }
    }

    /**
     * 指定されたCSVファイルのバックアップを作成する。
     * <p>
     * バックアップファイルが既に存在する場合は上書きする。 バックアップ作成に失敗した場合はエラーログ出力とダイアログ表示を行う。
     * </p>
     *
     * @param original バックアップ元のCSVファイル
     * @param backup 作成するバックアップファイル
     * @return バックアップ作成に成功した場合はtrue、失敗した場合はfalse
     * @author nishiyama
     */
    private boolean backupCsv(File original, File backup) {
        try {
            Files.copy(original.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MANAGER.printInfoLog("バックアップファイルの作成に成功しました");
            return true;
        } catch (IOException e) {
            MANAGER.printExceptionLog(e, "バックアップファイルの作成に失敗しました");
            showErrorDialog("バックアップファイルの作成に失敗しました");
            return false;
        }
    }

    /**
     * 社員情報リストの内容を指定されたCSVファイルに書き込む。
     * <p>
     * ファイルへの書き込み中はファイルロックを取得し、Shift-JISエンコードで出力する。 書き込み失敗時には例外をスローする。
     * </p>
     *
     * @param file 書き込み先のCSVファイル
     * @throws Exception CSVファイルの書き込みに失敗した場合にスローされる例外
     * @author nishiyama
     */
    private void writeCSV(File file) throws Exception {
        FileLock lock = null;
        try (FileOutputStream fos = new FileOutputStream(file); FileChannel channel = fos.getChannel(); PrintWriter pw = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(fos, "shift-JIS")))) {

            lock = channel.lock();

            pw.println(String.join(",", EmployeeManager.EMPLOYEE_CATEGORY));
            synchronized (EmployeeManager.employeeList) {
                for (EmployeeInformation emp : EmployeeManager.employeeList) {
                    pw.println(convertToCSV(emp));
                }
            }
            MANAGER.printInfoLog("CSVファイルの書き出しに成功しました");

        } catch (IOException e) {
            throw new Exception("CSV書き出し中にエラー", e);
        } finally {
            try {
                if (lock != null && lock.isValid()) {
                    lock.release();
                    MANAGER.printInfoLog("CSVファイルのロック解除成功");
                }
            } catch (IOException e) {
                MANAGER.printExceptionLog(e, "CSVファイルのロック解除に失敗しました");
            }
        }
    }

    /**
     * CSVファイルの保存処理失敗時にバックアップファイルから復元を行う。
     * <p>
     * 復元に失敗した場合はエラーログ出力とダイアログ表示を行う。
     * </p>
     *
     * @param original 復元対象のCSVファイル
     * @param backup 復元元のバックアップファイル
     * @param e 発生した例外情報
     * @author nishiyama
     */
    private void rollbackCsv(File original, File backup, Exception e) {
        MANAGER.printExceptionLog(e, "削除後の社員情報リストの保存に失敗しました");
        try {
            Files.deleteIfExists(original.toPath());
            if (!backup.renameTo(original)) {
                throw new IOException("バックアップからの復元に失敗");
            }
            MANAGER.printInfoLog("CSVファイルをバックアップから復元しました");
        } catch (IOException ex) {
            MANAGER.printExceptionLog(ex, "CSVファイルの復元に失敗しました");
            showErrorDialog("CSVファイルの復元に失敗しました");
        }
        showErrorDialog("削除後の社員情報の保存に失敗しました");
    }

    /**
     * エラー表示用に用意したダイアログに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    private void showErrorDialog(String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
        } else {
            SwingUtilities.invokeLater(()
                    -> JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE)
            );
        }
    }
}
