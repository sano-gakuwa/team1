import java.util.Date;
import java.text.SimpleDateFormat;

public class EmployeeInformation {
    private String employeeID;
    private String lastName;
    private String firstname;
    private String rubyLastName;
    private String rubyFirstname;
    private Date birthday;
    private Date joiningDate;
    private int engineerDate;
    private String availableLanguages;
    private String careerDate;
    private String trainingDate;
    private Double skillPoint;
    private Double attitudePoint;
    private Double communicationPoint;
    private Double leadershipPoint;
    private String remarks;
    private Date updatedDay;

    public EmployeeInformation() {

    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setRubyLastName(String rubyLastName) {
        this.rubyLastName = rubyLastName;
    }

    public void setRubyFirstname(String rubyFirstname) {
        this.rubyFirstname = rubyFirstname;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setJoiningDate(Date joiningDate) {
        this.joiningDate = joiningDate;
    }

    public void setEngineerDate(int engineerDate) {
        this.engineerDate = engineerDate;
    }

    public void setAvailableLanguages(String availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    public void setCareerDate(String careerDate) {
        this.careerDate = careerDate;
    }

    public void setTrainingDate(String trainingDate) {
        this.trainingDate = trainingDate;
    }

    public void setSkillPoint(Double skillPoint) {
        this.skillPoint = skillPoint;
    }

    public void setAttitudePoint(Double attitudePoint) {
        this.attitudePoint = attitudePoint;
    }

    public void setCommunicationPoint(Double communicationPoint) {
        this.communicationPoint = communicationPoint;
    }

    public void setLeadershipPoint(Double leadershipPoint) {
        this.leadershipPoint = leadershipPoint;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setUpdatedDay(Date updatedDay) {
        this.updatedDay = updatedDay;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getRubyLastName() {
        return rubyLastName;
    }

    public String getRubyFirstname() {
        return rubyFirstname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Date getJoiningDate() {
        return joiningDate;
    }

    public int getEngineerDate() {
        return engineerDate;
    }

    public String getAvailableLanguages() {
        return availableLanguages;
    }

    public String getCareerDate() {
        return careerDate;
    }

    public String getTrainingDate() {
        return trainingDate;
    }

    public Double getSkillPoint() {
        return skillPoint;
    }

    public Double getAttitudePoint() {
        return attitudePoint;
    }

    public Double getCommunicationPoint() {
        return communicationPoint;
    }

    public Double getLeadershipPoint() {
        return leadershipPoint;
    }

    public String getRemarks() {
        return remarks;
    }

    public Date getUpdatedDay() {
        return updatedDay;
    }

    /**
     * 指定された {@link Date} オブジェクトを "yyyy/MM/dd" 形式の文字列にフォーマットを返す
     * 日付が null の場合は空文字列を返す
     *
     * @param date フォーマット対象の日付
     * @return フォーマットされた日付文字列、または null の場合は空文字列
     * 
     * @author nishiyama
     */
    public static String formatDate(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }
}
