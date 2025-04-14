import javax.swing.*;
import java.awt.*;

public class SetUpJframe extends JFrame {
    protected JPanel fullScreenPanel;
    

    public SetUpJframe() {
        
        

        setTitle("社員管理アプリ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        fullScreenPanel = new JPanel();
        fullScreenPanel.setLayout(new BoxLayout(fullScreenPanel, BoxLayout.Y_AXIS)); // ここは継承先で上書き可
        add(fullScreenPanel); // メインパネルをフレームに追加

        setVisible(true); // 表示必須
    }
}
