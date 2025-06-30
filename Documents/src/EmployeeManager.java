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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * 社員情報を登録・管理するためのマネージャークラス
 * <ul>
 * <li>呼び出せる変数
 * <ul>
 * <li>employeeList 読み込んだ社員情報リスト
 * <li>LOGGER ログ
 * <ul>
 * <li>LOGGER.info(String {文言}) ログに情報を残すために使用
 * <li>LOGGER.warning(String {文言}) ログにヒューマンエラーを残すために使用
 * </ul>
 * <li>EMPLOYEE_CSV 社員情報保存用CSVファイル
 * <li>EMPLOYEE_CATEGORY 社員情報のカテゴリー
 * </ul>
 * <li>呼び出せるメソッド
 * <ul>
 * <li>printErrorLog(Exception e, String errorString) ログにシステムエラーを残すために使用
 * </ul>
 * </ul>
 * 
 * @atuthor 下村
 */
public class EmployeeManager extends SystemLog {
    public static ArrayList<EmployeeInformation> employeeList = new ArrayList<>();
    private static final String CSV_FOLDER = "CSV";
    private static final String CSV_FILEPATH = CSV_FOLDER + "/employee_data.csv";
    public static final File EMPLOYEE_CSV = new File(CSV_FILEPATH);
    public static final String[] EMPLOYEE_CATEGORY = {
            "社員ID", "名字", "名前", "名字フリガナ", "名前フリガナ", "生年月日（西暦）", "入社年月", "エンジニア歴",
            "扱える言語", "経歴", "研修の受講歴", "技術力", "受講態度", "コミュニケーション能力", "リーダーシップ",
            "備考", "更新日"
    };
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    public EmployeeManager() {
        // インスタンス生成時のメソッド等無し
    }

    /**
     * EmployeeManagerの起動
     * 
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
     * 
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
     * 
     * @return 社員情報CSVの存在するかの真偽
     * @author 下村
     */
    private boolean verificationEmployeeData() {
        try {
            if (EMPLOYEE_CSV.exists()) {
                if (EMPLOYEE_CSV.isFile() && EMPLOYEE_CSV.canWrite()) {
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
     * 
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
        if (EMPLOYEE_CSV.isFile() && EMPLOYEE_CSV.canWrite()) {
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
     * 
     * @author 下村
     */
    private void employeeLoading() {
        try {
            FileInputStream fileInputStream = new FileInputStream(EMPLOYEE_CSV);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "Shift-JIS"));
            Scanner scanner = new Scanner(reader);
            scanner.next();
            try {
                while (scanner.hasNext()) { // 次に読み込むべき行があるか判定
                    ArrayList<String> loadEmployeeDate = new ArrayList<String>(
                            Arrays.asList(scanner.next().split(",")));
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
     * 
     * @param e           スタックトレースを持っている例外クラス
     * @param errorString ログに出力するエラー文言
     * @author 下村
     */
    public void printErrorLog(Exception e, String errorString) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        LOGGER.severe(String.format("%s\n%s", errorString, stringWriter.toString()));
    }
    /**
     * 社員情報をCSV形式の文字列に変換
     *
     * @param employee 変換する社員情報
     * @return CSV形式の文字列
     *
     * @author simomura
     */
    public String convertToCSV(EmployeeInformation employee) {
        // 社員情報をCSV形式の文字列に変換するメソッド
        //カンマ区切りの文字列
        String csvTypeString = null;
        // 社員情報の各フィールドをカンマ区切りで連結
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append(employee.getEmployeeID()).append(",");
        csvBuilder.append(employee.getLastName()).append(",");
        csvBuilder.append(employee.getFirstname()).append(",");
        csvBuilder.append(employee.getRubyLastName()).append(",");
        csvBuilder.append(employee.getRubyFirstname()).append(",");
        csvBuilder.append(EmployeeInformation.formatDate(employee.getBirthday())).append(",");
        csvBuilder.append(EmployeeInformation.formatDate(employee.getJoiningDate())).append(",");
        csvBuilder.append(employee.getEngineerDate()).append(",");
        csvBuilder.append(employee.getAvailableLanguages()).append(",");
        csvBuilder.append(employee.getCareerDate()).append(",");
        csvBuilder.append(employee.getTrainingDate()).append(",");
        csvBuilder.append(employee.getSkillPoint()).append(",");
        csvBuilder.append(employee.getAttitudePoint()).append(",");
        csvBuilder.append(employee.getCommunicationPoint()).append(",");
        csvBuilder.append(employee.getLeadershipPoint()).append(",");
        csvBuilder.append(employee.getRemarks()).append(",");
        csvBuilder.append(EmployeeInformation.formatDate(employee.getUpdatedDay())).append(",");
        //カンマ区切りで連結した文字列を返す
        csvTypeString = csvBuilder.toString();
        return csvTypeString;
    }
    /**
     * 社員情報の形式が正しいかを検証 必須項目がすべて入力されているかを確認
     *
     * @param employee 検証する社員情報
     * @return 形式が正しい場合はtrue、そうでない場合はfalse
     * @author nishiyama
     */
    public boolean validateNotNull(EmployeeInformation employee) {
        boolean validate = true;
        if (employee.getEmployeeID() == null || employee.getEmployeeID().isEmpty()) {
            LOGGER.warning("社員ID欄が空欄です");
            validate = false;
        }
        if (employee.getLastName() == null || employee.getLastName().isEmpty()) {
            LOGGER.warning("名字欄が空欄です");
            validate = false;
        }
        if (employee.getFirstname() == null || employee.getFirstname().isEmpty()) {
            LOGGER.warning("名前欄が空欄です");
            validate = false;
        }
        if (employee.getRubyLastName() == null || employee.getRubyLastName().isEmpty()) {
            LOGGER.warning("名字のフリガナ欄が空欄です");
            validate = false;
        }
        if (employee.getRubyFirstname() == null || employee.getRubyFirstname().isEmpty()) {
            LOGGER.warning("名前のフリガナ欄が空欄です");
            validate = false;
        }
        if (employee.getBirthday() == null) {
            LOGGER.warning("誕生日欄が空欄です");
            validate = false;
        }
        if (employee.getJoiningDate() == null) {
            LOGGER.warning("入社年月欄が空欄です");
            validate = false;
        }
        if (employee.getSkillPoint() == null) {
            LOGGER.warning("技術欄が空欄です");
            validate = false;
        }
        if (employee.getCommunicationPoint() == null) {
            LOGGER.warning("コミュニケーション能力欄が空欄です");
            validate = false;
        }
        if (employee.getAttitudePoint() == null) {
            LOGGER.warning("受講態度欄が空欄です");
            validate = false;
        }
        if (employee.getLeadershipPoint() == null) {
            LOGGER.warning("リーダーシップ欄が空欄です");
            validate = false;
        }
        return validate;
    }
    /**
     * 要求仕様書通りの仕様になっているのか確認用
     * 
     * @param employee 新規追加しようとしている社員情報
     * @return true or false
     * @author 下村
     */
    public boolean validateEmployee(EmployeeInformation employee) {
        boolean validate = true;
        try {
            if (employee.getEmployeeID().length() != 7) {
                validate = false;
            }
            if (employee.getLastName().length() > 15) {
                validate = false;
            }
            if (employee.getFirstname().length() > 15) {
                validate = false;
            }
            if (employee.getRubyLastName().length() > 15) {
                validate = false;
            }
            if (employee.getRubyFirstname().length() > 15) {
                validate = false;
            }
            if (validateNotFuture(employee.getBirthday())) {
                validate = false;
            }
            if (validateNotFuture(employee.getJoiningDate())) {
                validate = false;
            }
            if (employee.getEngineerDate() >= 600) {
                validate = false;
            }
        } catch (Exception e) {
            printErrorLog(e, "形式エラー");
        }
        return validate;
    }
    /**
     * 日付が未来の日付では無いか確認用
     * 
     * @param date 日付
     * @return true or false
     * @author 下村
     */
    private boolean validateNotFuture(Date date) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("[]y年[]M月[]d日"));
        return targetDate.isBefore(today);
    }
    /**
     * 重複する社員IDが存在するかを検証
     *
     * @param employee 検証する社員情報
     * @return 重複がある場合はtrue、そうでない場合はfalse
     * @author nishiyama
     */
    public boolean validateOverlappingEmployee(EmployeeInformation employee) {
        // 重複チェック：既に同じ社員IDが存在していないか
        for (EmployeeInformation existing : employeeList) {
            if (existing.getEmployeeID().equals(employee.getEmployeeID())) {
                return true;
            }
        }
        return false;
    }
}
