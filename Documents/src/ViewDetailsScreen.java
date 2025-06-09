import java.awt.Font;
import java.awt.GridLayout;

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

    // 各種入力コンポーネント
    private JTextField employeeIdField;
    private JTextField rubyLastNameField, rubyFirstNameField;
    private JTextField lastNameField, firstNameField;
    private JTextField birthDateField, joinDateField, engDurationField;
    private JTextField languageField;
    private JTextArea careerArea, trainingArea, remarksArea;
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;
    private JButton buckButton;

    // 表示対象のエンジニア情報
    private EmployeeInformation employeeInformation;

    /**
     * コンストラクタ：エンジニア情報を受け取る
     */
    public ViewDetailsScreen(EmployeeInformation employeeInformation) {
        super();
        this.employeeInformation = employeeInformation;
    }

    /**
     * 画面を構築・表示するメイン処理
     */
    public void view() {
        fullScreenPanel.removeAll();
        fullScreenPanel.setLayout(null);
        frame.setTitle("エンジニア情報 表示画面");
        frame.setSize(850, 600);
        frame.setResizable(false);

        JPanel container = new JPanel(null);
        container.setBounds(25, 25, 800, 550);

        // 各UIセクションの構築
        setupEmployeeId(container);
        setupNameFields(container);
        setupDateAndLanguageFields(container);
        setupCareerAndSkills(container);
        setupTrainingAndRemarks(container);
        setupButtons(container); // 戻る・編集ボタン

        fullScreenPanel.add(container);
        frame.setContentPane(fullScreenPanel);
        frame.setVisible(true);

        // 受け取ったエンジニア情報を各UIへ反映
        setValues();
    }

    /**
     * テキスト入力欄を全て読み取り専用（編集不可）にする共通処理
     */
    private void makeTextComponentReadOnly(JTextComponent comp) {
        comp.setEditable(false);
        comp.setFocusable(false);
    }

    /**
     * 社員IDフィールドの設定
     */
    private void setupEmployeeId(JPanel panel) {
        employeeIdField = new JTextField();
        employeeIdField.setBounds(0, 0, 130, 30);
        makeTextComponentReadOnly(employeeIdField);
        panel.add(employeeIdField);
    }

    /**
     * 氏名・ふりがなの入力欄を設定
     */
    private void setupNameFields(JPanel panel) {
        // ふりがな
        rubyLastNameField = new JTextField();
        rubyLastNameField.setBounds(0, 40, 195, 30);
        makeTextComponentReadOnly(rubyLastNameField);
        panel.add(rubyLastNameField);

        rubyFirstNameField = new JTextField();
        rubyFirstNameField.setBounds(205, 40, 195, 30);
        makeTextComponentReadOnly(rubyFirstNameField);
        panel.add(rubyFirstNameField);

        // 漢字氏名（太字フォント）
        lastNameField = new JTextField();
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(0, 80, 195, 40);
        makeTextComponentReadOnly(lastNameField);
        panel.add(lastNameField);

        firstNameField = new JTextField();
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(205, 80, 195, 40);
        makeTextComponentReadOnly(firstNameField);
        panel.add(firstNameField);
    }

    /**
     * 生年月日、入社日、経験年数、言語入力欄の設定
     */
    private void setupDateAndLanguageFields(JPanel panel) {
        panel.add(createLabel("生年月日", 0, 130));
        panel.add(createLabel("入社年月日", 220, 130));
        panel.add(createLabel("エンジニア歴", 440, 130));
        panel.add(createLabel("扱える言語", 585, 130));

        birthDateField = new JTextField();
        birthDateField.setBounds(0, 150, 195, 25);
        makeTextComponentReadOnly(birthDateField);
        panel.add(birthDateField);

        joinDateField = new JTextField();
        joinDateField.setBounds(220, 150, 195, 25);
        makeTextComponentReadOnly(joinDateField);
        panel.add(joinDateField);

        engDurationField = new JTextField();
        engDurationField.setBounds(440, 150, 130, 25);
        makeTextComponentReadOnly(engDurationField);
        panel.add(engDurationField);

        languageField = new JTextField();
        languageField.setBounds(585, 150, 155, 25);
        makeTextComponentReadOnly(languageField);
        panel.add(languageField);
    }

    /**
     * 経歴・スキル情報の表示パネル
     */
    private void setupCareerAndSkills(JPanel panel) {
        panel.add(createLabel("経歴", 0, 190));
        panel.add(createLabel("スキル", 400, 190));

        // 経歴（複数行）
        careerArea = new JTextArea();
        makeTextComponentReadOnly(careerArea);
        JScrollPane careerScroll = new JScrollPane(careerArea);
        careerScroll.setBounds(0, 210, 375, 120);
        panel.add(careerScroll);

        // スキル（コンボボックス 4種）
        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        techCombo = createScoreCombo();
        commCombo = createScoreCombo();
        attitudeCombo = createScoreCombo();
        leaderCombo = createScoreCombo();

        // 編集不可にする
        techCombo.setEnabled(false);
        commCombo.setEnabled(false);
        attitudeCombo.setEnabled(false);
        leaderCombo.setEnabled(false);

        skillPanel.add(new JLabel("技術力"));
        skillPanel.add(techCombo);
        skillPanel.add(new JLabel("コミュニケーション能力"));
        skillPanel.add(commCombo);
        skillPanel.add(new JLabel("受講態度"));
        skillPanel.add(attitudeCombo);
        skillPanel.add(new JLabel("リーダーシップ"));
        skillPanel.add(leaderCombo);
        skillPanel.setBounds(400, 210, 265, 120);
        panel.add(skillPanel);
    }

    /**
     * 研修履歴・備考欄の設定
     */
    private void setupTrainingAndRemarks(JPanel panel) {
        panel.add(createLabel("研修受講歴", 0, 340));
        panel.add(createLabel("備考", 400, 340));

        trainingArea = new JTextArea();
        makeTextComponentReadOnly(trainingArea);
        JScrollPane trainingScroll = new JScrollPane(trainingArea);
        trainingScroll.setBounds(0, 360, 375, 100);
        panel.add(trainingScroll);

        remarksArea = new JTextArea();
        makeTextComponentReadOnly(remarksArea);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setBounds(400, 360, 340, 100);
        panel.add(remarksScroll);
    }

    /**
     * 「一覧に戻る」「編集」ボタンの設定
     */
    private void setupButtons(JPanel panel) {
        // 一覧に戻るボタン
        buckButton = new JButton("< 一覧画面に戻る");
        buckButton.setBounds(0, 470, 140, 30);
        buckButton.addActionListener(e -> {
            refreshUI();
        
            ViewTopScreen top = new ViewTopScreen();
            top.View(); // ViewTopScreenを表示
        });
        panel.add(buckButton);
    
    //     // 編集ボタン
    //     JButton editButton = new JButton("編集 >");
    //     editButton.setBounds(660, 470, 100, 30);
    //     editButton.addActionListener(e -> {
    //         frame.setVisible(false); // こちらも非表示にするだけ
    //         ViewEditScreen editScreen = new ViewEditScreen(employeeInformation);
    //         editScreen.view();  // 編集画面を開く
    //     });
    //     panel.add(editButton);
    }
    /**
     * ラベルの共通作成処理
     */
    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    /**
     * スコア選択用コンボボックスを作成（1.0～5.0）
     */
    private JComboBox<String> createScoreCombo() {
        String[] scores = {"1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};
        return new JComboBox<>(scores);
    }

    /**
     * エンジニア情報を各入力欄に反映する処理
     */
    private void setValues() {
        employeeIdField.setText(employeeInformation.employeeID);
        rubyLastNameField.setText(employeeInformation.rubyLastName);
        rubyFirstNameField.setText(employeeInformation.rubyFirstname);
        lastNameField.setText(employeeInformation.lastName);
        firstNameField.setText(employeeInformation.firstname);

        // 日付形式（yyyy/MM/dd）に変換して表示
        String birthDateStr = EmployeeInformation.formatDate(employeeInformation.birthday);
        birthDateField.setText(birthDateStr);

        String joinDateStr = EmployeeInformation.formatDate(employeeInformation.joiningDate);
        joinDateField.setText(joinDateStr);

        // 経験年数を表示（例：3年 0ヶ月）
        String engDuration = employeeInformation.engineerDate + "年 0ヶ月";
        engDurationField.setText(engDuration);

        languageField.setText(employeeInformation.availableLanguages);
        careerArea.setText(employeeInformation.careerDate);

        // 各スキルポイントを反映
        techCombo.setSelectedItem(String.format("%.1f", employeeInformation.skillPoint));
        attitudeCombo.setSelectedItem(String.format("%.1f", employeeInformation.attitudePoint));
        commCombo.setSelectedItem(String.format("%.1f", employeeInformation.communicationPoint));
        leaderCombo.setSelectedItem(String.format("%.1f", employeeInformation.leadershipPoint));

        trainingArea.setText(employeeInformation.trainingDate);
        remarksArea.setText(employeeInformation.remarks);
    }
}