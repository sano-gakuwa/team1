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
    String useLanguageDate;
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
        String useLanguageDate,String careerDate,String trainingDate,
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
        this.useLanguageDate=useLanguageDate;
        this.careerDate=careerDate;
        this.trainingDate=trainingDate;
        this.skillPoint=skillPoint;
        this.attitudePoint=attitudePoint;
        this.communicationPoint=communicationPoint;
        this.leadershipPoint=leadershipPoint;
        this.remarks=remarks;
        this.updatedDay=updatedDay;
    }
}
