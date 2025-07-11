import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

public class ViewAdditionScreen extends SetUpDetailsScreen {
    // UIコンポーネント（インスタンス変数）
    private JTextField employeeIdField;
    private JTextField rubyLastNameField, rubyFirstNameField;
    private JTextField lastNameField, firstNameField;
    // 生年月日
    private JComboBox<String> birthYearCombo;
    private JComboBox<String> birthMonthCombo;
    private JComboBox<String> birthDayCombo;
    // 入社年月日
    private JComboBox<String> joinYearCombo;
    private JComboBox<String> joinMonthCombo;
    private JComboBox<String> joinDayCombo;
    // エンジニア歴
    private JComboBox<String> engYearCombo = new JComboBox<>();
    private JComboBox<String> engMonthCombo = new JComboBox<>();
    private JPanel birthPanel = new JPanel();
    private JPanel joinPanel = new JPanel();
    private JPanel engPanel = new JPanel();
    // 扱える言語
    private JTextField availableLanguageField;
    private JTextArea careerArea, trainingArea, remarksArea;
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;
    private JButton saveButton, backButton;
    private final EmployeeManager MANAGER = new EmployeeManager();

    public ViewAdditionScreen() {
        setupViewAdditionScreen();
    }

    private void setupViewAdditionScreen() {
        frame.setTitle("エンジニア情報 新規追加画面");
        setupEmployeeId();
        setupNameFields();
        setupDateAndLanguageFields();
        setupCareer();
        setupSkills();
        setupTraining();
        setupRemarks();
        setupButtons();
    }

    // メイン画面の表示処理
    public void view() {
        frame.setVisible(true);
    }

    // 社員ID
    private void setupEmployeeId() {
        employeeIdField = placeholderTextField("01234xx");
        employeeIdField.setBounds(15, 5, 130, 30);
        idPanel.add(employeeIdField);
    }

    // 氏名（フリガナ + 氏名）
    private void setupNameFields() {
        rubyLastNameField = placeholderTextField("ヤマダ");
        rubyLastNameField.setBounds(15, 15, 195, 30);
        namePanel.add(rubyLastNameField);

        rubyFirstNameField = placeholderTextField("タロウ");
        rubyFirstNameField.setBounds(215, 15, 195, 30);
        namePanel.add(rubyFirstNameField);

        lastNameField = placeholderTextField("山田");
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(15, 55, 195, 40);
        namePanel.add(lastNameField);

        firstNameField = placeholderTextField("太郎");
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(215, 55, 195, 40);
        namePanel.add(firstNameField);
    }

    // upperPanelに要素追加（生年月日、入社年月、エンジニア歴、扱える言語）
    private void setupDateAndLanguageFields() {
        //現在の年月日
        LocalDate now = LocalDate.now();
        Integer[] nowInteger = { now.getYear(), now.getMonthValue(), now.getDayOfMonth() };
        // 生年月日
        birthdDayPanel.add(new JLabel("生年月日"), BorderLayout.NORTH);
        birthYearCombo = new JComboBox<>(yearModel(nowInteger));
        birthMonthCombo = new JComboBox<>(monthModel(nowInteger));
        birthDayCombo = new JComboBox<>(dayModel(birthYearCombo, birthMonthCombo, nowInteger));
        birthPanel.add(dateSelector(birthYearCombo, birthMonthCombo, birthDayCombo));
        birthPanel.setBackground(Color.WHITE);
        birthdDayPanel.add(birthPanel, BorderLayout.SOUTH);
        birthYearCombo.addItemListener(e -> {
            updateDayCombo(birthYearCombo, birthMonthCombo, birthDayCombo);
        });
        birthMonthCombo.addActionListener(e -> {
            updateDayCombo(birthYearCombo, birthMonthCombo, birthDayCombo);
        });
        // 入社年月日
        joiningDatePanel.add(new JLabel("入社年月"), BorderLayout.NORTH);
        joinYearCombo = new JComboBox<>(yearModel(nowInteger));
        joinMonthCombo = new JComboBox<>(monthModel(nowInteger));
        joinDayCombo = new JComboBox<>(dayModel(joinYearCombo, joinMonthCombo, nowInteger));
        joinPanel.add(dateSelector(joinYearCombo, joinMonthCombo, joinDayCombo));
        joiningDatePanel.add(joinPanel, BorderLayout.SOUTH);
        joinYearCombo.addItemListener(e -> {
            updateDayCombo(joinYearCombo, joinMonthCombo, joinDayCombo);
        });
        joinMonthCombo.addItemListener(e -> {
            updateDayCombo(joinYearCombo, joinMonthCombo, joinDayCombo);
        });
        // エンジニア歴
        engineerDatePanel.add(new JLabel("エンジニア歴"), BorderLayout.NORTH);
        engPanel.add(engineerDateSelector(engYearCombo, engMonthCombo));
        engineerDatePanel.add(engPanel, BorderLayout.SOUTH);
        // 扱える言語
        JLabel availableLanguagesLabel = new JLabel("扱える言語");
        availableLanguagesLabel.setBounds(0, -3, 100, 20);
        availableLanguagesPanel.add(availableLanguagesLabel);
        JPanel availableLanguageFieldPanel = new JPanel();
        availableLanguageFieldPanel.setBounds(0, 15, 190, 40);
        availableLanguageFieldPanel.setBackground(Color.LIGHT_GRAY);
        availableLanguageFieldPanel.setLayout(null);
        availableLanguageField = placeholderTextField("html・CSS");
        availableLanguageField.setBounds(5, 5, 180, 30);
        availableLanguageFieldPanel.add(availableLanguageField);
        availableLanguagesPanel.add(availableLanguageFieldPanel);
    }

    // middlePanelの要素追加-------------------------------------------------------
    /**
     * 経歴欄の設置
     * 
     * @author 下村
     */
    private void setupCareer() {
        careerPanel.add(createLabel("経歴", 0, 0), BorderLayout.NORTH);
        careerArea = new JTextArea(5, 30);
        careerArea.setLineWrap(true);
        placeholderTextArea("経歴", careerArea);
        JScrollPane careerScroll = new JScrollPane(careerArea);
        careerPanel.add(careerScroll, BorderLayout.CENTER);
    }

    /**
     * スキル欄の設置
     * 
     * @author 下村
     */
    private void setupSkills() {
        skillsPanel.add(createLabel("スキル", 0, 0), BorderLayout.NORTH);
        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        skillPanel.setBackground(Color.LIGHT_GRAY);
        techCombo = createScoreCombo();
        commCombo = createScoreCombo();
        attitudeCombo = createScoreCombo();
        leaderCombo = createScoreCombo();
        skillPanel.add(new JLabel("技術力"));
        skillPanel.add(techCombo);
        skillPanel.add(new JLabel("コミュニケーション能力"));
        skillPanel.add(commCombo);
        skillPanel.add(new JLabel("受講態度"));
        skillPanel.add(attitudeCombo);
        skillPanel.add(new JLabel("リーダーシップ"));
        skillPanel.add(leaderCombo);
        skillPanel.setBounds(0, 10, 360, 10);
        skillsPanel.add(skillPanel, BorderLayout.CENTER);
    }

    /**
     * 研修受講歴欄の設置
     * 
     * @author 下村
     */
    private void setupTraining() {
        trainingRecordsPanel.add(createLabel("研修受講歴", 0, 0), BorderLayout.NORTH);
        trainingArea = new JTextArea(5, 30);
        trainingArea.setLineWrap(true);
        placeholderTextArea("2000年4月1日株式会社XXXX入社", trainingArea);
        JScrollPane trainingScroll = new JScrollPane(trainingArea);
        trainingRecordsPanel.add(trainingScroll, BorderLayout.CENTER);
    }

    /**
     * 備考欄の設置
     * 
     * @author 下村
     */
    private void setupRemarks() {
        remarksPanel.add(createLabel("備考", 440, 340), BorderLayout.NORTH);
        remarksArea = new JTextArea(5, 30);
        remarksArea.setLineWrap(true);
        placeholderTextArea("特になし", remarksArea);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksPanel.add(remarksScroll, BorderLayout.CENTER);
    }

    /**
     * 保存・戻るボタンの設置
     * 
     * @author nishiyama
     */
    private void setupButtons() {
        // 戻るボタン（左）
        bottomPanel.setLayout(null);
        backButton = new JButton("< 一覧画面へ戻る");
        backButton.setBounds(0, 0, 140, 30);
        bottomPanel.add(backButton);
        backButton.addActionListener(e -> {
            int result = javax.swing.JOptionPane.showConfirmDialog(
                    null,
                    "現在の入力内容を破棄してもよろしいですか？",
                    "確認",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            if (result == javax.swing.JOptionPane.YES_OPTION) {
                refreshUI();
                ViewTopScreen top = new ViewTopScreen();
                top.View();
            } else if (result == JOptionPane.NO_OPTION) {
                // NO_OPTION の場合は何もしない（入力画面に留まる）
            }
        });
        // 保存ボタン（中央）
        saveButton = new JButton("保存");
        saveButton.setBounds(350, 0, 80, 30);
        saveButton.addActionListener(e -> {
            EmployeeInfoAddition addition = new EmployeeInfoAddition();
            if (addition.validateAdditionLock()) {
                // 新規追加のロックがかかっている場合
                JOptionPane.showMessageDialog(frame, "新規追加中です。しばらくお待ちください。", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }
            EmployeeInformation info = collectInputData();
            if (!MANAGER.validateNotNull(info)) {
                showErrorDialog("必須項目が入力されていません");
                showErrorMessageOnPanel("必須項目が入力されていません");
                return;
            }
            if(!MANAGER.validateEmployee(info)){
                showErrorDialog("社員情報の内容に誤りがあります");
                showErrorMessageOnPanel("社員情報の内容に誤りがあります");
                return;
            }
            if (!MANAGER.validateOverlappingEmployee(info)) {
                showErrorDialog("重複する社員IDが存在します");
                showErrorMessageOnPanel("重複する社員IDが存在します");
                return;
            }
            addition.addition(info);
            Thread additionThread = new Thread(addition);
            additionThread.start();
            refreshUI();
            ViewTopScreen top = new ViewTopScreen();
            top.View();
        });
        bottomPanel.add(saveButton);
    }

    // 画面構成で呼び出すメソッド-------------------------------------------------------
    /**
     * ラベル生成
     * 
     * @param title カテゴリー名
     * @param x     横幅
     * @param y     縦幅
     * @return カテゴリー表示用の JLabel コンポネント
     * @author nishiyama
     */
    private JLabel createLabel(String title, int x, int y) {
        JLabel label = new JLabel(title);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    /**
     * スキルスコア用コンボボックス生成
     * 
     * @return スキルスコア選択用の JComboBox<String> コンポネント
     * @author nishiyama
     */
    private JComboBox<String> createScoreCombo() {
        String[] scores = { "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0" };
        JComboBox<String> comboBox = new JComboBox<>(scores);
        return comboBox;
    }

    private DefaultComboBoxModel<String> yearModel(Integer nowInteger[]) {
        // 年のコンボボックス設定
        String[] yearString = {};
        DefaultComboBoxModel<String> yearModel = new DefaultComboBoxModel<>(yearString);
        for (int i = nowInteger[0] - 100; i <= nowInteger[0]; i++) {
            yearModel.addElement(i+"年");
        }
        return yearModel;
    }

    private DefaultComboBoxModel<String> monthModel(Integer nowInteger[]) {
        // 月のコンボボックス設定
        String[] monthString = {};
        DefaultComboBoxModel<String> monthModel = new DefaultComboBoxModel<>(monthString);
        for (int i = 1; i <= 12; i++) {
            monthModel.addElement(i+"月");
        }
        return monthModel;
    }

    private DefaultComboBoxModel<String> dayModel(JComboBox<String> yearBox, JComboBox<String> monthBox,
            Integer nowInteger[]) {
        // 日のコンボボックス設定
        String yearsString=(String)yearBox.getSelectedItem();
        yearsString=yearsString.replace("年","");
        int year = Integer.parseInt(yearsString);
        String mounthString=(String)monthBox.getSelectedItem();
        mounthString=mounthString.replace("月","");
        int month = Integer.parseInt(mounthString);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] dayString = {};
        DefaultComboBoxModel<String> dayModel = new DefaultComboBoxModel<>(dayString);
        for (int i = 1; i <= maxDay; i++) {
            dayModel.addElement(i+"日");
        }
        return dayModel;
    }

    /**
     * 年月（必要に応じて日）を選択するためのセレクターパネルを作成。
     * 
     * @param yearBox  年の JComboBox の参照を格納する配列（長さ1の配列）
     * @param monthBox 月の JComboBox の参照を格納する配列（長さ1の配列）
     * @param dayBox   日の JComboBox の参照を格納する配列（長さ1の配列、includeDay=true 時のみ使用）
     * @return 年月（＋日）選択用の JPanel コンポネント
     * @author nishiyama
     */
    private JPanel dateSelector(JComboBox<String> yearBox, JComboBox<String> monthBox, JComboBox<String> dayBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        Dimension size = new Dimension(210, 40);
        int wrapperWidth = size.width;
        int wrapperHeight = size.height;
        panel.setPreferredSize(new Dimension(wrapperWidth, wrapperHeight));
        panel.setMaximumSize(new Dimension(size.width, wrapperHeight));
        panel.setBackground(Color.LIGHT_GRAY);
        yearBox.setSize(1,1);
        panel.add(yearBox);
        panel.add(monthBox);
        panel.add(dayBox);
        return panel;
    }

    /**
     * エンジニア歴を表示させるパネルの生成
     * 
     * @param yearBox  何年か表示するコンボボックス
     * @param monthBox 何か月か表示するコンボボックス
     * @return エンジニア歴を表示するコンボボックス等を載せたパネル
     * @author 下村
     */
    private JPanel engineerDateSelector(JComboBox<String> yearBox, JComboBox<String> monthBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        Dimension size = new Dimension(200, 40);
        int wrapperWidth = size.width + 15;
        int wrapperHeight = size.height;
        panel.setPreferredSize(new Dimension(wrapperWidth, size.height));
        panel.setMaximumSize(new Dimension(size.width, wrapperHeight));
        panel.setBackground(Color.LIGHT_GRAY);
        // 年のコンボボックス設定
        String[] yearString = {};
        DefaultComboBoxModel<String> yearModel = new DefaultComboBoxModel<>(yearString);
        for (int i = 0; i < 50; i++) {
            yearModel.addElement(i+"年");
        }
        yearBox = new JComboBox<>(yearModel);
        // 月のコンボボックス設定
        String[] monthString = {};
        DefaultComboBoxModel<String> monthModel = new DefaultComboBoxModel<>(monthString);
        for (int i = 0; i <= 11; i++) {
            monthModel.addElement(i+"ヵ月");
        }
        monthBox = new JComboBox<>(monthModel);
        panel.add(yearBox);
        panel.add(monthBox);
        return panel;
    }

    /**
     * 年と月の選択に応じて、指定された日の JComboBox を更新
     * 月ごとの最大日数に基づいて日数を再構築
     * 
     * @param yearCombo  年の JComboBox
     * @param monthCombo 月の JComboBox
     * @param dayCombo   日の JComboBox（再構築対象）
     * @author nishiyama
     */
    private void updateDayCombo(JComboBox<String> yearBox, JComboBox<String> monthBox, JComboBox<String> dayBox) {
        String yearsString=(String)yearBox.getSelectedItem();
        yearsString=yearsString.replace("年","");
        int year = Integer.parseInt(yearsString);
        String mounthString=(String)monthBox.getSelectedItem();
        mounthString=mounthString.replace("月","");
        int month = Integer.parseInt(mounthString);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayBox.removeAllItems();
        for (int i = 1; i <= maxDay; i++) {
            dayBox.addItem(i+"日");
        }
    }

    /**
     * プレースホルダー付きの JTextField を作成。
     * ユーザーがフィールドにフォーカスすると、プレースホルダーが消え入力可能。
     * フォーカスが外れ、入力が空の場合は再びプレースホルダーが表示される。
     * 
     * @param placeholder 初期表示されるプレースホルダーテキスト
     * @return プレースホルダー付きの JTextField オブジェクト
     * @author nishiyama
     */
    private JTextField placeholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder, 7);
        textField.setForeground(Color.GRAY);
        // 初期状態の判定用フラグ
        final boolean[] showingPlaceholder = { true };
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (showingPlaceholder[0]) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                    showingPlaceholder[0] = false;
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                    showingPlaceholder[0] = true;
                }
            }
        });
        return textField;
    }

    /**
     * プレースホルダー付きの JTextArea を作成。
     * ユーザーがフィールドにフォーカスすると、プレースホルダーが消え入力可能。
     * フォーカスが外れ、入力が空の場合は再びプレースホルダーが表示される。
     * 
     * @param placeholder 初期表示されるプレースホルダーテキスト
     * @param textArea    追加するテキストエリア
     * @return プレースホルダー付きの JTextArea オブジェクト
     * @author nishiyama
     */
    private JTextArea placeholderTextArea(String placeholder, JTextArea textArea) {
        textArea.setText(placeholder);
        textArea.setForeground(Color.GRAY);
        // フォーカスが当たった時
        textArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholder)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            // フォーカスが外れた時
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText(placeholder);
                    textArea.setForeground(Color.GRAY);
                }
            }
        });
        return textArea;
    }
    // 入力データ取得＆保存メソッド呼び出し------------------------------------------

    /**
     * GUIの各入力フィールドから情報を取得し、EmployeeInformation オブジェクトにまとめて返却。
     * 
     * @return EmployeeInformation:入力された社員情報を格納したインスタンス
     *         取得中にエラーが発生した場合は null を返却し、エラーメッセージを表示
     * @throws Exception 例外発生時、 showValidationError("データ取得中にエラーが発生しました")
     *                   を呼び出し、ユーザーに通知
     * @author nishiyama
     */
    public EmployeeInformation collectInputData() {
        try {
            EmployeeInformation employee = new EmployeeInformation();
            // 各フィールドから値を取得
            // 社員ID、氏名、フリガナ、誕生日、入社年月、エンジニア歴、扱える言語、経歴、研修受講歴、スキルスコア
            // 、受講態度、コミュニケーション能力、リーダーシップ、備考
            employee.setEmployeeID(getFieldValue(employeeIdField, "01234xx"));
            employee.setlastName(getFieldValue(lastNameField, "山田"));
            employee.setFirstname(getFieldValue(firstNameField, "太郎"));
            employee.setRubyLastName(getFieldValue(rubyLastNameField, "ヤマダ"));
            employee.setRubyFirstname(getFieldValue(rubyFirstNameField, "タロウ"));
            employee.setBirthday(getDateFromSelector(birthPanel));
            employee.setJoiningDate(getDateFromSelector(joinPanel));
            employee.setEngineerDate(getYearMonthFromSelector(engPanel)); // 月数換算
            employee.setAvailableLanguages(getFieldValue(availableLanguageField, "html・CSS"));
            employee.setCareerDate(getFieldValue(careerArea, "XXXXXXX"));
            employee.setTrainingDate(getFieldValue(trainingArea, "2000年4月1日株式会社XXXX入社"));
            employee.setSkillPoint(parseScore(techCombo));
            employee.setAttitudePoint(parseScore(attitudeCombo));
            employee.setCommunicationPoint(parseScore(commCombo));
            employee.setLeadershipPoint(parseScore(leaderCombo));
            employee.setRemarks(getFieldValue(remarksArea, "特になし"));
            employee.setUpdatedDay(new Date());
            return employee;
        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "データ取得中にエラーが発生しました");
            showErrorDialog("データ取得中にエラーが発生しました");
            return null;
        }
    }

    /**
     * プレースホルダーと同じ値が入力されていた場合は空文字列を返却。それ以外は実際の入力値を返却。
     * 
     * @param field       入力フィールド (JTextField や JTextAreaなど)
     * @param placeholder 初期表示されるプレースホルダー文字列
     * @return 実際の入力値または空文字列
     * @author nishiyama
     */
    private String getFieldValue(JTextComponent field, String placeholder) {
        String text = field.getText();
        return text.equals(placeholder) ? "" : text;
    }

    /**
     * 年・月・日が選択可能な JPanel から Date 型の日時を構築して返す。
     * 
     * @param panel Jpanelに配置されたJcomboBox
     *              0番目: 年の JComboBox
     *              1番目: 月の JComboBox
     *              2番目（存在する場合）: 日の JComboBox
     * @return Date: 選択された年月日を表す java.util.Date オブジェクト
     * @author nishiyama
     */
    private Date getDateFromSelector(JPanel panel) {
        JPanel datePanel = (JPanel) panel.getComponent(0);
        JComboBox<?> yearBox = (JComboBox<?>) datePanel.getComponent(0);
        JComboBox<?> monthBox = (JComboBox<?>) datePanel.getComponent(1);
        JComboBox<?> dayBox=(JComboBox<?>) datePanel.getComponent(2);
        // 年、月、日を取得
        String yearsString=(String)yearBox.getSelectedItem();
        yearsString=yearsString.replace("年","");
        int year = Integer.parseInt(yearsString);
        String mounthString=(String)monthBox.getSelectedItem();
        mounthString=mounthString.replace("月","");
        int month = Integer.parseInt(mounthString);
        String dayString=(String)dayBox.getSelectedItem();
        dayString=dayString.replace("日","");
        int day =Integer.parseInt(dayString);
        // 日付を Calendar で構築し、Date 型に変換して返す
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    /**
     * 経歴年数のような年＋月情報を月単位に変換して返す
     * 
     * @param panel Jpanelに配置されたJcomboBox
     *              0番目: 年の JComboBox
     *              1番目: 月の JComboBox
     * @return int: 総月数（年×12 + 月）
     * @author nishiyama
     */
    private int getYearMonthFromSelector(JPanel panel) {
        JPanel datePanel = (JPanel) panel.getComponent(0);
        JComboBox<?> yearBox = (JComboBox<?>) datePanel.getComponent(0);
        JComboBox<?> monthBox = (JComboBox<?>) datePanel.getComponent(1);
        String yearsString=(String)yearBox.getSelectedItem();
        yearsString=yearsString.replace("年","");
        int year = Integer.parseInt(yearsString);
        String mounthString=(String)monthBox.getSelectedItem();
        mounthString=mounthString.replace("ヵ月","");
        int month = Integer.parseInt(mounthString);
        return year * 12 + month;
    }

    /**
     * JComboBox から選択された数値文字列を double に変換して返す。
     * 
     * @param combo スコアが格納された JComboBox<String>
     * @return double: スコア値
     * @author nishiyama
     */
    private double parseScore(JComboBox<String> combo) {
        return Double.parseDouble((String) combo.getSelectedItem());
    }

    // ダイアログ関係-------------------------------------------------------
    /**
     * エラーメッセージを保存ボタン上部に表示。
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    public void showErrorMessageOnPanel(String message) {
        errorPanel.removeAll();
        JLabel errorLabel = new JLabel(message);
        errorLabel.setBounds(0,0,750, 26);
        errorLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorPanel.add(errorLabel);
        errorPanel.repaint();
    }

    /**
     * エラー表示用に用意したダイアログに文言表示させる
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }
}
