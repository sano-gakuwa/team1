import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadsManager extends SystemLog{
    private final List<Thread> USING_THREAD = Collections.synchronizedList(new ArrayList<>());

    public ThreadsManager() {
    }

    public void startUsing(Thread thread) {
        if (thread != null) {
            LOGGER.info(thread.getName() + "が開始しました");
            USING_THREAD.add(thread);
        }

    }

    public void endUsing(Thread thread) {
        if (thread != null) {
            LOGGER.info(thread.getName() + "が終了しました");
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
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        LOGGER.severe(String.format("%s\n%s", errorString, stringWriter.toString()));
    }
    @Override
    public void printInfoLog(String infoString){
        LOGGER.info(infoString);
    }
    @Override
    public void printErrorLog(String errString) {
        LOGGER.warning(errString);
        
    }
}
