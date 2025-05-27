import java.util.Date;

public class EmployeeInformation {
    String employeeID;
    String lastName;
    String firstname;
    String rubyLastName;
    String rubyFirstname;
    Date birthday;
    Date joiningDate;
    int engineerDate;
    String availableLanguages; // 元が「useLanguageDate」だったので変更
    String careerDate;
    String trainingDate;
    Double skillPoint;
    Double attitudePoint;
    Double communicationPoint;
    Double leadershipPoint;
    String remarks;
    Date updatedDay;

    public EmployeeInformation(
        String employeeID,
        String lastName,String firstname,
        String rubyLastName,String rubyFirstname,
        Date birthday,Date joiningDate,int engineerDate,
        String availableLanguages,String careerDate,String trainingDate,
        Double skillPoint,Double attitudePoint,Double communicationPoint,Double leadershipPoint,
        String remarks,Date updatedDay
    ){
        this.employeeID=employeeID;
        this.lastName=lastName;
        this.firstname=firstname;
        this.rubyLastName=rubyLastName;
        this.rubyFirstname=rubyFirstname;
        this.birthday=birthday;
        this.joiningDate=joiningDate;
        this.engineerDate=engineerDate;
        this.availableLanguages= availableLanguages;
        this.careerDate=careerDate;
        this.trainingDate=trainingDate;
        this.skillPoint=skillPoint;
        this.attitudePoint=attitudePoint;
        this.communicationPoint=communicationPoint;
        this.leadershipPoint=leadershipPoint;
        this.remarks=remarks;
        this.updatedDay=updatedDay;
    }

    /**
 * 指定された {@link Date} オブジェクトを "yyyy/MM/dd" 形式の文字列にフォーマットを返す
 * 日付が null の場合は空文字列を返す
 *
 * @param date フォーマット対象の日付
 * @return フォーマットされた日付文字列、または null の場合は空文字列
 * 
 *  @author nishiyama
 */
    public static String formatDate(Date date) {
        if (date == null) return "";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }
}
