import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TestEmployeeManager extends EmployeeManager {

    private static final String CSV_PATH = "src/data/engineerList.csv"; // CSVのパスは本番と同じ

    @Override
    public void setUp() {
        // CSVファイルが存在しないか、空ならダミーデータ生成
        System.out.println("TestEmployeeManager#setUp 開始"); // ← これを一時追加してログ確認用に
        File csvFile = new File(CSV_PATH);
        if (!csvFile.exists() || csvFile.length() == 0) {
            System.out.println("ダミーデータを生成しています...");
            generateDummyCSV();
            
        } else {
            System.out.println("既にデータが存在しています。生成をスキップします。");
        }

        // 本来のEmployeeManager処理へ（画面表示等）
        System.out.println("ダミーデータ生成後の社員数: " + EmployeeManager.employeeList.size());
        super.setUp();
        
    }

    private void generateDummyCSV() {
        try {
            File file = EmployeeManager.ENPLOYEE_CSV;
            File parentDir = file.getParentFile();
    
            if (!parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    System.out.println("ディレクトリを作成しました: " + parentDir.getPath());
                } else {
                    System.err.println("ディレクトリの作成に失敗しました: " + parentDir.getPath());
                    return;
                }
            }
    
            try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), "Shift_JIS"))) {

            // EmployeeInformationのフィールド順
            writer.println("社員ID,姓,名,姓カナ,名カナ,生年月日,入社日,エンジニア歴,使用言語,経歴,研修,"
                        + "Java,態度,コミュニケーション,リーダーシップ,備考,更新日");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());

            for (int i = 1; i <= 999; i++) {
                String employeeID = String.format("EMP%04d", i);

                // 氏名（姓 + 名）
                String lastName;
                String firstName;
                switch (i) {
                    case 1 -> {
                        lastName = "佐藤";
                        firstName = "花子";
                    }
                    case 2 -> {
                        lastName = "伊藤";
                        firstName = "浩史";
                    }
                    case 3 -> {
                        lastName = "鈴木";
                        firstName = "太郎";
                    }
                    default -> {
                        lastName = "山田";
                        firstName = "太郎" + i;
                    }
                }
                String rubyLastName = "ルビ" + lastName;
                String rubyFirstName = "ルビ" + firstName;

                String birthday = sdf.format(new GregorianCalendar(1990 + (i % 10), 1, 1).getTime());
                String joiningDate = sdf.format(new GregorianCalendar(2015 + (i % 5), 4, 1).getTime());
                int engineerDate = i % 10;
                String useLanguage = getDummyLanguage(i);
                String careerDate = "経歴あり";
                String trainingDate = "研修済み";

                double skill = (i % 5) + 1;
                double attitude = (i % 4) + 1;
                double communication = (i % 3) + 1;
                double leadership = (i % 2) + 1;

                String remarks = "備考なし";

                writer.printf("%s,%s,%s,%s,%s,%s,%s,%d,%s,%s,%s,%.1f,%.1f,%.1f,%.1f,%s,%s%n",
                    employeeID, lastName, firstName, rubyLastName, rubyFirstName,
                    birthday, joiningDate, engineerDate, useLanguage, careerDate, trainingDate,
                    skill, attitude, communication, leadership, remarks, today);
            }

            System.out.println("999人分のダミーデータ（5列）をCSVに出力しました。");

        }

    } catch (IOException e) {
        System.err.println("CSV書き込み中にエラーが発生しました: " + e.getMessage());
    }

    }
    
    


    private String getDummyLanguage(int i) {
        // シンプルなランダム選定
        String[] langs = {"Java", "Python", "C#", "JavaScript", "Go", "Ruby"};
        return langs[i % langs.length];
    }
}

