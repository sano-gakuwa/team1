import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.swing.JOptionPane;

public class AppLock extends SystemLog{
    private static final String LOCK_FILE_NAME = "app.lock"; // 任意のロックファイル名
    private static FileLock lock;
    private static FileChannel channel;

    public void tryAppLock() {
        try {
            File lockFile = new File(LOCK_FILE_NAME); // 任意のロックファイル名
            if (lockFile.exists()) {
                printErrorLog("このアプリケーションはすでに起動しています");
                JOptionPane.showMessageDialog(null, "このアプリケーションはすでに起動しています。", "エラー", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            lockFile.deleteOnExit(); // JVM終了時に削除
            RandomAccessFile randomAccessFile=new RandomAccessFile(lockFile, "rw");
            channel=randomAccessFile.getChannel();
            lock = channel.lock();
            randomAccessFile.close();
            printInfoLog("アプリケーションのロックに成功");

        } catch (Exception e) {
            printExceptionLog(e,"多重起動チェック中にエラー");
            System.exit(0);
        } finally {
            try {
                if (lock != null)
                    lock.release();
                if (channel != null)
                    channel.close();
            } catch (Exception e) {
                printExceptionLog(e,"app.lockファイルの開放に失敗");
            }
        }
    }
    @Override
    public void printExceptionLog(Exception e, String errorString) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        LOGGER.severe(String.format("%s\n%s", errorString, stringWriter.toString()));
    }

    @Override
    public void printInfoLog(String infoString) {
        LOGGER.info(infoString);
    }

    @Override
    public void printErrorLog(String errString){
        LOGGER.warning(errString);
    }
}
