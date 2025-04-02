import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class SetUpJframe1 {
    // タイトル
    static JFrame frame = new JFrame("テスト");
    static JPanel framePanel = new JPanel();

    protected SetUpJframe1() {
        // 画面が閉じてもプログラムは動き続ける設定
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // 渡された引数から画面サイズを設定
        frame.setSize(900, 700);
        // 画面(Frame)をディスプレイの中央に表示する
        frame.setLocationRelativeTo(null);
        framePanel.setBackground(Color.DARK_GRAY);
        frame.add(framePanel);
        framePanel.setLayout(new FlowLayout());
    }

    protected void refreshUI() {
        // 画面内のパーツ削除
        framePanel.removeAll();
        frame.repaint();
    }
}