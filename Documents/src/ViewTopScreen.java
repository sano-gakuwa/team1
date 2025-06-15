import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
    private EmployeeListOperator employeeListOperator;//検索機能　6/9追記

    // 記載順間違えると起動しなくなるから注意
    public ViewTopScreen() {
        frame.setTitle("一覧画面");
        engineerTable = new JTable();// 先にテーブルを初期化してから refreshTable を呼ぶ
        tableEmployee = EmployeeManager.employeeList;// JTablに表示用に社員情報リストからコピー
        employeeListOperator = new EmployeeListOperator(tableEmployee);//
        setupViewTopScreen();// 一覧画面の初期化
        refreshTable(); // 画面初期表示とデータ同期
    }

    private void setupViewTopScreen() {

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
        searchButton.addActionListener(e -> {
            // 6/8 検索フィールドの値を取得（topPanelのコンポーネント順と合わせて取得）
            String idQuery = ((JTextField)topPanel.getComponent(1)).getText();
            String nameQuery = ((JTextField)topPanel.getComponent(3)).getText();
            String ageQuery = ((JTextField)topPanel.getComponent(5)).getText();
            String engQuery = ((JTextField)topPanel.getComponent(7)).getText();
            String langQuery = ((JTextField)topPanel.getComponent(9)).getText();

            executeSearch(idQuery, nameQuery, ageQuery, engQuery, langQuery);
        });
        // centerPanel 取得
        JPanel centerWrapper = (JPanel) fullScreenPanel.getComponent(3);
        JPanel centerPanel = (JPanel) centerWrapper.getComponent(0);
        JPanel functionButtonsPanel = (JPanel) centerPanel.getComponent(0);
        JPanel employeeListPanel = (JPanel) centerPanel.getComponent(2);
        centerPanel.setOpaque(false);// 背景透過
        // ボタン配置
        functionButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        functionButtonsPanel.setOpaque(false); // 背景透過
        functionButtonsPanel.add(new JLabel("エンジニア一覧"));
        JButton addEmployeeButton = new JButton("新規");
        JButton loadButton = new JButton("読込");
        JButton templateButton = new JButton("テンプレート出力");
        JButton bulkSelectButton = new JButton("ページ内一括選択");

        // =============================================
        // 各種ボタンイベント設定（下記担当者記載）
        // =============================================

        // 「新規追加」ボタン押下後イベント※{}内追記お願いします
        addEmployeeButton.addActionListener(e -> {
            refreshUI();
            ViewAdditionScreen addition = new ViewAdditionScreen();
            addition.view();
        });
        functionButtonsPanel.add(addEmployeeButton);

        // 「読込」ボタン押下後イベント※{}内追記お願いします
        loadButton.addActionListener(e -> {
            selectFile();
        });
        functionButtonsPanel.add(loadButton);

        // 「テンプレート出力」ボタン押下後イベント
        templateButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("テンプレートファイルの保存先フォルダを選択してください");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // フォルダー選択モード

            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedDir = fileChooser.getSelectedFile();
                // 保存確認ダイアログ
                int saveConfirm = JOptionPane.showConfirmDialog(
                        null,
                        "この場所にテンプレートファイル「employee_template.csv」を保存しますか？",
                        "保存確認",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (saveConfirm != JOptionPane.YES_OPTION) {
                    return;
                }
                CsvConverter converter=new CsvConverter();
                converter.createTemplate(selectedDir);
            }
        });
        // ボタンをパネルに追加
        functionButtonsPanel.add(templateButton);
        // 選択画面（ViewSelectedScreen ）に遷移
        functionButtonsPanel.add(bulkSelectButton);
        bulkSelectButton.addActionListener(e -> {
            for (int i = 0; i < 10; i++) {
                selected.add((model.getValueAt(i, 0)).toString());
            }
            refreshUI();
            ViewSelectedScreen selectedScreen = new ViewSelectedScreen();
            selectedScreen.View(tableEmployee, selected, currentPage);
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
        employeeListOperator.searchAsync(
            idQuery, nameQuery, ageQuery, engQuery, langQuery,
            new EmployeeListOperator.SearchCallback() {
                @Override
                public void onSearchFinished(boolean success, List<EmployeeInformation> results, String errorMessage) {
                    if (success) {
                        tableEmployee = new ArrayList<>(results);
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(null, errorMessage, "検索失敗", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        );
    }
    


    /*
     * refreshTableメソッドはengineerTable のデータモデルを更新
     * JTable のインスタンスであり、setupEngineerList() メソッド内で初期化されていることを前提
     * EmployeeManager.getInitialData() は、最新の従業員データを2次元配列で返すメソッドであると仮定
     */
    public void refreshTable() {

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

        // 詳細ボタン設定（データがある場合のみ安全に処理） 6/1追加
        if (pageData.length > 0) {
            try {
                TableColumn detailColumn = engineerTable.getColumn("詳細");
                detailColumn.setCellRenderer(new ButtonRenderer());
            } catch (IllegalArgumentException e) {
                MANAGER.printErrorLog(e, "詳細列の設定失敗：");
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
                        MANAGER.LOGGER.info("社員番号が" + selectID + "の詳細ボタンが押されました");
                    }
                    if (selectID != "") {
                        EmployeeInformation selectedEmployee = null;// 詳細ボタンを押された行の社員情報
                        try {
                            Iterator<EmployeeInformation> employeeIterator = tableEmployee.iterator();
                            while (employeeIterator.hasNext()) {
                                EmployeeInformation employee = employeeIterator.next();
                                if (selectID == employee.employeeID) {
                                    selectedEmployee = employee;
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            MANAGER.printErrorLog(ex, "社員情報の取得に失敗");
                            return;
                        }
                        MANAGER.LOGGER.info("社員番号が" + selectID + "の社員情報を表示");
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
                    ArrayList<String> selectedIds = new ArrayList<>();
                    selectedIds.add(id);
                    // ViewSelectedScreen に切り替える前に ViewTopScreen の画面をクリア
                    refreshUI();
                    // ViewSelectedScreen に遷移
                    ViewSelectedScreen selectedScreen = new ViewSelectedScreen();
                    selectedScreen.View(tableEmployee, selectedIds, currentPage);
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

    public void View(int currentPage) {
        this.currentPage = currentPage;
        refreshTable(); // 画面初期表示とデータ同期
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
            displayCount = tableEmployee.size();// 社員数10以下
        } else if (currentPage * maxDisplayCount < tableEmployee.size()) {
            displayCount = maxDisplayCount;
        } else {
            displayCount = tableEmployee.size() - ((currentPage - 1) * maxDisplayCount);
        }
        for (int i = 0; i < displayCount; i++) {
            EmployeeInformation empioyee = tableEmployee.get(i + ((currentPage - 1) * maxDisplayCount));
            displayList[i][0] = empioyee.employeeID;
            displayList[i][1] = empioyee.lastName + " " + empioyee.firstname;
            displayList[i][2] = calcAge(empioyee.birthday, now);
            displayList[i][3] = empioyee.engineerDate;
            displayList[i][4] = empioyee.availableLanguages;
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
        int selectButton = JOptionPane.showOptionDialog(
                null,
                "以下のCSVファイルを読み込みます\n"
                        + "選択中" + selectedFile,
                "確認ダイアログ",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                label,
                null);
        if (selectButton == 0) {
            MANAGER.LOGGER.info("CSV読み込みを開始");
            CsvConverter csvConverter = new CsvConverter();
            csvConverter.readCsv(selectedFile);
        } else if (selectButton == 1) {
            MANAGER.LOGGER.info("CSV読み込みをキャンセル");
        } else if (selectButton == 2) {
            MANAGER.LOGGER.info("読み込むCSVを変更");
            selectFile();
        }
    }
}
