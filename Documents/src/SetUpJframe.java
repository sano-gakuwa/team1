import java.awt.event.*;
import javax.swing.*;

public abstract class SetUpJframe {

    // フィールド（クラス内の変数）を static にする
    protected static JFrame frame;
    protected static JPanel fullScreenPanel;

    // static初期化ブロック
    static {
        Frame();
    }

    // JFrame の設定
    protected static void Frame() {
        frame = new JFrame(); // method内のローカル変数
        frame.setSize(850, 600);
        frame.setResizable(false);  // サイズ変更不可
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ✕で即終了させない
        frame.setLocationRelativeTo(null); // 画面中央にダイアログ表示

        // メインパネル
        fullScreenPanel = new JPanel();
        frame.add(fullScreenPanel);

        // ✕ボタンの処理
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

        frame.setVisible(true);// 画面画面表示
    }

    // 共通の終了確認メソッド
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

    // UI を再描画するメソッド
    protected void frameClear() {
        frame.revalidate();  // レイアウトの再計算
        frame.repaint();  // 画面の再描画
    }
}
