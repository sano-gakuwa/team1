import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

public abstract class SetUpJframe {

    // フィールド（クラス内の変数）を static にする
    protected static JFrame frame=new JFrame();
    protected static JPanel fullScreenPanel=new JPanel();

    // staticな変数はクラス自体に属しているため、初期化を行う（初期化しないとnullのまま）
    protected SetUpJframe(){
        Frame();
    }

    // JFrame の設定
    protected static void Frame() {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // frame = new JFrame(); // method内のローカル変数
        frame.setSize(850, 690);
        frame.setResizable(false);  // サイズ変更不可
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ✕で即終了させない
        frame.setLocationRelativeTo(null); // 画面中央にウィンドウ表示

        // メインパネル
        // fullScreenPanel = new JPanel();
        //frame.add(fullScreenPanel);
        frame.setContentPane(fullScreenPanel);

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
    protected static void confirmAndExit() {
        int result = JOptionPane.showConfirmDialog(
            frame,
            "本当に終了しますか？",
            "アプリ終了確認",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /*
     *  UI を再描画するメソッド
     *
     *  @author nishiyama
    */
    protected void refreshUI() {
        fullScreenPanel.removeAll();
        frame.revalidate();  // レイアウトの再計算
        frame.repaint();  // 画面の再描画
    }
}
