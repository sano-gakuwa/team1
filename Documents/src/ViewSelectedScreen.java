import javax.swing.JButton;

public class ViewSelectedScreen extends SetUpTopScreen{
    JButton b1=new JButton("仮ボタン1");
    JButton topButton=new JButton("一覧画面");
    protected ViewSelectedScreen(){
        frame.setTitle("選択画面");
        mainPanel.add(b1);
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
    }
}
