import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;

public class ViewSelectedScreen extends SetUpTopScreen {
    private JButton bulkSelectButton;// 一括選択ボタン
    private JButton bulkCancellationtButton;// 一括解除ボタン
    private JButton createCsvButton;// CSV出力ボタン
    private JButton deleteButton;// 削除ボタン

    private JLabel selectedLabel = new JLabel("", SwingConstants.CENTER);
    private JTable engineerTable;// 社員情報表示欄
    private JLabel pageLabel = new JLabel("", SwingConstants.CENTER);// ページ数表示箇所
    public int currentPage = 1; // 現在のページ数
    public int totalPages = 1;// ページ数
    private ArrayList<String> selected = new ArrayList<>();// 選択された社員情報
    private DefaultTableModel model;// JTablの表示モデル
    private ArrayList<EmployeeInformation> tableEmployee = null;// JTablに表示する社員情報
    private final EmployeeManager MANAGER = new EmployeeManager();// 社員情報の管理用
    private EmployeeListOperator employeeListOperator;// 検索機能 6/9追記
    private JPanel functionButtonsPanel;
    private JPanel employeeListPanel;
    private ViewDialog dialog = new ViewDialog();
    private String[] columnNames = { "社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語" };
    // それぞれの検索フィールドを変数化して保持
    private JTextField idField = new JTextField(5);
    private JTextField nameField = new JTextField(5);
    private JTextField ageField = new JTextField(5);
    private JTextField engField = new JTextField(5);
    private JTextField langField = new JTextField(5);

    public ViewSelectedScreen() {
    }

    private void setupEngineerList() {
        // 検索バー
        JPanel topWrapper = (JPanel) fullScreenPanel.getComponent(1);
        JPanel topPanel = (JPanel) topWrapper.getComponent(0);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.setOpaque(false);
        // ラベル + フィールドを追加
        topPanel.add(new JLabel(columnNames[0]));
        topPanel.add(idField);
        topPanel.add(new JLabel(columnNames[1]));
        topPanel.add(nameField);
        topPanel.add(new JLabel(columnNames[2]));
        topPanel.add(ageField);
        topPanel.add(new JLabel("歳"));
        topPanel.add(new JLabel(columnNames[3]));
        topPanel.add(engField);
        topPanel.add(new JLabel(columnNames[4]));
        topPanel.add(langField);

        // 入力制限フィルター（最大100文字）をそれぞれ適用
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        ((AbstractDocument) ageField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        ((AbstractDocument) engField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        ((AbstractDocument) langField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        inactiveSearchBox();

        // centerPanel 取得
        JPanel centerWrapper = (JPanel) fullScreenPanel.getComponent(3);
        JPanel centerPanel = (JPanel) centerWrapper.getComponent(0);
        functionButtonsPanel = (JPanel) centerPanel.getComponent(0);
        employeeListPanel = (JPanel) centerPanel.getComponent(2);
        centerPanel.setOpaque(false);// 背景透過

        // ボタン配置
        functionButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        functionButtonsPanel.setOpaque(false); // 背景透過
        bulkCancellationtButton = new JButton("ページ内一括解除");
        bulkSelectButton = new JButton("ページ内一括選択");
        createCsvButton = new JButton("CSV出力");
        deleteButton = new JButton("削除");
        selectedLabel.setText(selected.size() + "/" + tableEmployee.size() + "選択中");
        functionButtonsPanel.add(new JLabel("エンジニア一覧"));
        functionButtonsPanel.add(bulkCancellationtButton);
        functionButtonsPanel.add(bulkSelectButton);
        functionButtonsPanel.add(createCsvButton);
        functionButtonsPanel.add(deleteButton);
        functionButtonsPanel.add(selectedLabel);

        // =============================================
        // 各種ボタンイベント設定（下記担当者記載）
        // =============================================

        // 「ページ内一括解除」ボタン押下後イベント
        bulkCancellationtButton.addActionListener(e -> {
            MANAGER.printInfoLog("ページ内一括解除ボタンが押されました");
            for (int row = 0; row < 10; row++) {
                String select = (model.getValueAt(row, 0)).toString();
                if (selected.contains(select) == true) {
                    selected.remove(selected.indexOf(select));
                }
                if (selected.size() <= 0) {
                    // 選択されている社員情報が0名分
                    viewTopScreen();
                }
                showselected();
            }
        });

        // 「ページ内一括選択」ボタン押下後イベント
        bulkSelectButton.addActionListener(e -> {
            MANAGER.printInfoLog("ページ内一括選択ボタンが押されました");
            for (int row = 0; row < 10; row++) {
                String select = (model.getValueAt(row, 0)).toString();
                if (selected.contains(select) == false) {
                    selected.add(select);
                }
                showselected();
            }
        });

        // 「CSV出力」ボタン押下後イベント
        createCsvButton.addActionListener(e -> {
            MANAGER.printInfoLog("CSV出力ボタンが押されました");
            CreateCsv createCsv = new CreateCsv();
            // CSV出力中のロックがかかっているか確認
            if (createCsv.validateCreateCsvLock()) {
                // CSV出力中のロックがかかっている場合
                dialog.viewWarningDialog("CSV出力中です。しばらくお待ちください。");
                return;
            }
            selectFolder();
        });

        // 「削除」ボタン押下後イベント
        deleteButton.addActionListener(e -> {
            MANAGER.printInfoLog("削除ボタンが押されました");
            EmployeeInfoDeletion deletion = new EmployeeInfoDeletion();
            if (deletion.validateDeleteLock()) {
                // 削除のロックがかかっている場合
                dialog.viewWarningDialog("削除中です。しばらくお待ちください。");
                return;
            }
            deletion.delete(selected);
            Thread deletionThread = new Thread(deletion);
            deletionThread.start();
            refreshUI();
            ViewTopScreen top = new ViewTopScreen();
            top.View();
        });

        // テーブル構築
        Object[][] pageData = getPageData(currentPage, 10);// 下村作成部分
        // ソート対象外の列インデックス（「詳細」列）
        Set<Integer> unsortableColumns = Set.of(5);

        // 従業員０名時の表示
        if (pageData.length == 0) {
            showNoDataLabel(employeeListPanel);
        } else {
            // ページ数自動計算(10n+1でページ新規生成)、最大100ページ
            int totalEmployees = tableEmployee.size();
            totalPages = Math.min((totalEmployees + 9) / 10, 100);
            DefaultTableModel tableModel = new DefaultTableModel(pageData, columnNames) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            engineerTable = new JTable(tableModel) {
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component comp = super.prepareRenderer(renderer, row, column);
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setOpaque(true);
                        if (selected.contains(engineerTable.getValueAt(row, 0))) {
                            label.setBackground(Color.LIGHT_GRAY);
                        } else {
                            label.setBackground(Color.WHITE);
                        }
                        label.setForeground(Color.BLACK);
                        return label;
                    }
                    return comp;
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
                if (column >= 4) {
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

        employeeListOperator = new EmployeeListOperator(tableEmployee);
        header.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                if (unsortableColumns.contains(col))
                    return;

                int current = sortStates.getOrDefault(col, 0);
                int next = switch (current) {
                    case 0 -> 1; // 未ソート → 昇順
                    case 1 -> 2; // 昇順 → 降順
                    default -> 1; // 降順 → 昇順
                };

                // まず全ての状態リセット
                sortStates.replaceAll((k, v) -> 0);
                // 今回クリックされた列のみ状態を更新
                sortStates.put(col, next);
                header.repaint(); // ヘッダー再描画
                // ソートキー判定
                EmployeeListOperator.SortKey sortKey = switch (col) {
                    case 0 -> EmployeeListOperator.SortKey.EMPLOYEE_ID;
                    case 1 -> EmployeeListOperator.SortKey.NAME;
                    case 2 -> EmployeeListOperator.SortKey.AGE;
                    case 3 -> EmployeeListOperator.SortKey.ENGINEER_DATE;
                    default -> null;
                };
                if (sortKey != null) {
                    if (next == 0) {
                        // 未ソート時は元の順（登録順）に戻す
                        tableEmployee = new ArrayList<>(EmployeeManager.employeeList);
                    } else {
                        boolean ascending = (next == 1);
                        tableEmployee=employeeListOperator.sortEmployee(sortKey, ascending, tableEmployee);
                    }
                    currentPage = 1;
                    refreshTable();
                }
            }
        });

        engineerTable.setRowHeight(34);

        // 全列中央寄せ
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < engineerTable.getColumnCount(); i++) {
            engineerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane scrollPane = new JScrollPane(engineerTable);
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
    /**
     * 検索欄の検索ワードをセットする関数
     * 
     * @param searchKeyWord 検索欄の検索ワード
     */
    public void setSearchKeyWord(String[] searchKeyWord){
        idField.setText(searchKeyWord[0]);
        nameField.setText(searchKeyWord[1]);
        ageField.setText(searchKeyWord[2]);
        engField.setText(searchKeyWord[3]);
        langField.setText(searchKeyWord[4]);
    }

    private void inactiveSearchBox(){
        idField.setEditable(false);
        nameField.setEditable(false);
        ageField.setEditable(false);
        engField.setEditable(false);
        langField.setEditable(false);
    }

    // 社員情報を選択した際の再表示メソッド
    private void showselected() {
        selectedLabel.setText(selected.size() + "/" + tableEmployee.size() + "選択中");
        selectedLabel.repaint();
        engineerTable.repaint();
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
        if (tableEmployee.isEmpty()) {
            employeeListPanel.removeAll();
            employeeListPanel.setLayout(new BorderLayout());
            showNoDataLabel(employeeListPanel);
            employeeListPanel.revalidate();
            employeeListPanel.repaint();
            pageLabel.setText("0 / 0");
            return;
        }
        employeeListPanel.removeAll();
        employeeListPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(engineerTable);
        employeeListPanel.add(scrollPane, BorderLayout.CENTER);

        employeeListPanel.revalidate();
        employeeListPanel.repaint();

        int totalEmployees = tableEmployee.size();// 下村作成部分(本番時利用コード)
        totalPages = Math.min((totalEmployees + 9) / 10, 100);
        if (totalPages == 0)
            totalPages = 1; // 0ページにならないように

        Object[][] pageData = getPageData(currentPage, 10);
        // テーブルのヘッダー

        // テーブルモデル作成（編集不可）
        model = new DefaultTableModel(pageData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        engineerTable.setModel(model); // モデルの設定後にレンダラー設定
        engineerTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, false, false, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);

                // 「扱える言語」列（index = 4）の文字数制限
                if (column == 4 && value != null) {
                    String str = value.toString();
                    label.setText(str.length() > 10 ? str.substring(0, 10) + "..." : str);
                }

                // 「エンジニア歴」列（index = 3）の年月変換
                if (column == 3 && value != null) {
                    try {
                        String valStr = value.toString();
                        if (valStr.matches("\\d+")) {
                            int months = Integer.parseInt(valStr);
                            int years = months / 12;
                            int remain = months % 12;
                            label.setText(years + "年" + remain + "ヶ月");
                        } else {
                            label.setText(valStr);
                        }
                    } catch (NumberFormatException e) {
                        label.setText(value.toString());
                    }
                }
                return label;
            }
        });

        JTableHeader header = engineerTable.getTableHeader(); // ← 表示されているテーブルのヘッダー
        header.setReorderingAllowed(false);// テーブルの列移動を不許可にする。
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        engineerTable.setRowHeight(34);
        pageLabel.setText(currentPage + " / " + totalPages);
        // 表示更新の後で、マウスイベント登録メソッドを呼び出す
        setupTableClickEvent(model);
    }

    // マウスイベント設定
    private void setupTableClickEvent(DefaultTableModel model) {
        // 既存のマウスリスナーを削除（多重登録防止）
        for (MouseListener listener : engineerTable.getMouseListeners()) {
            engineerTable.removeMouseListener(listener);
        }
        engineerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = engineerTable.rowAtPoint(e.getPoint());
                if (row > -1) {
                    // 選択された社員のIDを取得
                    String select = (model.getValueAt(row, 0)).toString();
                    if (select == "") {
                        // 空白行を選択
                        MANAGER.printInfoLog("空白行を選択");
                    } else if (selected.contains(select) == false) {
                        // 未選択の社員情報の行をクリック
                        selected.add(select);
                        MANAGER.printInfoLog("社員ID:" + select + "を選択");
                        showselected();
                    } else if (selected.contains(select) == true) {
                        // 選択済みの社員情報の行をクリック
                        selected.remove(selected.indexOf(select));
                        MANAGER.printInfoLog("社員ID:" + select + "を選択解除");
                        showselected();
                    }
                    if (selected.size() <= 0) {
                        // 選択されている社員情報が0名分
                        viewTopScreen();
                    }
                }
            }
        });
    }

    // ここまで
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

    private void viewTopScreen() {
        refreshUI();
        ViewTopScreen top = new ViewTopScreen();
        top.View();
        MANAGER.printInfoLog("一覧画面へ遷移");
    }

    public void View(ArrayList<EmployeeInformation> tableEmployee, ArrayList<String> selected, int currentPage) {
        this.currentPage = currentPage;
        this.selected = selected;
        this.tableEmployee = tableEmployee;
        frame.setTitle("選択画面");
        setupEngineerList();
        refreshTable(); // 画面初期表示とデータ同期
        setupTableClickEvent(model);
        frame.setVisible(true);
    }

    public Object[][] getPageData(int currentPage, int maxDisplayCount) {
        int totalEmployees = tableEmployee.size();

        if (totalEmployees == 0) {
            return new Object[0][6]; // 0件時は空配列を返す処理
        }
        Object[][] displayList = new Object[10][6];
        Date now = new Date();
        int displayCount;
        if (tableEmployee.size() <= 10) {
            // 社員数10名以下
            displayCount = tableEmployee.size();
        } else if (currentPage * maxDisplayCount < tableEmployee.size()) {
            // 社員数10名以上で10名表示
            displayCount = maxDisplayCount;
        } else {
            // 社員数10名以上で10名未満表示
            displayCount = tableEmployee.size() - ((currentPage - 1) * maxDisplayCount);
        }
        if (displayCount == 10) {
            // 10名分表示
            for (int i = 0; i < displayCount; i++) {
                EmployeeInformation empioyee = tableEmployee.get(i + ((currentPage - 1) * maxDisplayCount));
                displayList[i][0] = empioyee.getEmployeeID();
                displayList[i][1] = empioyee.getLastName() + " " + empioyee.getFirstname();
                displayList[i][2] = calcAge(empioyee.getBirthday(), now) + "歳";
                displayList[i][3] = calcEngineerDate(empioyee.getEngineerDate());
                displayList[i][4] = empioyee.getAvailableLanguages();
            }
        } else {
            // 社員〇人分＋空きスペース
            for (int i = 0; i < displayCount; i++) {
                EmployeeInformation empioyee = tableEmployee.get(i + ((currentPage - 1) * maxDisplayCount));
                displayList[i][0] = empioyee.getEmployeeID();
                displayList[i][1] = empioyee.getLastName() + " " + empioyee.getFirstname();
                displayList[i][2] = calcAge(empioyee.getBirthday(), now) + "歳";
                displayList[i][3] = calcEngineerDate(empioyee.getEngineerDate());
                displayList[i][4] = empioyee.getAvailableLanguages();
            }
            for (int i = displayCount; i < maxDisplayCount; i++) {
                displayList[i][0] = "";
                displayList[i][1] = "";
                displayList[i][2] = "";
                displayList[i][3] = "";
                displayList[i][4] = "";
            }
        }
        return displayList;
    }

    /**
     * エンジニア歴を〇年△ヶ月に計算するメソッド
     * 
     * @param months   エンジニア歴(〇ヶ月表記)
     * @param years    〇年
     * @param remonths △ヶ月
     * @return 〇年△ヶ月
     * @author 下村
     */
    private String calcEngineerDate(int months) {
        int years = months / 12;
        int reremonths = months % 12;
        return years + "年" + reremonths + "ヶ月";
    }

    /**
     * 年齢を計算するメソッド（第１引数：誕生日、第2引数：現在日）
     * 
     * @param birthday 誕生日
     * @param now      現在日
     * @return 〇歳
     */
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

    private void selectFolder() {
        JFileChooser filechooser = new JFileChooser();
        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int selected = filechooser.showOpenDialog(filechooser);
        if (selected == JFileChooser.APPROVE_OPTION) {
            String directoryPath = filechooser.getSelectedFile().toString();
            showCreateCsvDialog(directoryPath);
        }
    }

    private void showCreateCsvDialog(String directory) {
        String[] label = { "出力", "キャンセル", "参照" };
        String message = "出力先を選択してください";
        int selectButton = dialog.ioConfirmation(message, directory, label);
        if (selectButton == 0) {
            MANAGER.printInfoLog("CSV出力を開始");
            CreateCsv createCsv = new CreateCsv();
            createCsv.createCsv(directory, selected);
            Thread createCsvThread = new Thread(createCsv);
            createCsvThread.start();
            viewTopScreen();
        } else if (selectButton == 1) {
            MANAGER.printInfoLog("CSV出力をキャンセル");
        } else if (selectButton == 2) {
            MANAGER.printInfoLog("CSV出力先を変更");
            selectFolder();
        }
    }


    // 入力文字数制限用フィルター（最大100文字）
    private static class TextLengthFilter extends DocumentFilter {
        private int maxLength;

        public TextLengthFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null)
                return;

            int currentLength = fb.getDocument().getLength();
            int newLength = currentLength + string.length();
            if (newLength <= maxLength) {
                super.insertString(fb, offset, string, attr);
            } else {
                int allowedLength = maxLength - currentLength;
                if (allowedLength > 0) {
                    super.insertString(fb, offset, string.substring(0, allowedLength), attr);
                }
                // それ以上は無視（入力しない）
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null)
                return;

            int currentLength = fb.getDocument().getLength();
            int newLength = currentLength - length + text.length();
            if (newLength <= maxLength) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                int allowedLength = maxLength - (currentLength - length);
                if (allowedLength > 0) {
                    super.replace(fb, offset, length, text.substring(0, allowedLength), attrs);
                }
                // それ以上は無視（入力しない）
            }
        }
    }
}
