import java.awt.*; // 画面部品やレイアウト関連のクラスを読み込み
import java.awt.event.*; // ボタンやコンボボックスのイベント処理に使用
import java.time.LocalDate; // 現在の日付などの取得に使用
import java.util.*; // Date や Calendar などのユーティリティ

import javax.swing.*; // Swingライブラリ（UI構築）
import javax.swing.text.JTextComponent; // JTextField / JTextArea を共通操作するために使用

/**
 * エンジニア情報を表示する画面クラス（編集不可モード）
 */
public class ViewEditScreen extends SetUpDetailsScreen {

    // ===== 各種UI部品の定義 =====

    // 社員ID入力欄
    private JTextField employeeIdField;
    // 氏名のフリガナ（姓・名）
    private JTextField rubyLastNameField, rubyFirstNameField;
    // 氏名の漢字（姓・名）
    private JTextField lastNameField, firstNameField;

    // 生年月日選択用のコンボボックス（年・月・日）
    private JComboBox<Integer> birthYearCombo = new JComboBox<>();
    private JComboBox<Integer> birthMonthCombo = new JComboBox<>();
    private JComboBox<Integer> birthDayCombo = new JComboBox<>();

    // 入社年月日のコンボボックス（年・月・日）
    private JComboBox<Integer> joinYearCombo = new JComboBox<>();
    private JComboBox<Integer> joinMonthCombo = new JComboBox<>();
    private JComboBox<Integer> joinDayCombo = new JComboBox<>();

    // エンジニア歴のコンボボックス（年・月）
    private JComboBox<Integer> engYearCombo = new JComboBox<>();
    private JComboBox<Integer> engMonthCombo = new JComboBox<>();

    // 日付パネル（JPanel）設定
    private JPanel birthPanel = new JPanel();
    private JPanel joinPanel = new JPanel();
    private JPanel engPanel = new JPanel();

    // 扱える言語（テキスト入力）
    private JTextField availableLanguageField;

    // 経歴・研修・備考の複数行入力欄
    private JTextArea careerArea, trainingArea, remarksArea;

    // スキルスコア用コンボボックス
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;

    // ボタン（保存・戻る）
    private JButton saveButton, backButton;

    // 社員情報を管理するクラス
    private final EmployeeManager MANAGER = new EmployeeManager();

    // 現在表示中の社員情報（編集対象）
    private EmployeeInformation employeeInformation;

    /**
     * コンストラクタ：エンジニア情報を受け取る
     */
    public ViewEditScreen() {
        // 画面タイトルの設定
        frame.setTitle("エンジニア情報 編集画面");
    }

    // 編集画面の構成要素をまとめて初期化する
    private void setupEditScreen() {
        setupEmployeeId();           // 社員ID欄を設定
        setupNameFields();           // 氏名欄（漢字・フリガナ）を設定
        setupDateAndLanguageFields();// 生年月日、入社年月日、言語欄を設定
        setupCareer();               // 経歴欄の設定
        setupSkills();               // スキルスコア欄の設定
        setupTraining();             // 研修受講歴の設定
        setupRemarks();              // 備考欄の設定
        setupButtons();              // 保存・戻るボタンの設定
    }

    // 表示処理（外部からこの画面を表示するために使用）
    public void view(EmployeeInformation employeeInformation) {
        this.employeeInformation = employeeInformation; // 編集対象の情報を保存
        setupEditScreen(); // UI構築
        setValues();       // 画面に社員情報を反映
        frame.setVisible(true); // 画面表示
    }

    // 社員ID欄のUI設定
    private void setupEmployeeId() {
        employeeIdField = placeholderTextField("01234xx");
        employeeIdField.setBounds(15, 5, 130, 30);
        idPanel.add(employeeIdField); // ID入力欄を idPanel に追加
    }

    // 氏名（フリガナ・漢字）のUIを構築
    private void setupNameFields() {
        // フリガナ（姓）
        rubyLastNameField = placeholderTextField("ヤマダ");
        rubyLastNameField.setBounds(15, 15, 195, 30);
        namePanel.add(rubyLastNameField);

        // フリガナ（名）
        rubyFirstNameField = placeholderTextField("タロウ");
        rubyFirstNameField.setBounds(215, 15, 195, 30);
        namePanel.add(rubyFirstNameField);

        // 漢字（姓）
        lastNameField = placeholderTextField("山田");
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(15, 55, 195, 40);
        namePanel.add(lastNameField);

        // 漢字（名）
        firstNameField = placeholderTextField("太郎");
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(215, 55, 195, 40);
        namePanel.add(firstNameField);
    }

    // 日付・言語入力欄のUIを構築
    private void setupDateAndLanguageFields() {
        // 生年月日欄
        birthdDayPanel.add(new JLabel("生年月日"), BorderLayout.NORTH);
        birthPanel.add(dateSelector(birthYearCombo, birthMonthCombo, birthDayCombo));
        birthPanel.setBackground(Color.WHITE);
        birthdDayPanel.add(birthPanel, BorderLayout.SOUTH);

        // 入社年月日欄
        joiningDatePanel.add(new JLabel("入社年月"), BorderLayout.NORTH);
        joinPanel.add(dateSelector(joinYearCombo, joinMonthCombo, joinDayCombo));
        joinPanel.setBackground(Color.WHITE);
        joiningDatePanel.add(joinPanel, BorderLayout.SOUTH);

        // エンジニア歴欄
        engineerDatePanel.add(new JLabel("エンジニア歴"), BorderLayout.NORTH);
        engPanel.add(engineerDateSelector(engYearCombo, engMonthCombo));
        engPanel.setBackground(Color.WHITE);
        engineerDatePanel.add(engPanel, BorderLayout.SOUTH);

        // 扱える言語欄
        JLabel availableLanguagesLabel = new JLabel("扱える言語");
        availableLanguagesLabel.setBounds(0, -3, 100, 20);
        availableLanguagesPanel.add(availableLanguagesLabel);

        JPanel availableLanguageFieldPanel = new JPanel();
        availableLanguageFieldPanel.setBounds(0, 15, 190, 40);
        availableLanguageFieldPanel.setBackground(Color.LIGHT_GRAY);
        availableLanguageFieldPanel.setLayout(null);

        availableLanguageField = placeholderTextField("html・CSS");
        availableLanguageField.setBounds(0, 5, 190, 30);
        availableLanguageFieldPanel.add(availableLanguageField);
        availableLanguagesPanel.add(availableLanguageFieldPanel);
    }

    // 経歴入力欄のUI構築
    private void setupCareer() {
        careerPanel.add(createLabel("経歴", 0, 0), BorderLayout.NORTH);
        careerArea = new JTextArea(5, 30);
        careerArea.setLineWrap(true); // 自動折り返し
        placeholderTextArea("経歴", careerArea);
        JScrollPane careerScroll = new JScrollPane(careerArea);
        careerPanel.add(careerScroll, BorderLayout.CENTER);
    }

    // スキルスコア欄のUI構築
    private void setupSkills() {
        skillsPanel.add(createLabel("スキル", 0, 0), BorderLayout.NORTH);

        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        skillPanel.setBackground(Color.LIGHT_GRAY);

        techCombo = createScoreCombo();      // 技術力
        commCombo = createScoreCombo();      // コミュニケーション
        attitudeCombo = createScoreCombo();  // 受講態度
        leaderCombo = createScoreCombo();    // リーダーシップ

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

    // 研修受講歴欄のUI構築
    private void setupTraining() {
        trainingRecordsPanel.add(createLabel("研修受講歴", 0, 0), BorderLayout.NORTH);
        trainingArea = new JTextArea(5, 30);
        trainingArea.setLineWrap(true);
        placeholderTextArea("2000年4月1日株式会社XXXX入社", trainingArea);
        JScrollPane trainingScroll = new JScrollPane(trainingArea);
        trainingRecordsPanel.add(trainingScroll, BorderLayout.CENTER);
    }

    // 備考欄のUI構築
    private void setupRemarks() {
        remarksPanel.add(createLabel("備考", 440, 340), BorderLayout.NORTH);
        remarksArea = new JTextArea(5, 30);
        remarksArea.setLineWrap(true);
        placeholderTextArea("特になし", remarksArea);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksPanel.add(remarksScroll, BorderLayout.CENTER);
    }

    // 戻る・保存ボタンの設定
    private void setupButtons() {
        bottomPanel.setLayout(null);

        // 戻るボタン
        backButton = new JButton("< 詳細画面へ戻る");
        backButton.setBounds(0, 0, 140, 30);
        bottomPanel.add(backButton);

        // 戻るボタン押下時の確認ダイアログ
        backButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                null,
                "現在の入力内容を破棄してもよろしいですか？",
                "確認",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                refreshUI();
                ViewDetailsScreen details = new ViewDetailsScreen();
                details.view(employeeInformation); // 詳細画面に戻る
            }
            // NO_OPTIONの場合は何もしない
        });

        // 保存ボタン
        saveButton = new JButton("保存");
        saveButton.setBounds(350, 0, 80, 30);
        bottomPanel.add(saveButton);

        // 保存ボタン押下時の処理
        saveButton.addActionListener(e -> {
            MANAGER.LOGGER.info("一覧画面に遷移");
            EmployeeInformation editInfo = collectInputData(); // 入力データを取得

            if (editInfo != null) {
                new EmployeeUpdater().update(editInfo); // 社員情報を更新
            }

            refreshUI();
            ViewTopScreen top = new ViewTopScreen();
            top.View(); // 一覧画面に戻る
        });
    }
    // ラベル（JLabel）を生成する汎用メソッド
    private JLabel createLabel(String title, int x, int y) {
        JLabel label = new JLabel(title);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    // スキルスコア用のコンボボックス（1.0〜5.0）を作成
    private JComboBox<String> createScoreCombo() {
        String[] scores = { "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0" };
        return new JComboBox<>(scores);
    }

    /**
     * 生年月日・入社日などの日付選択パネル（年・月・日）を作成
     */
    private JPanel dateSelector(JComboBox<Integer> yearBox, JComboBox<Integer> monthBox, JComboBox<Integer> dayBox) {
        LocalDate now = LocalDate.now(); // 現在日付の取得
        Integer[] nowInteger = { now.getYear(), now.getMonthValue(), now.getDayOfMonth() };

        // 年のモデル（現在から過去100年分）
        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>();
        for (int i = nowInteger[0] - 100; i <= nowInteger[0]; i++) {
            yearModel.addElement(i);
        }
        yearBox.setModel(yearModel);

        // 月のモデル（1～12月）
        DefaultComboBoxModel<Integer> monthModel = new DefaultComboBoxModel<>();
        for (int i = 1; i <= 12; i++) {
            monthModel.addElement(i);
        }
        monthBox.setModel(monthModel);

        // 日のモデル（各月に応じて最大日数を決定）
        int year = yearModel.getElementAt(0);
        int month = monthModel.getElementAt(0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1); // 月は0始まり
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DefaultComboBoxModel<Integer> dayModel = new DefaultComboBoxModel<>();
        for (int i = 1; i <= maxDay; i++) {
            dayModel.addElement(i);
        }
        dayBox.setModel(dayModel);

        // パネルに追加
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(205, 40));
        panel.setMaximumSize(new Dimension(205, 40));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.add(yearBox);
        panel.add(new JLabel("年"));
        panel.add(monthBox);
        panel.add(new JLabel("月"));
        panel.add(dayBox);
        panel.add(new JLabel("日"));
        return panel;
    }

    // エンジニア歴入力用の年・月セレクターを作成
    private JPanel engineerDateSelector(JComboBox<Integer> yearBox, JComboBox<Integer> monthBox) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(155, 40));
        panel.setMaximumSize(new Dimension(140, 40));
        panel.setBackground(Color.LIGHT_GRAY);

        // 年（0年～49年）
        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>();
        for (int i = 0; i < 50; i++) {
            yearModel.addElement(i);
        }
        yearBox.setModel(yearModel);

        // 月（0～11ヶ月）
        DefaultComboBoxModel<Integer> monthModel = new DefaultComboBoxModel<>();
        for (int i = 0; i <= 11; i++) {
            monthModel.addElement(i);
        }
        monthBox.setModel(monthModel);

        panel.add(yearBox);
        panel.add(new JLabel("年"));
        panel.add(monthBox);
        panel.add(new JLabel("月"));
        return panel;
    }

    /**
     * プレースホルダー付きの JTextField を作成
     */
    private JTextField placeholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder, 7);
        textField.setForeground(Color.GRAY); // 初期文字はグレー表示
        return textField;
    }

    /**
     * プレースホルダー付きの JTextArea を作成（focus制御付き）
     */
    private JTextArea placeholderTextArea(String placeholder, JTextArea textArea) {
        textArea.setText(placeholder);
        textArea.setForeground(Color.GRAY);

        // 入力開始時にプレースホルダーを消す
        textArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholder)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            // 入力が空の場合、プレースホルダーを復元
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText(placeholder);
                    textArea.setForeground(Color.GRAY);
                }
            }
        });

        return textArea;
    }

    /**
     * 入力フィールドからデータを取得し、EmployeeInformation を生成
     */
    public EmployeeInformation collectInputData() {
        try {
            System.out.println("【DEBUG】データ取得開始");

            // 各フィールドの入力値を取得（プレースホルダーは無視）
            String employeeID = getFieldValue(employeeIdField, "01234xx");
            String lastName = getFieldValue(lastNameField, "山田");
            String firstName = getFieldValue(firstNameField, "太郎");
            String rubyLastName = getFieldValue(rubyLastNameField, "ヤマダ");
            String rubyFirstName = getFieldValue(rubyFirstNameField, "タロウ");
            Date birthday = getDateFromComboBoxes(birthYearCombo, birthMonthCombo, birthDayCombo);
            Date joiningDate = getDateFromComboBoxes(joinYearCombo, joinMonthCombo, joinDayCombo);
            int years = (int) engYearCombo.getSelectedItem();
            int months = (int) engMonthCombo.getSelectedItem();
            int engineerDate = years * 12 + months;
            String availableLanguages = getFieldValue(availableLanguageField, "html・CSS");
            String careerDate = getFieldValue(careerArea, "XXXXXXX");

            // スキルスコア
            double skillPoint = parseScore(techCombo);
            double communicationPoint = parseScore(commCombo);
            double attitudePoint = parseScore(attitudeCombo);
            double leadershipPoint = parseScore(leaderCombo);

            String trainingDate = getFieldValue(remarksArea, "2000年4月1日株式会社XXXX入社");
            String remarks = getFieldValue(remarksArea, "特になし");

            Date updatedDay = new Date();

            System.out.println("【DEBUG】データ取得完了");

            // 入力データから新しい EmployeeInformation を作成して返す
            return new EmployeeInformation(
                employeeID, lastName, firstName, rubyLastName, rubyFirstName,
                birthday, joiningDate, engineerDate, availableLanguages,
                careerDate, trainingDate, skillPoint, attitudePoint,
                communicationPoint, leadershipPoint, remarks, updatedDay);

        } catch (Exception e) {
            e.printStackTrace(); // エラーの詳細をコンソールに出力
            showValidationError("データ取得中にエラーが発生しました");
            return null;
        }
    }

    /**
     * プレースホルダーと同じ値なら空文字に置き換えるユーティリティメソッド
     */
    private String getFieldValue(JTextComponent field, String placeholder) {
        String text = field.getText();
        return text.equals(placeholder) ? "" : text;
    }

    /**
     * JComboBox から選択されたスコア値（文字列）を double に変換
     */
    private double parseScore(JComboBox<String> combo) {
        return Double.parseDouble((String) combo.getSelectedItem());
    }

    /**
     * 年・月・日のコンボボックスから Date を生成
     */
    private Date getDateFromComboBoxes(JComboBox<Integer> yearCombo, JComboBox<Integer> monthCombo,
                                       JComboBox<Integer> dayCombo) {
        int year = (int) yearCombo.getSelectedItem();
        int month = (int) monthCombo.getSelectedItem() - 1; // 月は0始まり
        int day = (dayCombo != null) ? (int) dayCombo.getSelectedItem() : 1;

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    // ========================== ダイアログ・画面操作 ==============================

    /**
     * エラーメッセージを画面に赤字で表示する（保存ボタンの上などに表示）
     */
    public void showErrorMessageOnPanel(String message) {
        errorPanel.removeAll();
        JLabel errorLabel = new JLabel(message);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        errorPanel.add(errorLabel);
    }

    /**
     * 成功時に情報ダイアログを表示
     */
    public void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * バリデーションエラーが発生した場合にエラーダイアログを表示
     */
    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * EmployeeInformation オブジェクトの値を画面の各項目に反映
     */
    private void setValues() {
        // 社員ID・名前など基本情報のセット
        employeeIdField.setText(employeeInformation.employeeID);
        rubyLastNameField.setText(employeeInformation.rubyLastName);
        rubyFirstNameField.setText(employeeInformation.rubyFirstname);
        lastNameField.setText(employeeInformation.lastName);
        firstNameField.setText(employeeInformation.firstname);
        availableLanguageField.setText(employeeInformation.availableLanguages);
        careerArea.setText(employeeInformation.careerDate);

        // スキルスコアの反映
        techCombo.setSelectedItem(String.format("%.1f", employeeInformation.skillPoint));
        attitudeCombo.setSelectedItem(String.format("%.1f", employeeInformation.attitudePoint));
        commCombo.setSelectedItem(String.format("%.1f", employeeInformation.communicationPoint));
        leaderCombo.setSelectedItem(String.format("%.1f", employeeInformation.leadershipPoint));

        // 研修履歴・備考
        trainingArea.setText(employeeInformation.trainingDate);
        remarksArea.setText(employeeInformation.remarks);

        // 生年月日
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(employeeInformation.birthday);
        birthYearCombo.setSelectedItem(birthCal.get(Calendar.YEAR));
        birthMonthCombo.setSelectedItem(birthCal.get(Calendar.MONTH) + 1);
        birthDayCombo.setSelectedItem(birthCal.get(Calendar.DAY_OF_MONTH));

        // 入社年月日
        Calendar joinCal = Calendar.getInstance();
        joinCal.setTime(employeeInformation.joiningDate);
        joinYearCombo.setSelectedItem(joinCal.get(Calendar.YEAR));
        joinMonthCombo.setSelectedItem(joinCal.get(Calendar.MONTH) + 1);
        joinDayCombo.setSelectedItem(joinCal.get(Calendar.DAY_OF_MONTH));

        // エンジニア歴（月→年+月 に分割）
        int totalMonths = employeeInformation.engineerDate;
        engYearCombo.setSelectedItem(totalMonths / 12);
        engMonthCombo.setSelectedItem(totalMonths % 12);
    }
}
