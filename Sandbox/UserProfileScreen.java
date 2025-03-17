import java.awt.*;
import javax.swing.*;

public class UserProfileScreen {
    public static void main(String[] args) {
        // メインフレームの作成
        JFrame frame = new JFrame("User Profile Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setResizable(false); // サイズ固定
        frame.setLayout(new BorderLayout());

        // メインパネル
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // ヘッダー
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        // ユーザーID
        JLabel userIdLabel = new JLabel("xx01234");
        userIdLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 20));
        JPanel userIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userIdPanel.add(userIdLabel);

        // フリガナ (ルビ)
        JPanel rubyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField firstNameRubyField = new JTextField("ヤマダ", 5);
        JTextField lastNameRubyField = new JTextField("タロウ", 5);
        firstNameRubyField.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
        lastNameRubyField.setFont(new Font("Yu Gothic UI", Font.PLAIN, 18));
        rubyPanel.add(firstNameRubyField);
        rubyPanel.add(lastNameRubyField);

        // 名前
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField firstNameField = new JTextField("山田", 5);
        JTextField lastNameField = new JTextField("太郎", 5);
        firstNameField.setFont(new Font("Yu Gothic UI", Font.BOLD, 30));
        lastNameField.setFont(new Font("Yu Gothic UI", Font.BOLD, 30));
        namePanel.add(firstNameField);
        namePanel.add(lastNameField);

        // ヘッダーにパネルを追加
        headerPanel.add(userIdPanel);
        headerPanel.add(rubyPanel);
        headerPanel.add(namePanel);

        // 情報セクション
        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 10, 10)); // ギャップを調整
        infoPanel.setBorder(BorderFactory.createTitledBorder(""));

        // 生年月日
        infoPanel.add(createDateSelector("生年月日"));

        // 入社年月
        infoPanel.add(createDateSelector("入社年月"));

        // エンジニア歴
        infoPanel.add(createDateSelector("エンジニア歴"));

        // 扱える言語
        JPanel languagePanel = new JPanel(new BorderLayout());
        languagePanel.setBorder(BorderFactory.createTitledBorder("扱える言語"));
        JTextField languageField = new JTextField("html,css");
        languagePanel.add(languageField, BorderLayout.CENTER);
        infoPanel.add(languagePanel);

        // 経歴セクション
        JPanel careerPanel = createEditableSection("経歴",
                "2024年10月 株式会社カスタマーリレーションテレマーケティング退社\n" +
                        "2024年11月 Fulfill株式会社入社");

        // スキルセクション
        JPanel skillPanel = new JPanel(new BorderLayout());
        skillPanel.setBorder(BorderFactory.createTitledBorder("スキル"));

        String[] scores = {"1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};
        JPanel skillList = new JPanel(new GridLayout(4, 2, 10, 10));
        skillList.add(new JLabel("技術力:"));
        skillList.add(new JComboBox<>(scores));
        skillList.add(new JLabel("コミュニケーション能力:"));
        skillList.add(new JComboBox<>(scores));
        skillList.add(new JLabel("受講態度:"));
        skillList.add(new JComboBox<>(scores));
        skillList.add(new JLabel("リーダーシップ:"));
        skillList.add(new JComboBox<>(scores));
        skillPanel.add(skillList, BorderLayout.CENTER);

        // 経歴とスキルを横並びにする
        JPanel careerSkillPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        careerSkillPanel.add(careerPanel);
        careerSkillPanel.add(skillPanel);

        // 研修の受講歴
        JPanel trainingPanel = createEditableSection("研修の受講歴",
                "2024年9月 HTML,CSS研修終了\n2024年10月 JAVA研修中");

        // 備考セクション
        JPanel notesPanel = createEditableSection("備考", "親譲りの無鉄砲で小供の時から損ばかりしている。");

        // 研修の受講歴と備考を横並びにする
        JPanel trainingNotesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        trainingNotesPanel.add(trainingPanel);
        trainingNotesPanel.add(notesPanel);

        // 保存ボタン
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        footerPanel.add(saveButton);

        // 詳細画面へ戻るリンク
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("< 詳細画面へ戻る");
        backPanel.add(backButton);

        // パネルをメインパネルに追加
        mainPanel.add(headerPanel);
        mainPanel.add(infoPanel);
        mainPanel.add(careerSkillPanel); // 経歴とスキル
        mainPanel.add(trainingNotesPanel); // 研修の受講歴と備考
        mainPanel.add(footerPanel);
        mainPanel.add(backPanel);

        // フレームにパネルを追加
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // 日付セレクタを作成
    private static JPanel createDateSelector(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JComboBox<String> yearCombo = new JComboBox<>();
        JComboBox<String> monthCombo = new JComboBox<>();
        JComboBox<String> dayCombo = new JComboBox<>();

        // 年・月・日を設定
        for (int i = 1900; i <= 2100; i++) yearCombo.addItem(i + "年");
        for (int i = 1; i <= 12; i++) monthCombo.addItem(i + "月");
        for (int i = 1; i <= 31; i++) dayCombo.addItem(i + "日");

        panel.add(yearCombo);
        panel.add(monthCombo);
        panel.add(dayCombo);

        return panel;
    }

    // 編集可能なセクションを作成
    private static JPanel createEditableSection(String title, String content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JTextArea textArea = new JTextArea(content, 5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        return panel;
    }
}
