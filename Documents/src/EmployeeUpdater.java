
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

    private static  EmployeeInformation newEmployee;
    private final ViewAdditionScreen callerScreen = new ViewAdditionScreen();
    private  EmployeeManager manager = new EmployeeManager();
    //下村追加分-------------------------------------------------------
    private final Lock LOCK = new ReentrantLock();

    /**
     * コンストラクタ 社員情報リスト、追加する社員情報、呼び出し元の画面を設定
     *
     * @param employeeList 社員情報を保持するリスト
     * @param newEmployee 新規に追加する社員情報
     * @param callerScreen この処理を呼び出した画面
     */

     // instanceはなし
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
    public void addition(EmployeeInformation newE) {
        //         this.employeeList = employeeList;
        // this.new Employee = newEmployee;
        // this.callerScreen = callerScreen;
        this.newEmployee = newE;
        // 形式チェック（例：必須項目が空ではないか）
        if (!validateEmployee(newE)) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                callerScreen.showValidationError("必須項目が入力されていません");
            });
            return;
        }

        // 重複チェック：既に同じ社員IDが存在していないか
        for (EmployeeInformation existing : manager.employeeList) {
            if (existing.employeeID.equals(newEmployee.employeeID)) {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    callerScreen.showValidationError("社員IDが既に存在します。別のIDを入力してください。");
                });
                return;
            }
        }

        // バックアップファイル作成
        File originalFile = new File("employee_data.csv");
        File backupFile = new File("employee_data_backup.csv");
        try {
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // bakkuappuログ
        } catch (IOException e) {
            showError("バックアップファイルの作成に失敗しました");
            return;
        }

        // CSVファイルのロック処理
        synchronized (EmployeeUpdater.class) {
            FileLock lock = null;
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(originalFile, true);
                FileChannel channel = fos.getChannel();

                lock = channel.lock(); // CSVファイルの排他ロック（同時書き込み防止）

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

                // 追加情報を記述
                bw.write(convertToCSV(newE));
                bw.newLine();
                bw.flush(); // 念のため flush
                bw.close();
                lock.release(); // ロック解除（正常時）

            } catch (IOException e) {
                // 追記中エラー時のロールバック処理を追加
                try {
                    if (lock != null && lock.isValid()) {
                        lock.release(); // エラーでもロック解除
                    }
                } catch (IOException ex) {
                    manager.LOGGER.info("ロック解除失敗");
                }

                try {
                    Files.deleteIfExists(originalFile.toPath()); // 失敗時にCSVファイルを削除
                    Files.move(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING); // バックアップを復元
                } catch (IOException ex) {
                    // ロールバック失敗ログ
                }

                javax.swing.SwingUtilities.invokeLater(() -> {
                    callerScreen.showErrorMessageOnPanel("CSVファイルへの追加に失敗しました。"); // UIclassでエラーメッセージを表示
                });

                return; // スレッド終了（deactivateサブ）

            // ファイル操作終了時、必ず対象ファイルに対するアクセス制御（ロック）や、開いたファイルのリソースを解放する
            } finally {
                try {
        // // バックアップ削除
        // backupFile.delete();
                    if (lock != null && lock.isValid()) {
                        lock.release();  // ファイルのロックを必ず解除
                    }
                    if (fos != null) {
                        fos.close(); // ファイル出力ストリームを必ず閉じる
                    }
                } catch (IOException e) {
                    // 後処理でのログ出力も検討する？
                }
            }
        }
        
        try {
            manager.employeeList.add(newE);
        } catch (Exception e) {
            // 追加失敗した場合はエラー出すだけ
        }

        // リスト更新＋UI更新
        javax.swing.SwingUtilities.invokeLater(() -> {
            callerScreen.showSuccessDialog("新規追加が完了しました。");
        });
    }

    /**
     * 社員情報の形式が正しいかを検証 必須項目がすべて入力されているかを確認
     *
     * @param e 検証する社員情報
     * @return 形式が正しい場合はtrue、そうでない場合はfalse
     *
     * @author nishiyama
     */
    private boolean validateEmployee(EmployeeInformation e) {
        return e.employeeID != null && !e.employeeID.isEmpty()
                && e.lastName != null && !e.lastName.isEmpty()
                && e.firstname != null && !e.firstname.isEmpty()
                && e.rubyLastName != null && !e.rubyLastName.isEmpty()
                && e.rubyFirstname != null && !e.rubyFirstname.isEmpty()
                && e.birthday != null
                && e.joiningDate != null
                && e.skillPoint != null
                && e.attitudePoint != null
                && e.communicationPoint != null
                && e.leadershipPoint != null;
    }

    /**
     * 社員情報をCSV形式の文字列に変換
     *
     * @param e 変換する社員情報
     * @return CSV形式の文字列
     *
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
                EmployeeInformation.formatDate(e.updatedDay)
        );
    }

    /**
     * エラー表示用に用意したパネルに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     *
     * @author nishiyama
     */
    private void showError(String message) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            callerScreen.showErrorMessageOnPanel(message);
        });
    }
    
    //-------------------------------------------------------
    // 下村作成部分
    public void delete(ArrayList<String> selected) {
        if (LOCK.tryLock()) {
            LOCK.lock();
            //削除処理
            try {
                //社員情報リストから選択した社員情報を削除
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
                        manager.printErrorLog(e, "選択された社員情報の削除に失敗しました");
                        // employeeListにbackupEmployeeListをコピー
                        EmployeeManager.employeeList.clear();
                        EmployeeManager.employeeList = backupEmployeeList;
                        manager.LOGGER.info("社員情報リストを削除処理前に戻しました");
                        showErrorDialog("選択された社員情報の削除に失敗しました");
                        return;
                    }
                }
                //社員情報保存CSVから選択した社員情報を削除
                File originalFile = EmployeeManager.ENPLOYEE_CSV;
                File backupFile = new File("CSV/employee_data_backup.csv");
                try {
                    try {
                        // バックアップファイル作成
                        Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        manager.printErrorLog(e, "バックアップファイルの作成に失敗しました");
                        showErrorDialog("バックアップファイルの作成に失敗しました");
                        return;
                    }
                    PrintWriter pw = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(originalFile), "Shift-JIS")));
                    // 社員情報保存CSVファイルの1行目に項目名を記載
                    for (String category : EmployeeManager.EMPLOYEE_CATEGORY) {
                        pw.append(category + ",");
                    }
                    // 行の最後で改行
                    pw.append("\n");
                    // 社員情報リストの内容を社員情報保存CSVに上書き保存
                    for (EmployeeInformation employee : EmployeeManager.employeeList) {
                        pw.println(convertToCSV(employee));
                    }
                    pw.close();
                } catch (Exception e) {
                    manager.printErrorLog(e, "削除後の社員情報リストの保存に失敗しました");
                    if (originalFile.exists() && backupFile.exists()) {
                        try {
                            Files.deleteIfExists(originalFile.toPath());
                        } catch (Exception ex) {
                            // オリジナルの社員情報保存CSVファイルが削除に失敗した場合
                            manager.printErrorLog(ex, "オリジナルの社員情報保存CSVファイルが削除できませんでした");
                            showErrorDialog("オリジナルの社員情報保存CSVファイルが削除できませんでした");
                            return;
                        }
                        backupFile.renameTo(originalFile);
                    }
                    manager.LOGGER.info("社員情報保存CSVファイルを削除処理前に戻しました");
                    showErrorDialog("削除後の社員情報リストの保存に失敗しました");
                    return;
                }
                Files.deleteIfExists(backupFile.toPath());
            } catch (Exception e) {
                //社員情報保存CSV＆社員情報リスト以外で例外が発生した場合　(メモリがいっぱいなど)
                manager.printErrorLog(e, "社員情報の削除に失敗しました");
                showErrorDialog("社員情報の削除に失敗しました");
                return;
            } finally {
                LOCK.unlock();
            }
        }
    }

    /**
     * エラー表示用に用意したパネルに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     *
     * @author nishiyama
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }
    // -------------------
}
