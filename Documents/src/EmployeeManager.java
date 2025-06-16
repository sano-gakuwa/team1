import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 社員情報を登録・管理するためのマネージャークラス
 * <ul>
 *  <li>呼び出せる変数
 *      <ul>
 *      <li>employeeList 読み込んだ社員情報リスト
 *      <li>LOGGER ログ
 *          <ul>
 *          <li>LOGGER.info(String {文言}) ログに情報を残すために使用
 *          <li>LOGGER.warning(String {文言}) ログにヒューマンエラーを残すために使用
 *          </ul>
 *      <li>ENPLOYEE_CSV　社員情報保存用CSVファイル
 *      <li>EMPLOYEE_CATEGORY　社員情報のカテゴリー
 *      </ul>
 *  <li>呼び出せるメソッド
 *      <ul>
 *      <li>printErrorLog(Exception e, String errorString) ログにシステムエラーを残すために使用
 *      </ul>
 * </ul>
 * 
 * @atuthor 下村
 */
public class EmployeeManager extends SystemLog {
    public static ArrayList<EmployeeInformation> employeeList = new ArrayList<>();
    private static final String CSV_FOLDER = "CSV";
    private static final String CSV_FILEPATH = CSV_FOLDER + "/employee_data.csv";
    public static final File ENPLOYEE_CSV = new File(CSV_FILEPATH);
    public static final String[] EMPLOYEE_CATEGORY = {
            "社員ID", "名字", "名前", "名字フリガナ", "名前フリガナ", "生年月日（西暦）", "入社年月", "エンジニア歴",
            "扱える言語", "経歴", "研修の受講歴", "技術力", "受講態度", "コミュニケーション能力", "リーダーシップ",
            "備考", "更新日"
    };
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    public EmployeeManager(){
        //インスタンス生成時のメソッド等無し
    }
    /**
     * EmployeeManagerの起動
     * @author 下村
     */
    public void setUp() {
        setUpLog();
        LOGGER.info("起動");
        setUpCSV();
        ViewTopScreen top = new ViewTopScreen();
        top.View();
    }
    /**
     * 社員情報保存用CSVから社員情報を読み込み
     * @author 下村
     */
    private void setUpCSV() {
        try {
            Files.createDirectories(Paths.get(CSV_FOLDER));
            if (verificationEmployeeData()) {
                LOGGER.info("社員情報保存用CSVファイル読み込み成功");
            }
            employeeLoading();
            // checkArrayList();
        } catch (Exception e) {
            printErrorLog(e, "社員情報保存用CSVファイル読み込み失敗");
        }
    }

    /**
     * 社員情報を登録・管理するためのCSVファイルの存在確認用
     * @return　社員情報CSVの存在するかの真偽
     * @author 下村
     */
    private boolean verificationEmployeeData() {
        try {
            if (ENPLOYEE_CSV.exists()) {
                if (ENPLOYEE_CSV.isFile() && ENPLOYEE_CSV.canWrite()) {
                    return true;
                }
            } else {
                makeEmployeeData();
                LOGGER.info("社員情報保存用CSVファイル作成");
                return true;
            }
        } catch (Exception e) {
            printErrorLog(e, "社員情報保存用CSVファイルの存在が確認出来ません");
        }
        return false;
    }

    /**
     * CSVファイルが存在しない場合に新規作成する用
     * @author 下村
    */
    private void makeEmployeeData() {
        Path path = Paths.get(CSV_FILEPATH);
        try {
            Files.createFile(path);// ファイルが存在しない為、ファイルを新規作成
        } catch (FileAlreadyExistsException e) {
            // ファイルがすでに存在する場合
            printErrorLog(e, "同じファイルがすでに存在します");
        } catch (Exception e) {
            printErrorLog(e, "ファイル新規作成で例外が発生しました");
        }
        if (ENPLOYEE_CSV.isFile() && ENPLOYEE_CSV.canWrite()) {
            try {
                // 文字コードを指定する
                PrintWriter newFileWriter = new PrintWriter(
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CSV_FILEPATH), "Shift-JIS")));
                // ヘッダー行(必須などの項目は記載無し)を記載する
                for (String category : EMPLOYEE_CATEGORY) {
                    newFileWriter.append(category + ",");
                }
                newFileWriter.append("\n");
                newFileWriter.close();
            } catch (Exception e) {
                printErrorLog(e, "社員情報保存用CSVファイルのヘッダー部分が作成出来ませんでした");
            }
        }
    }
    /**
     * 社員情報の読み込み
     * @author 下村
     */
    private void employeeLoading() {
        try {
            FileInputStream fileInputStream = new FileInputStream(ENPLOYEE_CSV);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "Shift-JIS"));
            Scanner scanner = new Scanner(reader);
            scanner.next();
            try {
                while (scanner.hasNext()) { // 次に読み込むべき行があるか判定
                    ArrayList<String> loadEmployeeDate = new ArrayList<String>(
                            Arrays.asList(scanner.next().split(",")));
                    EmployeeInformation employee = new EmployeeInformation(
                            loadEmployeeDate.get(0),
                            loadEmployeeDate.get(1), loadEmployeeDate.get(2),
                            loadEmployeeDate.get(3), loadEmployeeDate.get(4),
                            dateFormat.parse(loadEmployeeDate.get(5)),
                            dateFormat.parse(loadEmployeeDate.get(6)),
                            Integer.parseInt(loadEmployeeDate.get(7)),
                            loadEmployeeDate.get(8),
                            loadEmployeeDate.get(9),
                            loadEmployeeDate.get(10),
                            Double.parseDouble(loadEmployeeDate.get(11)),
                            Double.parseDouble(loadEmployeeDate.get(12)),
                            Double.parseDouble(loadEmployeeDate.get(13)),
                            Double.parseDouble(loadEmployeeDate.get(14)),
                            loadEmployeeDate.get(15),
                            dateFormat.parse(loadEmployeeDate.get(16)));
                    employeeList.add(employee);
                }
            } catch (Exception e) {
                printErrorLog(e, "社員情報保存用CSVファイルから情報の読み込みが出来ませんでした");
            }
            scanner.close();
        } catch (Exception e) {
            printErrorLog(e, "社員情報保存用CSVファイルから情報の読み込みが出来ませんでした");
        }
    }

    /**
     * ログにスタックトレースを出力する
     * @param e           スタックトレースを持っている例外クラス
     * @param errorString ログに出力するエラー文言
     * @author 下村
     */
    public void printErrorLog(Exception e, String errorString) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter  = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter );
        LOGGER.severe(String.format("%s\n%s", errorString, stringWriter.toString()));
    }
}
