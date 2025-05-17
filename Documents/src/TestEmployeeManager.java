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
                // 17項目のヘッダー（実際はEmployeeManagerの読み込み順に合わせる）
                writer.println("社員ID,氏名,年齢,エンジニア歴,扱える言語,"
                            + "生年月日,入社日,勤続年数,最終学歴,学校名,学部名,"
                            + "Java,Python,JavaScript,SQL,備考,更新日");
    
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//ダミーテスト時にyyyy/MM/ddから変更
                String today = sdf.format(new Date());
    
                for (int i = 1; i <= 999; i++) {
                    String id = String.format("EMP%04d", i);
                
                    String name;
                    if (i == 1) {
                        name = "佐藤花子";
                    } else if (i == 2) {
                        name = "伊藤浩史";
                    } else if (i == 3) {
                        name = "鈴木太郎";
                    } else {
                        name = "山田太郎" + i;
                    }
                
                    int age = 25 + (i % 30);
                    int years = i % 10;
                    String lang = getDummyLanguage(i);
                    String birthday = sdf.format(new GregorianCalendar(1990 + (i % 10), 1, 1).getTime());
                    String joinDate = sdf.format(new GregorianCalendar(2015 + (i % 5), 4, 1).getTime());
                    int workYears = 2025 - (2015 + (i % 5));
                    String edu = "大学卒";
                    String school = "○○大学";
                    String dept = "工学部";
                    double javaScore = (i % 5) + 1;
                    double pyScore = (i % 4) + 1;
                    double jsScore = (i % 3) + 1;
                    double sqlScore = (i % 2) + 1;
                    String note = "備考なし";
                
                    writer.printf("%s,%s,%d,%d,%s,%s,%s,%d,%s,%s,%s,%.1f,%.1f,%.1f,%.1f,%s,%s%n",
                        id, name, age, years, lang,
                        birthday, joinDate, workYears, edu, school, dept,
                        javaScore, pyScore, jsScore, sqlScore, note, today);
                }
                
    
                System.out.println("999人分のダミーデータ（17列）をCSVに出力しました。");
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

