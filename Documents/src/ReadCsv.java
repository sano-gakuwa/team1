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
import java.text.SimpleDateFormat;
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
        MANAGER.LOGGER.info("CSV読み込み処理を開始します");
        // ロックを取得
        readCsvLock.lock();
        MANAGER.LOGGER.info("CSV読み込み処理をロックしました");

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
                    String[] loadEmployeeData = scanner.nextLine().split(",");
                    ArrayList<String> loadEmployeeDate = new ArrayList<String>(Arrays.asList(loadEmployeeData));
                    // 日付のフォーマットを指定
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                    // 読み込んだデータをEmployeeInformationオブジェクトに変換
                    EmployeeInformation employee = new EmployeeInformation();
                    employee.setEmployeeID(loadEmployeeDate.get(0));
                    employee.setlastName(loadEmployeeDate.get(1));
                    employee.setFirstname(loadEmployeeDate.get(2));
                    employee.setRubyLastName(loadEmployeeDate.get(3));
                    employee.setRubyFirstname(loadEmployeeDate.get(4));
                    employee.setBirthday(dateFormat.parse(loadEmployeeDate.get(5)));
                    employee.setJoiningDate(dateFormat.parse(loadEmployeeDate.get(6)));
                    employee.setEngineerDate(Integer.parseInt(loadEmployeeDate.get(7)));
                    employee.setAvailableLanguages(loadEmployeeDate.get(8));
                    employee.setCareerDate(loadEmployeeDate.get(9));
                    employee.setTrainingDate(loadEmployeeDate.get(10));
                    employee.setSkillPoint(Double.parseDouble(loadEmployeeDate.get(11)));
                    employee.setAttitudePoint(Double.parseDouble(loadEmployeeDate.get(12)));
                    employee.setCommunicationPoint(Double.parseDouble(loadEmployeeDate.get(13)));
                    employee.setLeadershipPoint(Double.parseDouble(loadEmployeeDate.get(14)));
                    employee.setRemarks(loadEmployeeDate.get(15));
                    employee.setUpdatedDay(dateFormat.parse(loadEmployeeDate.get(16)));
                    // 読み込んだデータが要求仕様書通りの仕様になっているか確認
                    if (MANAGER.validateEmployee(employee) != false) {
                        String message = "指定されたCSVファイルに形式エラーが有ります";
                        MANAGER.LOGGER.warning(message);
                        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // 読み込んだ社員情報を新規社員情報リストに追加
                    newEmployeeList.add(employee);
                }
            } catch (Exception e) {
                String message = "指定されたCSVファイルから情報の読み込みが出来ませんでした";
                MANAGER.printErrorLog(e, message);
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
                MANAGER.printErrorLog(e, "バックアップファイルの作成に失敗しました");
                showErrorDialog("バックアップファイルの作成に失敗しました");
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(originalFile, true);
            FileChannel originalFilechannel = fileOutputStream.getChannel();
            // CSVファイルの排他ロック（同時書き込み防止）
            try (FileLock originalFileLock = originalFilechannel.lock()) {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "Shift-JIS");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                try (PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
                    // 読み込んだ社員情報リストの内容を社員情報保存CSVに保存
                    for (EmployeeInformation employee : newEmployeeList) {
                        printWriter.println(MANAGER.convertToCSV(employee));
                    }
                } catch (Exception e) {
                    MANAGER.printErrorLog(e, "社員情報リストの内容を社員情報保存CSVに保存失敗");
                    return;
                }
                try {
                    if (originalFileLock != null && originalFileLock.isValid()) {
                        originalFileLock.release(); // エラーでもロック解除
                        MANAGER.LOGGER.info("社員情報保存CSVファイルのロックを解除しました");
                    }
                } catch (Exception ex) {
                    MANAGER.printErrorLog(ex, "社員情報保存CSVファイルのロック解除に失敗しました");
                }
            } catch (Exception e) {
                // 追記中エラー時のロールバック処理を追加
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
            // -------------------------------------------------------
            // 読み込んだ社員情報を社員情報リストに追加
            synchronized (EmployeeManager.employeeList) {
                EmployeeManager.employeeList.addAll(newEmployeeList);
            }
            String message = "CSV読み込み成功";
            MANAGER.LOGGER.info(message);
            showDialog(message);
        } catch (Exception e) {
            String message = "指定されたCSVファイルの読み込みが出来ませんでした";
            MANAGER.printErrorLog(e, message);
            JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
            return;
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
