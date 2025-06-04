import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

// 詳細表示画面クラス（SetUpDetailsScreenを継承）
public class ViewDetailsScreen extends SetUpDetailsScreen {

    // 各UIコンポーネントのフィールド定義
    private JTextField employeeIdField;
    private JTextField rubyLastNameField, rubyFirstNameField;
    private JTextField lastNameField, firstNameField;
    private JComboBox<String> birthYearBox, birthMonthBox, birthDayBox;
    private JComboBox<String> joinYearBox, joinMonthBox, joinDayBox;
    private JComboBox<String> engYearBox, engMonthBox;
    private JTextField languageField;
    private JTextArea careerArea, trainingArea, remarksArea;
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;
    private JButton editButton, buckButton;

    // コンストラクタ
    public ViewDetailsScreen() {
        super(); // SetUpDetailsScreenの初期化
    }

    // 画面表示メソッド
    public void view() {
        fullScreenPanel.removeAll();
        fullScreenPanel.setLayout(null);
        frame.setTitle("エンジニア情報 表示画面");
        frame.setSize(850, 600);
        frame.setResizable(false);

        JPanel container = new JPanel(null);
        container.setBounds(25, 25, 800, 550);

        // 各UI要素をセットアップ
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

    // テキスト入力を編集不可にするユーティリティ
    private void makeTextComponentReadOnly(JTextComponent comp) {
        comp.setEditable(false);
        comp.setFocusable(false);
    }

    // 社員IDフィールドの設定
    private void setupEmployeeId(JPanel panel) {
        employeeIdField = new JTextField();
        employeeIdField.setBounds(0, 0, 130, 30);
        makeTextComponentReadOnly(employeeIdField);
        panel.add(employeeIdField);
    }

    // 氏名関連の設定
    private void setupNameFields(JPanel panel) {
        rubyLastNameField = new JTextField();
        rubyLastNameField.setBounds(0, 40, 195, 30);
        makeTextComponentReadOnly(rubyLastNameField);
        panel.add(rubyLastNameField);

        rubyFirstNameField = new JTextField();
        rubyFirstNameField.setBounds(205, 40, 195, 30);
        makeTextComponentReadOnly(rubyFirstNameField);
        panel.add(rubyFirstNameField);

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

    // 日付や言語入力のUIを構成
    private void setupDateAndLanguageFields(JPanel panel) {
        // ラベル配置
        panel.add(createLabel("生年月日", 0, 130));
        panel.add(createLabel("入社年月", 220, 130));
        panel.add(createLabel("エンジニア歴", 440, 130));
        panel.add(createLabel("扱える言語", 585, 130));

        // 生年月日（年、月、日）
        birthYearBox = createComboBox(1970, 2025, "年");
        birthYearBox.setBounds(0, 150, 70, 25);
        birthYearBox.setEnabled(false);
        panel.add(birthYearBox);

        birthMonthBox = createComboBox(1, 12, "月");
        birthMonthBox.setBounds(75, 150, 60, 25);
        birthMonthBox.setEnabled(false);
        panel.add(birthMonthBox);

        birthDayBox = createComboBox(1, 31, "日");
        birthDayBox.setBounds(140, 150, 60, 25);
        birthDayBox.setEnabled(false);
        panel.add(birthDayBox);

        // 入社年月（年、月、日）
        joinYearBox = createComboBox(2000, 2025, "年");
        joinYearBox.setBounds(220, 150, 70, 25);
        joinYearBox.setEnabled(false);
        panel.add(joinYearBox);

        joinMonthBox = createComboBox(1, 12, "月");
        joinMonthBox.setBounds(295, 150, 60, 25);
        joinMonthBox.setEnabled(false);
        panel.add(joinMonthBox);

        joinDayBox = createComboBox(1, 31, "日");
        joinDayBox.setBounds(360, 150, 60, 25);
        joinDayBox.setEnabled(false);
        panel.add(joinDayBox);

        // エンジニア歴（年、月）
        engYearBox = createComboBox(0, 50, "年");
        engYearBox.setBounds(440, 150, 60, 25);
        engYearBox.setEnabled(false);
        panel.add(engYearBox);

        engMonthBox = createComboBox(0, 11, "月");
        engMonthBox.setBounds(505, 150, 60, 25);
        engMonthBox.setEnabled(false);
        panel.add(engMonthBox);

        // 扱える言語
        languageField = new JTextField();
        languageField.setBounds(585, 150, 155, 25);
        makeTextComponentReadOnly(languageField);
        panel.add(languageField);
    }

    // 経歴とスキル評価欄の構成
    private void setupCareerAndSkills(JPanel panel) {
        panel.add(createLabel("経歴", 0, 190));
        panel.add(createLabel("スキル", 400, 190));

        careerArea = new JTextArea();
        makeTextComponentReadOnly(careerArea);
        JScrollPane careerScroll = new JScrollPane(careerArea);
        careerScroll.setBounds(0, 210, 375, 120);
        panel.add(careerScroll);

        // スキル評価（4つの評価項目）
        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        techCombo = createScoreCombo();
        commCombo = createScoreCombo();
        attitudeCombo = createScoreCombo();
        leaderCombo = createScoreCombo();

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

    // 研修履歴と備考欄の設定
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


    // ラベル生成ヘルパー
    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    // 年月日やスキル評価用のコンボボックス生成
    private JComboBox<String> createComboBox(int from, int to, String suffix) {
        JComboBox<String> box = new JComboBox<>();
        for (int i = from; i <= to; i++) {
            box.addItem(i + suffix);
        }
        return box;
    }

    // スキル評価（1.0〜5.0）の選択肢
    private JComboBox<String> createScoreCombo() {
        String[] scores = {"1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};
        return new JComboBox<>(scores);
    }




    // ボタン配置（戻る、編集）
    private void setupButtons(JPanel panel) {
        buckButton = new JButton("< 一覧画面に戻る");
        buckButton.setBounds(0, 470, 140, 30);
        buckButton.addActionListener(e -> {
            frame.dispose(); // 現在の画面を閉じる
            ViewTopScreen top = new ViewTopScreen();
            top.View(); // 一覧画面を開く
        });
        panel.add(buckButton);
    
        editButton = new JButton("編集");
        editButton.setBounds(360, 470, 80, 30);
        editButton.addActionListener(e -> {
            frame.dispose(); // 詳細画面を閉じる
    
            // 編集画面に選択中のエンジニア情報を渡して開く
            ViewEditScreen editScreen = new ViewEditScreen(selectedEngineer);
            editScreen.View(); // 編集画面を表示（Viewメソッドは適宜修正）
        });
        panel.add(editButton);
    }


    // 起動用main関数
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewDetailsScreen().view());
    }
}