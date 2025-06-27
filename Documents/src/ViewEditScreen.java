import java.awt.*; // 画面部品やレイアウト関連のクラスを読み込み
import java.awt.event.*; // ボタンやコンボボックスのイベント処理に使用
import java.time.LocalDate; // 現在の日付などの取得に使用
import java.util.*; // Date や Calendar などのユーティリティ
import java.util.regex.Pattern;

import javax.swing.*; // Swingライブラリ（UI構築）
import javax.swing.text.JTextComponent; // JTextField / JTextArea を共通操作するために使用

/**
 * エンジニア情報を表示する画面クラス（編集不可モード）
 */
public class ViewEditScreen extends SetUpDetailsScreen {

    // ===== 各種UI部品の定義 =====

    // 社員ID入力欄
    private JTextField employeeIdField;
    // 氏名のフリガナ（姓・名）
    private JTextField rubyLastNameField, rubyFirstNameField;
    // 氏名の漢字（姓・名）
    private JTextField lastNameField, firstNameField;

    // 生年月日選択用のコンボボックス（年・月・日）
    private JComboBox<Integer> birthYearCombo = new JComboBox<>();
    private JComboBox<Integer> birthMonthCombo = new JComboBox<>();
    private JComboBox<Integer> birthDayCombo = new JComboBox<>();

    // 入社年月日のコンボボックス（年・月・日）
    private JComboBox<Integer> joinYearCombo = new JComboBox<>();
    private JComboBox<Integer> joinMonthCombo = new JComboBox<>();
    private JComboBox<Integer> joinDayCombo = new JComboBox<>();

    // エンジニア歴のコンボボックス（年・月）
    private JComboBox<Integer> engYearCombo = new JComboBox<>();
    private JComboBox<Integer> engMonthCombo = new JComboBox<>();

    // 日付パネル（JPanel）設定
    private JPanel birthPanel = new JPanel();
    private JPanel joinPanel = new JPanel();
    private JPanel engPanel = new JPanel();

    // 扱える言語（テキスト入力）
    private JTextField availableLanguageField;

    // 経歴・研修・備考の複数行入力欄
    private JTextArea careerArea, trainingArea, remarksArea;

    // スキルスコア用コンボボックス
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;

    // ボタン（保存・戻る）
    private JButton saveButton, backButton;

    // 社員情報を管理するクラス
    private final EmployeeManager MANAGER = new EmployeeManager();

    // 現在表示中の社員情報（編集対象）
    private EmployeeInformation employeeInformation;

    /**
     * コンストラクタ：エンジニア情報を受け取る
     */
    public ViewEditScreen() {
        // 画面タイトルの設定
        frame.setTitle("エンジニア情報 編集画面");
    }

    // 編集画面の構成要素をまとめて初期化する
    private void setupEditScreen() {
        setupEmployeeId(); // 社員ID欄を設定
        setupNameFields(); // 氏名欄（漢字・フリガナ）を設定
        setupDateAndLanguageFields();// 生年月日、入社年月日、言語欄を設定
        setupCareer(); // 経歴欄の設定
        setupSkills(); // スキルスコア欄の設定
        setupTraining(); // 研修受講歴の設定
        setupRemarks(); // 備考欄の設定
        setupButtons(); // 保存・戻るボタンの設定
    }

    // 表示処理（外部からこの画面を表示するために使用）
    public void view(EmployeeInformation employeeInformation) {
        this.employeeInformation = employeeInformation; // 編集対象の情報を保存
        setupEditScreen(); // UI構築
        setValues(); // 画面に社員情報を反映
        frame.setVisible(true); // 画面表示
    }

    // 社員ID欄のUI設定
    private void setupEmployeeId() {
        employeeIdField = placeholderTextField("01234xx");
        employeeIdField.setBounds(15, 5, 130, 30);
        employeeIdField.setEditable(false);
        idPanel.add(employeeIdField); // ID入力欄を idPanel に追加
    }

    // 氏名（フリガナ・漢字）のUIを構築
    private void setupNameFields() {
        // フリガナ（姓）
        rubyLastNameField = placeholderTextField("ヤマダ");
        rubyLastNameField.setBounds(15, 15, 195, 30);
        namePanel.add(rubyLastNameField);

        // フリガナ（名）
        rubyFirstNameField = placeholderTextField("タロウ");
        rubyFirstNameField.setBounds(215, 15, 195, 30);
        namePanel.add(rubyFirstNameField);
        // 漢字（姓）
        lastNameField = placeholderTextField("山田");
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(15, 55, 195, 40);
        namePanel.add(lastNameField);

        // 漢字（名）
        firstNameField = placeholderTextField("太郎");
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(215, 55, 195, 40);
        namePanel.add(firstNameField);
    }

    // 日付・言語入力欄のUIを構築
    private void setupDateAndLanguageFields() {
        // 生年月日欄
        birthdDayPanel.add(new JLabel("生年月日"), BorderLayout.NORTH);
        birthPanel.add(dateSelector(birthYearCombo, birthMonthCombo, birthDayCombo));
        birthPanel.setBackground(Color.WHITE);
        birthdDayPanel.add(birthPanel, BorderLayout.SOUTH);

        // 入社年月日欄
        joiningDatePanel.add(new JLabel("入社年月"), BorderLayout.NORTH);
        joinPanel.add(dateSelector(joinYearCombo, joinMonthCombo, joinDayCombo));
        joinPanel.setBackground(Color.WHITE);
        joiningDatePanel.add(joinPanel, BorderLayout.SOUTH);

        // エンジニア歴欄
        engineerDatePanel.add(new JLabel("エンジニア歴"), BorderLayout.NORTH);
        engPanel.add(engineerDateSelector(engYearCombo, engMonthCombo));
        engPanel.setBackground(Color.WHITE);
        engineerDatePanel.add(engPanel, BorderLayout.SOUTH);

        // 扱える言語欄
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

    // 経歴入力欄のUI構築
    private void setupCareer() {
        careerPanel.add(createLabel("経歴", 0, 0), BorderLayout.NORTH);
        careerArea = new JTextArea(5, 30);
        careerArea.setLineWrap(true); // テキストがはみ出さないよう自動で折り返す
        placeholderTextArea("経歴", careerArea); // プレースホルダを設定
        JScrollPane careerScroll = new JScrollPane(careerArea); // スクロールバー付き
        careerPanel.add(careerScroll, BorderLayout.CENTER); // パネル中央に配置
    }

    // スキルスコア欄のUI構築
    private void setupSkills() {
        skillsPanel.add(createLabel("スキル", 0, 0), BorderLayout.NORTH); // タイトルラベルを上部に追加

        // 4行2列のグリッドレイアウトでスキル入力欄を配置
        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        skillPanel.setBackground(Color.LIGHT_GRAY);

        techCombo = createScoreCombo(); // 技術力用コンボボックス
        commCombo = createScoreCombo(); // コミュニケーション能力用
        attitudeCombo = createScoreCombo(); // 受講態度用
        leaderCombo = createScoreCombo(); // リーダーシップ用

        // ラベルとコンボボックスを交互に追加（横並びに配置）
        skillPanel.add(new JLabel("技術力"));
        skillPanel.add(techCombo);
        skillPanel.add(new JLabel("コミュニケーション能力"));
        skillPanel.add(commCombo);
        skillPanel.add(new JLabel("受講態度"));
        skillPanel.add(attitudeCombo);
        skillPanel.add(new JLabel("リーダーシップ"));
        skillPanel.add(leaderCombo);

        skillPanel.setBounds(0, 10, 360, 10);
        skillsPanel.add(skillPanel, BorderLayout.CENTER); // スキルパネルを中央に配置
    }

    // 研修受講歴欄のUI構築
    private void setupTraining() {
        trainingRecordsPanel.add(createLabel("研修受講歴", 0, 0), BorderLayout.NORTH); // 上部にラベル
        trainingArea = new JTextArea(5, 30); // 複数行の入力欄
        trainingArea.setLineWrap(true); // 自動折り返し
        placeholderTextArea("2000年4月1日株式会社XXXX入社", trainingArea); // プレースホルダー設定
        JScrollPane trainingScroll = new JScrollPane(trainingArea); // スクロール可能に
        trainingRecordsPanel.add(trainingScroll, BorderLayout.CENTER); // 中央に追加
    }

    // 備考欄のUI構築
    private void setupRemarks() {
        remarksPanel.add(createLabel("備考", 440, 340), BorderLayout.NORTH); // ラベル追加
        remarksArea = new JTextArea(5, 30); // 複数行の備考欄
        remarksArea.setLineWrap(true); // 自動折り返し
        placeholderTextArea("特になし", remarksArea); // プレースホルダー
        JScrollPane remarksScroll = new JScrollPane(remarksArea); // スクロール可能に
        remarksPanel.add(remarksScroll, BorderLayout.CENTER); // 備考欄をパネルに追加
    }

    // 戻る・保存ボタンの設定
    private void setupButtons() {
        bottomPanel.setLayout(null); // パネル内の配置を自由に設定

        // 戻るボタン（編集中止）
        backButton = new JButton("< 編集キャンセル");
        backButton.setBounds(0, 0, 140, 30); // ボタンの位置とサイズ
        bottomPanel.add(backButton); // パネルに追加

        // 戻るボタン押下時の確認ダイアログ処理
        backButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "保存せず前画面に戻ると\n編集中の内容は破棄されますが\n本当によろしいですか？",
                    "確認",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                refreshUI(); // UIリセット
                ViewDetailsScreen details = new ViewDetailsScreen();
                details.view(employeeInformation); // 詳細画面に戻る
            }
            // 「いいえ」の場合は何もせずにそのまま
        });

        // 保存ボタン
        saveButton = new JButton("保存");
        saveButton.setBounds(350, 0, 80, 30); // ボタン位置とサイズ
        bottomPanel.add(saveButton); // パネルに追加
        // 保存ボタン押下時の処理（確認ダイアログ → バリデーション → 保存 → 一覧画面へ）
        saveButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "この情報で上書きしますか？",
                    "確認",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            // 「いいえ」または「×」を選んだ場合は何もせず終了
            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            // 各種バリデーションチェック（未入力・文字数制限・禁止文字）
            // --- 氏名（漢字）の取得とチェック ---
            String lastName = lastNameField.getText().trim();
            String firstName = firstNameField.getText().trim();

            if (lastName.isEmpty() || firstName.isEmpty()) {
                showValidationError("姓と名は必須です");
                return;
            }

            if (lastName.codePointCount(0, lastName.length()) > 15 ||
                    firstName.codePointCount(0, firstName.length()) > 15) {
                showValidationError("氏名（漢字）は15文字以内で入力してください");
                return;
            }

            // 禁止文字チェック（漢字）
            if (lastName.matches(".*[\\uFF61-\\uFF9F].*")
                    || lastName.matches(".*[Ａ-Ｚａ-ｚ].*")
                    || lastName.matches(".*[！＠＃＄％＾＆＊（）＿＋＝￥|｛｝［］：；“”’＜＞？／\\\\].*")
                    || firstName.matches(".*[\\uFF61-\\uFF9F].*")
                    || firstName.matches(".*[Ａ-Ｚａ-ｚ].*")
                    || firstName.matches(".*[！＠＃＄％＾＆＊（）＿＋＝￥|｛｝［］：；“”’＜＞？／\\\\].*")) {
                showValidationError("使用できない文字が含まれています");
                return;
            }

            // サロゲートペア（絵文字など）の禁止
            // 環境依存文字一覧（拡張可能）
            String[] envDependentChars = {
                    "髙", "﨑", "𠮷", "辻", "①", "②", "③", "㊤", "㈱", "㈲", "℡", "㎜", "㌔", "🈂", "🅰", "🅱", "©", "®", "™",
                    "😃", "💻"
            };

            // 氏名に環境依存文字が含まれていないかをチェック
            for (String ch : envDependentChars) {
                if (lastName.contains(ch) || firstName.contains(ch)) {
                    showValidationError("使用できない文字が含まれています");
                    return;
                }
            }

            // サロゲートペア（絵文字など）の禁止（既存）
            Pattern surrogatePattern = Pattern.compile("[\\uD800-\\uDBFF][\\uDC00-\\uDFFF]");
            if (surrogatePattern.matcher(lastName).find() || surrogatePattern.matcher(firstName).find()) {
                showValidationError("使用できない文字が含まれています");
                return;
            }

            // --- フリガナのチェック（全角カタカナのみ・15文字以内） ---
            String rubyLastName = rubyLastNameField.getText().trim();
            String rubyFirstName = rubyFirstNameField.getText().trim();

            if (rubyLastName.isEmpty() || rubyFirstName.isEmpty()) {
                showValidationError("フリガナは必須です");
                return;
            }

            if (rubyLastName.codePointCount(0, rubyLastName.length()) > 15 ||
                    rubyFirstName.codePointCount(0, rubyFirstName.length()) > 15) {
                showValidationError("氏名（フリガナ）は15文字以内で入力してください");
                return;
            }
            // 禁止文字チェック（フリガナ）
            if (rubyLastName.matches(".*[\\uFF61-\\uFF9F].*")
                    || rubyLastName.matches(".*[\\u3040-\\u309F].*") // ひらがな
                    || rubyLastName.matches(".*[\\u4E00-\\u9FFF].*") // 漢字
                    || rubyLastName.matches(".*[A-Za-z].*") // 半角英字
                    || rubyLastName.matches(".*[！＠＃＄％＾＆＊（）＿＋＝￥|｛｝［］：；“”’＜＞？／\\\\].*")
                    || rubyFirstName.matches(".*[\\uFF61-\\uFF9F].*")
                    || rubyFirstName.matches(".*[\\u3040-\\u309F].*")
                    || rubyFirstName.matches(".*[\\u4E00-\\u9FFF].*")
                    || rubyFirstName.matches(".*[A-Za-z].*")
                    || rubyFirstName.matches(".*[！＠＃＄％＾＆＊（）＿＋＝￥|｛｝［］：；“”’＜＞？／\\\\].*")) {
                showValidationError("使用できない文字が含まれています");
                return;
            }

            // --- 扱える言語のチェック ---
            String setAvailable = availableLanguageField.getText().trim();

            if (setAvailable.isEmpty()) {
                showValidationError("使える言語は必須です");
                return;
            }

if (setAvailable.codePointCount(0, setAvailable.length()) > 100) {
    showValidationError("使える言語は100文字以内で入力してください");
    return;
}

// 禁止文字（記号・全角英字・環境依存記号など）チェックを追加
if (setAvailable.matches(".*[!@#$%^&*()_+=|{}\\[\\]:;\"'<>?/\\\\Ａ-Ｚａ-ｚ①-⑩©®™😃💻！＠＃＄％＾＆＊（）＿＋＝￥｛｝［］：“”’＜＞？／\\\\].*")) {
    showValidationError("使用できない文字が含まれています");
    return;
}

// 環境依存文字リストからのチェック
for (String ch : envDependentChars) {
    if (setAvailable.contains(ch)) {
        showValidationError("使用できない文字が含まれています");
        return;
    }
}

// サロゲートペア（絵文字など）のチェック
if (surrogatePattern.matcher(setAvailable).find()) {
    showValidationError("使用できない文字が含まれています");
    return;
}

            // --- 経歴のチェック ---
            String career = careerArea.getText().trim();
            if (career.isEmpty()) {
                showValidationError("経歴は必須です");
                return;
            }

            if (career.codePointCount(0, career.length()) > 400) {
                showValidationError("経歴は400文字以内で入力してください");
                return;
            }

            // --- 研修受講歴のチェック ---
            String training = trainingArea.getText().trim();
            if (training.isEmpty()) {
                showValidationError("研修受講歴は必須です");
                return;
            }

            if (training.codePointCount(0, training.length()) > 400) {
                showValidationError("研修受講歴は400文字以内で入力してください");
                return;
            }

            // --- 備考のチェック（任意だが400文字制限） ---
            String remarks = remarksArea.getText().trim();

            if (remarks.codePointCount(0, remarks.length()) > 400) {
                showValidationError("備考は400文字以内で入力してください");
                return;
            }

            // バリデーションOK：保存処理へ進む
            EmployeeInformation editInfo = collectInputData(); // 入力情報をオブジェクトにまとめる
            if (editInfo != null) {
                EmployeeInfoUpdate update = new EmployeeInfoUpdate(); // 更新スレッド作成
                update.update(editInfo); // 更新処理をセット
                Thread updateThread = new Thread(update); // スレッドに詰める
                updateThread.start(); // スレッドを実行
            }

            MANAGER.LOGGER.info("一覧画面に遷移"); // ログ出力
            refreshUI(); // 画面をクリア
            ViewTopScreen top = new ViewTopScreen();
            top.View(); // 一覧画面に遷移
        });
    }

    /**
     * ラベル（JLabel）を生成する汎用メソッド
     * 
     * @param title 表示するラベルの文字列
     * @param x     ラベルのx座標
     * @param y     ラベルのy座標
     * @return JLabelインスタンス
     */
    private JLabel createLabel(String title, int x, int y) {
        JLabel label = new JLabel(title);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    /**
     * スキルスコア用のコンボボックス（1.0〜5.0）を作成
     * 
     * @return JComboBoxインスタンス（スキルスコア選択用）
     */
    private JComboBox<String> createScoreCombo() {
        String[] scores = { "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0" };
        return new JComboBox<>(scores);
    }

    /**
     * 生年月日・入社日などの日付選択パネル（年・月・日）を作成
     */
    private JPanel dateSelector(JComboBox<Integer> yearBox, JComboBox<Integer> monthBox, JComboBox<Integer> dayBox) {
        LocalDate now = LocalDate.now(); // 現在日付の取得
        Integer[] nowInteger = { now.getYear(), now.getMonthValue(), now.getDayOfMonth() };

        // 年のモデル（現在から過去100年分）
        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>();
        for (int i = nowInteger[0] - 100; i <= nowInteger[0]; i++) {
            yearModel.addElement(i);
        }
        yearBox.setModel(yearModel);

        // 月のモデル（1～12月）
        DefaultComboBoxModel<Integer> monthModel = new DefaultComboBoxModel<>();
        for (int i = 1; i <= 12; i++) {
            monthModel.addElement(i);
        }
        monthBox.setModel(monthModel);

        // 日のモデル（各月に応じて最大日数を決定）
        int year = yearModel.getElementAt(0);
        int month = monthModel.getElementAt(0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1); // 月は0始まり
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DefaultComboBoxModel<Integer> dayModel = new DefaultComboBoxModel<>();
        for (int i = 1; i <= maxDay; i++) {
            dayModel.addElement(i);
        }
        dayBox.setModel(dayModel);

        // パネルに追加して返す
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(205, 40));
        panel.setMaximumSize(new Dimension(205, 40));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.add(yearBox);
        panel.add(new JLabel("年"));
        panel.add(monthBox);
        panel.add(new JLabel("月"));
        panel.add(dayBox);
        panel.add(new JLabel("日"));
        return panel;
    }

    /**
     * エンジニア歴入力用の年・月セレクターを作成
     */
    private JPanel engineerDateSelector(JComboBox<Integer> yearBox, JComboBox<Integer> monthBox) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(155, 40));
        panel.setMaximumSize(new Dimension(140, 40));
        panel.setBackground(Color.LIGHT_GRAY);

        // 年（0年～49年）
        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>();
        for (int i = 0; i < 50; i++) {
            yearModel.addElement(i);
        }
        yearBox.setModel(yearModel);

        // 月（0～11ヶ月）
        DefaultComboBoxModel<Integer> monthModel = new DefaultComboBoxModel<>();
        for (int i = 0; i <= 11; i++) {
            monthModel.addElement(i);
        }
        monthBox.setModel(monthModel);

        panel.add(yearBox);
        panel.add(new JLabel("年"));
        panel.add(monthBox);
        panel.add(new JLabel("月"));
        return panel;
    }

    /**
     * プレースホルダー付きの JTextField を作成
     */
    private JTextField placeholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder, 7);
        textField.setForeground(Color.GRAY); // 初期文字はグレー表示
        return textField;
    }

    /**
     * プレースホルダー付きの JTextArea を作成（focus制御付き）
     */
    private JTextArea placeholderTextArea(String placeholder, JTextArea textArea) {
        textArea.setText(placeholder);
        textArea.setForeground(Color.GRAY);

        // 入力開始時にプレースホルダーを消す
        textArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholder)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            // 入力が空の場合、プレースホルダーを復元
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText(placeholder);
                    textArea.setForeground(Color.GRAY);
                }
            }
        });

        return textArea;
    }

    /**
     * 入力フィールドからデータを取得し、EmployeeInformation を生成
     */
    public EmployeeInformation collectInputData() {
        try {
            System.out.println("【DEBUG】データ取得開始");

            // 各フィールドの入力値を取得（プレースホルダーは無視）
            EmployeeInformation employee = new EmployeeInformation();
            employee.setEmployeeID(getFieldValue(employeeIdField, "01234xx"));
            employee.setlastName(getFieldValue(lastNameField, "山田"));
            employee.setFirstname(getFieldValue(firstNameField, "太郎"));
            employee.setRubyLastName(getFieldValue(rubyLastNameField, "ヤマダ"));
            employee.setRubyFirstname(getFieldValue(rubyFirstNameField, "タロウ"));
            employee.setBirthday(getDateFromComboBoxes(birthYearCombo, birthMonthCombo, birthDayCombo));
            employee.setJoiningDate(getDateFromComboBoxes(joinYearCombo, joinMonthCombo, joinDayCombo));
            int years = (int) engYearCombo.getSelectedItem();
            int months = (int) engMonthCombo.getSelectedItem();
            employee.setEngineerDate(years * 12 + months);
            employee.setAvailableLanguages(getFieldValue(availableLanguageField, "html・CSS"));
            employee.setCareerDate(getFieldValue(careerArea, "XXXXXXX"));
            employee.setSkillPoint(parseScore(techCombo));
            employee.setAttitudePoint(parseScore(attitudeCombo));
            employee.setCommunicationPoint(parseScore(commCombo));
            employee.setLeadershipPoint(parseScore(leaderCombo));
            employee.setTrainingDate(getFieldValue(trainingArea, "2000年4月1日株式会社XXXX入社"));
            employee.setRemarks(getFieldValue(remarksArea, "特になし"));
            employee.setUpdatedDay(new Date()); // 更新日時は現在時刻
            System.out.println("【DEBUG】データ取得完了");
            return employee;
        } catch (Exception e) {
            e.printStackTrace(); // エラーの詳細をコンソールに出力
            showValidationError("データ取得中にエラーが発生しました");
            return null;
        }
    }

    /**
     * プレースホルダーと同じ値なら空文字に置き換えるユーティリティメソッド
     */
    private String getFieldValue(JTextComponent field, String placeholder) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        // プレースホルダーと同じ文字列でも、実際に編集していれば有効とみなす
        return text;
    }

    /**
     * JComboBox から選択されたスコア値（文字列）を double に変換
     */
    private double parseScore(JComboBox<String> combo) {
        return Double.parseDouble((String) combo.getSelectedItem());
    }

    /**
     * 年・月・日のコンボボックスから Date を生成
     */
    private Date getDateFromComboBoxes(JComboBox<Integer> yearCombo, JComboBox<Integer> monthCombo,
            JComboBox<Integer> dayCombo) {
        int year = (int) yearCombo.getSelectedItem();
        int month = (int) monthCombo.getSelectedItem() - 1; // 月は0始まり
        int day = (dayCombo != null) ? (int) dayCombo.getSelectedItem() : 1;

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    // ========================== ダイアログ・画面操作 ==============================

    /**
     * エラーメッセージを画面に赤字で表示する（保存ボタンの上などに表示）
     */
    public void showErrorMessageOnPanel(String message) {
        errorPanel.removeAll();
        JLabel errorLabel = new JLabel(message);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        errorPanel.add(errorLabel);
    }

    /**
     * 成功時に情報ダイアログを表示
     */
    public void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * バリデーションエラーが発生した場合にエラーダイアログを表示
     */
    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * EmployeeInformation オブジェクトの値を画面の各項目に反映
     */
    private void setValues() {
        employeeIdField.setText(employeeInformation.getEmployeeID());
        rubyLastNameField.setText(employeeInformation.getRubyLastName());
        rubyFirstNameField.setText(employeeInformation.getRubyFirstname());
        lastNameField.setText(employeeInformation.getLastName());
        firstNameField.setText(employeeInformation.getFirstname());
        availableLanguageField.setText(employeeInformation.getAvailableLanguages());
        careerArea.setText(employeeInformation.getCareerDate());

        techCombo.setSelectedItem(String.format("%.1f", employeeInformation.getSkillPoint()));
        attitudeCombo.setSelectedItem(String.format("%.1f", employeeInformation.getAttitudePoint()));
        commCombo.setSelectedItem(String.format("%.1f", employeeInformation.getCommunicationPoint()));
        leaderCombo.setSelectedItem(String.format("%.1f", employeeInformation.getLeadershipPoint()));

        trainingArea.setText(employeeInformation.getTrainingDate());
        remarksArea.setText(employeeInformation.getRemarks());

        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(employeeInformation.getBirthday());
        birthYearCombo.setSelectedItem(birthCal.get(Calendar.YEAR));
        birthMonthCombo.setSelectedItem(birthCal.get(Calendar.MONTH) + 1);
        birthDayCombo.setSelectedItem(birthCal.get(Calendar.DAY_OF_MONTH));

        Calendar joinCal = Calendar.getInstance();
        joinCal.setTime(employeeInformation.getJoiningDate());
        joinYearCombo.setSelectedItem(joinCal.get(Calendar.YEAR));
        joinMonthCombo.setSelectedItem(joinCal.get(Calendar.MONTH) + 1);
        joinDayCombo.setSelectedItem(joinCal.get(Calendar.DAY_OF_MONTH));

        int totalMonths = employeeInformation.getEngineerDate();
        engYearCombo.setSelectedItem(totalMonths / 12);
        engMonthCombo.setSelectedItem(totalMonths % 12);
    }
}
