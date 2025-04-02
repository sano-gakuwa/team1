
import javax.swing.JButton;

public class ViewDetailsScreen extends SetUpDetailsScreen {
    JButton topButton = new JButton("一覧画面");
    JButton editButton = new JButton("編集画面");

    protected ViewDetailsScreen() {
        frame.setTitle("詳細画面");
        mainPanel.add(topButton);
        mainPanel.add(editButton);
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
        //編集画面ボタンイベント
        editButton.addActionListener(e -> {
            // 画面内のパーツ削除
            refreshUI();
            // 編集画面表示
            ViewEditScreen edit = new ViewEditScreen();
            edit.View();
        });
    };
}
