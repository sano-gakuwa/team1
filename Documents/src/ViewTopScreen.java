import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.text.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ViewTopScreen extends SetUpTopScreen {
    private JTable engineerTable;// 社員情報表示欄
    private JLabel pageLabel = new JLabel("", SwingConstants.CENTER);// ページ数表示箇所
    public int currentPage = 1; // 現在のページ数
    public int totalPages = 1;// ページ数
    private ArrayList<String> selected = new ArrayList<>();// 選択された社員情報
    private DefaultTableModel model;// JTablの表示モデル
    private ArrayList<EmployeeInformation> tableEmployee = null;// JTablに表示する社員情報
    private final EmployeeManager MANAGER = new EmployeeManager();// 社員情報の管理用
    private EmployeeListOperator employeeListOperator;// 検索機能 6/9追記
    // 検索中オーバーレイ表示用パネル・ラベル・ボタンのフィールド宣言
    private JPanel functionButtonsPanel;
    private JPanel searchOverlayPanel;
    private JLabel searchingLabel;
    private JButton cancelSearchButton;
    private JButton clearSearchResultButton;
    private JPanel employeeListPanel;
    private ViewDialog dialog = new ViewDialog();
    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField engField;
    private JTextField langField;

    // 記載順間違えると起動しなくなるから注意
    public ViewTopScreen() {
        frame.setTitle("一覧画面");
        engineerTable = new JTable();

        // ★中身を必ずコピー
        tableEmployee = new ArrayList<>(EmployeeManager.employeeList);
        employeeListOperator = new EmployeeListOperator(tableEmployee);

        setupViewTopScreen();
        refreshTable(); // ←ここで0件ではなく表示される
    }

    private void setupViewTopScreen() {

        // 検索バー
        JPanel topWrapper = (JPanel) fullScreenPanel.getComponent(1);
        JPanel topPanel = (JPanel) topWrapper.getComponent(0);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        topPanel.setOpaque(false);

        // それぞれの検索フィールドを変数化して保持
        idField = new JTextField(5);
        nameField = new JTextField(5);
        ageField = new JTextField(5);
        engField = new JTextField(5);
        langField = new JTextField(5);

        // ラベル + フィールドを追加
        topPanel.add(new JLabel("社員ID"));
        topPanel.add(idField);
        topPanel.add(new JLabel("氏名"));
        topPanel.add(nameField);
        topPanel.add(new JLabel("年齢"));
        topPanel.add(ageField);
        topPanel.add(new JLabel("歳"));
        topPanel.add(new JLabel("エンジニア歴"));
        topPanel.add(engField);
        topPanel.add(new JLabel("扱える言語"));
        topPanel.add(langField);

        // 入力制限フィルター（最大100文字）をそれぞれ適用
        ((AbstractDocument) idField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        ((AbstractDocument) ageField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        ((AbstractDocument) engField.getDocument()).setDocumentFilter(new TextLengthFilter(100));
        ((AbstractDocument) langField.getDocument()).setDocumentFilter(new TextLengthFilter(100));

        // 検索ボタンの設定
        JButton searchButton = new JButton("検索");
        topPanel.add(searchButton);
        searchButton.setBackground(new Color(30, 144, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        searchButton.addActionListener(e -> {
            if (searchOverlayPanel == null) {
                setupSearchOverlay(); // 初期化
            }
            showSearchOverlay();

            // 検索キーワード取得
            String idQuery = idField.getText();
            String nameQuery = nameField.getText();
            String ageQuery = ageField.getText();
            String engQuery = engField.getText();
            String langQuery = langField.getText();

            executeSearch(idQuery, nameQuery, ageQuery, engQuery, langQuery);
        });

        // centerPanel 取得
        JPanel centerWrapper = (JPanel) fullScreenPanel.getComponent(3);
        JPanel centerPanel = (JPanel) centerWrapper.getComponent(0);
        functionButtonsPanel = (JPanel) centerPanel.getComponent(0);
        employeeListPanel = (JPanel) centerPanel.getComponent(2);
        centerPanel.setOpaque(false);// 背景透過
        // ボタン配置
        functionButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        functionButtonsPanel.setOpaque(false); // 背景透過
        functionButtonsPanel.add(new JLabel("エンジニア一覧"));
        JButton addEmployeeButton = new JButton("新規");
        functionButtonsPanel.add(addEmployeeButton);// 検索クリアボタン出現後の配置ずれ防止
        JButton loadButton = new JButton("読込");
        functionButtonsPanel.add(loadButton);
        JButton templateButton = new JButton("テンプレート出力");
        functionButtonsPanel.add(templateButton);
        JButton bulkSelectButton = new JButton("ページ内一括選択");
        functionButtonsPanel.add(bulkSelectButton);
        clearSearchResultButton = new JButton("検索クリア");
        clearSearchResultButton.setVisible(false); // 検索後のみ表示
        functionButtonsPanel.add(clearSearchResultButton);

        // =============================================
        // 各種ボタンイベント設定（下記担当者記載）
        // =============================================

        // 「新規追加」ボタン押下後イベント※{}内追記お願いします
        addEmployeeButton.addActionListener(e -> {
            refreshUI();
            ViewAdditionScreen addition = new ViewAdditionScreen();
            MANAGER.printInfoLog("追加画面表示");
            addition.view();
        });

        // 「読込」ボタン押下後イベント※{}内追記お願いします
        loadButton.addActionListener(e -> {
            ReadCsv readCsv = new ReadCsv();
            // CSV出力中のロックがかかっているか確認
            if (readCsv.validateReadCsvLock()) {
                // CSV読み込みのロックがかかっている場合
                dialog.viewWarningDialog("CSV読み込み中です。しばらくお待ちください。");
                return;
            }
            selectFile();
        });

        // 「テンプレート出力」ボタン押下後イベント
        templateButton.addActionListener(e -> {
            CreateTemplate createTemplate = new CreateTemplate();
            if (createTemplate.validateCreateTemplateLock()) {
                // CSVテンプレート出力のロックがかかっている場合
                dialog.viewWarningDialog("CSVテンプレート出力中です。しばらくお待ちください。");
                return;
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("テンプレートファイルの保存先フォルダを選択してください");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // フォルダー選択モード
            int userSelection = fileChooser.showOpenDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedDir = fileChooser.getSelectedFile();
                // 保存確認ダイアログ
                String message = "この場所にテンプレートファイル「employee_template.csv」を保存しますか？";
                String title = "保存確認";
                int saveConfirm = dialog.questionConfirmation(message, title);
                if (saveConfirm != 0) {
                    return;
                }
                // テンプレート作成処理
                createTemplate.createTemplate(selectedDir);
                Thread templateThread = new Thread(createTemplate);
                templateThread.start();
            }
        });

        // 選択画面（ViewSelectedScreen ）に遷移
        bulkSelectButton.addActionListener(e -> {
            for (int i = 0; i < 10; i++) {
                selected.add((model.getValueAt(i, 0)).toString());
            }
            refreshUI();
            viewSelectedScreen();
        });
        // 検索結果クリア（検索後のみ表示）
        clearSearchResultButton.addActionListener(e -> {
            tableEmployee = new ArrayList<>(EmployeeManager.employeeList); // 全件表示に戻す
            employeeListOperator.setEmployeeList(tableEmployee);
            currentPage = 1;
            refreshTable();
            // 検索欄をクリア（← ここを追加）
            ((JTextField) topPanel.getComponent(1)).setText("");
            ((JTextField) topPanel.getComponent(3)).setText("");
            ((JTextField) topPanel.getComponent(5)).setText("");
            ((JTextField) topPanel.getComponent(7)).setText("");
            ((JTextField) topPanel.getComponent(9)).setText("");
            clearSearchResultButton.setVisible(false); // ボタン非表示に戻す
            MANAGER.printInfoLog("検索結果クリア：全件表示に戻しました");
        });
        // テーブル構築
        String[] columnNames = { "社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細" };
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
                    return column == 5;
                }
            };
            engineerTable.setModel(tableModel);
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

    // 検索処理（検索ボタン押下時に呼ばれる）
    private void executeSearch(String idQuery, String nameQuery, String ageQuery, String engQuery, String langQuery) {
        if (searchOverlayPanel == null)
            setupSearchOverlay();
        showSearchOverlay();
        employeeListOperator.searchAsync(idQuery, nameQuery, ageQuery, engQuery, langQuery,
                new EmployeeListOperator.SearchCallback() {
                    @Override
                    public void onSearchFinished(boolean success, List<EmployeeInformation> results,
                            String errorMessage) {
                        SwingUtilities.invokeLater(() -> {
                            hideSearchOverlay();
                            if (success) {
                                currentPage = 1;
                                employeeListOperator.setEmployeeList(EmployeeManager.employeeList);
                                tableEmployee = new ArrayList<>(results);
                                refreshTable();
                            } else {
                                dialog.viewErrorDialog(errorMessage);
                            }
                        });
                    }
                });
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
        String[] columnNames = { "社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細" };

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
                label.setBackground(Color.WHITE); // 選択しても背景白のまま
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

        // 詳細ボタン設定（データがある場合のみ安全に処理） 6/1追加
        if (pageData.length > 0) {
            try {
                TableColumn detailColumn = engineerTable.getColumn("詳細");
                detailColumn.setCellRenderer(new ButtonRenderer());
            } catch (IllegalArgumentException e) {
                MANAGER.printExceptionLog(e, "詳細列の設定失敗：");
            }
        }
        // 表示更新の後で、マウスイベント登録メソッドを呼び出す
        setupTableClickEvent(model);
    }

    // 従業員欄押下後ViewSelectedScreen に遷移。マウスクリックイベントをテーブルに設定、クリック時の社員IDを取得
    private void setupTableClickEvent(DefaultTableModel model) {
        // 既存のマウスリスナーを削除（多重登録防止）
        for (MouseListener listener : engineerTable.getMouseListeners()) {
            engineerTable.removeMouseListener(listener);
        }
        engineerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = engineerTable.rowAtPoint(e.getPoint());// クリックされた行
                int column = engineerTable.columnAtPoint(e.getPoint());// クリックされた列
                String columnName = engineerTable.getColumnName(column);// クリックされた列のヘッダー名
                // クリックされた列が詳細か判断
                if (columnName.equals("詳細")) {
                    String selectID = "";// 詳細ボタンを押された行の社員ID
                    if (model.getValueAt(row, 0) != null) {
                        selectID = (model.getValueAt(row, 0)).toString();
                        MANAGER.printInfoLog("社員番号が" + selectID + "の詳細ボタンが押されました");
                    }
                    if (selectID != "") {
                        EmployeeInformation selectedEmployee = null;// 詳細ボタンを押された行の社員情報
                        try {
                            Iterator<EmployeeInformation> employeeIterator = tableEmployee.iterator();
                            while (employeeIterator.hasNext()) {
                                EmployeeInformation employee = employeeIterator.next();
                                if (selectID == employee.getEmployeeID()) {
                                    selectedEmployee = employee;
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            MANAGER.printExceptionLog(ex, "社員情報の取得に失敗");
                            return;
                        }
                        MANAGER.printInfoLog("社員番号が" + selectID + "の社員情報を表示");
                        // ここに詳細画面表示メソッド実装よろしく！！
                        // 詳細画面を開く処理
                        refreshUI();
                        ViewDetailsScreen detailsScreen = new ViewDetailsScreen();
                        detailsScreen.view(selectedEmployee); // 詳細画面の表示メソッド呼び出し
                    }
                    return;
                }

                if (row >= 0) {
                    String id = model.getValueAt(row, 0).toString();
                    selected.add(id);
                    // ViewSelectedScreen に切り替える前に ViewTopScreen の画面をクリア
                    refreshUI();
                    viewSelectedScreen();
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

    // ----------------------
    // 下村作成部分↓
    public void View() {
        frame.setVisible(true);
    }

    private void viewSelectedScreen() {
        // ViewSelectedScreen に遷移
        String[] searchKeyWord = new String[5];
        // 検索キーワード取得
        searchKeyWord[0] = idField.getText();
        searchKeyWord[1] = nameField.getText();
        searchKeyWord[2] = ageField.getText();
        searchKeyWord[3] = engField.getText();
        searchKeyWord[4] = langField.getText();
        ViewSelectedScreen selectedScreen = new ViewSelectedScreen();
        //検索後か確認
        if(clearSearchResultButton.isVisible() ){
            selectedScreen.setSearchKeyWord(searchKeyWord);
        }
        selectedScreen.View(tableEmployee, selected, currentPage);
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
            displayCount = tableEmployee.size();// 社員数10以下
        } else if (currentPage * maxDisplayCount < tableEmployee.size()) {
            displayCount = maxDisplayCount;
        } else {
            displayCount = tableEmployee.size() - ((currentPage - 1) * maxDisplayCount);
        }
        for (int i = 0; i < displayCount; i++) {
            EmployeeInformation empioyee = tableEmployee.get(i + ((currentPage - 1) * maxDisplayCount));
            displayList[i][0] = empioyee.getEmployeeID();
            displayList[i][1] = empioyee.getLastName() + " " + empioyee.getFirstname();
            displayList[i][2] = calcAge(empioyee.getBirthday(), now) + "歳";

            // 修正点：エンジニア歴を「○年○ヶ月」に変換して代入
            displayList[i][3] = empioyee.getEngineerDate();

            displayList[i][4] = empioyee.getAvailableLanguages();
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

    private void selectFile() {
        JFileChooser filechooser = new JFileChooser();
        filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int selected = filechooser.showOpenDialog(filechooser);
        if (selected == JFileChooser.APPROVE_OPTION) {
            String selectedFilePath = filechooser.getSelectedFile().toString();
            showCreateCsvDialog(selectedFilePath);
        }
    }

    private void showCreateCsvDialog(String selectedFile) {
        String[] label = { "読み込み", "キャンセル", "参照" };
        String message = "以下のCSVファイルを読み込みます";
        int selectButton = dialog.ioConfirmation(message, selectedFile, label);
        if (selectButton == 0) {
            MANAGER.printInfoLog("CSV読み込みを開始");
            ReadCsv readCsv = new ReadCsv();
            readCsv.readCsv(selectedFile);
            Thread readCsvThread = new Thread(readCsv);
            readCsvThread.start();
        } else if (selectButton == 1) {
            MANAGER.printInfoLog("CSV読み込みをキャンセル");
        } else if (selectButton == 2) {
            MANAGER.printInfoLog("読み込むCSVを変更");
            selectFile();
        }
    }

    // 検索中オーバーレイを準備するメソッド（setupViewTopScreenの後かクラス末尾に配置推奨）
    private void setupSearchOverlay() {
        searchOverlayPanel = new JPanel();
        searchOverlayPanel.setLayout(null);
        searchOverlayPanel.setBackground(new Color(0, 0, 0, 120)); // 半透明黒
        searchOverlayPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        searchOverlayPanel.setVisible(false);

        searchingLabel = new JLabel("検索中…", SwingConstants.CENTER);
        searchingLabel.setForeground(Color.WHITE);
        searchingLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        searchingLabel.setBounds((frame.getWidth() - 200) / 2, (frame.getHeight() - 50) / 2, 200, 50);
        searchOverlayPanel.add(searchingLabel);

        cancelSearchButton = new JButton("検索終了");
        cancelSearchButton.setBounds(frame.getWidth() - 130, 10, 110, 30);
        cancelSearchButton.addActionListener(e -> {
            hideSearchOverlay();
            tableEmployee = new ArrayList<>(EmployeeManager.employeeList);
            refreshTable();
        });
        searchOverlayPanel.add(cancelSearchButton);

        // 最前面に表示するため layeredPane に追加
        frame.getLayeredPane().add(searchOverlayPanel, Integer.valueOf(Integer.MAX_VALUE));
    }

    // 検索中オーバーレイ表示メソッド
    private void showSearchOverlay() {
        searchOverlayPanel.setVisible(true);
        MANAGER.printInfoLog("検索中画面活性");
        searchOverlayPanel.repaint();
        MANAGER.printInfoLog("検索中画面表示");
    }

    // 検索中オーバーレイ非表示メソッド
    private void hideSearchOverlay() {
        searchOverlayPanel.setVisible(false);
        MANAGER.printInfoLog("検索中画面非活性");
        if (clearSearchResultButton != null) {
            clearSearchResultButton.setVisible(true);
            MANAGER.printInfoLog("検索クリアボタン活性");
            functionButtonsPanel.revalidate();
            functionButtonsPanel.repaint();
            MANAGER.printInfoLog("検索中画面非表示");
        } else {
            MANAGER.printErrorLog("検索クリアボタン未定義");
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
