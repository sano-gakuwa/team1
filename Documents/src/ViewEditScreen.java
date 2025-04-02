import javax.swing.JButton;

public class ViewEditScreen extends SetUpDetailsScreen {
    JButton detailsButton = new JButton("詳細画面");

    protected ViewEditScreen() {
        frame.setTitle("編集画面");
        mainPanel.add(detailsButton);
        frame.add(framePanel);// フレームにパネルを追加

    }

    public void View() {
        // 画面の表示
        frame.setVisible(true);
        //詳細画面ボタンイベント
        detailsButton.addActionListener(e -> {
            // 画面内のパーツ削除
            refreshUI();
            // 詳細画面表示
            ViewDetailsScreen details = new ViewDetailsScreen();
            details.View();
        });
    };
}
