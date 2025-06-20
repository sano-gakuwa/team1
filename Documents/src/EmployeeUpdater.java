import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JOptionPane;

public class EmployeeUpdater extends Thread {

    private final ViewAdditionScreen CALLERSCREEN = new ViewAdditionScreen();
    private final EmployeeManager MANAGER = new EmployeeManager();
    // 下村追加分-------------------------------------------------------
    private final Lock LOCK = new ReentrantLock();

    /**
     * コンストラクタ 社員情報リスト、追加する社員情報、呼び出し元の画面を設定
     *
     * @param employeeList 社員情報を保持するリスト
     * @param newEmployee  新規に追加する社員情報
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
        // 必須項目が空か確認
        if (!validateEmployee(newEmployee)) {
            showErrorDialog("必須項目が入力されていません");
            return;
        }
        // 重複チェック：既に同じ社員IDが存在していないか
        for (EmployeeInformation existing : EmployeeManager.employeeList) {
            if (existing.employeeID.equals(newEmployee.employeeID)) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    CALLERSCREEN.showValidationError("社員IDが既に存在します。別のIDを入力してください。");
                    MANAGER.LOGGER.info("新規追加処理: 重複する社員IDが存在します（社員ID: " + newEmployee.employeeID + "）"); // 例外処理の記述がないためinfoLog扱い
                });
                return;
            }
        }
        // バックアップファイル作成
        File originalFile = EmployeeManager.ENPLOYEE_CSV;
        File backupFile = new File("employee_data_backup.csv");
        try {
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MANAGER.LOGGER.info("CSVバックアップ作成成功");
        } catch (IOException e) {
            MANAGER.printErrorLog(e, "CSVバックアップ作成失敗");
            showError("CSVバックアップ作成に失敗しました");
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
            MANAGER.LOGGER.info("CSVファイルに社員情報を追記成功（社員ID: " + newEmployee.employeeID + "）");
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
            MANAGER.LOGGER.info("社員リストに新規データを追加成功（社員ID: " + newEmployee.employeeID + "）");
        } catch (Exception e) {
            MANAGER.printErrorLog(e, "社員リストに新規データを追加失敗（社員ID: " + newEmployee.employeeID + "）");
        }
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
        if (e.employeeID == null || e.employeeID.isEmpty()) {
            MANAGER.LOGGER.warning("社員ID欄が空欄です");
            validate = false;
        }
        if (e.lastName == null || e.lastName.isEmpty()) {
            MANAGER.LOGGER.warning("名字欄が空欄です");
            validate = false;
        }
        if (e.firstname == null || e.firstname.isEmpty()) {
            MANAGER.LOGGER.warning("名前欄が空欄です");
            validate = false;
        }
        if (e.rubyLastName == null || e.rubyLastName.isEmpty()) {
            MANAGER.LOGGER.warning("名字のフリガナ欄が空欄です");
            validate = false;
        }
        if (e.rubyFirstname == null || e.rubyFirstname.isEmpty()) {
            MANAGER.LOGGER.warning("名前のフリガナ欄が空欄です");
            validate = false;
        }
        if (e.birthday == null) {
            MANAGER.LOGGER.warning("誕生日欄が空欄です");
            validate = false;
        }
        if (e.joiningDate == null) {
            MANAGER.LOGGER.warning("入社年月欄が空欄です");
            validate = false;
        }
        if (e.skillPoint == null) {
            MANAGER.LOGGER.warning("技術欄が空欄です");
            validate = false;
        }
        if (e.communicationPoint == null) {
            MANAGER.LOGGER.warning("コミュニケーション能力欄が空欄です");
            validate = false;
        }
        if (e.attitudePoint == null) {
            MANAGER.LOGGER.warning("受講態度欄が空欄です");
            validate = false;
        }
        if (e.leadershipPoint == null) {
            MANAGER.LOGGER.warning("リーダーシップ欄が空欄です");
            validate = false;
        }
        return validate;
    }

    /**
     * 社員情報をCSV形式の文字列に変換
     *
     * @param e 変換する社員情報
     * @return CSV形式の文字列
     * @author nishiyama
     */
    private String convertToCSV(EmployeeInformation e) {
        return String.join(",",
                e.employeeID,
                e.lastName,
                e.firstname,
                e.rubyLastName,
                e.rubyFirstname,
                EmployeeInformation.formatDate(e.birthday),
                EmployeeInformation.formatDate(e.joiningDate),
                String.valueOf(e.engineerDate),
                e.availableLanguages,
                e.careerDate,
                e.trainingDate,
                String.valueOf(e.skillPoint),
                String.valueOf(e.attitudePoint),
                String.valueOf(e.communicationPoint),
                String.valueOf(e.leadershipPoint),
                e.remarks,
                EmployeeInformation.formatDate(e.updatedDay));
    }

    /**
     * エラー表示用に用意したパネルに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    private void showError(String message) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CALLERSCREEN.showErrorMessageOnPanel(message);
        });
    }

    // -------------------------------------------------------
    // 下村作成部分
    public void delete(ArrayList<String> selected) {
        if (LOCK.tryLock()) {
            LOCK.lock();
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
                            if (selected.contains(employee.employeeID) == true) {
                                employeeIterator.remove();
                            }
                        }
                    } catch (Exception e) {
                        MANAGER.printErrorLog(e, "選択された社員情報の削除に失敗しました");
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
                FileOutputStream fos = null;
                try {
                    try {
                        // バックアップファイル作成
                        Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        MANAGER.printErrorLog(e, "バックアップファイルの作成に失敗しました");
                        showErrorDialog("バックアップファイルの作成に失敗しました");
                        return;
                    }
                    fos = new FileOutputStream(originalFile);
                    FileChannel channel = fos.getChannel();
                    lock = channel.lock(); // CSVファイルの排他ロック（同時書き込み防止）
                    PrintWriter pw = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(fos, "Shift-JIS")));
                    // 社員情報保存CSVファイルの1行目に項目名を記載
                    pw.println(String.join(",", EmployeeManager.EMPLOYEE_CATEGORY));
                    // 社員情報リストの内容を社員情報保存CSVに上書き保存
                    for (EmployeeInformation employee : EmployeeManager.employeeList) {
                        pw.println(convertToCSV(employee));
                    }
                    pw.close();
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
                    MANAGER.printErrorLog(e, "削除後の社員情報リストの保存に失敗しました");
                    if (originalFile.exists() && backupFile.exists()) {
                        try {
                            Files.deleteIfExists(originalFile.toPath());
                        } catch (Exception ex) {
                            // オリジナルの社員情報保存CSVファイルが削除に失敗した場合
                            MANAGER.printErrorLog(ex, "オリジナルの社員情報保存CSVファイルが削除できませんでした");
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
                MANAGER.printErrorLog(e, "社員情報の削除に失敗しました");
                showErrorDialog("社員情報の削除に失敗しました");
                return;
            } finally {
                LOCK.unlock();
                MANAGER.LOGGER.info("deleteメソッドの解除成功");
            }
        }
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

    // -------------------
    // 佐野作成部分
       /**
     * 指定された社員IDの情報を更新するメソッド
     * 既存の社員情報リストとCSVファイルの内容を、新しい情報で上書きします。
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
            if (current.employeeID.equals(updatedEmployee.employeeID)) {
                EmployeeManager.employeeList.set(i, updatedEmployee);  // 上書き
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
        File originalFile = EmployeeManager.ENPLOYEE_CSV;                          // 元のCSVファイル
        File backupFile = new File("CSV/employee_data_backup.csv");                // バックアップファイル
        FileLock lock = null;                                                      // ファイルロック用
        FileOutputStream fos = null;                                               // 出力ストリーム

        try {
            // --- 元のCSVファイルをバックアップ ---
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // --- CSVファイル書き込みの準備（ロック取得） ---
            fos = new FileOutputStream(originalFile);  // 上書きモードで開く
            FileChannel channel = fos.getChannel();
            lock = channel.lock();  // 排他ロック

            // --- CSVファイルにヘッダと社員情報を書き出し ---
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos, "Shift-JIS")));

            // カテゴリ（1行目）出力
            for (String category : EmployeeManager.EMPLOYEE_CATEGORY) {
                pw.print(category + ",");
            }
            pw.println();  // 改行

            // 社員リスト全件をCSV形式で出力
            for (EmployeeInformation employee : EmployeeManager.employeeList) {
                pw.println(convertToCSV(employee));
            }

            pw.close();
            MANAGER.LOGGER.info("社員情報更新成功（社員ID: " + updatedEmployee.employeeID + "）");

        } catch (IOException e) {
            // --- 書き込みエラー時：ロールバック処理（元に戻す） ---
            MANAGER.printErrorLog(e, "社員情報更新失敗");
            try {
                Files.deleteIfExists(originalFile.toPath());  // 壊れたファイル削除
                Files.move(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);  // バックアップ復元
            } catch (IOException ex) {
                MANAGER.printErrorLog(ex, "CSVロールバック失敗");
            }
            showErrorDialog("社員情報の保存に失敗しました");
            return;
        } finally {
            // --- ファイルロックやリソースの後始末 ---
            try {
                if (lock != null && lock.isValid()) lock.release();  // ロック解除
                if (fos != null) fos.close();                         // ストリームクローズ
                Files.deleteIfExists(backupFile.toPath());           // バックアップ削除
            } catch (IOException e) {
                MANAGER.printErrorLog(e, "リソースの解放に失敗しました");
            }
        }
    }
}
