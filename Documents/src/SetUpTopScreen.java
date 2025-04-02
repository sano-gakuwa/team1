import java.awt.BorderLayout;
import javax.swing.JPanel;

public abstract class SetUpTopScreen extends SetUpJframe {
    // ヘッダー、メイン、フッターパネルの初期化
    protected JPanel headerPanel=new JPanel();
    protected JPanel mainPanel=new JPanel();
    protected JPanel footerPanel=new JPanel();

    protected SetUpTopScreen() {
        framePanel.add(headerPanel, BorderLayout.NORTH);
        framePanel.add(mainPanel, BorderLayout.CENTER);
        framePanel.add(footerPanel, BorderLayout.SOUTH);
    }
}
