
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;
import javax.swing.JOptionPane;

public class EmployeeUpdater extends Thread {

    private final EmployeeManager MANAGER = new EmployeeManager();
    private final Lock LOCK = new ReentrantLock();

    /**
     * コンストラクタ 社員情報リスト、追加する社員情報、呼び出し元の画面を設定
     *
     * @param employeeList 社員情報を保持するリスト
     * @param newEmployee 新規に追加する社員情報
     * @param CALLERSCREEN この処理を呼び出した画面
     */
    public EmployeeUpdater() {
    }

    /**
     * 新規社員情報のリスト追加処理を担当:
     * <ul>
     * <li>社員情報の形式チェック</li>
     * <li>社員IDの重複チェック</li>
     * <li>既存CSVファイルのバックアップ作成</li>
     * <li>CSVファイルへの社員情報の追記</li>
     * <li>エラー時のロールバック処理</li>
     * <li>画面表示の更新</li>
     * </ul>
     * 入力内容の形式チェックを行い、問題があればエラーメッセージを表示。
     *
     * <p>
     * 形式チェック後、社員情報をCSVファイルに追加し、追加後のリストを画面に反映。
     * もしエラーが発生した場合、ロールバック処理を行い、エラーダイアログを表示。(setupDetailsClassでエラー用のパネル用意できてなかった)
     * </p>
     *
     * @author nishiyama
     */
    public void addition(EmployeeInformation newEmployee) {
        final ViewAdditionScreen CALLERSCREEN = new ViewAdditionScreen();
        // 必須項目が空か確認
        if (!validateEmployee(newEmployee)) {
            showErrorDialog("必須項目が入力されていません");
            return;
        }
        // 重複チェック：既に同じ社員IDが存在していないか
        for (EmployeeInformation existing : EmployeeManager.employeeList) {
            if (existing.getEmployeeID().equals(newEmployee.getEmployeeID())) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    CALLERSCREEN.showValidationError("社員IDが既に存在します。別のIDを入力してください。");
                    MANAGER.LOGGER.info("新規追加処理: 重複する社員IDが存在します（社員ID: " + newEmployee.getEmployeeID() + "）"); // 例外処理の記述がないためinfoLog扱い
                });
                return;
            }
        }
        // バックアップファイル作成
        File originalFile = EmployeeManager.EMPLOYEE_CSV;
        File backupFile = new File("employee_data_backup.csv");
            if (!backupCsv(originalFile, backupFile)) {
                return;
            }

        // CSVファイルのロック処理
        FileLock lock = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(originalFile, true);
            FileChannel channel = fos.getChannel();
            lock = channel.lock(); // CSVファイルの排他ロック（同時書き込み防止）
            PrintWriter pw = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(fos, "Shift-JIS")));
            // 追加情報を記述
            pw.println(convertToCSV(newEmployee));
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
                CALLERSCREEN.showErrorMessageOnPanel("CSVファイルへの追加に失敗しました。"); // UIclassでエラーメッセージを表示
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
                if (fos != null) {
                    fos.close();
                    MANAGER.LOGGER.info("ファイル出力ストリームクローズ成功");
                }
            } catch (IOException e) {
                MANAGER.printErrorLog(e, "finally句での後処理に失敗");
            }
        }

        try {
            EmployeeManager.employeeList.add(newEmployee);
            MANAGER.LOGGER.info("社員リストに新規データを追加成功（社員ID: " + newEmployee.getEmployeeID() + "）");
        } catch (Exception e) {
            MANAGER.printErrorLog(e, "社員リストに新規データを追加失敗（社員ID: " + newEmployee.getEmployeeID() + "）");
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
            MANAGER.LOGGER.warning(message);
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
            MANAGER.LOGGER.warning(message);
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
    private String convertToCSV(EmployeeInformation e) {
        String csvTypeString = null;
        String.join(",", e.getEmployeeID());
        String.join(",", e.getLastName());
        String.join(",", e.getFirstname());
        String.join(",", e.getRubyLastName());
        String.join(",", e.getRubyFirstname());
        String.join(",", EmployeeInformation.formatDate(e.getBirthday()));
        String.join(",", EmployeeInformation.formatDate(e.getJoiningDate()));
        String.join(",", String.valueOf(e.getEngineerDate()));
        String.join(",", e.getAvailableLanguages());
        String.join(",", e.getCareerDate());
        String.join(",", e.getTrainingDate());
        String.join(",", String.valueOf(e.getSkillPoint()));
        String.join(",", String.valueOf(e.getAttitudePoint()));
        String.join(",", String.valueOf(e.getCommunicationPoint()));
        String.join(",", String.valueOf(e.getLeadershipPoint()));
        String.join(",", e.getRemarks());
        String.join(",", EmployeeInformation.formatDate(e.getUpdatedDay()));
        return csvTypeString;
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
        if (!LOCK.tryLock()) {
            return;
        }
        LOCK.lock();
        MANAGER.LOGGER.info("deleteメソッドのロック取得成功");
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
            MANAGER.printErrorLog(e, "社員情報の削除に失敗しました");
            showErrorDialog("社員情報の削除に失敗しました");
        } finally {
            LOCK.unlock();
            MANAGER.LOGGER.info("deleteメソッドのロック解除成功");
        }
    }

    /**
     * 選択された社員IDに該当する社員情報を社員リストから削除する。
     * <p>
     * 削除処理は同期化されており、例外発生時には社員リストをバックアップ内容に復元する。
     * 処理失敗時にはエラーログ出力とダイアログ表示を行う。
     * </p>
     *
     * @param selected 削除対象の社員IDリスト
     * @param backup   削除前の社員情報リストのバックアップ
     * @return 削除処理が成功した場合はtrue、失敗した場合はfalse
     * @author nishiyama
     */
    private boolean removeSelectedEmployees(List<String> selected, List<EmployeeInformation> backup) {
        try {
            synchronized (EmployeeManager.employeeList) {
                EmployeeManager.employeeList.removeIf(emp -> selected.contains(emp.getEmployeeID()));
            }
            MANAGER.LOGGER.info("選択された社員情報の削除に成功しました");
            return true;
        } catch (Exception e) {
            MANAGER.printErrorLog(e, "選択された社員情報の削除に失敗しました");
            EmployeeManager.employeeList.clear();
            EmployeeManager.employeeList.addAll(backup);
            MANAGER.LOGGER.info("社員情報リストを削除処理前に戻しました");
            showErrorDialog("選択された社員情報の削除に失敗しました");
            return false;
        }
    }

    /**
     * 指定されたCSVファイルのバックアップを作成する。
     * <p>
     * バックアップファイルが既に存在する場合は上書きする。
     * バックアップ作成に失敗した場合はエラーログ出力とダイアログ表示を行う。
     * </p>
     *
     * @param original バックアップ元のCSVファイル
     * @param backup   作成するバックアップファイル
     * @return バックアップ作成に成功した場合はtrue、失敗した場合はfalse
     * @author nishiyama
     */
    private boolean backupCsv(File original, File backup) {
        try {
            Files.copy(original.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MANAGER.LOGGER.info("バックアップファイルの作成に成功しました");
            return true;
        } catch (IOException e) {
            MANAGER.printErrorLog(e, "バックアップファイルの作成に失敗しました");
            showErrorDialog("バックアップファイルの作成に失敗しました");
            return false;
        }
    }

    /**
     * 社員情報リストの内容を指定されたCSVファイルに書き込む。
     * <p>
     * ファイルへの書き込み中はファイルロックを取得し、Shift-JISエンコードで出力する。
     * 書き込み失敗時には例外をスローする。
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
            MANAGER.LOGGER.info("CSVファイルの書き出しに成功しました");

        } catch (IOException e) {
            throw new Exception("CSV書き出し中にエラー", e);
        } finally {
            try {
                if (lock != null && lock.isValid()) {
                    lock.release();
                    MANAGER.LOGGER.info("CSVファイルのロック解除成功");
                }
            } catch (IOException e) {
                MANAGER.LOGGER.warning("CSVファイルのロック解除に失敗しました");
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
     * @param backup   復元元のバックアップファイル
     * @param e        発生した例外情報
     * @author nishiyama
     */
    private void rollbackCsv(File original, File backup, Exception e) {
        MANAGER.printErrorLog(e, "削除後の社員情報リストの保存に失敗しました");
        try {
            Files.deleteIfExists(original.toPath());
            if (!backup.renameTo(original)) {
                throw new IOException("バックアップからの復元に失敗");
            }
            MANAGER.LOGGER.info("CSVファイルをバックアップから復元しました");
        } catch (Exception ex) {
            MANAGER.printErrorLog(ex, "CSVファイルの復元に失敗しました");
            showErrorDialog("CSVファイルの復元に失敗しました");
        }
        showErrorDialog("削除後の社員情報の保存に失敗しました");
    }

    /**
     * 指定された社員IDの情報を更新するメソッド 既存の社員情報リストとCSVファイルの内容を、新しい情報で上書きします。
     *
     * @param updatedEmployee 編集後の社員情報
     */
    public void update(EmployeeInformation updatedEmployee) {
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
                pw.println(convertToCSV(employee));
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
                if (lock != null && lock.isValid()) {
                    lock.release(); // ロック解除

                }
                if (fos != null) {
                    fos.close(); // ストリームクローズ

                }
                Files.deleteIfExists(backupFile.toPath()); // バックアップ削除
            } catch (IOException e) {
                MANAGER.printErrorLog(e, "リソースの解放に失敗しました");
            }
        }
    }

    /**
     * エラー表示用に用意したパネルに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    private void showError(String message, ViewAdditionScreen CALLERSCREEN) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CALLERSCREEN.showErrorMessageOnPanel(message);
        });
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
