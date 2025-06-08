
import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class ViewAdditionScreen extends SetUpDetailsScreen {

    // UIコンポーネント（インスタンス変数）
    private JTextField employeeIdField;
    private JTextField rubyLastNameField, rubyFirstNameField;
    private JTextField lastNameField, firstNameField;
    // 生年月日
    private JComboBox<Integer> birthYearCombo;
    private JComboBox<Integer> birthMonthCombo;
    private JComboBox<Integer> birthDayCombo;

    // 入社年月日
    private JComboBox<Integer> joinYearCombo;
    private JComboBox<Integer> joinMonthCombo;
    private JComboBox<Integer> joinDayCombo;

    // エンジニア歴
    private JComboBox<Integer> engYearCombo;
    private JComboBox<Integer> engMonthCombo;
    private JPanel birthPanel;
    private JPanel joinPanel;
    private JPanel engPanel;
    // 扱える言語
    private JTextField languageField;
    private JTextArea careerArea, trainingArea, remarksArea;
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;
    private JButton saveButton, backButton;

    public ViewAdditionScreen()  {
    }

    // メイン画面の表示処理
    public void view() {
        refreshUI();
        fullScreenPanel.setLayout(null);
        frame.setTitle("エンジニア情報 新規追加画面");
        JPanel container = new JPanel(null);
        container.setBounds(25, 25, 800, 550);

        setupEmployeeId(container);
        setupNameFields(container);
        setupDateAndLanguageFields(container);
        setupCareerAndSkills(container);
        setupTrainingAndRemarks(container);
        setupButtons(container);

        fullScreenPanel.add(container);
        frame.setVisible(true);
    }

    // 社員ID
    private void setupEmployeeId(JPanel panel) {
        employeeIdField = placeholderTextField("01234xx");
        employeeIdField.setBounds(0, 0, 130, 30);
        panel.add(employeeIdField);
    }

    // 氏名（フリガナ + 氏名）
    private void setupNameFields(JPanel panel) {
        rubyLastNameField = placeholderTextField("ヤマダ");
        rubyLastNameField.setBounds(0, 40, 195, 30);
        panel.add(rubyLastNameField);

        rubyFirstNameField = placeholderTextField("タロウ");
        rubyFirstNameField.setBounds(205, 40, 195, 30);
        panel.add(rubyFirstNameField);

        lastNameField = placeholderTextField("山田");
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(0, 80, 195, 40);
        panel.add(lastNameField);

        firstNameField = placeholderTextField("太郎");
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(205, 80, 195, 40);
        panel.add(firstNameField);
    }

    // upperPanelに要素追加（生年月日、入社年月、エンジニア歴、扱える言語）
    private void setupDateAndLanguageFields(JPanel panel) {
        // ラベル
        panel.add(createLabel("生年月日", 0, 130));
        panel.add(createLabel("入社年月", 220, 130));
        panel.add(createLabel("エンジニア歴", 440, 130));
        panel.add(createLabel("扱える言語", 610, 130));

        // 生年月日
        JComboBox<Integer>[] birthYear = new JComboBox[10];
        JComboBox<Integer>[] birthMonth = new JComboBox[1];
        JComboBox<Integer>[] birthDay = new JComboBox[1];
        birthPanel = dateSelector(true, birthYear, birthMonth, birthDay);
        birthYearCombo = birthYear[0];
        birthMonthCombo = birthMonth[0];
        birthDayCombo = birthDay[0];
        birthPanel.setBounds(0, 150, 210, 60);
        panel.add(birthPanel);

        // 入社年月日
        JComboBox<Integer>[] joinYear = new JComboBox[1];
        JComboBox<Integer>[] joinMonth = new JComboBox[1];
        JComboBox<Integer>[] joinDay = new JComboBox[1];
        joinPanel = dateSelector(true, joinYear, joinMonth, joinDay);
        joinYearCombo = joinYear[0];
        joinMonthCombo = joinMonth[0];
        joinDayCombo = joinDay[0];
        joinPanel.setBounds(220, 150, 210, 60);
        panel.add(joinPanel);

        // エンジニア歴
        JComboBox<Integer>[] engYear = new JComboBox[1];
        JComboBox<Integer>[] engMonth = new JComboBox[1];
        engPanel = dateSelector(false, engYear, engMonth, null);
        engYearCombo = engYear[0];
        engMonthCombo = engMonth[0];
        engPanel.setBounds(440, 150, 140, 60);
        panel.add(engPanel);

        // 扱える言語
        languageField = placeholderTextField("html・CSS");
        languageField.setBounds(610, 155, 170, 25);
        panel.add(languageField);
    }

    // middlePanelの要素追加（経歴・スキル・スキルスコア）
    private void setupCareerAndSkills(JPanel panel) {
        panel.add(createLabel("経歴", 0, 190)); // 何かが上にのってる？
        panel.add(createLabel("スキル", 440, 190)); // 何かが上にのってる？

        careerArea = placeholderTextArea("XXXXXXX");
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
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            if (result == javax.swing.JOptionPane.YES_OPTION) {
                refreshUI();
                ViewTopScreen top=new ViewTopScreen();
                top.View();
            } else if (result == JOptionPane.NO_OPTION) {
                // NO_OPTION の場合は何もしない（入力画面に留まる）
            }
        });

        // 保存ボタン（中央）
        EmployeeInformation info = collectInputData();
        saveButton = new JButton("保存");
        saveButton.setBounds(350, 470, 80, 30);
        saveButton.addActionListener(e->{
            EmployeeUpdater Updater = new EmployeeUpdater();
            Updater.addition(info); 
            ViewTopScreen top=new ViewTopScreen();
            top.View();
        });
        panel.add(saveButton);
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
     * @param includeDay 日付の選択を含める場合は true、含めない場合は false
     * @param yearBox    年の JComboBox の参照を格納する配列（長さ1の配列）
     * @param monthBox   月の JComboBox の参照を格納する配列（長さ1の配列）
     * @param dayBox     日の JComboBox の参照を格納する配列（長さ1の配列、includeDay=true 時のみ使用）
     * @return 年月（＋日）選択用の JPanel コンポネント
     * @author nishiyama
     */
    private JPanel dateSelector(boolean includeDay,
            JComboBox<Integer>[] yearBox,
            JComboBox<Integer>[] monthBox,
            JComboBox<Integer>[] dayBox) {
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

        if (includeDay) {
            int year = yearCombo.getItemAt(0);
            int month = monthCombo.getItemAt(0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, 1);
            int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= maxDay; i++) {
                dayCombo.addItem(i);
            }
        }

        yearCombo.addActionListener(e -> {
            if (includeDay) {
                updateDayCombo(yearCombo, monthCombo, dayCombo);
            }
        });
        monthCombo.addActionListener(e -> {
            if (includeDay) {
                updateDayCombo(yearCombo, monthCombo, dayCombo);
            }
        });

        panel.add(yearCombo);
        panel.add(new JLabel("年"));
        panel.add(monthCombo);
        panel.add(new JLabel("月"));
        if (includeDay) {
            panel.add(dayCombo);
            panel.add(new JLabel("日"));
        }

        // 呼び出し元に参照を返す
        yearBox[0] = yearCombo;
        monthBox[0] = monthCombo;
        if (includeDay) {
            dayBox[0] = dayCombo;
        }

        return panel;
    }

    /**
     * 年と月の選択に応じて、指定された日の JComboBox を更新
     * 月ごとの最大日数に基づいて日数を再構築
     * 
     * @param yearCombo  年の JComboBox
     * @param monthCombo 月の JComboBox
     * @param dayCombo   日の JComboBox（再構築対象）
     * @author nishiyama
     */
    private void updateDayCombo(JComboBox<Integer> yearCombo, JComboBox<Integer> monthCombo,
            JComboBox<Integer> dayCombo) {
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

        // 初期状態の判定用フラグ
        final boolean[] showingPlaceholder = { true };
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

    /**
     * プレースホルダー付きの JTextArea を作成。
     * ユーザーがフィールドにフォーカスすると、プレースホルダーが消え入力可能。
     * フォーカスが外れ、入力が空の場合は再びプレースホルダーが表示される。
     * 
     * @param placeholder 初期表示されるプレースホルダーテキスト
     * @return プレースホルダー付きの JTextArea オブジェクト
     * @author nishiyama
     */
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

    // 入力データ取得＆保存メソッド呼び出し------------------------------------------

    /**
     * GUIの各入力フィールドから情報を取得し、EmployeeInformation オブジェクトにまとめて返却。
     * 
     * @return EmployeeInformation:入力された社員情報を格納したインスタンス
     *         取得中にエラーが発生した場合は null を返却し、エラーメッセージを表示
     * @throws Exception 例外発生時、 showValidationError("データ取得中にエラーが発生しました")
     *                   を呼び出し、ユーザーに通知
     * @author nishiyama
     */
    public EmployeeInformation collectInputData() {
        try {
            String employeeID = getFieldValue(employeeIdField, "01234xx");
            String lastName = getFieldValue(lastNameField, "山田");
            String firstName = getFieldValue(firstNameField, "太郎");
            String rubyLastName = getFieldValue(rubyLastNameField, "ヤマダ");
            String rubyFirstName = getFieldValue(rubyFirstNameField, "タロウ");

            Date birthday = getDateFromSelector(birthPanel);
            Date joiningDate = getDateFromSelector(joinPanel);
            int engineerDate = getYearMonthFromSelector(engPanel); // 月数換算

            String availableLanguages = getFieldValue(languageField, "html・CSS");
            String careerDate = getFieldValue(careerArea, "XXXXXXX");

            double skillPoint = parseScore(techCombo);
            double communicationPoint = parseScore(commCombo);
            double attitudePoint = parseScore(attitudeCombo);
            double leadershipPoint = parseScore(leaderCombo);
            String trainingDate = getFieldValue(remarksArea, "2000年4月1日株式会社XXXX入社");
            String remarks = getFieldValue(remarksArea, "特になし");

            Date updatedDay = new Date();

            return new EmployeeInformation(
                    employeeID, lastName, firstName, rubyLastName, rubyFirstName,
                    birthday, joiningDate, engineerDate, availableLanguages,
                    careerDate, trainingDate, skillPoint, attitudePoint,
                    communicationPoint, leadershipPoint, remarks, updatedDay);
        } catch (Exception e) {
            showValidationError("データ取得中にエラーが発生しました");
            return null;
        }
    }

    /**
     * プレースホルダーと同じ値が入力されていた場合は空文字列を返却。それ以外は実際の入力値を返却。
     * 
     * @param field       入力フィールド (JTextField や JTextAreaなど)
     * @param placeholder 初期表示されるプレースホルダー文字列
     * @return 実際の入力値または空文字列
     * @author nishiyama
     */
    private String getFieldValue(JTextComponent field, String placeholder) {
        String text = field.getText();
        return text.equals(placeholder) ? "" : text;
    }

    /**
     * 年・月・日が選択可能な JPanel から Date 型の日時を構築して返す。
     * 
     * @param panel Jpanelに配置されたJcomboBox
     *              0番目: 年の JComboBox
     *              2番目: 月の JComboBox
     *              4番目（存在する場合）: 日の JComboBox
     * @return Date: 選択された年月日を表す java.util.Date オブジェクト
     * @author nishiyama
     */
    private Date getDateFromSelector(JPanel panel) {
        JComboBox<?> yearBox = (JComboBox<?>) panel.getComponent(0);
        JComboBox<?> monthBox = (JComboBox<?>) panel.getComponent(2);
        JComboBox<?> dayBox = panel.getComponentCount() > 4 ? (JComboBox<?>) panel.getComponent(4) : null;

        int year = (int) yearBox.getSelectedItem();
        int month = (int) monthBox.getSelectedItem() - 1;
        int day = dayBox != null ? (int) dayBox.getSelectedItem() : 1;

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    /**
     * 経歴年数のような年＋月情報を月単位に変換して返す
     * 
     * @param panel Jpanelに配置されたJcomboBox
     *              0番目: 年の JComboBox
     *              2番目: 月の JComboBox
     * @return int: 総月数（年×12 + 月）
     * @author nishiyama
     */
    private int getYearMonthFromSelector(JPanel panel) {
        JComboBox<?> yearBox = (JComboBox<?>) panel.getComponent(0);
        JComboBox<?> monthBox = (JComboBox<?>) panel.getComponent(2);
        int years = (int) yearBox.getSelectedItem();
        int months = (int) monthBox.getSelectedItem();
        return years * 12 + months;
    }

    /**
     * JComboBox から選択された数値文字列を double に変換して返す。
     * 
     * @param combo スコアが格納された JComboBox<String>
     * @return double: スコア値
     * @author nishiyama
     */
    private double parseScore(JComboBox<String> combo) {
        return Double.parseDouble((String) combo.getSelectedItem());
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
}
