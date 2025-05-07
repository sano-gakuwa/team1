import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

public class ViewDetailsScreen extends JFrame {

    public ViewDetailsScreen() {
        setTitle("エンジニア情報 詳細画面");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // メインパネル
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 各パネルを追加
        mainPanel.add(createTopPanel());
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(createUpperPanel());
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(createMiddlePanel());
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(createBottomPanel());

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        panel.setPreferredSize(new Dimension(750, 135));
        panel.setBorder(new TitledBorder("topPanel"));

        panel.add(new JLabel("社員ID:"));
        JTextField idField = new JTextField(10);
        panel.add(idField);

        JTextField lastNameField = new JTextField("山田", 10);
        JTextField firstNameField = new JTextField("太郎", 10);
        panel.add(lastNameField);
        panel.add(firstNameField);

        return panel;
    }

    private JPanel createUpperPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 5));
        panel.setPreferredSize(new Dimension(750, 60));
        panel.setBorder(new TitledBorder("upperPanel"));

        panel.add(new JLabel("入社年月:"));
        panel.add(new JLabel("エンジニア歴:"));
        panel.add(new JLabel("扱える言語:"));

        panel.add(new JTextField("2023年08月"));
        panel.add(new JTextField("3年"));
        panel.add(new JTextField("HTML, CSS"));

        return panel;
    }

    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setPreferredSize(new Dimension(750, 245));
        panel.setBorder(new TitledBorder("middlePanel"));

        // 左上 CareerPanel
        JTextArea careerArea = new JTextArea("2024年1月 株式会社カスタマーシミュレーションテレマーケティング研修\n2024年1月 IT研修に参加");
        careerArea.setLineWrap(true);
        JScrollPane careerScroll = new JScrollPane(careerArea);
        careerScroll.setBorder(BorderFactory.createTitledBorder("コア業務"));
        panel.add(careerScroll);

        // 右上 SkillPanel
        JPanel skillsPanel = new JPanel(new GridLayout(4, 2));
        skillsPanel.setBorder(BorderFactory.createTitledBorder("スキル"));

        String[] skills = {"技術力", "コミュニケーション能力", "意欲", "リーダーシップ"};
        for (String skill : skills) {
            skillsPanel.add(new JLabel(skill));
            skillsPanel.add(new JComboBox<>(new String[]{"1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"}));
        }
        panel.add(skillsPanel);

        // 左下 TrainingPanel
        JTextArea trainingArea = new JTextArea("2024年10月 HTML/CSS 研修修了\n2024年10月 JAVA 研修修了");
        trainingArea.setLineWrap(true);
        JScrollPane trainingScroll = new JScrollPane(trainingArea);
        trainingScroll.setBorder(BorderFactory.createTitledBorder("研修履歴"));
        panel.add(trainingScroll);

        // 右下 RemarksPanel
        JTextArea remarksArea = new JTextArea();
        remarksArea.setLineWrap(true);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setBorder(BorderFactory.createTitledBorder("備考"));
        panel.add(remarksScroll);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        panel.setPreferredSize(new Dimension(750, 34));
        panel.setBorder(new TitledBorder("bottomPanel"));
    
        JButton backButton = new JButton("一覧画面に戻る");
        JButton editButton = new JButton("編集");
        panel.add(backButton);
        panel.add(editButton);
    
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ViewDetailsScreen().setVisible(true);
        });
    }
}