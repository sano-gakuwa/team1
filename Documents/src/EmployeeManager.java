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
            validate = validateEmployeeID(employee, validate);
            validate = validateName(employee, validate);
            validate = validateRuby(employee, validate);
            validate = validateBirthday(employee, validate);
            validate = validateJoiningDate(employee, validate);
            validate = validateEngineerDate(employee, validate);
            validate = validateLanguages(employee, validate);
            validate = validateCareer(employee, validate);
            validate = validateTraining(employee, validate);
            validate = validateAllScores(employee, validate);
            validate = validateRemarks(employee, validate);

        } catch (Exception e) {
            printExceptionLog(e, "形式エラー");
        }
        return validate;
    }

    public boolean validateEmployeeID(EmployeeInformation employee, boolean validate) {
        if (employee.getEmployeeID().length() != 7) {
            printErrorLog("エラー:社員IDが7桁ではありません");
            validate = false;
        }
        return validate;
    }

    /**
     * 氏名（姓・名）の必須、文字数、形式、記号、サロゲートペアのチェック
     */
    public boolean validateName(EmployeeInformation employee, boolean validate) {
        String lastName = employee.getLastName();
        String firstName = employee.getFirstname();

        if (lastName.length() > 15) {
            printErrorLog("エラー:姓が15文字を超えています");
            validate = false;
        }
        if (firstName.length() > 15) {
            printErrorLog("エラー:名が15文字を超えています");
            validate = false;
        }
        if (containsForbiddenChars(lastName) || containsForbiddenChars(firstName)) {
            printErrorLog("エラー:氏名に使用できない文字が含まれています");
            validate = false;
        }
        if (containsSurrogatePair(lastName) || containsSurrogatePair(firstName)) {
            printErrorLog("エラー:氏名に環境依存文字が含まれています");
            validate = false;
        }

        return validate;
    }

    /**
     * フリガナ（姓・名）の文字数、文字種、記号、サロゲートペアのチェック
     */
    public boolean validateRuby(EmployeeInformation employee, boolean validate) {
        String rubyLastName = employee.getRubyLastName();
        String rubyFirstName = employee.getRubyFirstname();

        if (rubyLastName.length() > 15) {
            printErrorLog("エラー:姓のフリガナが15文字を超えています");
            validate = false;
        }
        if (rubyFirstName.length() > 15) {
            printErrorLog("エラー:名のフリガナが15文字を超えています");
            validate = false;
        }
        if (!rubyLastName.matches("^[ァ-ヴー]+$") || !rubyFirstName.matches("^[ァ-ヴー]+$")) {
            printErrorLog("エラー:フリガナに使用できない文字が含まれています（全角カタカナのみ）");
            validate = false;
        }
        if (containsSurrogatePair(rubyLastName) || containsSurrogatePair(rubyFirstName)) {
            printErrorLog("エラー:フリガナに環境依存文字が含まれています");
            validate = false;
        }

        return validate;
    }

    /**
     * 生年月日の未来日チェック
     */
    public boolean validateBirthday(EmployeeInformation employee, boolean validate) {
        if (validateNotFuture(employee.getBirthday())) {
            printErrorLog("エラー:生年月日が未来日です");
            validate = false;
        }
        return validate;
    }

    /**
     * 入社年月の未来日チェック
     */
    public boolean validateJoiningDate(EmployeeInformation employee, boolean validate) {
        if (validateNotFuture(employee.getJoiningDate())) {
            printErrorLog("エラー:入社年月が未来日です");
            validate = false;
        }
        return validate;
    }

    /**
     * エンジニア歴の上限・下限チェック（月数）
     */
    public boolean validateEngineerDate(EmployeeInformation employee, boolean validate) {
        int months = employee.getEngineerDate();
        if (months < 0) {
            printErrorLog("エラー:エンジニア歴が0ヶ月以下です");
            validate = false;
        }
        if (months > 50 * 12) {
            printErrorLog("エラー:エンジニア歴が50年を超えています");
            validate = false;
        }
        return validate;
    }

    /**
     * 扱える言語：文字数、区切り形式、サロゲートペアチェック
     */
    public boolean validateLanguages(EmployeeInformation employee, boolean validate) {
        String langs = employee.getAvailableLanguages();
        if (langs.length() > 100) {
            printErrorLog("エラー:扱える言語が100文字を超えています");
            validate = false;
        }
        if (langs.contains(" ")) {
            printErrorLog("エラー:言語は空白ではなく中黒（・）で区切ってください");
            validate = false;
        }
        if (containsSurrogatePair(langs)) {
            printErrorLog("エラー:扱える言語に環境依存文字が含まれています");
            validate = false;
        }
        return validate;
    }

    /**
     * 経歴：文字数、記号、サロゲートペアチェック
     */
    public boolean validateCareer(EmployeeInformation employee, boolean validate) {
        String career = employee.getCareerDate();
        if (career.length() > 400) {
            printErrorLog("エラー:経歴が400文字を超えています");
            validate = false;
        }
        if (containsForbiddenChars(career)) {
            printErrorLog("エラー:経歴に使用できない記号が含まれています");
            validate = false;
        }
        if (containsSurrogatePair(career)) {
            printErrorLog("エラー:経歴に環境依存文字が含まれています");
            validate = false;
        }
        return validate;
    }

    /**
     * 研修：文字数、記号、サロゲートペアチェック
     */
    public boolean validateTraining(EmployeeInformation employee, boolean validate) {
        String training = employee.getTrainingDate();
        if (training.length() > 400) {
            printErrorLog("エラー:研修歴が400文字を超えています");
            validate = false;
        }
        if (containsForbiddenChars(training)) {
            printErrorLog("エラー:研修歴に使用できない記号が含まれています");
            validate = false;
        }
        if (containsSurrogatePair(training)) {
            printErrorLog("エラー:研修歴に環境依存文字が含まれています");
            validate = false;
        }
        return validate;
    }

    /**
     * 備考：文字数、記号、サロゲートペアチェック
     */
    public boolean validateRemarks(EmployeeInformation employee, boolean validate) {
        String remarks = employee.getRemarks();
        if (remarks.length() > 400) {
            printErrorLog("エラー:備考が400文字を超えています");
            validate = false;
        }
        if (containsForbiddenChars(remarks)) {
            printErrorLog("エラー:備考に使用できない記号が含まれています");
            validate = false;
        }
        if (containsSurrogatePair(remarks)) {
            printErrorLog("エラー:備考に環境依存文字が含まれています");
            validate = false;
        }
        return validate;
    }

    /**
     * 評価項目：1〜5の0.5刻みであることを確認（共通）
     */
    public boolean validateScore(double score, String itemName, boolean validate) {
        if (score < 1 || score > 5) {
            printErrorLog("エラー:" + itemName + "が1〜5の範囲外です");
            validate = false;
        }
        if (score % 0.5 != 0) {
            printErrorLog("エラー:" + itemName + "は0.5刻みで入力してください");
            validate = false;
        }
        return validate;
    }

    public boolean validateAllScores(EmployeeInformation employee, boolean validate) {
        validate = validateScore(employee.getSkillPoint(), "技術力", validate);
        validate = validateScore(employee.getAttitudePoint(), "受講態度", validate);
        validate = validateScore(employee.getCommunicationPoint(), "コミュニケーション能力", validate);
        validate = validateScore(employee.getLeadershipPoint(), "リーダーシップ", validate);
        return validate;
    }

    /**
     * サロゲートペアを含むかどうかの共通チェック
     */
    public boolean containsSurrogatePair(String input) {
        if (input == null)
            return false;
        return Pattern.compile("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]").matcher(input).find();
    }

    /**
     * 記号や全角英字、半角カナを含むかどうか
     */
    public boolean containsForbiddenChars(String input) {
        return input.matches(".*[\\uFF61-\\uFF9FＡ-Ｚａ-ｚ！＠＃＄％＾＆＊（）＿＋＝￥|｛｝［］：；“”’＜＞？／\\\\].*");
    }

    /**
     * 日付が未来の日付では無いか確認用
     * 
     * @param date 日付
     * @return 日付が未来の日付の場合はtrue、そうでない場合はfalse
     * @author 下村
     */
    public boolean validateNotFuture(Date date) {
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
