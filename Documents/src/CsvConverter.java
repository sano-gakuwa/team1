import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class CsvConverter {
    CsvConverter() {
        // インスタンスは使用しない予定
    }

    private final EmployeeManager MANAGER = new EmployeeManager();
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    // -------------------------------------------------------
    // 下村作成部分
    /**
     * @param selectedFilePath 読み込みたい社員情報ファイルのパス
     * @author 下村
     */
    public void readCsv(String selectedFilePath) {
        FileInputStream fis = null;
        ArrayList<EmployeeInformation> newEmployeeList = new ArrayList<EmployeeInformation>();
        try {
            File selectedFile = new File(selectedFilePath);
            fis = new FileInputStream(selectedFile);
            BufferedReader b_reader = new BufferedReader(new InputStreamReader(fis, "Shift-JIS"));
            Scanner scanner = new Scanner(b_reader);
            scanner.next();
            try {
                // 次に読み込むべき行が無くなるまでループ
                while (scanner.hasNext()) {
                    ArrayList<String> loadEmployeeDate = new ArrayList<String>(
                            Arrays.asList(scanner.next().split(",")));
                    if (validateEmployee(loadEmployeeDate) != false) {
                        String message = "指定されたCSVファイルに形式エラーが有ります";
                        MANAGER.LOGGER.warning(message);
                        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
                        scanner.close();
                        return;
                    }
                    EmployeeInformation employee = new EmployeeInformation(
                            loadEmployeeDate.get(0),
                            loadEmployeeDate.get(1), loadEmployeeDate.get(2),
                            loadEmployeeDate.get(3), loadEmployeeDate.get(4),
                            dateFormat.parse(loadEmployeeDate.get(5)),
                            dateFormat.parse(loadEmployeeDate.get(6)),
                            Integer.parseInt(loadEmployeeDate.get(7)),
                            loadEmployeeDate.get(8),
                            loadEmployeeDate.get(9),
                            loadEmployeeDate.get(10),
                            Double.parseDouble(loadEmployeeDate.get(11)),
                            Double.parseDouble(loadEmployeeDate.get(12)),
                            Double.parseDouble(loadEmployeeDate.get(13)),
                            Double.parseDouble(loadEmployeeDate.get(14)),
                            loadEmployeeDate.get(15),
                            dateFormat.parse(loadEmployeeDate.get(16)));
                    newEmployeeList.add(employee);
                }
            } catch (Exception e) {
                String message = "指定されたCSVファイルから情報の読み込みが出来ませんでした";
                MANAGER.printErrorLog(e, message);
                JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
                scanner.close();
                return;
            }
            scanner.close();
            // -------------------------------------------------------
            // 読み込んだ社員情報を保存用CSVファイルに追加
            File originalFile = EmployeeManager.ENPLOYEE_CSV;
            File backupFile = new File("CSV/employee_data_backup.csv");
            FileLock originalFileLock = null;
            FileOutputStream fos = null;
            try {
                try {
                    // バックアップファイル作成
                    Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    MANAGER.printErrorLog(e, "バックアップファイルの作成に失敗しました");
                    showErrorDialog("バックアップファイルの作成に失敗しました");
                    return;
                }
                fos = new FileOutputStream(originalFile, true);
                FileChannel originalFilechannel = fos.getChannel();
                originalFileLock = originalFilechannel.lock(); // CSVファイルの排他ロック（同時書き込み防止）
                PrintWriter pw = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(fos, "Shift-JIS")));
                // 読み込んだ社員情報リストの内容を社員情報保存CSVに保存
                for (EmployeeInformation employee : newEmployeeList) {
                    pw.println(convertToCSV(employee));
                }
                pw.close();
                try {
                    if (originalFileLock != null && originalFileLock.isValid()) {
                        originalFileLock.release(); // エラーでもロック解除
                    }
                } catch (IOException ex) {
                    MANAGER.LOGGER.info("ロック解除失敗");
                }
            } catch (Exception e) {
                // 追記中エラー時のロールバック処理を追加
                try {
                    if (originalFileLock != null && originalFileLock.isValid()) {
                        originalFileLock.release(); // エラーでもロック解除
                    }
                } catch (IOException ex) {
                    MANAGER.LOGGER.info("ロック解除失敗");
                }
                MANAGER.printErrorLog(e, "削除後の社員情報リストの保存に失敗しました");
                if (originalFile.exists() && backupFile.exists()) {
                    try {
                        Files.deleteIfExists(originalFile.toPath());
                    } catch (Exception ex) {
                        // オリジナルの社員情報保存CSVファイルが削除に失敗した場合
                        MANAGER.printErrorLog(ex, "オリジナルの社員情報保存CSVファイルが削除できませんでした");
                        showErrorDialog("オリジナルの社員情報保存CSVファイルが削除できませんでした");
                        return;
                    }
                    backupFile.renameTo(originalFile);
                }
                MANAGER.LOGGER.info("社員情報保存CSVファイルを削除処理前に戻しました");
                showErrorDialog("削除後の社員情報リストの保存に失敗しました");
                return;
            }
            Files.deleteIfExists(backupFile.toPath());
            // -------------------------------------------------------
            // 読み込んだ社員情報を社員情報リストに追加
            synchronized (EmployeeManager.employeeList) {
                EmployeeManager.employeeList.addAll(newEmployeeList);
            }
            String message = "CSV読み込み成功";
            MANAGER.LOGGER.info(message);
            showDialog(message);
        } catch (Exception e) {
            String message = "指定されたCSVファイルの読み込みが出来ませんでした";
            MANAGER.printErrorLog(e, message);
            JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /**
     * 要求仕様書通りの仕様になっているのか確認用
     * 
     * @param employee 新規追加しようとしている社員情報
     * @return true or false
     * @author 下村
     */
    private boolean validateEmployee(ArrayList<String> employee) {
        boolean validate = true;
        try {
            // if (employeeDate.get(0).length() != 7) {
            // validate = false;
            // }
            if (employee.get(1).length() > 15) {
                validate = false;
            }
            if (employee.get(2).length() > 15) {
                validate = false;
            }
            if (employee.get(3).length() > 15) {
                validate = false;
            }
            if (employee.get(4).length() > 15) {
                validate = false;
            }
            if (validateNotFuture(employee.get(5))) {
                validate = false;
            }
            if (validateNotFuture(employee.get(6))) {
                validate = false;
            }
            if (Integer.parseInt(employee.get(7)) >= 600) {
                validate = false;
            }
        } catch (Exception e) {
            MANAGER.printErrorLog(e, "形式エラー");
        }
        return validate;
    }

    /**
     * 日付が未来の日付では無いか確認用
     * 
     * @param date 日付
     * @return true or false
     * @author 下村
     */
    private boolean validateNotFuture(String date) {
        LocalDate nowDate = LocalDate.now();
        LocalDate isDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("[]y年[]M月[]d日"));
        boolean validate = false;
        if (isDate.isBefore(nowDate)) {
            validate = true;
        }
        return validate;
    }

    // -------------------------------------------------------
    /**
     * 選択された社員情報をCSVファイルに出力
     * 
     * @param directory 指定したディレクトリ(フォルダー)
     * @param selected  選択された社員情報リスト
     * @author 下村
     */
    public void createCsv(String directory, ArrayList<String> selected) {
        // 現在日時を取得
        LocalDateTime nowDate = LocalDateTime.now();
        // 表示形式を指定
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm");
        String formatNowDate = dtf1.format(nowDate);
        String fileName = directory + "/employee_list_" + formatNowDate + ".csv";
        Path filePath = Paths.get(fileName);
        File makeCsvFile = new File(filePath.toString());
        try {
            try {
                // 出力先のファイルの新規作成
                Files.createFile(filePath);
            } catch (FileAlreadyExistsException e) {
                // ファイルがすでに存在する場合、1秒だけ一時停止
                Thread.sleep(1000);
                // もう一回createCsv()を起動
                createCsv(directory, selected);
            } catch (Exception e) {
                // ファイル新規作成で例外が発生
                MANAGER.printErrorLog(e, "ファイル新規作成で例外が発生しました");
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
                MANAGER.printErrorLog(ex, "出力しようとしたファイルの削除に失敗");
            }
            MANAGER.printErrorLog(e, "CSV出力失敗しました");
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

    /**
     * エラー表示用に用意したダイアログに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author 下村
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 成功表示用に用意したダイアログに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    private void showDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }
    // -------------------------------------------------------
}
