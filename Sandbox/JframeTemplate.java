
import java.awt.*;
import javax.swing.*;

public abstract class JframeTemplate {

    // フィールド（クラス内の変数）
    protected JFrame frame;
    protected JPanel mainPanel;
    protected JPanel headerPanel;
    protected JPanel footerPanel;

    public JframeTemplate() {
        // メインフレームの作成（フィールドに代入する）
        frame = new JFrame("TOP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);  // サイズ変更不可

        // ヘッダー、メイン、フッターパネルの初期化
        headerPanel = new JPanel();
        mainPanel = new JPanel();
        footerPanel = new JPanel();

        // フレームにパネルを追加
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(footerPanel, BorderLayout.SOUTH);

        // フレームの表示
        this.frame.setVisible(true);
    }

    // UIを再描画するメソッド
    protected void refreshUI() {
        frame.revalidate();  // レイアウトの再計算
        frame.repaint();  // 画面の再描画
    }
}
