
import javax.swing.JButton;

public class ViewAdditionScreen extends SetUpDetailsScreen {
    JButton topButton = new JButton("一覧画面");

    protected ViewAdditionScreen() {
        frame.setTitle("追加画面");
        mainPanel.add(topButton);
        frame.add(framePanel);// フレームにパネルを追加
    }

    public void View() {
        // 画面の表示
        frame.setVisible(true);
        //一覧画面ボタンイベント
        topButton.addActionListener(e -> {
            // 画面内のパーツ削除
            refreshUI();
            // 一覧画面表示
            ViewTopScreen top = new ViewTopScreen();
            top.View();
        });
    };
}
