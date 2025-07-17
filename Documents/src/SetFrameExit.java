import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class SetFrameExit extends SetUpJframe {
    private ThreadsManager threadsManager = new ThreadsManager();
    private EmployeeManager manager = new EmployeeManager();

    SetFrameExit() {
    }

    public void setExit() {
        // ✕ボタンのカスタム処理
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });
        // Escキー処理
        KeyStroke escapeKey = KeyStroke.getKeyStroke("ESCAPE");
        String actionKey = "confirmExit";
        fullScreenPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, actionKey);
        fullScreenPanel.getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmAndExit();
            }
        });
    }

    /*
     * 共通の終了確認メソッド
     *
     * @author nishiyama
     */
    private void confirmAndExit() {
        int result = JOptionPane.showConfirmDialog(
                frame,
                "本当に終了しますか？",
                "アプリ終了確認",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            frame.dispose();
            manager.printInfoLog("画面を閉じました");
            while (0 < threadsManager.usingThread()) {
                manager.printInfoLog("使用中のスレッド:"+threadsManager.usingThread());
                try {
                    manager.printInfoLog("mainスレッド1秒停止");
                    Thread.sleep(1000);
                } catch (Exception e) {
                    manager.printExceptionLog(e, "mainスレッド1秒停止失敗");
                }
            }
            manager.printInfoLog("終了");
            System.exit(0);
        }
    }
}
