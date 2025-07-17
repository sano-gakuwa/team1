import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class CreateCsv implements Runnable {
    private String directory;
    private ArrayList<String> selected;
    private final EmployeeManager MANAGER = new EmployeeManager();
    private ThreadsManager threadsManager = new ThreadsManager();
    private static ReentrantLock createCsvLock = new ReentrantLock();
    private ViewDialog dialog = new ViewDialog();

    /**
     * CSV出力のロックを取得
     * 
     * @return ロックの状態 true:ロック中, false:ロックされていない
     * @author 下村
     */
    public boolean validateCreateCsvLock() {
        return createCsvLock.isLocked();
    }

    /**
     * 選択された社員情報をCSVファイルに出力
     * 
     * @param directory 指定したディレクトリ(フォルダー)
     * @param selected  選択された社員情報リスト
     * @author 下村
     */
    public void createCsv(String directory, ArrayList<String> selected) {
        this.directory = directory;
        this.selected = selected;
    }

    // CSV出力処理
    @Override
    public void run() {
        MANAGER.printInfoLog("CSV出力処理を開始します");
        threadsManager.startUsing(Thread.currentThread());
        // ロックを取得
        createCsvLock.lock();
        MANAGER.printInfoLog("CSV出力処理をロックしました");
        Path filePath = createCsvPath(directory);
        // 今から出力しようとしているファイル
        File makeCsvFile = makeCsvFile(filePath);
        // 出力処理を実行
        try {
            try {
                // 出力先のファイルの新規作成
                Files.createFile(filePath);
            } catch (Exception e) {
                // ファイル新規作成で例外が発生
                MANAGER.printExceptionLog(e, "ファイル新規作成で例外が発生しました");
                if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
                    dialog.viewErrorDialog("ファイル新規作成で例外が発生しました");
                }
                return;
            }
            MANAGER.printInfoLog("CSV出力先のファイルを作成しました: " + makeCsvFile.getAbsolutePath());
            // 社員情報リストをロックしてCSV出力処理を行う
            synchronized (EmployeeManager.employeeList) {
                MANAGER.printInfoLog("社員情報リストをロックしました");
                // PrintWriterクラスのオブジェクトを生成する
                FileOutputStream fileOutputStream = new FileOutputStream(makeCsvFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "Shift-JIS");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
                // 社員情報保存CSVファイルの1行目に項目名を記載
                printWriter.println(String.join(",", EmployeeManager.EMPLOYEE_CATEGORY));
                // 選択された社員情報をCSV形式で出力
                for (Iterator<EmployeeInformation> employeeIterator = EmployeeManager.employeeList
                        .iterator(); employeeIterator.hasNext();) {
                    EmployeeInformation employee = employeeIterator.next();
                    // 選択された社員情報と合致したらファイルに出力
                    if (selected.contains(employee.getEmployeeID())) {
                        printWriter.println(MANAGER.convertToCSV(employee));
                    }
                }
                printWriter.close();
            }
        } catch (Exception e) {
            // CSV出力時に例外発生
            MANAGER.printExceptionLog(e, "CSV出力時に例外発生");
            if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
                dialog.viewErrorDialog("CSV出力時に例外が発生しました");
            }
            try {
                // 出力しようとしたファイルを削除
                Files.deleteIfExists(makeCsvFile.toPath());
                // 削除に成功したことをログに記録
                MANAGER.printInfoLog("出力しようとしたファイルを削除しました: " + makeCsvFile.getAbsolutePath());
            } catch (Exception ex) {
                // 出力しようとしたファイルの削除に失敗
                MANAGER.printExceptionLog(ex, "出力しようとしたファイルの削除に失敗");
            }
        } finally {
            // CSV出力処理が完了したのでロックを解除
            createCsvLock.unlock();
            threadsManager.endUsing(Thread.currentThread());
        }
        // CSV出力処理が成功したことをログに記録
        MANAGER.printInfoLog("CSV出力処理が完了しました");
        // 成功ダイアログを表示
        if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
            dialog.viewEndDialog("CSVファイルを出力しました: " + makeCsvFile.getAbsolutePath());
        }
    }

    /**
     * CSVファイルのパスを作成
     *
     * @param directory 出力先のディレクトリ
     * @return 作成されたCSVファイルのパス
     * @author simomura
     */
    private Path createCsvPath(String directory) {
        // 現在日時を取得
        LocalDateTime nowDate = LocalDateTime.now();
        // 表示形式を指定
        DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm");
        // フォーマットした日時を文字列に変換
        String formattedNow = DateFormatter.format(nowDate);
        // 出力先のファイルパスを作成
        String fileName = directory + "/employee_list_" + formattedNow + ".csv";
        Path filePath = Paths.get(fileName);
        // ファイルパスを返す
        return filePath;
    }

    /**
     * 作成するCSVファイルの作成
     * 
     * @param filePath
     * @return 作成しようとしているFile
     */
    private File makeCsvFile(Path filePath) {
        try {
            // 同じ名前のCSVファイルがフォルダー(ディレクトリー)にあるか確認
            while (Files.exists(filePath)) {
                MANAGER.printInfoLog(filePath + "は存在します");
                // ファイルがすでに存在する場合、1秒だけ一時停止
                MANAGER.printInfoLog("CSV出力:1秒待機中...");
                Thread.sleep(1000);
                // ファイルのパスを再設定
                filePath = createCsvPath(directory);
            }
        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "スレッドの一時停止に失敗しました");
        }
        return new File(filePath.toString());
    }
}
