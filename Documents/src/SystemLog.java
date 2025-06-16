import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class SystemLog {
    protected Date nowTime = new Date();
    protected static final String LOG_FOLDER = "LOG";
    protected static final String LOG_FILEPATH = LOG_FOLDER + "/system_log_" + LocalDate.now() + ".txt";
    protected static final File SYSTEM_LOG = new File(LOG_FILEPATH);
    public final Logger LOGGER = Logger.getLogger("SystemLog");;

    /**
     * ログファイルの読み込み。
     * @author 下村
     */
    protected void setUpLog() {
        try {
            Files.createDirectories(Paths.get(LOG_FOLDER));
            if (verificationLogFile()) {
                FileHandler fileHandler = new FileHandler(LOG_FILEPATH, true);
                // ログのフォーマットを設定
                // valueに書式指定子とSimpleFormatterの引数でフォーマット指定
                System.setProperty("java.util.logging.SimpleFormatter.format",
                        "[%1$tF %1$tT] [%4$s] [%2$s] - %5$s%6$s%n");
                fileHandler.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(fileHandler);
            }
        } catch (Exception e) {
            printErrorLog(e, "ログ設定で例外が発生しました");
        }
    }

    // 
    /**
     * ログファイルの存在確認用。
     * @return ログファイルの存在確認するかの真偽
     * @author 下村
     */
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
            printErrorLog(e, "ログファイルの存在確認で例外が発生しました");
        }
        return false;
    }

    /**
     * ログファイルが存在しない場合にログファイルを作成する用。
     * @author 下村
     */
    protected void makeLogFile() {
        Path path = Paths.get(LOG_FILEPATH);
        try {
            Files.createFile(path);// ファイルが存在しない為、ファイルを新規作成
        } catch (Exception e) {
            printErrorLog(e, "ログファイルの新規作成で例外が発生しました");
        }
    }

    /**
     * ログにスタックトレースを出力する
     * @param e           スタックトレースを持っている例外クラス
     * @param errorString ログに出力するエラー文言
     * @author 下村
     */
    public void printErrorLog(Exception e, String errorString) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter  = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter );
        LOGGER.severe(String.format("%s\n%s", errorString, stringWriter.toString()));
    }
}
