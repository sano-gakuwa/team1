import javax.swing.JFrame;
import javax.swing.BorderFactory;

public class ViewEditScreen extends SetUpDetailsScreen {

    public ViewEditScreen() {
        refreshUI();
        fullScreenPanel.setLayout(new BoxLayout(fullScreenPanel, BoxLayout.Y_AXIS));

        // ★ 各パネルに見出し付きの枠を設定（デバッグ・確認用）
        topPanel.setBorder(BorderFactory.createTitledBorder("topPanel"));
        upperPanel.setBorder(BorderFactory.createTitledBorder("upperPanel"));
        middlePanel.setBorder(BorderFactory.createTitledBorder("middlePanel"));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("bottomPanel"));

        fullScreenPanel.add(wrapCentered(topPanel));
        fullScreenPanel.add(wrapCentered(upperPanel));
        fullScreenPanel.add(wrapCentered(middlePanel));
        fullScreenPanel.add(wrapCentered(bottomPanel));

        frame.setVisible(true);
    }
}