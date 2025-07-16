import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ThreadsManager extends SystemLog{
    private static ArrayList<Thread> usingThread = new ArrayList<>();

    public ThreadsManager() {
    }

    public void startUsing(Thread thread) {
        if (thread != null) {
            printInfoLog(thread.getName() + "が開始しました");
            usingThread.add(thread);
        }

    }

    public void endUsing(Thread thread) {
        if (thread != null) {
            printInfoLog(thread.getName() + "が終了しました");
            usingThread.remove(thread);
        }

    }

    public int usingThread() {
        synchronized (usingThread) {
            return usingThread.size();
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
