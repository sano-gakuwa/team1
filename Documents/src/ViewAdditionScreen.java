
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import javax.swing.*;

public class ViewAdditionScreen extends SetUpDetailsScreen {

    // UIコンポーネント（インスタンス変数）
    private JTextField employeeIdField;
    private JTextField rubyLastNameField, rubyFirstNameField;
    private JTextField lastNameField, firstNameField;
    private JTextField languageField;
    private JTextArea careerArea, trainingArea, remarksArea;
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;
    private JButton saveButton, backButton;

    public ViewAdditionScreen() {
        super(); // 親クラスの初期化
    }

    // メイン画面の表示処理
    public void view() {
        fullScreenPanel.removeAll();
        fullScreenPanel.setLayout(null);
        frame.setTitle("エンジニア情報 新規追加画面");
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

    // 社員ID
    private void setupEmployeeId(JPanel panel) {
        employeeIdField = placeholderTextField("01234xx");
        employeeIdField.setBounds(0, 0, 130, 30);
        String employeeID = employeeIdField.getText();
        if (employeeID.equals("01234xx")) {
            employeeID = ""; // プレースホルダーのままなら空にする
        }
        panel.add(employeeIdField);
    }

    // 氏名（フリガナ + 氏名）
    private void setupNameFields(JPanel panel) {
        rubyLastNameField = placeholderTextField("ヤマダ");
        rubyLastNameField.setBounds(0, 40, 195, 30);
        String rubyLastName = rubyLastNameField.getText();
        if (rubyLastName.equals("ヤマダ")) {
            rubyLastName = ""; // プレースホルダーのままなら空にする
        }
        panel.add(rubyLastNameField);

        rubyFirstNameField = placeholderTextField("タロウ");
        rubyFirstNameField.setBounds(205, 40, 195, 30);
        String rubyFirstname = rubyFirstNameField.getText();
        if (rubyFirstname.equals("タロウ")) {
            rubyFirstname = ""; // プレースホルダーのままなら空にする
        }
        panel.add(rubyFirstNameField);

        lastNameField = placeholderTextField("山田");
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(0, 80, 195, 40);
        String lastName = lastNameField.getText();
        if (lastName.equals("山田")) {
            lastName = ""; // プレースホルダーのままなら空にする
        }
        panel.add(lastNameField);

        firstNameField = placeholderTextField("太郎");
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(205, 80, 195, 40);
        String firstname = firstNameField.getText();
        if (firstname.equals("太郎")) {
            firstname = ""; // プレースホルダーのままなら空にする
        }
        panel.add(firstNameField);
    }

    // upperPanelに要素追加（生年月日、入社年月、エンジニア歴、扱える言語）
    private void setupDateAndLanguageFields(JPanel panel) {
        // ラベル
        panel.add(createLabel("生年月日", 0, 130));
        panel.add(createLabel("入社年月", 220, 130));
        panel.add(createLabel("エンジニア歴", 440, 130));
        panel.add(createLabel("扱える言語", 610, 130));

        // 生年月日セレクタ（年月日）
        JPanel birthPanel = dateSelector(true);
        birthPanel.setBounds(0, 150, 210, 60);
        panel.add(birthPanel);

        // 入社年月セレクタ（年月日）
        JPanel joinPanel = dateSelector(true);
        joinPanel.setBounds(220, 150, 210, 60);
        panel.add(joinPanel);

        // エンジニア歴セレクタ（年月）
        JPanel engPanel = dateSelector(false);
        engPanel.setBounds(440, 150, 140, 60);
        panel.add(engPanel);

        // 扱える言語
        languageField = placeholderTextField("html・CSS");
        languageField.setBounds(610, 155, 170, 25);
        String availableLanguages = languageField.getText();
        if (availableLanguages.equals("html・CSS")) {
            availableLanguages = ""; // プレースホルダーのままなら空にする
        }
        panel.add(languageField);
    }

    // middlePanelの要素追加（経歴・スキル・スキルスコア）
    private void setupCareerAndSkills(JPanel panel) {
        panel.add(createLabel("経歴", 0, 190)); //何かが上にのってる？
        panel.add(createLabel("スキル", 440, 190)); //何かが上にのってる？

        careerArea = placeholderTextArea("XXXXXXX");
        JScrollPane careerScroll = new JScrollPane(careerArea);
        careerScroll.setBounds(0, 210, 375, 120);
        panel.add(careerScroll);

        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        techCombo = createScoreCombo(60,25); // 反映されない
        commCombo = createScoreCombo(60,25); // 反映されない
        attitudeCombo = createScoreCombo(60,25); // 反映されない
        leaderCombo = createScoreCombo(60,25); // 反映されない
        skillPanel.add(new JLabel("技術力"));
        skillPanel.add(techCombo);
        skillPanel.add(new JLabel("コミュニケーション能力"));
        skillPanel.add(commCombo);
        skillPanel.add(new JLabel("受講態度"));
        skillPanel.add(attitudeCombo);
        skillPanel.add(new JLabel("リーダーシップ"));
        skillPanel.add(leaderCombo);
        skillPanel.setBounds(440, 210, 360, 120);
        panel.add(skillPanel);
    }

    // 研修受講歴と備考
    private void setupTrainingAndRemarks(JPanel panel) {
        panel.add(createLabel("研修受講歴", 0, 340));
        panel.add(createLabel("備考", 440, 340));

        trainingArea = placeholderTextArea("2000年4月1日株式会社XXXX入社");
        JScrollPane trainingScroll = new JScrollPane(trainingArea);
        trainingScroll.setBounds(0, 360, 375, 100);
        panel.add(trainingScroll);

        remarksArea = placeholderTextArea("特になし");
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setBounds(440, 360, 340, 100);
        panel.add(remarksScroll);
    }

    // 保存・戻るボタン（左）
    private void setupButtons(JPanel panel) {
        backButton = new JButton("< 一覧画面へ戻る");
        backButton.setBounds(0, 470, 140, 30);
        panel.add(backButton);
        backButton.addActionListener(e -> {
            int result = javax.swing.JOptionPane.showConfirmDialog(
                    null,
                    "現在の入力内容を破棄してもよろしいですか？",
                    "確認",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );
            if (result == javax.swing.JOptionPane.YES_OPTION) {
                // ViewTopScreen を表示
                refreshUI();
                new ViewTopScreen().View();
            } else if (result == JOptionPane.NO_OPTION) {
                // NO_OPTION の場合は何もしない（入力画面に留まる）
            }
        });

        // 保存ボタン（中央）
        saveButton = new JButton("保存");
        saveButton.setBounds(350, 470, 80, 30);
        saveButton.addActionListener(e -> {
            // 新規追加メソッド
            refreshUI();
            new ViewTopScreen().View();
        });
        panel.add(saveButton);
    }

    // ラベル生成
    private JLabel createLabel(String title, int x, int y) {
        JLabel label = new JLabel(title);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    // スキルスコア
    private JComboBox<String> createScoreCombo(int width, int height) {
        String[] scores = {"1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};
        JComboBox<String> comboBox = new JComboBox<>(scores);
        return comboBox;
    }

    // 日付セレクタ
    private JPanel dateSelector(boolean includeDay) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 60));

        JComboBox<Integer> yearCombo = new JComboBox<>();
        JComboBox<Integer> monthCombo = new JComboBox<>();
        JComboBox<Integer> dayCombo = new JComboBox<>();

        for (int i = 1900; i <= 2100; i++) {
            yearCombo.addItem(i);
        }
        for (int i = 1; i <= 12; i++) {
            monthCombo.addItem(i);
        }

        // イベントリスナーで日を更新
        ActionListener updateListener = e -> {
            if (includeDay) {
                int year = (int) yearCombo.getSelectedItem();
                int month = (int) monthCombo.getSelectedItem();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, 1);
                int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                dayCombo.removeAllItems();
                for (int i = 1; i <= maxDay; i++) {
                    dayCombo.addItem(i);
                }
            }
        };

        yearCombo.addActionListener(updateListener);
        monthCombo.addActionListener(updateListener);
        updateListener.actionPerformed(null); // 初期日付更新

        panel.add(yearCombo);
        panel.add(new JLabel("年"));
        panel.add(monthCombo);
        panel.add(new JLabel("月"));

        if (includeDay) {
            panel.add(dayCombo);
            panel.add(new JLabel("日"));
        }

        return panel;
    }

    // プレースホルダー付きJTextFieldを作成
    private JTextField placeholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder, 7);
        textField.setForeground(Color.GRAY);

        // 初期状態の判定用フラグ
        final boolean[] showingPlaceholder = {true};
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

    // プレースホルダー付きJTextArea
    private JTextArea placeholderTextArea(String placeholder) {
        JTextArea textArea = new JTextArea(5, 30);
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

    // 新規追加成功時ダイアログ
    public void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * エラーメッセージをパネルに表示。
     *
     * @param message 表示するエラーメッセージ
     *
     * @authnor nishiyama
     */
    public void showErrorMessageOnPanel(String message) {
        errorPanel.removeAll();

        JLabel errorLabel = new JLabel(message);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));

        errorPanel.add(errorLabel);
    }

    // エラーメッセージをポップアップやラベルで表示する
    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    // 実行用メインメソッド
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewAdditionScreen().view());
    }
}
