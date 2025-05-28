
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.*;
//5/24  下記３つ追記
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import org.w3c.dom.events.MouseEvent;
import java.util.Date;//下村追加分

public class ViewTopScreen extends SetUpTopScreen {
    private JButton bulkSelectButton;// 選択操作
    JPanel topScreenPanel; // 一覧画面
    JPanel selectedScreenPanel; // 選択画面

    private JTable engineerTable;
    private JLabel pageLabel = new JLabel("", SwingConstants.CENTER);
    private JPanel bottomPanel; // 追加で保持（再描画防止）
    public int currentPage = 1; // UpdateEmployee クラスからの外部アクセス
    public int totalPages = 1;
    private Set<Integer> selectedRows = new HashSet<>(); // 選択操作
    private ArrayList<String> selected = new ArrayList<>();
    private DefaultTableModel model;

    // 記載順間違えると起動しなくなるから注意
    public ViewTopScreen() {
        engineerTable = new JTable();// 先にテーブルを初期化してから refreshTable を呼ぶ
        setupEngineerList();
        refreshTable(); // 画面初期表示とデータ同期

    }
    private void setupEngineerList() {
        
        // 検索バー
        JPanel topWrapper = (JPanel) fullScreenPanel.getComponent(1);
        JPanel topPanel = (JPanel) topWrapper.getComponent(0);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.setOpaque(false);
        String[] labels = { "社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語" };
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
        centerPanel.setOpaque(false);// 背景透過
        // 仮ボタン配置
        functionButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        functionButtonsPanel.setOpaque(false); // 背景透過
        functionButtonsPanel.add(new JLabel("エンジニア一覧"));
        JButton addEmployeeButton = new JButton("新規");
        JButton loadButton = new JButton("読込");
        JButton templateButton = new JButton("テンプレート出力");
        
        
        
        // =============================================
        // 各種ボタンイベント設定（下記担当者記載）
        // =============================================

        //「新規追加」ボタン押下後イベント※{}内追記お願いします
        addEmployeeButton.addActionListener(e -> {
        });
        functionButtonsPanel.add(addEmployeeButton);
        
        //「読込」ボタン押下後イベント※{}内追記お願いします
        loadButton.addActionListener(e -> {       
        });
        functionButtonsPanel.add(loadButton);

        //「テンプレート出力」ボタン押下後イベント※{}内追記お願いします
        templateButton.addActionListener(e -> {       
        });
        functionButtonsPanel.add(templateButton);
        // 選択画面（ViewSelectedScreen ）に遷移
        bulkSelectButton = new JButton("ページ内一括選択");
        functionButtonsPanel.add(bulkSelectButton);
        bulkSelectButton.addActionListener(e -> {
            for (int i = 0; i < 10; i++) {
                selected.add((model.getValueAt(i, 0)).toString());
            }
            refreshUI();
            ViewSelectedScreen selectedScreen = new ViewSelectedScreen();
            selectedScreen.View(selected, currentPage);
        });
        // テーブル構築
        String[] columnNames = { "社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細" };
        Object[][] data = getPageData(currentPage, 10);// 下村作成部分
        // ソート対象外の列インデックス（「詳細」列）
        Set<Integer> unsortableColumns = Set.of(5);

        // 従業員０名時の表示
        if (data.length == 0) {
            showNoDataLabel(employeeListPanel);
        } else {
            // ページ数自動計算(10n+1でページ新規生成)、最大100ページ
            int totalEmployees = EmployeeManager.employeeList.size();// 下村作成部分
            totalPages = Math.min((totalEmployees + 9) / 10, 100);
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
                public boolean isCellEditable(int row, int column) {
                    return column == 5;
                }
            };
            

            
        }

        // ヘッダーソート状態マップ（0:ー, 1:↑, 2:↓）
        Map<Integer, Integer> sortStates = new HashMap<>();
        JTableHeader header = engineerTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 18)); // 太字に
        header.setBackground(new Color(200, 200, 255)); // ヘッダーの背景色を設定
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                // 「詳細」列（index = 5）はソート記載なし
                if (column == 5) {
                    label.setText(value.toString());
                } else {
                    String base = value.toString();
                    String symbol = switch (sortStates.getOrDefault(column, 0)) {
                        case 1 -> " ↑";
                        case 2 -> " ↓";
                        default -> " ー";
                    };
                    label.setText(base + symbol);
                }
                return label;
            }
        });

        header.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                if (unsortableColumns.contains(col))
                    return; // 「詳細」列はソート対象外
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
        if (data.length != 0) {
            TableColumn detailColumn = engineerTable.getColumn("詳細");
            detailColumn.setCellRenderer(new ButtonRenderer());
            detailColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
            JScrollPane scrollPane = new JScrollPane(engineerTable);
            scrollPane.setPreferredSize(new Dimension(715, 363));
            employeeListPanel.setLayout(new BorderLayout());
            employeeListPanel.add(scrollPane, BorderLayout.CENTER);
        }

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

    // 0件時のラベル表示（共通化）
    private void showNoDataLabel(JPanel panel) {
        panel.removeAll(); // 既存コンポーネントを削除
        panel.setLayout(new BorderLayout());
        JLabel noDataLabel = new JLabel("データがありません", SwingConstants.CENTER);
        noDataLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(noDataLabel, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    /*
     * refreshTableメソッドはengineerTable のデータモデルを更新
     * JTable のインスタンスであり、setupEngineerList() メソッド内で初期化されていることを前提
     * EmployeeManager.getInitialData() は、最新の従業員データを2次元配列で返すメソッドであると仮定
     */
    public void refreshTable() {
        int totalEmployees = EmployeeManager.employeeList.size();// 下村作成部分(本番時利用コード)
        totalPages = Math.min((totalEmployees + 9) / 10, 100);
        if (totalPages == 0)
            totalPages = 1; // 0ページにならないように

        Object[][] pageData = getPageData(currentPage, 10);// 下村作成部分
        // テーブルのヘッダー
        String[] columnNames = { "社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細" };

        // テーブルモデル作成（編集不可）
        DefaultTableModel model = new DefaultTableModel(pageData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        engineerTable.setModel(model); // モデルの設定後にレンダラー設定
        // refreshTable() の最後にこのループを追加
        for (int i = 0; i < engineerTable.getColumnCount(); i++) {
            if (i == 4) {
                // 文字数カット＋中央寄せのカスタムレンダラー
                engineerTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                row, column);
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        if (value != null) {
                            String str = value.toString();
                            label.setText(str.length() > 10 ? str.substring(0, 10) + "..." : str);
                        }
                        return label;
                    }
                });
            } else {
                // その他中央寄せ（これが必要なら）
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                engineerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        JTableHeader header = engineerTable.getTableHeader(); // ← 表示されているテーブルのヘッダー
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        engineerTable.setRowHeight(34);
        pageLabel.setText(currentPage + " / " + totalPages);

        // 詳細ボタン再設定（データがある場合のみ安全に処理）
        if (pageData.length > 0) {
            try {
                TableColumn detailColumn = engineerTable.getColumn("詳細");
                detailColumn.setCellRenderer(new ButtonRenderer());
                detailColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
            } catch (IllegalArgumentException e) {
                System.err.println("詳細列の設定失敗：" + e.getMessage());
            }
        }
        // 表示更新の後で、マウスイベント登録メソッドを呼び出す
        setupTableClickEvent(model);
    }
    //  従業員欄押下後ViewSelectedScreen に遷移。マウスクリックイベントをテーブルに設定、クリック時の社員IDを取得
    private void setupTableClickEvent(DefaultTableModel model) {
        // 既存のマウスリスナーを削除（多重登録防止）
        for (MouseListener listener : engineerTable.getMouseListeners()) {
            engineerTable.removeMouseListener(listener);
        }

        engineerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = engineerTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String id = model.getValueAt(row, 0).toString();

                    ArrayList<String> selectedIds = new ArrayList<>();
                    selectedIds.add(id);

                    ViewSelectedScreen selectedScreen = new ViewSelectedScreen();
                    selectedScreen.View(selectedIds, currentPage);
                }
            }
        });
    }

    //ここまで

    

    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(UIManager.getColor("Button.background"));
            setForeground(Color.BLACK);
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

    // ----------------------
    // 下村作成部分↓
    public void View() {
        frame.setVisible(true);
    }

    public void View(int currentPage) {
        this.currentPage = currentPage;
        refreshTable(); // 画面初期表示とデータ同期
        frame.setVisible(true);
    }

    public Object[][] getPageData(int currentPage, int maxDisplayCount) {
        int totalEmployees = EmployeeManager.employeeList.size(); 

        if (totalEmployees == 0) {
            return new Object[0][6]; // 0件時は空配列を返す処理
        }
        Object[][] displayList = new Object[10][6];
        Date now = new Date();
        int displayCount;

        if (EmployeeManager.employeeList.size() <= 10) {
            displayCount = EmployeeManager.employeeList.size();// 社員数10以下
        } else if (currentPage * maxDisplayCount < EmployeeManager.employeeList.size()) {
            displayCount = maxDisplayCount;
        } else {
            displayCount = EmployeeManager.employeeList.size() - ((currentPage - 1) * maxDisplayCount);
        }
        for (int i = 0; i < displayCount; i++) {
            EmployeeInformation empioyee = EmployeeManager.employeeList.get(i + ((currentPage - 1) * maxDisplayCount));
            displayList[i][0] = empioyee.employeeID;
            displayList[i][1] = empioyee.lastName + " " + empioyee.firstname;
            displayList[i][2] = calcAge(empioyee.birthday, now);
            displayList[i][3] = empioyee.engineerDate;
            displayList[i][4] = empioyee.useLanguageDate;
            displayList[i][5] = "詳細";
        }
        return displayList;
    }

    // 年齢を計算するメソッド（第１引数：誕生日、第2引数：現在日）
    public static int calcAge(Date birthday, Date now) {
        // Calendar型のインスタンス生成
        Calendar calendarBirth = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();

        // Date型をCalendar型に変換する
        calendarBirth.setTime(birthday);
        calendarNow.setTime(now);

        // （現在年ー生まれ年）で年齢の計算
        int age = calendarNow.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR);
        // 誕生月を迎えていなければ年齢-1
        if (calendarNow.get(Calendar.MONTH) < calendarBirth.get(Calendar.MONTH)) {
            age -= 1;
        } else if (calendarNow.get(Calendar.MONTH) == calendarBirth.get(Calendar.MONTH)) {
            // 誕生月は迎えているが、誕生日を迎えていなければ年齢−１
            if (calendarNow.get(Calendar.DATE) < calendarBirth.get(Calendar.DATE)) {
                age -= 1;
            }
        }

        return age;
    }
}
