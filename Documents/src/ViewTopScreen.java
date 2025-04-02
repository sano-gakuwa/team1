import javax.swing.JButton;
import javax.swing.JPanel;

public final class ViewTopScreen extends SetUpTopScreen {
    JPanel textPanel=new JPanel();
    JButton additiButton = new JButton("追加");
    JButton detailsButton = new JButton("詳細");
    JButton selectedButton = new JButton("選択");

    protected ViewTopScreen() {
        frame.setTitle("一覧画面");
        mainPanel.add(additiButton);
        mainPanel.add(detailsButton);
        mainPanel.add(selectedButton);
        frame.add(framePanel);// フレームにパネルを追加
    }

    public void View() {
        // 画面の表示
        frame.setVisible(true);
        //追加画面ボタンイベント
        additiButton.addActionListener(e -> {
            // 画面内のパーツ削除
            refreshUI();
            // 追加画面表示
            ViewAdditionScreen additi = new ViewAdditionScreen();
            additi.View();
        });
        //詳細画面ボタンイベント
        detailsButton.addActionListener(e -> {
            // 画面内のパーツ削除
            refreshUI();
            // 詳細画面表示
            ViewDetailsScreen details = new ViewDetailsScreen();
            details.View();
        });
        //選択画面ボタンイベント
        selectedButton.addActionListener(e ->{
            // 画面内のパーツ削除
            refreshUI();
            //選択中画面表示
            ViewSelectedScreen selected=new ViewSelectedScreen();
            selected.View();
        });
    };
}
