// ViewTopScreen.java
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.table.*;

public class ViewTopScreen extends SetUpTopScreen {

    private JTable engineerTable;
    private JLabel pageLabel;
    public int currentPage = 1; //UpdateEmployee クラスからの外部アクセス
    public int totalPages = 1;


    public ViewTopScreen() {
        super();
        setupEngineerList();
        refreshTable(); // 画面初期表示とデータ同期
    }
    /*消すかも
     * refreshTableメソッドはengineerTable のデータモデルを更新
     * JTable のインスタンスであり、setupEngineerList() メソッド内で初期化されていることを前提
     * EmployeeManager.getInitialData() は、最新の従業員データを2次元配列で返すメソッドであると仮定
     */
    public void refreshTable() {
        int totalEmployees = EmployeeManager.getEmployeeCount();
        totalPages = Math.min((totalEmployees + 9) / 10, 100);
    
        Object[][] pageData = EmployeeManager.getPageData(currentPage, 10);
    
        DefaultTableModel model = new DefaultTableModel(pageData, new String[]{
            "社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細"
        }) {
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
    
        engineerTable.setModel(model); // ← これが重要！
        engineerTable.setRowHeight(34);
        pageLabel.setText(currentPage + " / " + totalPages);
    
        // 詳細ボタン再設定
        TableColumn detailColumn = engineerTable.getColumn("詳細");
        detailColumn.setCellRenderer(new ButtonRenderer());
        detailColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    
    

    private void setupEngineerList() {
        // topPanel に検索バー追加、ここは仮で表示してるだけ
        JPanel topWrapper = (JPanel) fullScreenPanel.getComponent(1);
        JPanel topPanel = (JPanel) topWrapper.getComponent(0);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.setBackground(Color.LIGHT_GRAY);


        String[] labels = {"社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語"};
        for (String label : labels) {
            topPanel.add(new JLabel(label));
            JTextField field = new JTextField(5);
            topPanel.add(field);
        }
        topPanel.add(new JButton("検索"));

        // centerPanel 取得
        JPanel centerWrapper = (JPanel) fullScreenPanel.getComponent(3);
        JPanel centerPanel = (JPanel) centerWrapper.getComponent(0);
        JPanel functionButtonsPanel = (JPanel) centerPanel.getComponent(0);
        JPanel employeeListPanel = (JPanel) centerPanel.getComponent(2);

        // ボタン配置
        functionButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        functionButtonsPanel.setBackground(Color.WHITE); // ← fullScreenPanel と同じ色に
        functionButtonsPanel.setOpaque(true);
        functionButtonsPanel.add(new JLabel("エンジニア一覧"));
        functionButtonsPanel.add(new JButton("読込"));
        functionButtonsPanel.add(new JButton("新規"));
        functionButtonsPanel.add(new JButton("テンプレート出力"));


        // テーブル構築
        String[] columnNames = {"社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細"};
        // ソート対象外の列インデックス（「詳細」列）
        Set<Integer> unsortableColumns = Set.of(5);

        Object[][] data = EmployeeManager.getPageData(currentPage, 10);

        //従業員０名時の表示
        if (data.length == 0) {
            JLabel noDataLabel = new JLabel("データがありません", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            employeeListPanel.setLayout(new BorderLayout());
            employeeListPanel.add(noDataLabel, BorderLayout.CENTER);
            return;
        }
        //ページ数自動計算(10n+1でページ新規生成)、最大100ページ
        int totalEmployees = EmployeeManager.getEmployeeCount();
        totalPages = Math.min((totalEmployees + 9) / 10, 100);

        

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        engineerTable = new JTable(tableModel) {
            @Override //Jtanle拡張してる、ここ変更可能性あり
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (comp instanceof JLabel label) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
        
                    // 「扱える言語」列（index = 4）を省略表示
                    if (column == 4) {
                        String value = getValueAt(row, column).toString();
                        if (value.length() > 11) {
                            label.setText(value.substring(0, 11) + "...");
                        }
                    }
                }
                return comp;
            }
        };
        // ヘッダーソート状態マップ（0:ー, 1:↑, 2:↓）
        Map<Integer, Integer> sortStates = new HashMap<>();

        JTableHeader header = engineerTable.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);

                    String base = value.toString();
                    String symbol = switch (sortStates.getOrDefault(column, 0)) {
                        case 1 -> " ↑";
                        case 2 -> " ↓";
                        default -> " ー";
                    };
                    label.setText(base + symbol);
                    return label;
                }
        });
        header.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                if (unsortableColumns.contains(col)) return; // 「詳細」列はソート対象外
                int current = sortStates.getOrDefault(col, 0);
                int next = switch (current) {
                    case 0 -> 1;
                    case 1 -> 2;
                    default -> 1;
                };
                sortStates.put(col, next);
                header.repaint(); // 再描画して記号更新
            }
        });


        
        engineerTable.setRowHeight(34);

        // 全列中央寄せ
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < engineerTable.getColumnCount(); i++) {
            engineerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        TableColumn detailColumn = engineerTable.getColumn("詳細");
        detailColumn.setCellRenderer(new ButtonRenderer());
        detailColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(engineerTable);
        scrollPane.setPreferredSize(new Dimension(715, 363));
        employeeListPanel.setLayout(new BorderLayout());
        employeeListPanel.add(scrollPane, BorderLayout.CENTER);

        // bottomPanel にページネーション表示
        JPanel bottomWrapper = (JPanel) fullScreenPanel.getComponent(5);
        JPanel bottomPanel = (JPanel) bottomWrapper.getComponent(0);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(null);

        JButton prevButton = new JButton("←");
        JButton nextButton = new JButton("→");
        pageLabel = new JLabel(currentPage + " / " + totalPages, SwingConstants.CENTER);

        bottomPanel.add(prevButton, BorderLayout.WEST);
        bottomPanel.add(pageLabel, BorderLayout.CENTER);
        bottomPanel.add(nextButton, BorderLayout.EAST);



        /*下記に変更するかも
         * prevButton.addActionListener(e -> {
                if (currentPage > 1) {
                    currentPage--;
                    refreshTable();
                }
            });

            nextButton.addActionListener(e -> {
                if (currentPage < totalPages) {
                    currentPage++;
                    refreshTable();
                }
            });
         * 
         */

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                refreshTable(); // ページ切り替え時に refreshTable() を呼ぶ
            }
        });
        
        nextButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                refreshTable(); // ページ切り替え時に refreshTable() を呼ぶ
            }
        });
        

    }

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
                JOptionPane.showMessageDialog(button, "詳細ボタンがクリックされました: " + label);
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

