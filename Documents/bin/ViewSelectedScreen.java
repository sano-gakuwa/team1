import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.*;



public class ViewSelectedScreen extends SetUpTopScreen {
    private JButton bulkSelectButton;//選択操作
    JPanel topScreenPanel;         // 一覧画面
    JPanel selectedScreenPanel;    // 選択画面  

    private JTable engineerTable;
    private JLabel pageLabel;
    public int currentPage = 1; //UpdateEmployee クラスからの外部アクセス
    public int totalPages = 1;
    private ArrayList<String> selectedEmployeeIds;//受け取って処理
    private Set<Integer> selectedRows = new HashSet<>(); //選択操作
    private Map<Integer, Set<String>> pageSelectedEngineerIds = new HashMap<>();//ページごとに選択状態を保持するためのマップ


    


    public ViewSelectedScreen() {
        super();
        // ViewTopScreenのページ内全選択押下後、全グレーアウト状態表示
        int rowCount = EmployeeManager.getPageData(currentPage, 10).length;
        for (int i = 0; i < rowCount; i++) {
            selectedRows.add(i);
        }
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
    
        engineerTable.setModel(model); // 重要
        JTableHeader header = engineerTable.getTableHeader(); // ← 表示されているテーブルのヘッダー
        header.setFont(new Font("SansSerif", Font.BOLD, 16));



        engineerTable.setRowHeight(34);
        engineerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { //⭐️
                int row = engineerTable.rowAtPoint(e.getPoint());
                int col = engineerTable.columnAtPoint(e.getPoint());
        
                // 「詳細」列以外をクリックしたときのみトグル
                if (col != 5) {
                    if (selectedRows.contains(row)) {
                        selectedRows.remove(row);
                    } else {
                        selectedRows.add(row);
                    }
                    engineerTable.repaint();
                }
            }
        });
        // 現在ページのIDだけ抽出（nullなら空のSetとして扱う）
        Set<String> idsThisPage = pageSelectedEngineerIds.getOrDefault(currentPage, new HashSet<>());
        selectedRows.clear();
        for (int i = 0; i < pageData.length; i++) {
            String id = pageData[i][0].toString();
            if (idsThisPage.contains(id)) {
                selectedRows.add(i);
            }
        }



        // renderer での背景色判定もこっちに変更
        engineerTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String engineerId = table.getValueAt(row, 0).toString();

                if (idsThisPage.contains(engineerId)) {
                    c.setBackground(Color.LIGHT_GRAY);
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        
        
        

        pageLabel.setText(currentPage + " / " + totalPages);
    
        // 詳細ボタン再設定
        TableColumn detailColumn = engineerTable.getColumn("詳細");
        detailColumn.setCellEditor(new ButtonEditor(new JCheckBox()));


        detailColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        detailColumn.setCellEditor(new ButtonEditor(new JCheckBox()) {
            @Override
            public boolean stopCellEditing() {
                int editingRow = engineerTable.getEditingRow();
                // 選択中は詳細ボタン無効（反応させない）
                if (selectedRows.contains(editingRow)) {
                    return false; // 何もしない
                }
                return super.stopCellEditing();
            }
        });
        detailColumn.setCellRenderer(new ButtonRenderer(selectedRows)); //表示の再設定、選択中表記用
    }
    
    

    private void setupEngineerList() {
        //検索バー
        JPanel topWrapper = (JPanel) fullScreenPanel.getComponent(1);
        JPanel topPanel = (JPanel) topWrapper.getComponent(0);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.setOpaque(false);


        String[] labels = {"社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語"};

        for (String label : labels) {
            topPanel.add(new JLabel(label));
            
            JTextField field = new JTextField(5);
            topPanel.add(field);
        }
        JButton searchButton = new JButton("検索");
        topPanel.add(searchButton);  
        searchButton.setBackground(new Color(30, 144, 255)); // ボタン枠内塗りつぶし
        searchButton.setForeground(Color.WHITE);// 白文字
        searchButton.setFocusPainted(false); // フォーカス枠非表示（シンプル化）
        


        // centerPanel 取得
        JPanel centerWrapper = (JPanel) fullScreenPanel.getComponent(3);
        JPanel centerPanel = (JPanel) centerWrapper.getComponent(0);
        JPanel functionButtonsPanel = (JPanel) centerPanel.getComponent(0);
        JPanel employeeListPanel = (JPanel) centerPanel.getComponent(2);
        centerPanel.setOpaque(false);//背景透過


        

        // ボタン配置
        functionButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        functionButtonsPanel.setOpaque(false); //  背景透過
        functionButtonsPanel.add(new JLabel("エンジニア一覧"));
        functionButtonsPanel.add(new JButton("ページ内一括解除"));
        bulkSelectButton = new JButton("ページ内一括選択");
        functionButtonsPanel.add(bulkSelectButton);
        functionButtonsPanel.add(new JButton("CSV出力"));
        functionButtonsPanel.add(new JButton("削除"));
        
        // 選択状態
        bulkSelectButton.addActionListener(e -> {
            selectedRows.clear();
            int rowCount = engineerTable.getRowCount();
            Set<String> idsThisPage = new HashSet<>();
        
            for (int i = 0; i < rowCount; i++) {
                selectedRows.add(i);
                String engineerId = engineerTable.getValueAt(i, 0).toString();
                idsThisPage.add(engineerId);
            }
        
            // 現在のページ番号に対応する選択状態を保存
            pageSelectedEngineerIds.put(currentPage, idsThisPage);
        
            // 画面リロード
            refreshTable();
        });
        
        
        
        


        // テーブル構築
        String[] columnNames = {"社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細"};
        Object[][] data = EmployeeManager.getPageData(currentPage, 10);
        // ソート対象外の列インデックス（「詳細」列）
        Set<Integer> unsortableColumns = Set.of(5);

        //従業員０名時の表示
        if (data.length == 0) {
            JLabel noDataLabel = new JLabel("データがありません", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
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

        if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            // 「扱える言語」列（index = 4）を省略表示
            if (column == 4) {
                String value = getValueAt(row, column).toString();
                if (value.length() > 11) {
                    label.setText(value.substring(0, 11) + "...");
                } else {
                    label.setText(value);
                }
            }

            // 選択状態ならグレー背景
            if (selectedRows.contains(row)) {
                label.setBackground(new Color(211, 211, 211)); // LightGray
                label.setForeground(Color.BLACK);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }

            return label;
        }

        return comp;
    }

        };
        
        // ヘッダーソート状態マップ（0:ー, 1:↑, 2:↓）
        Map<Integer, Integer> sortStates = new HashMap<>();

        JTableHeader header = engineerTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 18)); // 太字に
        header.setBackground(new Color(200, 200, 255));       // ヘッダーの背景色を設定
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

    // ButtonRenderer に selectedRows を渡せるように変更
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        private Set<Integer> selectedRows;

        public ButtonRenderer(Set<Integer> selectedRows) {
            this.selectedRows = selectedRows;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
                    setText((value == null) ? "" : value.toString());

                    if (selectedRows.contains(row)) {
                        setBackground(new Color(200, 200, 200));
                        setForeground(Color.DARK_GRAY);
                        setEnabled(false); // 非活性
                    } else {
                        setBackground(UIManager.getColor("Button.background"));
                        setForeground(Color.BLACK);
                        setEnabled(true);
                    }

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

