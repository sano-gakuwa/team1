import java.io.*;
import java.util.*;
//仮の1000件

public class EmployeeManager {


    private static final List<EmployeeInformation> employeeList = new ArrayList<>();
    private static final int MAX_EMPLOYEES = 1000;
    private static final String CSV_FILE = "employees.csv";

    

    static {
        File file = new File(CSV_FILE);
        if (file.exists()) {
            loadFromCSV();
        } else {
            generateInitialData();
            saveToCSV();
        }
    }

    private static void generateInitialData() {
        for (int i = 1; i <= 999; i++) {
            employeeList.add(new EmployeeInformation(
                String.format("xx%05d", 1000 + i),
                "テスト ユーザー" + i,
                String.valueOf(20 + (i % 30)),
                (i % 10) + "年" + (i % 12) + "か月",
                "Java,HTML,CSS"
            ));
        }
    }

    private static void loadFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    employeeList.add(new EmployeeInformation(
                        data[0], data[1], data[2], data[3], data[4]
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            for (EmployeeInformation emp : employeeList) {
                bw.write(String.join(",", emp.toArray()));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object[][] getPageData(int pageNumber, int pageSize) {
        int totalEmployees = employeeList.size();
        int start = (pageNumber - 1) * pageSize;
        int end = Math.min(start + pageSize, totalEmployees);
    
        // 範囲外ページ番号の処理
        if (start >= totalEmployees || start < 0 || start >= end) {
            return new Object[0][];
        }
    
        Object[][] pageData = new Object[end - start][6];
        for (int i = start; i < end; i++) {
            EmployeeInformation emp = employeeList.get(i);
            pageData[i - start] = new Object[]{
                emp.id,
                emp.name,
                emp.age,
                emp.experience,
                emp.languages,
                "..." // 詳細ボタンの仮表示
            };
        }
    
        return pageData;
    }
    
    

    public static boolean addEmployee(String id, String name, String age, String experience, String languages) {
        if (employeeList.size() >= MAX_EMPLOYEES) {
            return false;
        }
        EmployeeInformation newEmp = new EmployeeInformation(id, name, age, experience, languages);
        employeeList.add(newEmp);
        saveToCSV();
        return true;
    }

    public static int getEmployeeCount() {
        return employeeList.size();
    }

    static class EmployeeInformation {
        String id;
        String name;
        String age;
        String experience;
        String languages;

        EmployeeInformation(String id, String name, String age, String experience, String languages) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.experience = experience;
            this.languages = languages;
        }

        String[] toArray() {
            return new String[]{id, name, age, experience, languages};
        }
    }
    //ページ指定のデータ取得メソッド
    
    
    
}

