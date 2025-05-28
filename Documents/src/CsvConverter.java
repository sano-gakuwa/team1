import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

public class CsvConverter {
    CsvConverter() {
        // インスタンスは使用しない予定
    }

    EmployeeManager manager = new EmployeeManager();

    // -------------------------------------------------------
    // 下村作成部分
    public void createCsv(String directory, ArrayList<String> selected) {
        // 現在日時を取得
            LocalDateTime nowDate = LocalDateTime.now();
            // 表示形式を指定
            DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            String formatNowDate = dtf1.format(nowDate);
            String fileName = directory + "/employee_list_" + formatNowDate + ".csv";
            Path filePath = Paths.get(fileName);
            File makeCsvFile= new File(filePath.toString());
        try {
            try {
                // 出力先のファイルの新規作成
                Files.createFile(filePath);
            } catch (FileAlreadyExistsException e) {
                // ファイルがすでに存在する場合
                manager.printErrorLog(e, "同じファイルがすでに存在します");
            } catch (Exception e) {
                // ファイル新規作成で例外が発生
                manager.printErrorLog(e, "ファイル新規作成で例外が発生しました");
            }
            synchronized (EmployeeManager.employeeList) {
                // PrintWriterクラスのオブジェクトを生成する
                PrintWriter pw = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(makeCsvFile), "Shift-JIS")));
                // 社員情報保存CSVファイルの1行目に項目名を記載
                for (String category : EmployeeManager.EMPLOYEE_CATEGORY) {
                    pw.append(category + ",");
                }
                // 行の最後で改行
                pw.append("\n");
                for (Iterator<EmployeeInformation> employeeIterator = EmployeeManager.employeeList
                        .iterator(); employeeIterator.hasNext();) {
                    EmployeeInformation employee = employeeIterator.next();
                    // 選択された社員情報と合致したらファイルに出力
                    if (selected.contains(employee.employeeID) == true) {
                        pw.println(convertToCSV(employee));
                    }
                }
                pw.close();
            }
        } catch (Exception e) {
            // CSV出力時に例外発生
            try {
                Files.deleteIfExists(makeCsvFile.toPath());
            } catch (Exception ex) {
                // 出力しようとしたファイルの削除に失敗
                manager.printErrorLog(ex, "出力しようとしたファイルの削除に失敗");
            }
            manager.printErrorLog(e, "CSV出力失敗しました");
        }
    }

    /**
     * 社員情報をCSV形式の文字列に変換
     *
     * @param e 変換する社員情報
     * @return CSV形式の文字列
     *
     * @author nishiyama
     */
    private String convertToCSV(EmployeeInformation e) {
        return String.join(",",
                e.employeeID,
                e.lastName,
                e.firstname,
                e.rubyLastName,
                e.rubyFirstname,
                EmployeeInformation.formatDate(e.birthday),
                EmployeeInformation.formatDate(e.joiningDate),
                String.valueOf(e.engineerDate),
                e.availableLanguages,
                e.careerDate,
                e.trainingDate,
                String.valueOf(e.skillPoint),
                String.valueOf(e.attitudePoint),
                String.valueOf(e.communicationPoint),
                String.valueOf(e.leadershipPoint),
                e.remarks,
                EmployeeInformation.formatDate(e.updatedDay));
    }
    // -------------------------------------------------------
}
