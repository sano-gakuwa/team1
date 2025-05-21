import java.awt.*;
import javax.swing.*;

public class ViewEditScreen extends SetUpDetailsScreen {

    // UIコンポーネント（インスタンス変数）
    private JTextField employeeIdField;
    private JTextField rubyLastNameField, rubyFirstNameField;
    private JTextField lastNameField, firstNameField;
    private JComboBox<String> birthYearBox, birthMonthBox, birthDayBox;
    private JComboBox<String> joinYearBox, joinMonthBox, joinDayBox;
    private JComboBox<String> engYearBox, engMonthBox;
    private JTextField languageField;
    private JTextArea careerArea, trainingArea, remarksArea;
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;
    private JButton saveButton, cancelButton;

    public ViewEditScreen() {
        super();  // 親クラスの初期化
    }

    // メイン画面の表示処理
    public void view() {
        fullScreenPanel.removeAll();
        fullScreenPanel.setLayout(null);
        frame.setTitle("エンジニア情報 編集画面");
        frame.setSize(850, 600);
        frame.setResizable(false);

        JPanel container = new JPanel(null);
        container.setBounds(25, 25, 800, 550);

        setupEmployeeId(container);
        setupNameFields(container);
        setupDateAndLanguageFields(container);
        setupCareerAndSkills(container);
        setupTrainingAndRemarks(container);
        setupButtons(container);

        fullScreenPanel.add(container);
        frame.setContentPane(fullScreenPanel);
        frame.setVisible(true);
    }

    // 社員IDの入力欄
    private void setupEmployeeId(JPanel panel) {
        employeeIdField = new JTextField();
        employeeIdField.setBounds(0, 0, 130, 30);
        employeeIdField.setEditable(false);
        panel.add(employeeIdField);
    }

    // 氏名（カナ・漢字）入力欄
    private void setupNameFields(JPanel panel) {
        rubyLastNameField = new JTextField();
        rubyLastNameField.setBounds(0, 40, 195, 30);
        panel.add(rubyLastNameField);

        rubyFirstNameField = new JTextField();
        rubyFirstNameField.setBounds(205, 40, 195, 30);
        panel.add(rubyFirstNameField);

        lastNameField = new JTextField();
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(0, 80, 195, 40);
        panel.add(lastNameField);

        firstNameField = new JTextField();
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(205, 80, 195, 40);
        panel.add(firstNameField);
    }

    // 日付系と扱える言語の入力欄
    private void setupDateAndLanguageFields(JPanel panel) {
        panel.add(createLabel("生年月日", 0, 130));
        panel.add(createLabel("入社年月", 220, 130));
        panel.add(createLabel("エンジニア歴", 440, 130));
        panel.add(createLabel("扱える言語", 585, 130));

        birthYearBox = createComboBox(1970, 2025, "年");
        birthYearBox.setBounds(0, 150, 70, 25);
        panel.add(birthYearBox);

        birthMonthBox = createComboBox(1, 12, "月");
        birthMonthBox.setBounds(75, 150, 60, 25);
        panel.add(birthMonthBox);

        birthDayBox = createComboBox(1, 31, "日");
        birthDayBox.setBounds(140, 150, 60, 25);
        panel.add(birthDayBox);

        joinYearBox = createComboBox(2000, 2025, "年");
        joinYearBox.setBounds(220, 150, 70, 25);
        panel.add(joinYearBox);

        joinMonthBox = createComboBox(1, 12, "月");
        joinMonthBox.setBounds(295, 150, 60, 25);
        panel.add(joinMonthBox);

        joinDayBox = createComboBox(1, 31, "日");
        joinDayBox.setBounds(360, 150, 60, 25);
        panel.add(joinDayBox);

        engYearBox = createComboBox(0, 50, "年");
        engYearBox.setBounds(440, 150, 60, 25);
        panel.add(engYearBox);

        engMonthBox = createComboBox(0, 11, "月");
        engMonthBox.setBounds(505, 150, 60, 25);
        panel.add(engMonthBox);

        languageField = new JTextField();
        languageField.setBounds(585, 150, 155, 25);
        panel.add(languageField);
    }

    // 経歴とスキルのパネル
    private void setupCareerAndSkills(JPanel panel) {
        panel.add(createLabel("経歴", 0, 190));
        panel.add(createLabel("スキル", 400, 190));

        careerArea = new JTextArea();
        JScrollPane careerScroll = new JScrollPane(careerArea);
        careerScroll.setBounds(0, 210, 375, 120);
        panel.add(careerScroll);

        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
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
        skillPanel.setBounds(400, 210, 265, 120);
        panel.add(skillPanel);
    }

    // 研修受講歴と備考
    private void setupTrainingAndRemarks(JPanel panel) {
        panel.add(createLabel("研修受講歴", 0, 340));
        panel.add(createLabel("備考", 400, 340));

        trainingArea = new JTextArea();
        JScrollPane trainingScroll = new JScrollPane(trainingArea);
        trainingScroll.setBounds(0, 360, 375, 100);
        panel.add(trainingScroll);

        remarksArea = new JTextArea();
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setBounds(400, 360, 340, 100);
        panel.add(remarksScroll);
    }

    // 保存・キャンセルボタン
    private void setupButtons(JPanel panel) {
        cancelButton = new JButton("< 編集キャンセル");
        cancelButton.setBounds(250, 470, 140, 30);
        panel.add(cancelButton);

        saveButton = new JButton("保存");
        saveButton.setBounds(400, 470, 80, 30);
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "保存しました！");
        });
        panel.add(saveButton);
    }

    // コンボボックス生成（年・月など）
    private JComboBox<String> createComboBox(int from, int to, String suffix) {
        JComboBox<String> box = new JComboBox<>();
        for (int i = from; i <= to; i++) {
            box.addItem(i + suffix);
        }
        return box;
    }

    // スキルスコア用の選択肢
    private JComboBox<String> createScoreCombo() {
        String[] scores = {"1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};
        return new JComboBox<>(scores);
    }

    // ラベル生成
    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    // 実行用メインメソッド
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewEditScreen().view());
    }
}
