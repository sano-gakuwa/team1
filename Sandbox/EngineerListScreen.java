import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class EngineerListScreen {

    private static int currentPage = 1; // 現在のページ番号
    private static final int totalPages = 5; // 総ページ数（例）

    public static void main(String[] args) {
        // メインフレームの作成
        JFrame frame = new JFrame("エンジニア一覧");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);  // サイズ変更不可

        // メインパネルにBoxLayoutを使用して縦に並べる
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 上部検索パネル
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 検索項目
        JTextField idField = new JTextField(5);
        JTextField nameField = new JTextField(5);
        JTextField ageField = new JTextField(5);
        JTextField experienceField = new JTextField(5);
        JTextField languagesField = new JTextField(10);
        JButton searchButton = new JButton("検索");

        searchPanel.add(new JLabel("社員ID"));
        searchPanel.add(idField);
        searchPanel.add(new JLabel("氏名"));
        searchPanel.add(nameField);
        searchPanel.add(new JLabel("年齢"));
        searchPanel.add(ageField);
        searchPanel.add(new JLabel("エンジニア歴"));
        searchPanel.add(experienceField);
        searchPanel.add(new JLabel("扱える言語"));
        searchPanel.add(languagesField);
        searchPanel.add(searchButton);

        // エンジニア一覧テキストとボタンのパネル作成
        JPanel titleAndButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        titleAndButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("エンジニア一覧");
        titleAndButtonPanel.add(titleLabel);

        // ボタンの作成
        JButton newButton = new JButton("新規");
        JButton selectButton = new JButton("選択");
        JButton readButton = new JButton("読込");
        JButton exportButton = new JButton("テンプレート出力");
        JButton updateButton = new JButton("更新");

        titleAndButtonPanel.add(newButton);
        titleAndButtonPanel.add(selectButton);
        titleAndButtonPanel.add(readButton);
        titleAndButtonPanel.add(exportButton);
        titleAndButtonPanel.add(updateButton);

        // テーブルモデルとデータ
        String[] columnNames = {"社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細"};
        Object[][] data = {
            {"xx01234", "ヤマダ タロウ", "21", "2年6か月", "html,css,javascript", "..."},
            {"xx01235", "サトウ ジロウ", "22", "1年3か月", "java,python", "..."},
            {"xx01236", "タカハシ シロウ", "25", "5年0か月", "javascript,react", "..."},
            {"xx01237", "スズキ ゴロウ", "30", "8年0か月", "html,css,javascript", "..."},
            {"xx01234", "ヤマダ タロウ", "21", "2年6か月", "html,css,javascript", "..."},
            {"xx01235", "サトウ ジロウ", "22", "1年3か月", "java,python", "..."},
            {"xx01236", "タカハシ シロウ", "25", "5年0か月", "javascript,react", "..."},
            {"xx01237", "スズキ ゴロウ", "30", "8年0か月", "html,css,javascript", "..."},
            {"xx01236", "タカハシ シロウ", "25", "5年0か月", "javascript,react", "..."},
            {"xx01237", "スズキ ゴロウ", "30", "8年0か月", "html,css,javascript", "..."},};

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // 「詳細」列のみ編集可能にする
            }
        };

        // テーブル作成
        JTable engineerTable = new JTable(tableModel);
        engineerTable.setRowHeight(40);

        // 「詳細」列にボタンをレンダリングするための設定
        TableColumn detailColumn = engineerTable.getColumn("詳細");
        detailColumn.setCellRenderer(new ButtonRenderer());
        detailColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tableScrollPane = new JScrollPane(engineerTable);

        // 下部ページ遷移パネル
        JPanel navigationPanel = new JPanel(new BorderLayout());
        
        // 「←」ボタン（左寄せ）
        JButton prevButton = new JButton("←");
        prevButton.setHorizontalAlignment(SwingConstants.LEFT);

        // 「→」ボタン（右寄せ）
        JButton nextButton = new JButton("→");
        nextButton.setHorizontalAlignment(SwingConstants.RIGHT);

        // 現在のページ表示用ラベル（中央寄せ）
        JLabel pageLabel = new JLabel(currentPage + " / " + totalPages, SwingConstants.CENTER);

        // ページ遷移ボタンのクリックイベント
        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                pageLabel.setText(currentPage + " / " + totalPages);
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                pageLabel.setText(currentPage + " / " + totalPages);
            }
        });

        // ボタンとラベルをパネルに追加
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // 左寄せ
        buttonPanel.add(prevButton);
        buttonPanel.add(pageLabel);
        buttonPanel.add(nextButton);

        // メインパネルに順番に追加
        mainPanel.add(searchPanel);              // 上部検索パネル
        mainPanel.add(titleAndButtonPanel);      // タイトルとボタンパネル
        mainPanel.add(tableScrollPane);          // テーブルパネル
        mainPanel.add(navigationPanel);          // 下部ページ遷移パネル
        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.PAGE_END);

        // フレームにメインパネルを追加
        frame.add(mainPanel);

        // フレームを表示
        frame.setVisible(true);
    }

    // ボタンを表示するレンダラークラス
    static class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // ボタンを動作可能にするエディタークラス
    static class ButtonEditor extends DefaultCellEditor {

        private JButton button;
        private String label;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // ボタンがクリックされたときの処理
                JOptionPane.showMessageDialog(button, "詳細ボタンがクリックされました (行: " + (label) + ")");
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
