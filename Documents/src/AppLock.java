import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.swing.JOptionPane;

public class AppLock {
    private static final String LOCK_FILE_NAME = "app.lock"; // 任意のロックファイル名
    private static FileLock lock;
    private static FileChannel channel;

    public void tryAppLock() {
        try {
            File lockFile = new File(LOCK_FILE_NAME); // 任意のロックファイル名
            if (lockFile.exists()) {
                System.out.println("このアプリケーションはすでに起動しています。");
                JOptionPane.showMessageDialog(null, "このアプリケーションはすでに起動しています。", "エラー", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            lockFile.deleteOnExit(); // JVM終了時に削除
            RandomAccessFile randomAccessFile=new RandomAccessFile(lockFile, "rw");
            channel=randomAccessFile.getChannel();
            lock = channel.lock();
            randomAccessFile.close();
            System.out.println("アプリケーションを起動しました。");

        } catch (Exception e) {
            System.err.println("多重起動チェック中にエラー: " + e.getMessage());
            System.exit(0);
        } finally {
            try {
                if (lock != null)
                    lock.release();
                if (channel != null)
                    channel.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
