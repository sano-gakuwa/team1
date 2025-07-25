import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;


public class AppLock extends SystemLog{
    private static final String LOCK_FILE_NAME = "app.lock"; // 任意のロックファイル名
    private static FileChannel channel;
    private ViewDialog dialog = new ViewDialog();

    public void tryAppLock() {
        setUpLog();
        try {
            File lockFile = new File(LOCK_FILE_NAME); // 任意のロックファイル名
            if (lockFile.exists()) {
                printErrorLog("このアプリケーションはすでに起動しています");
                dialog.viewFatalErrorDialog("アプリケーションが起動中");
            }
            lockFile.deleteOnExit(); // JVM終了時に削除
            RandomAccessFile randomAccessFile=new RandomAccessFile(lockFile, "rw");
            channel=randomAccessFile.getChannel();
            randomAccessFile.close();
            printInfoLog("アプリケーションのロックに成功");
        } catch (Exception e) {
            printExceptionLog(e,"多重起動チェック中にエラー");
            System.exit(0);
        } finally {
            try {
                if (channel != null)
                    channel.close();
            } catch (Exception e) {
                printExceptionLog(e,"FileChannelが閉じれませんでした");
            }
        }
    }
    @Override
    public void printExceptionLog(Exception e, String errorString) {
        Logger logger=getLogger();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        logger.severe(String.format("%s\n%s", errorString, stringWriter.toString()));
    }

    @Override
    public void printInfoLog(String infoString) {
        Logger logger=getLogger();
        logger.info(infoString);
    }

    @Override
    public void printErrorLog(String errString){
        Logger logger=getLogger();
        logger.warning(errString);
    }
}
