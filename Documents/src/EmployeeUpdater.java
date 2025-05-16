import java.io.*;
import java.nio.file.*;
import java.util.*;

public class EmployeeUpdater extends Thread {

    private final ArrayList<EmployeeInformation> employeeList;
    private final EmployeeInformation newEmployee;
    private final ViewAdditionScreen callerScreen;

    /**
     * コンストラクタ
     * 社員情報リスト、追加する社員情報、呼び出し元の画面を設定
     *
     * @param employeeList 社員情報を保持するリスト
     * @param newEmployee 新規に追加する社員情報
     * @param callerScreen この処理を呼び出した画面
     */
    public EmployeeUpdater(ArrayList<EmployeeInformation> employeeList, EmployeeInformation newEmployee, ViewAdditionScreen callerScreen) {
        this.employeeList = employeeList;
        this.newEmployee = newEmployee;
        this.callerScreen = callerScreen;
    }

    /**
     * 新規社員情報のリスト追加処理を担当:
 * <ul>
 *   <li>社員情報の形式チェック</li>
 *   <li>既存CSVファイルのバックアップ作成</li>
 *   <li>CSVファイルへの社員情報の追記</li>
 *   <li>エラー時のロールバック処理</li>
 *   <li>画面表示の更新</li>
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
    public void addition() {
        // 形式チェック（例：必須項目が空ではないか）
        if (!validateEmployee(newEmployee)) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                callerScreen.showValidationError("必須項目が入力されていません");
            });
            return;
        }

        // バックアップファイル作成
        File originalFile = new File("employee_data.csv");
        File backupFile = new File("employee_data_backup.csv");
        try {
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            showError("バックアップファイルの作成に失敗しました");
            return;
        }

        // CSVファイルの書き込み処理
        synchronized (EmployeeUpdater.class) {
            try (FileWriter fw = new FileWriter(originalFile, true);
            BufferedWriter bw = new BufferedWriter(fw)) {

                // 追加
                employeeList.add(newEmployee);
                bw.write(convertToCSV(newEmployee));
                bw.newLine();

            } catch (IOException e) {
                // エラー時ロールバック処理
                originalFile.delete();
                backupFile.renameTo(originalFile);
                showError("CSV書き込み中にエラーが発生しました。");
                return;
            }
        }

        // リスト更新＋UI更新
        javax.swing.SwingUtilities.invokeLater(() -> {
            callerScreen.showSuccessDialog("新規追加が完了しました。");
            new ViewTopScreen().View();
        });

        // バックアップ削除
        backupFile.delete();
    }

    /**
     * 社員情報の形式が正しいかを検証
     * 必須項目がすべて入力されているかを確認
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
                && e.rubyLastName !=null && !e.rubyLastName.isEmpty()
                && e.rubyFirstname !=null && !e.rubyFirstname.isEmpty()
                && e.birthday !=null
                && e.joiningDate !=null
                && e.skillPoint !=null
                && e.attitudePoint !=null
                && e.communicationPoint !=null
                && e.leadershipPoint !=null;
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
            e.useLanguageDate,
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
     * 一旦ダイアログにエラーメッセージを表示するmethodになってます。
     * この部分はダイアログではなくてエラー表示用に用意したパネルに文言表示させるmethod
     *
     * @param message 表示するエラーメッセージ
     *
     * @author nishiyama
     */
    private void showError(String message) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            callerScreen.showErrorDialog(message);
        });
    }
}
