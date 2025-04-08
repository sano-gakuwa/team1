
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class ViewSelectedScreen extends SetUpTopScreen {

    public ViewSelectedScreen() {
        super(); // 親クラスのパネル配置呼び出し
        setupTopPanelContents();
        setupCenterPanelContents();
        frameClear(); // UIの再描画
    }

    // 検索機能部分UI
    private void setupTopPanelContents() {
        JPanel topPanel = getPanelFromFullScreen(1); // 上段パネルは fullScreenPanel の2番目に追加されてる

        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        for (int i = 0; i < 5; i++) {
            topPanel.add(new JLabel("項目" + (i + 1) + ":"));
            topPanel.add(new JTextField(10));
        }
        topPanel.add(new JButton("検索"));
    }

    // 社員情報一覧UI
    private void setupCenterPanelContents() {
        JPanel centerWrapper = getPanelFromFullScreen(3); // 中央パネルのラッパー
        JPanel centerPanel = (JPanel) centerWrapper.getComponent(0); // ラップされた中身を取得
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // 上段：ボタンとラベル
        JPanel upperSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        upperSubPanel.add(new JLabel("エンジニア一覧"));
        upperSubPanel.add(new JButton("読込"));
        upperSubPanel.add(new JButton("新規"));
        upperSubPanel.add(new JButton("テンプレート出力"));

        // ギャップ
        upperSubPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 下段：6列10行のカスタムリスト
        JPanel listPanel = new JPanel();
        listPanel.setPreferredSize(new Dimension(715, 363));
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // 列ごとのラベル名（固定）
        String[] columnNames = {"社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細"};

        // 仮データ：この部分は実際のデータ取得に差し替え（getEmployeeTable() は仮メソッド）
        ArrayList<ArrayList<EmployeeInformation>> employeeTable = getEmployeeTable();

        // 選択行の状態を保持（1つだけ選択とする）
        final JPanel[] selectedRow = {null}; // final 配列でクロージャ対応

        for (int row = 0; row < employeeTable.size(); row++) {
            JPanel rowPanel = new JPanel(new GridLayout(1, 6, 5, 0));
            rowPanel.setMaximumSize(new Dimension(715, 36));
            rowPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            rowPanel.setBackground(Color.WHITE);

            ArrayList<EmployeeInformation> rowData = employeeTable.get(row);

            for (int col = 0; col < 6; col++) {
                if (col < 5) {
                    JLabel label = new JLabel(rowData.get(col).toString());
                    label.setOpaque(true);
                    rowPanel.add(label);
                } else {
                    JButton actionButton = new JButton("選択");
                    int finalRow = row;
                    actionButton.addActionListener(e -> {
                        if (selectedRow[0] != null) {
                            selectedRow[0].setBackground(Color.WHITE);
                        }
                        rowPanel.setBackground(new Color(200, 230, 255));
                        selectedRow[0] = rowPanel;

                        // 必要ならここで employeeTable.get(finalRow) を処理
                    });
                    rowPanel.add(actionButton);
                }
            }

            listPanel.add(rowPanel);
            listPanel.add(Box.createRigidArea(new Dimension(0, 1)));
        }

        centerPanel.add(upperSubPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(listPanel);
    }

    // ユーティリティ：fullScreenPanelから指定順番のパネルを取得
    private JPanel getPanelFromFullScreen(int index) {
        Component comp = fullScreenPanel.getComponent(index);
        if (comp instanceof JPanel panel) {
            if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JPanel inner) {
                return inner;
            }
            return panel;
        }
        return new JPanel(); // fallback
    }
}
