import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * エンジニア情報を表示する画面クラス（編集不可モード）
 */
public class ViewDetailsScreen extends SetUpDetailsScreen {

    // UIコンポーネント（インスタンス変数）
    private JTextField employeeIdField;
    private JTextField rubyLastNameField, rubyFirstNameField;
    private JTextField lastNameField, firstNameField;
    // 生年月日
    private JComboBox<Integer> birthYearCombo = new JComboBox<>();
    private JComboBox<Integer> birthMonthCombo = new JComboBox<>();
    private JComboBox<Integer> birthDayCombo = new JComboBox<>();

    // 入社年月日
    private JComboBox<Integer> joinYearCombo = new JComboBox<>();
    private JComboBox<Integer> joinMonthCombo = new JComboBox<>();
    private JComboBox<Integer> joinDayCombo = new JComboBox<>();

    // エンジニア歴
    private JComboBox<Integer> engYearCombo = new JComboBox<>();
    private JComboBox<Integer> engMonthCombo = new JComboBox<>();
    private JPanel birthPanel = new JPanel();
    private JPanel joinPanel = new JPanel();
    private JPanel engPanel = new JPanel();
    // 扱える言語
    private JTextField availableLanguageField;
    private JTextArea careerArea, trainingArea, remarksArea;
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;
    private JButton saveButton, backButton;

    private final EmployeeManager MANAGER = new EmployeeManager();

    // 表示対象のエンジニア情報
    private EmployeeInformation employeeInformation;

    /**
     * コンストラクタ：エンジニア情報を受け取る
     */
    public ViewDetailsScreen() {
        frame.setTitle("エンジニア情報 詳細画面");
    }

    private void setupDetailsScreen() {
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
    public void view(EmployeeInformation employeeInformation) {
        this.employeeInformation = employeeInformation;
        setupDetailsScreen();
        setValues();
        setReadOnly();
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
        // 生年月日
        birthdDayPanel.add(new JLabel("生年月日"), BorderLayout.NORTH);
        birthPanel.add(dateSelector(birthYearCombo, birthMonthCombo, birthDayCombo, employeeInformation.getBirthday()));
        birthPanel.setBackground(Color.WHITE);
        birthdDayPanel.add(birthPanel, BorderLayout.SOUTH);
        // 入社年月日
        joiningDatePanel.add(new JLabel("入社年月"), BorderLayout.NORTH);
        joinPanel.add(dateSelector(joinYearCombo, joinMonthCombo, joinDayCombo, employeeInformation.getJoiningDate()));
        joinPanel.setBackground(Color.WHITE);
        joiningDatePanel.add(joinPanel, BorderLayout.SOUTH);
        // エンジニア歴
        engineerDatePanel.add(new JLabel("エンジニア歴"), BorderLayout.NORTH);
        engPanel.add(engineerDateSelector(engYearCombo, engMonthCombo, employeeInformation.getEngineerDate()));
        engPanel.setBackground(Color.WHITE);
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
        availableLanguageField.setBounds(0, 5, 190, 30);
        availableLanguageFieldPanel.add(availableLanguageField);
        availableLanguagesPanel.add(availableLanguageFieldPanel);
    }

    // middlePanelの要素追加（経歴・スキル・スキルスコア）
    private void setupCareer() {
        careerPanel.add(createLabel("経歴", 0, 0), BorderLayout.NORTH);
        careerArea = new JTextArea(5, 30);
        careerArea.setLineWrap(true);
        placeholderTextArea("経歴", careerArea);
        JScrollPane careerScroll = new JScrollPane(careerArea);
        careerPanel.add(careerScroll, BorderLayout.CENTER);
    }

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

    // 研修受講歴と備考
    private void setupTraining() {
        trainingRecordsPanel.add(createLabel("研修受講歴", 0, 0), BorderLayout.NORTH);
        trainingArea = new JTextArea(5, 30);
        trainingArea.setLineWrap(true);
        placeholderTextArea("2000年4月1日株式会社XXXX入社", trainingArea);
        JScrollPane trainingScroll = new JScrollPane(trainingArea);
        trainingRecordsPanel.add(trainingScroll, BorderLayout.CENTER);
    }

    private void setupRemarks() {
        remarksPanel.add(createLabel("備考", 440, 340), BorderLayout.NORTH);
        remarksArea = new JTextArea(5, 30);
        remarksArea.setLineWrap(true);
        placeholderTextArea("特になし", remarksArea);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksPanel.add(remarksScroll, BorderLayout.CENTER);
    }

    // 保存・戻るボタン（左）
    private void setupButtons() {
        bottomPanel.setLayout(null);
        backButton = new JButton("< 一覧画面へ戻る");
        backButton.setBounds(0, 0, 140, 30);
        bottomPanel.add(backButton);
        backButton.addActionListener(e -> {
            refreshUI();
            ViewTopScreen top = new ViewTopScreen();
            top.View();
        });

        // 保存ボタン（中央）
        saveButton = new JButton("編集");
        saveButton.setBounds(350, 0, 80, 30);
        saveButton.addActionListener(e -> {
            MANAGER.LOGGER.info("編集画面に遷移");
            refreshUI();
            ViewEditScreen edit = new ViewEditScreen();
            edit.view(employeeInformation);
        });
        bottomPanel.add(saveButton);
    }

    // 画面構成で呼び出すメソッド-------------------------------------------------------
    // ラベル生成
    private JLabel createLabel(String title, int x, int y) {
        JLabel label = new JLabel(title);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    // スキルスコア
    private JComboBox<String> createScoreCombo() {
        String[] scores = { "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0" };
        JComboBox<String> comboBox = new JComboBox<>(scores);
        return comboBox;
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
    private JPanel dateSelector(JComboBox<Integer> yearBox, JComboBox<Integer> monthBox, JComboBox<Integer> dayBox,
            Date date) {
        LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
        //
        JPanel panel = new JPanel();
        Dimension size = new Dimension(205, 40);
        int wrapperWidth = size.width;
        int wrapperHeight = size.height;
        panel.setPreferredSize(new Dimension(wrapperWidth, wrapperHeight));
        panel.setMaximumSize(new Dimension(size.width, wrapperHeight));
        panel.setBackground(Color.LIGHT_GRAY);
        //
        Integer[] yearInteger = {};
        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>(yearInteger);
        yearModel.addElement(Integer.valueOf(localDate.getYear()));
        yearBox = new JComboBox<>(yearModel);
        //
        Integer[] monthInteger = {};
        DefaultComboBoxModel<Integer> monthModel = new DefaultComboBoxModel<>(monthInteger);
        monthModel.addElement(Integer.valueOf(localDate.getMonthValue()));
        monthBox = new JComboBox<>(monthModel);
        //
        Integer[] dayInteger = {};
        DefaultComboBoxModel<Integer> dayModel = new DefaultComboBoxModel<>(dayInteger);
        dayModel.addElement(Integer.valueOf(localDate.getDayOfMonth()));
        dayBox = new JComboBox<>(dayModel);
        panel.add(yearBox);
        panel.add(new JLabel("年"));
        panel.add(monthBox);
        panel.add(new JLabel("月"));
        panel.add(dayBox);
        panel.add(new JLabel("日"));
        return panel;
    }

    private JPanel engineerDateSelector(JComboBox<Integer> yearBox, JComboBox<Integer> monthBox, int date) {
        Integer year;
        Integer month;
        year = date / 12;
        month = date % 12;
        JPanel panel = new JPanel();
        Dimension size = new Dimension(140, 40);
        int wrapperWidth = size.width + 15;
        int wrapperHeight = size.height;
        panel.setPreferredSize(new Dimension(wrapperWidth, size.height));
        panel.setMaximumSize(new Dimension(size.width, wrapperHeight));
        Integer[] yearInteger = {};
        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>(yearInteger);

        panel.setBackground(Color.LIGHT_GRAY);
        yearModel.addElement(year);
        yearBox = new JComboBox<>(yearModel);
        //
        Integer[] monthInteger = {};
        DefaultComboBoxModel<Integer> monthModel = new DefaultComboBoxModel<>(monthInteger);
        monthModel.addElement(month);
        monthBox = new JComboBox<>(monthModel);
        panel.add(yearBox);
        panel.add(new JLabel("年"));
        panel.add(monthBox);
        panel.add(new JLabel("月"));
        return panel;
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
        return textField;
    }

    /**
     * プレースホルダー付きの JTextArea を作成。
     * ユーザーがフィールドにフォーカスすると、プレースホルダーが消え入力可能。
     * フォーカスが外れ、入力が空の場合は再びプレースホルダーが表示される。
     * 
     * @param placeholder 初期表示されるプレースホルダーテキスト
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

    // ダイアログ関係-------------------------------------------------------
    /**
     * エラーメッセージを保存ボタン上部に表示。
     *
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    public void showErrorMessageOnPanel(String message) {
        errorPanel.removeAll();
        JLabel errorLabel = new JLabel(message);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        errorPanel.add(errorLabel);
    }

    /**
     * 新規追加成功時に表示されるダイアログ
     *
     * @param message ダイアログに表示される新規追加処理完了メッセージ
     */
    public void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 新規追加成功失敗時に表示されるダイアログ
     *
     * @param message 表示されるエラーメッセージ
     * @author nishiyama
     */
    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * エンジニア情報を各入力欄に反映する処理
     */
    private void setValues() {
        employeeIdField.setText(employeeInformation.getEmployeeID());
        rubyLastNameField.setText(employeeInformation.getRubyLastName());
        rubyFirstNameField.setText(employeeInformation.getRubyFirstname());
        lastNameField.setText(employeeInformation.getLastName());
        firstNameField.setText(employeeInformation.getFirstname());
        availableLanguageField.setText(employeeInformation.getAvailableLanguages());
        careerArea.setText(employeeInformation.getCareerDate());
        // 各スキルポイントを反映
        techCombo.setSelectedItem(String.format("%.1f", employeeInformation.getSkillPoint()));
        attitudeCombo.setSelectedItem(String.format("%.1f", employeeInformation.getAttitudePoint()));
        commCombo.setSelectedItem(String.format("%.1f", employeeInformation.getCommunicationPoint()));
        leaderCombo.setSelectedItem(String.format("%.1f", employeeInformation.getLeadershipPoint()));
        trainingArea.setText(employeeInformation.getTrainingDate());
        remarksArea.setText(employeeInformation.getRemarks());
    }

    /**
     * テキスト入力欄を全て読み取り専用（編集不可）にする共通処理
     */
    private void makeTextComponentReadOnly(JTextComponent comp) {
        comp.setEditable(false);
        comp.setFocusable(false);
    }

    private void setReadOnly() {
        makeTextComponentReadOnly(employeeIdField);
        makeTextComponentReadOnly(rubyLastNameField);
        makeTextComponentReadOnly(rubyFirstNameField);
        makeTextComponentReadOnly(lastNameField);
        makeTextComponentReadOnly(firstNameField);
        makeTextComponentReadOnly(availableLanguageField);
        makeTextComponentReadOnly(careerArea);
        makeTextComponentReadOnly(trainingArea);
        makeTextComponentReadOnly(remarksArea);
    }
}
