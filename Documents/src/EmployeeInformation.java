import java.util.Date;
import java.text.SimpleDateFormat;
/**
 * 社員情報型
 * @param employeeID 社員ID:String
 * @param lastName 姓名の姓:String
 * @param firstname 姓名の名:String
 * @param rubyLastName 姓名の姓(読み・ルビ):String
 * @param rubyFirstname 姓名の名(読み・ルビ):String
 * @param birthday 誕生日:Date
 * @param joiningDate 入社日:Date
 * @param engineerDate エンジニア歴:int
 * @param availableLanguages 扱える言語:String
 * @param careerDate 経歴:String
 * @param trainingDate 研修受講歴:String
 * @param skillPoint 技術力:Double
 * @param communicationPoint コミュニケーション能力:Double
 * @param attitudePoint 受講態度:Double
 * @param leadershipPoint リーダーシップ:Double
 * @param remarks 備考:String
 * @param updatedDay 更新日:Date
 * @author 下村
 */
public class EmployeeInformation {
    /**社員ID */
    private String employeeID;
    /**姓名の姓 */
    private String lastName;
    /**姓名の名 */
    private String firstname;
    /**姓名の姓(読み・ルビ) */
    private String rubyLastName;
    /**姓名の名(読み・ルビ) */
    private String rubyFirstname;
    /**誕生日 */
    private Date birthday;
    /**入社日 */
    private Date joiningDate;
    /**エンジニア歴 */
    private int engineerDate;
    /**扱える言語 */
    private String availableLanguages;
    /**経歴 */
    private String careerDate;
    /**研修受講歴 */
    private String trainingDate;
    /**技術力 */
    private Double skillPoint;
    /**コミュニケーション能力 */
    private Double communicationPoint;
    /**受講態度 */
    private Double attitudePoint;
    /**リーダーシップ */
    private Double leadershipPoint;
    /**備考 */
    private String remarks;
    /**更新日 */
    private Date updatedDay;

    public EmployeeInformation() {}

    /**
     * 社員情報の社員IDを設定
     * @param employeeID 社員ID
     */
    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }
    /**
     * 社員情報の姓を設定
     * @param lastName 姓
     */
    public void setlastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * 社員情報の名を設定
     * @param firstname 名
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    /**
     * 社員情報の姓(ルビ)を設定
     * @param rubyLastName 姓(ルビ)
     */
    public void setRubyLastName(String rubyLastName) {
        this.rubyLastName = rubyLastName;
    }
    /**
     * 社員情報の名(ルビ)を設定
     * @param rubyFirstname 名(ルビ)
     */
    public void setRubyFirstname(String rubyFirstname) {
        this.rubyFirstname = rubyFirstname;
    }
    /**
     * 社員情報の生年月日を設定
     * @param birthday 生年月日
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
    /**
     * 社員情報の入社日を設定
     * @param joiningDate 入社日
     */
    public void setJoiningDate(Date joiningDate) {
        this.joiningDate = joiningDate;
    }
    /**
     * 社員情報のエンジニア歴を設定
     * @param engineerDate エンジニア歴
     */
    public void setEngineerDate(int engineerDate) {
        this.engineerDate = engineerDate;
    }
    /**
     * 社員情報の扱える言語を設定
     * @param availableLanguages 扱える言語
     */
    public void setAvailableLanguages(String availableLanguages) {
        this.availableLanguages = availableLanguages;
    }
    /**
     * 社員情報の経歴を設定
     * @param careerDate 経歴
     */
    public void setCareerDate(String careerDate) {
        this.careerDate = careerDate;
    }
    /**
     * 社員情報の研修受講歴を設定
     * @param trainingDate 研修受講歴
     */
    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }
    /**
     * 社員情報の技術力を設定
     * @param skillPoint 技術力
     */
    public void setSkillPoint(Double skillPoint) {
        this.skillPoint = skillPoint;
    }
    /**
     * 社員情報のコミュニケーション能力スコアを設定
     * @param communicationPoint コミュニケーション能力スコア
     */
    public void setCommunicationPoint(Double communicationPoint) {
        this.communicationPoint = communicationPoint;
    }
    /**
     * 社員情報の受講態度スコアを設定
     * @param attitudePoint 受講態度スコア
     */
    public void setAttitudePoint(Double attitudePoint) {
        this.attitudePoint = attitudePoint;
    }
    /**
     * 社員情報のリーダーシップスコアを設定
     * @param leadershipPoint リーダーシップスコア
     */
    public void setLeadershipPoint(Double leadershipPoint) {
        this.leadershipPoint = leadershipPoint;
    }
    /**
     * 社員情報の備考を設定
     * @param remarks 備考
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    /**
     * 社員情報の更新日を設定
     * @param updatedDay 更新日
     */
    public void setUpdatedDay(Date updatedDay) {
        this.updatedDay = updatedDay;
    }
    //
    /**
     * 社員情報の社員IDを取得
     * @return 社員情報の社員ID
     */
    public String getEmployeeID() {
        return employeeID;
    }
    /**
     * 社員情報の姓を取得
     * @return 社員情報の姓
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * 社員情報の名を取得
     * @return 社員情報の名
     */
    public String getFirstname() {
        return firstname;
    }
    /**
     * 社員情報の姓(ルビ)を取得
     * @return 社員情報の姓(ルビ)
     */
    public String getRubyLastName() {
        return rubyLastName;
    }
    /**
     * 社員情報の名(ルビ)を取得
     * @return 社員情報の名(ルビ)
     */
    public String getRubyFirstname() {
        return rubyFirstname;
    }
    /**
     * 社員情報の生年月日を取得
     * @return 社員情報の生年月日
     */
    public Date getBirthday() {
        return birthday;
    }
    /**
     * 社員情報の入社日を取得
     * @return 社員情報の入社日
     */
    public Date getJoiningDate() {
        return joiningDate;
    }
    /**
     * 社員情報のエンジニア歴を取得
     * @return 社員情報のエンジニア歴
     */
    public int getEngineerDate() {
        return engineerDate;
    }
    /**
     * 社員情報の扱える言語を取得
     * @return 社員情報の扱える言語
     */
    public String getAvailableLanguages() {
        return availableLanguages;
    }
    /**
     * 社員情報の経歴を取得
     * @return 社員情報の経歴
     */
    public String getCareerDate() {
        return careerDate;
    }
    /**
     * 社員情報の研修受講歴を取得
     * @return 社員情報の研修受講歴
     */
    public String getTrainingDate() {
        return trainingDate;
    }
    /**
     * 社員情報の技術力を取得
     * @return 社員情報の技術力
     */
    public Double getSkillPoint() {
        return skillPoint;
    }
    /**
     * 社員情報のコミュニケーション能力を取得
     * @return 社員情報のコミュニケーション能力
     */
    public Double getCommunicationPoint() {
        return communicationPoint;
    }
    /**
     * 社員情報の受講態度を取得
     * @return 社員情報の受講態度
     */
    public Double getAttitudePoint() {
        return attitudePoint;
    }
    /**
     * 社員情報のリーダーシップを取得
     * @return 社員情報のリーダーシップ
     */
    public Double getLeadershipPoint() {
        return leadershipPoint;
    }
    /**
     * 社員情報の備考を取得
     * @return 社員情報の備考
     */
    public String getRemarks() {
        return remarks;
    }
    /**
     * 社員情報の更新日を取得
     * @return 社員情報の更新日
     */
    public Date getUpdatedDay() {
        return updatedDay;
    }

    /**
     * 指定された {@link Date} オブジェクトを "yyyy年MM月dd日" 形式の文字列にフォーマットを返す
     * 日付が null の場合は空文字列を返す
     *
     * @param date フォーマット対象の日付
     * @return フォーマットされた日付文字列、または null の場合は空文字列
     * @author nishiyama
     */
    protected String formatDate(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return simpleDateFormat.format(date);
    }
}
