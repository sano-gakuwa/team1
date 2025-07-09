import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public abstract class SetUpJframe {

    // フィールド（クラス内の変数）を static にする
    protected static JFrame frame = new JFrame();
    protected static JPanel fullScreenPanel = new JPanel();

    // staticな変数はクラス自体に属しているため、初期化を行う（初期化しないとnullのまま）
    protected SetUpJframe() {
        Frame();
    }

    // JFrame の設定
    protected static void Frame() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setSize(800, 645);
        frame.setResizable(false); // サイズ変更不可
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ✕で即終了させない
        frame.setLocationRelativeTo(null); // 画面中央にウィンドウ表示
        // メインパネル
        frame.setContentPane(fullScreenPanel);
    }

    /*
     * UI を再描画するメソッド
     *
     * @author nishiyama
     */
    protected void refreshUI() {
        fullScreenPanel.removeAll();
        frame.revalidate(); // レイアウトの再計算
        frame.repaint(); // 画面の再描画
    }
}
