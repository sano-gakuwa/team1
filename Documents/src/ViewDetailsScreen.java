import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
    private JComboBox<String> birthYearCombo = new JComboBox<>(); // String型に変更
    private JComboBox<String> birthMonthCombo = new JComboBox<>(); // String型に変更
    private JComboBox<String> birthDayCombo = new JComboBox<>(); // String型に変更

    // 入社年月日
    private JComboBox<String> joinYearCombo = new JComboBox<>(); // String型に変更
    private JComboBox<String> joinMonthCombo = new JComboBox<>(); // String型に変更
    private JComboBox<String> joinDayCombo = new JComboBox<>(); // String型に変更

    // エンジニア歴
    private JComboBox<String> engYearCombo = new JComboBox<>(); // String型に変更
    private JComboBox<String> engMonthCombo = new JComboBox<>(); // String型に変更
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

    private ViewDialog dialog = new ViewDialog();

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
        setupSkills((employeeInformation));
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
        employeeIdField = placeholderTextField("例)01234xx");
        employeeIdField.setBounds(5, 5, 150, 30);
        idPanel.add(employeeIdField);
    }

    // 氏名（フリガナ + 氏名）
    private void setupNameFields() {
        rubyLastNameField = placeholderTextField("例)ヤマダ");
        rubyLastNameField.setBounds(5, 5, 195, 30);
        namePanel.add(rubyLastNameField);
        rubyFirstNameField = placeholderTextField("例)タロウ");
        rubyFirstNameField.setBounds(215, 5, 195, 30);
        namePanel.add(rubyFirstNameField);
        lastNameField = placeholderTextField("例)山田");
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(5, 40, 195, 40);
        namePanel.add(lastNameField);
        firstNameField = placeholderTextField("例)太郎");
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(215, 40, 195, 40);
        namePanel.add(firstNameField);
    }

    // upperPanelに要素追加（生年月日、入社年月、エンジニア歴、扱える言語）
    private void setupDateAndLanguageFields() {
        // 生年月日
        birthdDayPanel.add(new JLabel("生年月日"), BorderLayout.NORTH);
        // `dateSelector`メソッドの引数を`JComboBox<String>`型に合わせる
        birthPanel.add(dateSelector(birthYearCombo, birthMonthCombo, birthDayCombo, employeeInformation.getBirthday()));
        birthPanel.setBackground(Color.WHITE);
        birthdDayPanel.add(birthPanel, BorderLayout.SOUTH);
        // 入社年月日
        joiningDatePanel.add(new JLabel("入社年月"), BorderLayout.NORTH);
        // `dateSelector`メソッドの引数を`JComboBox<String>`型に合わせる
        joinPanel.add(dateSelector(joinYearCombo, joinMonthCombo, joinDayCombo, employeeInformation.getJoiningDate()));
        joinPanel.setBackground(Color.WHITE);
        joiningDatePanel.add(joinPanel, BorderLayout.SOUTH);
        // エンジニア歴
        engineerDatePanel.add(new JLabel("エンジニア歴"), BorderLayout.NORTH);
        // `engineerDateSelector`メソッドの引数を`JComboBox<String>`型に合わせる
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

    //スキルパネル修正 20250709
    private void setupSkills(EmployeeInformation info) {
        skillsPanel.add(createLabel("スキル", 0, 0), BorderLayout.NORTH);
        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        skillPanel.setBackground(Color.LIGHT_GRAY);
     
        techCombo = createScoreCombo(info.getSkillPoint());
        commCombo = createScoreCombo(info.getCommunicationPoint());
        attitudeCombo = createScoreCombo(info.getAttitudePoint());
        leaderCombo = createScoreCombo(info.getLeadershipPoint());
     
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
            EmployeeInfoUpdate employeeInfoUpdate = new EmployeeInfoUpdate();
            if (employeeInfoUpdate.validateUpdateLock()) {
                // 更新中のロックがかかっている場合
                dialog.viewWarningDialog("社員情報の更新中です。しばらくお待ちください。");
                return;
            }
            MANAGER.printInfoLog("編集画面に遷移");
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

    private JComboBox<String> createScoreCombo(Double score) {
        String[] scores = { String.format("%.1f", score) }; // 例: "3.5"
        JComboBox<String> comboBox = new JComboBox<>(scores);
        comboBox.setSelectedIndex(0);
        comboBox.setEnabled(true);         // グレーアウトしない
        comboBox.setEditable(false);       // 編集不可
        comboBox.setFocusable(false);      // 枠の強調表示なし
        return comboBox;
    }

    /**
     * 年月（必要に応じて日）を選択するためのセレクターパネルを作成。
     * * @param yearBox  年の JComboBox の参照
     * @param monthBox 月の JComboBox の参照
     * @param dayBox   日の JComboBox の参照
     * @return 年月（＋日）選択用の JPanel コンポネント
     * @author nishiyama
     */
    private JPanel dateSelector(JComboBox<String> yearBox, JComboBox<String> monthBox, JComboBox<String> dayBox,
            Date date) {
        LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
         
        JPanel panel = new JPanel();
        Dimension size = new Dimension(210, 40);
        int wrapperWidth = size.width;
        int wrapperHeight = size.height;
        panel.setPreferredSize(new Dimension(wrapperWidth, wrapperHeight));
        panel.setMaximumSize(new Dimension(size.width, wrapperHeight));
        panel.setBackground(Color.LIGHT_GRAY);
         
        // 年のコンボボックスに「年」を追加
        // DefaultComboBoxModel<String> yearModel = new DefaultComboBoxModel<>();
        // yearModel.addElement(localDate.getYear() + "年"); // 数字に「年」を結合
        // yearBox.setModel(yearModel);


        // 年のコンボボックス
        yearBox.removeAllItems(); // 既存のアイテムをクリア
        yearBox.addItem(localDate.getYear() + "年"); // 数字に「年」を結合
        yearBox.setSelectedItem(localDate.getYear() + "年"); // 選択状態に設定

        // 月のコンボボックスに「月」を追加
        monthBox.removeAllItems();
        monthBox.addItem(localDate.getMonthValue() + "月"); // 数字に「月」を結合
        monthBox.setSelectedItem(localDate.getMonthValue() + "月");

        // 日のコンボボックスに「日」を追加
        dayBox.removeAllItems();
        dayBox.addItem(localDate.getDayOfMonth() + "日"); // 数字に「日」を結合
        dayBox.setSelectedItem(localDate.getDayOfMonth() + "日");

        panel.add(yearBox);
        // panel.add(new JLabel("年")); // 単位がコンボボックス内に入るので、このラベルは不要になる
        panel.add(monthBox);
        // panel.add(new JLabel("月")); // 単位がコンボボックス内に入るので、このラベルは不要になる
        panel.add(dayBox);
        // panel.add(new JLabel("日")); // 単位がコンボボックス内に入るので、このラベルは不要になる
        return panel;
    }

    private JPanel engineerDateSelector(JComboBox<String> yearBox, JComboBox<String> monthBox, int date) { // String型に変更
        Integer year = date / 12;
        Integer month = date % 12;
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        Dimension size = new Dimension(200, 40);
        int wrapperWidth = size.width + 15;
        int wrapperHeight = size.height;
        panel.setPreferredSize(new Dimension(wrapperWidth, size.height));
        panel.setMaximumSize(new Dimension(size.width, wrapperHeight));
        panel.setBackground(Color.LIGHT_GRAY);
         
        // 年のコンボボックスに「年」を追加
        yearBox.removeAllItems();
        yearBox.addItem(year + "年"); // 数字に「年」を結合
        yearBox.setSelectedItem(year + "年");

        // 月のコンボボックスに「月」を追加
        monthBox.removeAllItems();
        monthBox.addItem(month + "ヵ月"); // 数字に「カ月」を結合
        monthBox.setSelectedItem(month + "ヵ月");

        panel.add(yearBox);
        // panel.add(new JLabel("年")); // 単位がコンボボックス内に入るので、このラベルは不要になる
        panel.add(monthBox);
        // panel.add(new JLabel("月")); // 単位がコンボボックス内に入るので、このラベルは不要になる
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
     * * @param placeholder 初期表示されるプレースホルダーテキスト
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



