import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class SetUpJframe {
    protected static JFrame frame= new JFrame("TOP");
    protected static JPanel framePanel=new JPanel();

    protected SetUpJframe() {
        // メインフレームの作成（フィールドに代入する）
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);//画面の閉じ方
        frame.setSize(850, 650);//画面サイズ
        frame.setLocationRelativeTo(null);// 画面(Frame)をディスプレイの中央に表示する
        frame.setResizable(false);  // サイズ変更不可
        framePanel.setLayout(new BorderLayout());
        frame.add(framePanel, BorderLayout.CENTER);
    }

    // UIを再描画するメソッド
    protected void refreshUI() {
        framePanel.removeAll();//パネル上の要素を消す
        frame.repaint();  // 画面の再描画
    }
}