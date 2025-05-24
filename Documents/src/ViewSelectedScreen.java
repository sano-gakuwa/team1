import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.*;
import java.util.Date;

public class ViewSelectedScreen extends SetUpTopScreen {
    private JButton bulkSelectButton;// 一括選択ボタン
    private JButton bulkCancellationtButton;//一括解除ボタン
    private JButton createCsvButton;//CSV出力ボタン
    private JButton deleteButton;//削除ボタン

    private JTable engineerTable=new JTable();//社員情報表示欄
    private JTableHeader header;
    private String[] columnNames = { "社員ID", "氏名", "年齢", "エンジニア歴", "扱える言語", "詳細" };
    private Set<Integer> unsortableColumns;
    private Map<Integer, Integer> sortStates;
    private JLabel pageLabel = new JLabel("", SwingConstants.CENTER);//ページ数表示箇所
    public int currentPage = 1; //現在のページ数
    public int totalPages = 1;//最後のページ数
    private ArrayList<String> selected = new ArrayList<>();
    private DefaultTableModel model;
    private EmployeeManager manager=new EmployeeManager();

    public ViewSelectedScreen() {
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
        // ボタン配置
        functionButtonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        functionButtonsPanel.setOpaque(false); // 背景透過
        bulkCancellationtButton=new JButton("ページ内一括解除");
        bulkSelectButton = new JButton("ページ内一括選択");
        createCsvButton=new JButton("CSV出力");
        deleteButton=new JButton("削除");
        functionButtonsPanel.add(bulkCancellationtButton);
        functionButtonsPanel.add(bulkSelectButton);
        functionButtonsPanel.add(createCsvButton);
        functionButtonsPanel.add(deleteButton);
        bulkCancellationtButton.addActionListener(e->{
            //ページ内一括解除
            manager.LOGGER.info("ページ内一括解除ボタンが押されました");
            for(int row=0;row<10;row++){
                String select=(model.getValueAt(row,0)).toString();
                if(selected.contains(select)==true){
                    selected.remove(selected.indexOf(select));
                }
            }
            System.out.println(selected);
        });
        bulkSelectButton.addActionListener(e -> {
            //ページ内一括選択
            manager.LOGGER.info("ページ内一括選択ボタンが押されました");
            for(int row=0;row<10;row++){
                String select=(model.getValueAt(row,0)).toString();
                if(selected.contains(select)==false){
                    selected.add(select);
                }
            }
            System.out.println(selected);
        });
        createCsvButton.addActionListener(e->{
            //CSV出力
            manager.LOGGER.info("CSV出力ボタンが押されました");
        });
        deleteButton.addActionListener(e->{
            //削除
            manager.LOGGER.info("削除ボタンが押されました");
            EmployeeUpdater updater=new EmployeeUpdater();
            updater.delete(selected);
            refreshUI();
            ViewTopScreen top=new ViewTopScreen();
            top.View();
        });
        // テーブル構築
        Object[][] pageData = getPageData(currentPage, 10);
        // ソート対象外の列インデックス（「詳細」列）
        unsortableColumns = Set.of(5);

        // 従業員０名時の表示
        if (EmployeeManager.employeeList.size()==0) {
            showNoDataLabel(employeeListPanel);
        } else if(EmployeeManager.employeeList.size()>=1){
            // ページ数自動計算(10n+1でページ新規生成)、最大100ページ
            int totalEmployees = EmployeeManager.employeeList.size();
            totalPages = Math.min((totalEmployees + 9) / 10, 100);
            model = new DefaultTableModel(pageData, columnNames) {
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            engineerTable = new JTable(model) {
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component comp = super.prepareRenderer(renderer, row, column);
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        label.setHorizontalAlignment(SwingConstants.CENTER);
                        label.setOpaque(true);
                        if(selected.contains(engineerTable.getValueAt(row, 0))){
                            label.setBackground(Color.LIGHT_GRAY);
                        }else{
                            label.setBackground(Color.WHITE);
                        }
                        label.setForeground(Color.BLACK);
                        return label;
                    }
                    return comp;
                }
            };
        }else{
            manager.LOGGER.warning("表作成失敗");
        }

        // ヘッダーソート状態マップ（0:ー, 1:↑, 2:↓）
        sortStates = new HashMap<>();
        header = engineerTable.getTableHeader();
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
        engineerTable.setRowHeight(34);
        // 全列中央寄せ
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < engineerTable.getColumnCount(); i++) {
            engineerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        if (pageData.length != 0) {
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

    public void refreshTable() {
        //ページ数表示
        int totalEmployees = EmployeeManager.employeeList.size();
        totalPages = Math.min((totalEmployees + 9) / 10, 100);
        Object[][] pageData = getPageData(currentPage, 10);
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
        header.setReorderingAllowed(false);// テーブルの列移動を不許可にする。
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        engineerTable.setRowHeight(34);
        pageLabel.setText(currentPage + " / " + totalPages);
    }
    // マウスイベント設定
    private void setMouseEvent(){
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
        engineerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = engineerTable.rowAtPoint(e.getPoint());
                if (row >-1) {
                    // 選択された社員のIDを取得
                    String select=(model.getValueAt(row,0)).toString();
                    if(select==""){
                        // 空白行を選択
                        manager.LOGGER.info("空白行を選択");
                    }else if(selected.contains(select)==false){
                        // 未選択の社員情報の行をクリック
                        selected.add(select);
                        manager.LOGGER.info("社員ID:"+select+"を選択");
                        engineerTable.repaint();
                    }else if(selected.contains(select)==true){
                        // 選択済みの社員情報の行をクリック
                        selected.remove(selected.indexOf(select));
                        manager.LOGGER.info("社員ID:"+select+"を選択解除");
                        engineerTable.repaint();
                    }
                    if (selected.size()<=0) {
                        // 選択されている社員情報が0名分
                        refreshUI();
                        ViewTopScreen top=new ViewTopScreen();
                        top.View(currentPage);
                        manager.LOGGER.info("一覧画面へ遷移");
                    }
                }
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

    public void View(ArrayList<String> selected,int currentPage) {
        this.currentPage=currentPage;
        this.selected=selected;
        frame.setTitle("選択画面");
        setupEngineerList();
        refreshTable(); // 画面初期表示とデータ同期
        setMouseEvent();
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
            // 社員数10名以下
            displayCount = EmployeeManager.employeeList.size();
        } else if (currentPage * maxDisplayCount < EmployeeManager.employeeList.size()) {
            // 社員数10名以上で10名表示
            displayCount = maxDisplayCount;
        } else {
            // 社員数10名以上で10名未満表示
            displayCount = EmployeeManager.employeeList.size() - ((currentPage - 1) * maxDisplayCount);
        }
        if (displayCount == 10) {
            //10名分表示
            for (int i = 0; i < displayCount; i++) {
                EmployeeInformation empioyee = EmployeeManager.employeeList
                        .get(i + ((currentPage - 1) * maxDisplayCount));
                displayList[i][0] = empioyee.employeeID;
                displayList[i][1] = empioyee.lastName + " " + empioyee.firstname;
                displayList[i][2] = calcAge(empioyee.birthday, now);
                displayList[i][3] = empioyee.engineerDate;
                displayList[i][4] = empioyee.useLanguageDate;
            }
        } else {
            //社員〇人分＋空きスペース
            for (int i = 0; i < displayCount; i++) {
                EmployeeInformation empioyee = EmployeeManager.employeeList
                        .get(i + ((currentPage - 1) * maxDisplayCount));
                displayList[i][0] = empioyee.employeeID;
                displayList[i][1] = empioyee.lastName + " " + empioyee.firstname;
                displayList[i][2] = calcAge(empioyee.birthday, now);
                displayList[i][3] = empioyee.engineerDate;
                displayList[i][4] = empioyee.useLanguageDate;
            }
            for(int i=displayCount;i<maxDisplayCount;i++){
                displayList[i][0] = "";
                displayList[i][1] = "";
                displayList[i][2] = "";
                displayList[i][3] = "";
                displayList[i][4] = "";
            }
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
