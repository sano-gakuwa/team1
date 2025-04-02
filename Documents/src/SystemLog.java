import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

public abstract class SystemLog {
    protected Date nowTime = new Date();
    final String FILEPATH = "LOG/systemLog.txt";
    static final File SYSTEM_LOG = new File("LOG/systemLog.txt");
    private Logger logger;

    // ログファイルの読み込み。
    protected void setUpLog() {
        try {
            if (verificationLogFile()) {
                writeLog("起動\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ログを書き込むメソッド。
    protected void writeLog(String a) {
        try {
            // 文字コードを指定する
            PrintWriter filewriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILEPATH, true), "Shift-JIS")));
            //書き込み
            filewriter.append(a);
            //書き込み終了
            filewriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ログファイルの存在確認用。
    protected boolean verificationLogFile() {
        try {
            if (SYSTEM_LOG.exists()) {
                if (SYSTEM_LOG.isFile() && SYSTEM_LOG.canWrite()) {
                    return true;
                }
            } else {
                makeLogFile();
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    // ログファイルが存在しない場合にログファイルを作成する用。
    protected void makeLogFile() {
        Path path = Paths.get(FILEPATH);
        try {
            Files.createFile(path);// ファイルが存在しない為、ファイルを新規作成
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
