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
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

/**
 * 社員情報を登録・管理するためのマネージャークラス
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
        printInfoLog("起動");
        setUpCSV();
        setFrameExit exit = new setFrameExit();
        exit.setExit();
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
                printInfoLog("社員情報保存用CSVファイル読み込み成功");
            }
            employeeLoading();
            // checkArrayList();
        } catch (Exception e) {
            printExceptionLog(e, "社員情報保存用CSVファイル読み込み失敗");
            JOptionPane.showMessageDialog(null, "社員情報保存用CSVファイル読み込み失敗", "エラー", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
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
                printInfoLog("社員情報保存用CSVファイル作成");
                return true;
            }
        } catch (Exception e) {
            printExceptionLog(e, "社員情報保存用CSVファイルの存在が確認出来ません");
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
            printInfoLog("ファイルを新規作成");
        } catch (FileAlreadyExistsException e) {
            // ファイルがすでに存在する場合
            printExceptionLog(e, "同じファイルがすでに存在します");
        } catch (Exception e) {
            printExceptionLog(e, "ファイル新規作成で例外が発生しました");
        }
        if (EMPLOYEE_CSV.isFile() && EMPLOYEE_CSV.canWrite()) {
            // 文字コードを指定する
            try (PrintWriter newFileWriter = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(CSV_FILEPATH), "Shift-JIS")))) {
                // ヘッダー行(必須などの項目は記載無し)を記載する
                for (String category : EMPLOYEE_CATEGORY) {
                    newFileWriter.append(category + ",");
                }
                printInfoLog("ヘッダー部分にカテゴリー名入力済み");
                newFileWriter.append("\n");
            } catch (Exception e) {
                printExceptionLog(e, "社員情報保存用CSVファイルのヘッダー部分が作成出来ませんでした");
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
                    // 読み込んだ社員情報の文字列
                    ArrayList<String> loadEmployeeDate;
                    loadEmployeeDate = new ArrayList<String>(Arrays.asList(scanner.next().split(",")));
                    // 読み込んだ社員情報を社員情報型に変換
                    EmployeeInformation employee = convertEmployeeInformation(loadEmployeeDate);
                    // 社員情報リストに社員情報を追加
                    employeeList.add(employee);
                }
            } catch (Exception e) {
                printExceptionLog(e, "社員情報保存用CSVファイルから情報の読み込みが出来ませんでした");
            } finally {
                scanner.close();
            }
        } catch (Exception e) {
            printExceptionLog(e, "社員情報保存用CSVファイルから情報の読み込みが出来ませんでした");
        }
    }

    @Override
    public void printExceptionLog(Exception e, String errorString) {
        Logger logger = getLogger();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        logger.severe(String.format("%s\n%s", errorString, stringWriter.toString()));
    }

    @Override
    public void printInfoLog(String infoString) {
        Logger logger = getLogger();
        logger.info(infoString);
    }

    @Override
    public void printErrorLog(String errString) {
        Logger logger = getLogger();
        logger.warning(errString);
    }

    /**
     * 文字列を社員情報型に変換
     * 
     * @param loadEmployeeDate 読み込まれた社員情報の文字列
     * @return 社員情報型
     * @author simomura
     */
    public EmployeeInformation convertEmployeeInformation(ArrayList<String> loadEmployeeDate) {
        EmployeeInformation employee = new EmployeeInformation();
        try {
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
        } catch (Exception e) {
            printExceptionLog(e, "文字列から社員情報に変換に失敗しました");
        }
        return employee;
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
        // カンマ区切りの文字列
        String csvTypeString = null;
        // 社員情報の各フィールドをカンマ区切りで連結
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append(employee.getEmployeeID()).append(",");
        csvBuilder.append(employee.getLastName()).append(",");
        csvBuilder.append(employee.getFirstname()).append(",");
        csvBuilder.append(employee.getRubyLastName()).append(",");
        csvBuilder.append(employee.getRubyFirstname()).append(",");
        csvBuilder.append(employee.formatDate(employee.getBirthday())).append(",");
        csvBuilder.append(employee.formatDate(employee.getJoiningDate())).append(",");
        csvBuilder.append(employee.getEngineerDate()).append(",");
        csvBuilder.append(employee.getAvailableLanguages()).append(",");
        csvBuilder.append(employee.getCareerDate()).append(",");
        csvBuilder.append(employee.getTrainingDate()).append(",");
        csvBuilder.append(employee.getSkillPoint()).append(",");
        csvBuilder.append(employee.getAttitudePoint()).append(",");
        csvBuilder.append(employee.getCommunicationPoint()).append(",");
        csvBuilder.append(employee.getLeadershipPoint()).append(",");
        csvBuilder.append(employee.getRemarks()).append(",");
        csvBuilder.append(employee.formatDate(employee.getUpdatedDay())).append(",");
        // カンマ区切りで連結した文字列を返す
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
            printErrorLog("社員ID欄が空欄です");
            validate = false;
        }
        if (employee.getLastName() == null || employee.getLastName().isEmpty()) {
            printErrorLog("名字欄が空欄です");
            validate = false;
        }
        if (employee.getFirstname() == null || employee.getFirstname().isEmpty()) {
            printErrorLog("名前欄が空欄です");
            validate = false;
        }
        if (employee.getRubyLastName() == null || employee.getRubyLastName().isEmpty()) {
            printErrorLog("名字のフリガナ欄が空欄です");
            validate = false;
        }
        if (employee.getRubyFirstname() == null || employee.getRubyFirstname().isEmpty()) {
            printErrorLog("名前のフリガナ欄が空欄です");
            validate = false;
        }
        return validate;
    }

    /**
     * 要求仕様書通りの仕様になっているのか確認用
     * 
     * @param employee 新規追加しようとしている社員情報
     * @return 要求仕様書通りの仕様になっている場合はtrue、そうでない場合はfalse
     * @author 下村
     */
    public boolean validateEmployee(EmployeeInformation employee) {
        boolean validate = true;
        try {
            validateEmployeeID(employee, validate);
            validateName(employee, validate);
            validateirthday(employee, validate);
            validateJoiningDate(employee, validate);
            validateEngineerDate(employee, validate);
            validateCareerDate(employee, validate);
            validateTrainingDate(employee, validate);
            validateSkillPoint(employee, validate);
            validateCommunicationPoint(employee, validate);
            validateAttitudePoint(employee, validate);
            validateLeadershipPoint(employee, validate);
            validateRemarks(employee, validate);
            validateNameForbiddenChars(employee, validate);
        } catch (Exception e) {
            printExceptionLog(e, "形式エラー");
        }
        return validate;
    }

    private boolean validateEmployeeID(EmployeeInformation employee, boolean validate) {
        if (employee.getEmployeeID().length() != 7) {
            printErrorLog("エラー:社員IDが7桁ではありません");
            validate = false;
        }
        return validate;
    }

    /**
 * サロゲートペア（機種依存文字）を含むかを判定するメソッド
 * 
 * 例：「髙」「①」「🈂」「💻」「𠮷」などの機種依存文字が含まれていれば true を返します。
 * 
 * @param input チェック対象の文字列（姓、名、フリガナなど）
 * @return サロゲートペアが含まれていれば true、含まれていなければ false
 */
public boolean containsSurrogatePair(String input) {
    if (input == null) return false; // nullは機種依存文字ではないためfalse
    return Pattern.compile("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]").matcher(input).find();
}


    private boolean validateName(EmployeeInformation employee, boolean validate) {
        if (employee.getLastName().length() > 15) {
            printErrorLog("エラー:姓が15文字より多いです");
            validate = false;
        }
        if (employee.getFirstname().length() > 15) {
            printErrorLog("エラー:名が15文字より多いです");
            validate = false;
        }
        if (employee.getRubyLastName().length() > 15) {
            printErrorLog("エラー:姓の読みが15文字より多いです");
            validate = false;
        }
        if (employee.getRubyFirstname().length() > 15) {
            printErrorLog("エラー:名の読みがが15文字より多いです");
            validate = false;
        }
        return validate;
    }



    private boolean validateirthday(EmployeeInformation employee, boolean validate) {
        if (validateNotFuture(employee.getBirthday())) {
            printErrorLog("エラー:誕生日が未来の日付です");
            validate = false;
        }
        return validate;
    }

    private boolean validateJoiningDate(EmployeeInformation employee, boolean validate) {
        if (validateNotFuture(employee.getJoiningDate())) {
            printErrorLog("エラー:入社日が未来の日付です");
            validate = false;
        }
        return validate;
    }

    private boolean validateNameForbiddenChars(EmployeeInformation employee, boolean validate) {
    String lastName = employee.getLastName();
    String firstName = employee.getFirstname();

    if (lastName.matches(".*[\\uFF61-\\uFF9F].*")
        || lastName.matches(".*[Ａ-Ｚａ-ｚ].*")
        || lastName.matches(".*[！＠＃＄％＾＆＊（）＿＋＝￥|｛｝［］：；“”’＜＞？／\\\\].*")
        || firstName.matches(".*[\\uFF61-\\uFF9F].*")
        || firstName.matches(".*[Ａ-Ｚａ-ｚ].*")
        || firstName.matches(".*[！＠＃＄％＾＆＊（）＿＋＝￥|｛｝［］：；“”’＜＞？／\\\\].*")) {
        printErrorLog("エラー:氏名に使用できない文字が含まれています");
        validate = false;
    }
    return validate;
}

    private boolean validateEngineerDate(EmployeeInformation employee, boolean validate) {
        if (employee.getEngineerDate() < 0) {
            printErrorLog("エラー:エンジニア歴がマイナスです");
            validate = false;
        }
        if (employee.getEngineerDate() >= 600) {
            printErrorLog("エラー:エンジニア歴が50年以上です");
            validate = false;
        }
        return validate;
    }

    private boolean validateRemarks(EmployeeInformation employee, boolean validate) {
        if (employee.getRemarks().length() > 400) {
            printErrorLog("エラー:経歴が400文字より多いです");
            validate = false;
        }
        return validate;
    }

    private boolean validateTrainingDate(EmployeeInformation employee, boolean validate) {
        if (employee.getTrainingDate().length() > 400) {
            printErrorLog("エラー:受講歴が400文字より多いです");
            validate = false;
        }
        return validate;
    }

    private boolean validateSkillPoint(EmployeeInformation employee, boolean validate) {
        if (employee.getSkillPoint() % 0.5 != 0) {
            printErrorLog("エラー:技術力の項目が0.5刻みではありません");
            validate = false;
        }
        if (employee.getSkillPoint() > 5) {
            printErrorLog("エラー:技術力の項目が5より大きいです");
            validate = false;
        }
        if (employee.getSkillPoint() < 1) {
            printErrorLog("エラー:技術力の項目が1より小さいです");
            validate = false;
        }
        return validate;
    }

    private boolean validateCommunicationPoint(EmployeeInformation employee, boolean validate) {
        if (employee.getCommunicationPoint() % 0.5 != 0) {
            printErrorLog("エラー:コミュニケーション能力の項目が0.5刻みではありません");
            validate = false;
        }
        if (employee.getCommunicationPoint() > 5) {
            printErrorLog("エラー:コミュニケーション能力の項目が5より大きいです");
            validate = false;
        }
        if (employee.getCommunicationPoint() < 1) {
            printErrorLog("エラー:コミュニケーション能力の項目が1より小さいです");
            validate = false;
        }
        return validate;
    }

    private boolean validateAttitudePoint(EmployeeInformation employee, boolean validate) {
        if (employee.getAttitudePoint() % 0.5 != 0) {
            printErrorLog("エラー:受講態度の項目が0.5刻みではありません");
            validate = false;
        }
        if (employee.getAttitudePoint() > 5) {
            printErrorLog("エラー:受講態度の項目が5より大きいです");
            validate = false;
        }
        if (employee.getAttitudePoint() < 1) {
            printErrorLog("エラー:受講態度の項目が1より小さいです");
            validate = false;
        }
        return validate;
    }

    private boolean validateLeadershipPoint(EmployeeInformation employee, boolean validate) {
        if (employee.getLeadershipPoint() % 0.5 != 0) {
            printErrorLog("エラー:リーダーシップの項目が0.5刻みではありません");
            validate = false;
        }
        if (employee.getLeadershipPoint() > 5) {
            printErrorLog("エラー:リーダーシップの項目が5より大きいです");
            validate = false;
        }
        if (employee.getLeadershipPoint() < 1) {
            printErrorLog("エラー:リーダーシップの項目が1より小さいです");
            validate = false;
        }
        return validate;
    }

    private boolean validateCareerDate(EmployeeInformation employee, boolean validate) {
        if (employee.getCareerDate().length() > 400) {
            printErrorLog("エラー:備考が400文字より多いです");
            validate = false;
        }
        return validate;
    }

    /**
     * 日付が未来の日付では無いか確認用
     * 
     * @param date 日付
     * @return 日付が未来の日付の場合はtrue、そうでない場合はfalse
     * @author 下村
     */
    private boolean validateNotFuture(Date date) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean isFuture = targetDate.isAfter(today);
        if (isFuture) {
            printErrorLog("エラー:未来の日付です");
        }
        return isFuture;
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
                return false;
            }
        }
        return true;
    }
}
