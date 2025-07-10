import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ThreadsManager extends SystemLog{
    private final List<Thread> USING_THREAD = Collections.synchronizedList(new ArrayList<>());

    public ThreadsManager() {
    }

    public void startUsing(Thread thread) {
        if (thread != null) {
            printInfoLog(thread.getName() + "が開始しました");
            USING_THREAD.add(thread);
        }

    }

    public void endUsing(Thread thread) {
        if (thread != null) {
            printInfoLog(thread.getName() + "が終了しました");
            USING_THREAD.remove(thread);
        }

    }

    public int usingThread() {
        synchronized (USING_THREAD) {
            return (int) USING_THREAD.stream().filter(Thread::isAlive).count();
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
