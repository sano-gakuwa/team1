
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.text.View;

public class ViewAdditionScreen extends SetUpDetailsScreen {

    private void addComponents() {
        // 社員ID
        JLabel userIdLabel = new JLabel("xx01234");
        userIdLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 25));
        JPanel userIdPanel = findPanelBySize(135, 40);
        userIdPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        userIdPanel.add(userIdLabel);

        // 氏名（フリガナ + 氏名）
        JPanel namePanelContainer = findPanelBySize(750, 135);
        namePanelContainer.setLayout(new BoxLayout(namePanelContainer, BoxLayout.Y_AXIS));

        // ルビ（フリガナ）
        JPanel rubyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField firstNameRubyField = createPlaceholderTextField("ヤマダ");
        JTextField lastNameRubyField = createPlaceholderTextField("タロウ");
        rubyPanel.add(firstNameRubyField);
        rubyPanel.add(lastNameRubyField);

        // 名前
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField firstNameField = createPlaceholderTextField("山田");
        JTextField lastNameField = createPlaceholderTextField("太郎");
        firstNameField.setFont(new Font("Yu Gothic UI", Font.BOLD, 30));
        lastNameField.setFont(new Font("Yu Gothic UI", Font.BOLD, 30));
        namePanel.add(firstNameField);
        namePanel.add(lastNameField);

        namePanelContainer.add(rubyPanel);
        namePanelContainer.add(namePanel);

        // upperPanelに要素追加（生年月日、入社年月、エンジニア歴、扱える言語）
        JPanel upperPanel = findPanelBySize(750, 60);
        upperPanel.removeAll(); // ← 既存のパディングなどを消す

        upperPanel.add(createDateSelector("生年月日"));
        upperPanel.add(createDateSelector("入社年月"));
        upperPanel.add(createDateSelector("エンジニア歴"));

        JPanel languagePanel = new JPanel(new BorderLayout());
        languagePanel.setPreferredSize(new Dimension(190, 60));
        languagePanel.setBorder(BorderFactory.createTitledBorder("扱える言語"));
        JTextField languageField = createPlaceholderTextField("html・css");
        languageField.setEditable(true);
        languagePanel.add(languageField, BorderLayout.CENTER);
        upperPanel.add(languagePanel);

        // middlePanelの要素追加（経歴・スキル・スキルスコア）
        addMiddleComponents();

        // bottomPanel
        JPanel bottomPanel = findPanelBySize(750, 34);
        bottomPanel.removeAll(); // ← 既存のパディングなどを消す
        bottomPanel.setLayout(new BorderLayout());

        // 戻るボタン（左）
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("< 詳細画面へ戻る");
        backPanel.add(backButton);
        backButton.addActionListener(e -> {
            int result = javax.swing.JOptionPane.showConfirmDialog(
                    null,
                    "現在の入力内容を破棄してキャンセルしてもよろしいですか？",
                    "確認",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

            if (result == javax.swing.JOptionPane.YES_OPTION) {
                // ViewDetailScreen を表示
                refreshUI();
                new ViewDetailsScreen().View();// ViewDetailsScreen内にViewmethodがない
            }
            // NO_OPTION の場合は何もしない（入力画面に留まる）
        });

        // 保存ボタン（中央）
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("保存");
        footerPanel.add(saveButton);
        saveButton.addActionListener(e -> {
            refreshUI();
            new ViewTopScreen().View();
        });

        bottomPanel.add(backPanel, BorderLayout.WEST);
        bottomPanel.add(footerPanel, BorderLayout.CENTER);

        frame.setVisible(true);// 画面画面表示
    }

    private void addMiddleComponents() {
        // 経歴
        JPanel editableCareer = createEditableSection("経歴",
                "2024年10月 株式会社カスタマーリレーションテレマーケティング退社\n"
                + "2024年11月 Fulfill株式会社入社");
        careerPanel.setLayout(new BorderLayout());
        careerPanel.add(editableCareer, BorderLayout.CENTER);

        // スキル
        skillsPanel.setBorder(BorderFactory.createTitledBorder("スキル"));
        JPanel skillList = new JPanel(new GridLayout(4, 2, 10, 10));
        String[] scores = {"1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0"};
        skillList.add(new JLabel("技術力:"));
        skillList.add(new JComboBox<>(scores));
        skillList.add(new JLabel("コミュニケーション能力:"));
        skillList.add(new JComboBox<>(scores));
        skillList.add(new JLabel("受講態度:"));
        skillList.add(new JComboBox<>(scores));
        skillList.add(new JLabel("リーダーシップ:"));
        skillList.add(new JComboBox<>(scores));
        skillsScorePanel.setLayout(new BorderLayout());
        skillsScorePanel.add(skillList, BorderLayout.CENTER);

        // 研修受講歴
        trainingRecordsPanel.setLayout(new BorderLayout());
        trainingRecordsPanel.add(createEditableSection("研修の受講歴",
                "2024年9月 HTML,CSS研修終了\n2024年10月 JAVA研修中"), BorderLayout.CENTER);

        // 備考
        remarksPanel.setLayout(new BorderLayout());
        remarksPanel.add(createEditableSection("備考",
                "親譲りの無鉄砲で小供の時から損ばかりしている。"), BorderLayout.CENTER);
    }

    // Utility method: 指定されたサイズのパネルを探索する
    private JPanel findPanelBySize(int width, int height) {
        for (Component comp : getAllPanels(fullScreenPanel)) { // `fullScreenPanel` を使用
            if (comp instanceof JPanel) {
                Dimension size = comp.getPreferredSize();
                if (size != null && size.width == width && size.height == height) {
                    return (JPanel) comp;
                }
            }
        }
        throw new RuntimeException("指定サイズのパネルが見つかりません: " + width + "x" + height);
    }

    // 再帰的に全パネルを取得
    private java.util.List<Component> getAllPanels(Container container) {
        java.util.List<Component> list = new java.util.ArrayList<>();
        for (Component comp : container.getComponents()) {
            list.add(comp);
            if (comp instanceof Container) {
                list.addAll(getAllPanels((Container) comp));
            }
        }
        return list;
    }

    // プレースホルダー付きJTextFieldを作成
    private JTextField createPlaceholderTextField(String placeholder) {
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

    // サンプル：日付セレクター生成
    private JComboBox<Integer> yearCombo;
    private JComboBox<Integer> monthCombo;
    private JComboBox<Integer> dayCombo;

    private JPanel createDateSelector(String title) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(185, 60));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        // （1900〜2100年）
        panel.add(new JComboBox<>());
        for (int i = 1900; i <= 2100; i++) {
            yearCombo.addItem(i);
        }
        // 月（1〜12月）
        panel.add(new JComboBox<>());
        for (int i = 1; i <= 12; i++) {
            monthCombo.addItem(i);
        }
        // 日（初期値として1〜31日）
        panel.add(new JComboBox<>());
        updateDays(); // 初期表示

        // イベントリスナー：年月が変わったら日数を更新
        yearCombo.addActionListener(e -> updateDays());
        monthCombo.addActionListener(e -> updateDays());

        panel.add(yearCombo);
        panel.add(new JLabel("年"));
        panel.add(monthCombo);
        panel.add(new JLabel("月"));
        panel.add(dayCombo);
        panel.add(new JLabel("日"));;

        return panel;
    }

    // 年月から日を更新
    private void updateDays() {
        int year = (int) yearCombo.getSelectedItem();
        int month = (int) monthCombo.getSelectedItem();

        // 月は0ベースなので-1する（Calendarの仕様）
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        dayCombo.removeAllItems();
        for (int i = 1; i <= maxDay; i++) {
            dayCombo.addItem(i);
        }
    }

    // プレースホルダー付きJTextAreaを作成
    private JTextArea createPlaceholderTextArea(String placeholder) {
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

    // サンプル：編集可能セクションの生成
    private JPanel createEditableSection(String title, String content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JTextArea textArea = createPlaceholderTextArea(content);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

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

    public void view() {
        addComponents();
    }
}
