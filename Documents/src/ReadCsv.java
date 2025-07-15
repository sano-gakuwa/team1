import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JOptionPane;

public class ReadCsv implements Runnable {
    private String selectedFilePath;
    private final EmployeeManager MANAGER = new EmployeeManager();
    private static ReentrantLock readCsvLock = new ReentrantLock();

    /**
     * CSV読み込みのロックを取得
     * 
     * @return ロックの状態 true:ロック中, false:ロックされていない
     */
    public boolean validateReadCsvLock() {
        return readCsvLock.isLocked();
    }

    /**
     * 選択されたCSVファイルを読み込み
     * 
     * @param selectedFilePath 選択されたCSVファイルのパス
     */
    public void readCsv(String selectedFilePath) {
        this.selectedFilePath = selectedFilePath;
    }

    // CSV読み込み処理
    @Override
    public void run() {
        MANAGER.printInfoLog("CSV読み込み処理を開始します");
        // ロックを取得
        readCsvLock.lock();
        MANAGER.printInfoLog("CSV読み込み処理をロックしました");

        ArrayList<EmployeeInformation> newEmployeeList = new ArrayList<EmployeeInformation>();
        try {
            // BufferedReaderクラスのオブジェクトを生成する
            File selectedFile = new File(selectedFilePath);
            FileInputStream fileInputStream = new FileInputStream(selectedFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "Shift-JIS"));
            // 読み込み処理を実行
            try (Scanner scanner = new Scanner(bufferedReader)) {
                // 1行目はヘッダーなので読み飛ばす
                scanner.next();
                // 次に読み込むべき行が無くなるまでループ
                while (scanner.hasNext()) {
                    // 1行分のデータをカンマ区切りで分割
                    ArrayList<String> loadEmployeeDate;
                    loadEmployeeDate = new ArrayList<String>(Arrays.asList(scanner.next().split(",")));
                    // 読み込んだ社員情報を社員情報型に変換
                    EmployeeInformation employee = MANAGER.convertEmployeeInformation(loadEmployeeDate);
                    // 読み込んだデータがnullでないか確認
                    if (!MANAGER.validateNotNull(employee)) {
                        showErrorDialog("必須項目が入力されていません");
                        return;
                    }
                    // 読み込んだデータが要求仕様書通りの仕様になっているか確認
                    if (!MANAGER.validateEmployee(employee)) {
                        showErrorDialog("社員情報の内容に誤りがあります");
                        return;
                    }
                    // 読み込んだ社員IDが重複していないか確認
                    if (!MANAGER.validateOverlappingEmployee(employee)) {
                        showErrorDialog("重複する社員IDが存在します");
                        return;
                    }
                    // 読み込んだ社員情報を新規社員情報リストに追加
                    newEmployeeList.add(employee);
                }
                // 読み込んだ社員情報の総数が上限を超えていないか確認
                if (newEmployeeList.size() + EmployeeManager.employeeList.size() > 1000) {
                    showErrorDialog("社員情報の総数が上限を超えています");
                    return;
                }
            } catch (Exception e) {
                String message = "指定されたCSVファイルから情報の読み込みが出来ませんでした";
                MANAGER.printExceptionLog(e, message);
                JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 読み込んだ社員情報を保存用CSVファイルに追加
            File originalFile = EmployeeManager.EMPLOYEE_CSV;
            File backupFile = new File("CSV/employee_data_backup.csv");
            try {
                // バックアップファイル作成
                Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                MANAGER.printExceptionLog(e, "バックアップファイルの作成に失敗しました");
                showErrorDialog("バックアップファイルの作成に失敗しました");
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(originalFile, true);
            FileChannel originalFilechannel = fileOutputStream.getChannel();
            // CSVファイルの排他ロック（同時書き込み防止）
            FileLock originalFileLock = null;
            try {
                originalFileLock = originalFilechannel.lock(); // ロック取得

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "Shift-JIS");
                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                        PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

                    for (EmployeeInformation employee : newEmployeeList) {
                        printWriter.println(MANAGER.convertToCSV(employee));
                    }

                } catch (Exception e) {
                    MANAGER.printExceptionLog(e, "社員情報保存CSVへの書き込み中に失敗");
                    return;
                }

            } catch (Exception e) {
                MANAGER.printExceptionLog(e, "CSVファイルロックまたは書き込みに失敗");
                return;
            } finally {
                try {
                    if (originalFileLock != null && originalFileLock.isValid()) {
                        originalFileLock.release(); // ロックを明示的に解除
                    }
                    if (originalFilechannel.isOpen()) {
                        originalFilechannel.close(); // 明示的にチャンネルを閉じる
                    }
                } catch (Exception e) {
                    MANAGER.printExceptionLog(e, "CSVチャンネルまたはロックの解放に失敗");
                }
            }
            Files.deleteIfExists(backupFile.toPath());
            // -------------------------------------------------------
            // 読み込んだ社員情報を社員情報リストに追加
            synchronized (EmployeeManager.employeeList) {
                EmployeeManager.employeeList.addAll(newEmployeeList);
            }
            String message = "CSV読み込み成功";
            MANAGER.printInfoLog(message);
            showDialog(message);
        } catch (Exception e) {
            String message = "指定されたCSVファイルの読み込みが出来ませんでした";
            MANAGER.printExceptionLog(e, message);
            JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            readCsvLock.unlock();
        }
    }

    /**
     * エラー表示用に用意したダイアログに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author 下村
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 成功表示用に用意したダイアログに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    private void showDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }
}
