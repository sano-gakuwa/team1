import java.util.ArrayList;

public class EmployeeManager extends SystemLog{
    static ArrayList<ArrayList<EmployeeInformation>> Employee=new ArrayList<ArrayList<EmployeeInformation>>();
    public void setUp(){
        setUpLog();
        ViewTopScreen top=new ViewTopScreen();
        top.View();
    }
}
