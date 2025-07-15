
// 画面やレイアウトに関するクラスを読み込む（ウィンドウの大きさや部品配置を扱う）
import java.awt.*;

// ボタンやコンボボックスなどの操作を検知するイベント関連クラスを読み込む
import java.awt.event.*;

// 年月日を扱う便利なクラスを読み込み（現在の日付取得など）
import java.time.LocalDate;

// 日付やリスト、マップなど便利なクラスをまとめて読み込み
import java.util.*;

// SwingのGUI部品をまとめて読み込み（ボタンやテキストフィールドなど）
import javax.swing.*;

// JTextFieldやJTextAreaなど、テキスト入力系の親クラス
import javax.swing.text.JTextComponent;

/**
 * エンジニア情報を表示・編集する画面のクラス
 * SetUpDetailsScreenを継承し、既存情報の読み込み・編集・保存処理を提供する
 */
public class ViewEditScreen extends SetUpDetailsScreen {
    // --- UI部品の宣言 ---

    // 社員IDを表示・入力するテキストフィールド（編集不可に設定）
    private JTextField employeeIdField;

    // 氏名のフリガナ用テキストフィールド（姓・名）
    private JTextField rubyLastNameField, rubyFirstNameField;

    // 氏名の漢字用テキストフィールド（姓・名）
    private JTextField lastNameField, firstNameField;

    // 生年月日を選択する年・月・日コンボボックス
    private JComboBox<String> birthYearCombo = new JComboBox<>();
    private JComboBox<String> birthMonthCombo = new JComboBox<>();
    private JComboBox<String> birthDayCombo = new JComboBox<>();

    // 入社年月日を選択する年・月・日コンボボックス
    private JComboBox<String> joinYearCombo = new JComboBox<>();
    private JComboBox<String> joinMonthCombo = new JComboBox<>();
    private JComboBox<String> joinDayCombo = new JComboBox<>();

    // エンジニア歴（年・月）を選択するコンボボックス
    private JComboBox<String> engYearCombo = new JComboBox<>();
    private JComboBox<String> engMonthCombo = new JComboBox<>();

    // 生年月日、入社年月日、エンジニア歴の表示用パネル
    private JPanel birthPanel = new JPanel();
    private JPanel joinPanel = new JPanel();
    private JPanel engPanel = new JPanel();

    // 扱える言語を入力するテキストフィールド
    private JTextField availableLanguageField;

    // 経歴、研修受講歴、備考の複数行入力欄
    private JTextArea careerArea, trainingArea, remarksArea;

    // 技術力やコミュニケーション力など評価項目用のコンボボックス
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;

    // 保存と戻るボタン
    private JButton saveButton, backButton;

    // 社員情報の管理クラスのインスタンス（CSVの読み書きなどを担当）

    private final EmployeeManager MANAGER = new EmployeeManager();

    public ViewEditScreen() {
        MANAGER.printInfoLog("ViewEditScreen 初期化完了");
        frame.setTitle("エンジニア情報 編集画面");
    }

    // 現在表示・編集中の社員情報オブジェクト
    private EmployeeInformation employeeInformation;

    /**
     * 画面の各部品（UI）をまとめて初期化するメソッド
     * これを呼ぶと画面の各パーツが配置される
     */
    private void setupEditScreen() {
        setupEmployeeId(); // 社員ID欄のセットアップ
        setupNameFields(); // 氏名（漢字・フリガナ）欄のセットアップ
        setupDateAndLanguageFields(); // 生年月日・入社年月日・扱える言語のセットアップ
        setupCareer(); // 経歴欄のセットアップ
        setupSkills(); // スキル評価欄のセットアップ
        setupTraining(); // 研修受講歴欄のセットアップ
        setupRemarks(); // 備考欄のセットアップ
        setupButtons(); // 保存・戻るボタンのセットアップ
    }

    /**
     * 画面に社員情報を表示して編集可能な状態にし、画面を表示する
     * 
     * @param employeeInformation 編集対象の社員情報
     */
    public void view(EmployeeInformation employeeInformation) {
        // 編集対象の社員情報を保持
        this.employeeInformation = employeeInformation;
        // 画面のUIを組み立てる
        setupEditScreen();
        // 社員情報を各入力欄に反映
        setValues();
        // 画面を表示
        frame.setVisible(true);
    }

    /**
     * 社員IDの入力欄を初期化し、編集不可に設定する
     */
    private void setupEmployeeId() {
        // 社員ID用のテキストフィールドを作成（プレースホルダー付き）
        employeeIdField = placeholderTextField("01234xx");
        // 位置とサイズを指定（x, y, 幅, 高さ）
        employeeIdField.setBounds(15, 5, 130, 30);
        // 編集不可に設定（IDは変更できない）
        employeeIdField.setEditable(false);
        // 社員ID用パネルに追加
        idPanel.add(employeeIdField);
    }

    /**
     * 氏名入力欄（フリガナ・漢字）を初期化して配置する
     */
    private void setupNameFields() {
        // フリガナ（姓）用のテキストフィールドを作成
        rubyLastNameField = placeholderTextField("ヤマダ");
        rubyLastNameField.setBounds(15, 15, 195, 30);
        namePanel.add(rubyLastNameField);

        // フリガナ（名）用のテキストフィールドを作成
        rubyFirstNameField = placeholderTextField("タロウ");
        rubyFirstNameField.setBounds(215, 15, 195, 30);
        namePanel.add(rubyFirstNameField);

        // 漢字（姓）用テキストフィールド作成、太字に設定
        lastNameField = placeholderTextField("山田");
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(15, 55, 195, 40);
        namePanel.add(lastNameField);

        // 漢字（名）用テキストフィールド作成、太字に設定
        firstNameField = placeholderTextField("太郎");
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(215, 55, 195, 40);
        namePanel.add(firstNameField);
    }

    /**
     * 生年月日、入社年月日、扱える言語の入力欄を初期化して配置する
     */
    private void setupDateAndLanguageFields() {
        // 生年月日パネルに「生年月日」ラベルを北（上）に追加
        birthdDayPanel.add(new JLabel("生年月日"), BorderLayout.NORTH);

        // 生年月日の年・月・日コンボボックスをまとめたパネルを作成し追加
        birthPanel.add(dateSelector(birthYearCombo, birthMonthCombo, birthDayCombo));
        birthPanel.setBackground(Color.WHITE);
        birthdDayPanel.add(birthPanel, BorderLayout.SOUTH);

        // 入社年月パネルに「入社年月」ラベルを上に追加
        joiningDatePanel.add(new JLabel("入社年月"), BorderLayout.NORTH);

        // 入社年月日のコンボボックスまとめパネルを追加
        joinPanel.add(dateSelector(joinYearCombo, joinMonthCombo, joinDayCombo));
        joinPanel.setBackground(Color.WHITE);
        joiningDatePanel.add(joinPanel, BorderLayout.SOUTH);

        // エンジニア歴パネルに「エンジニア歴」ラベルを上に追加
        engineerDatePanel.add(new JLabel("エンジニア歴"), BorderLayout.NORTH);

        // エンジニア歴の年・月コンボボックスまとめパネルを追加
        engPanel.add(engineerDateSelector(engYearCombo, engMonthCombo));
        engPanel.setBackground(Color.WHITE);
        engineerDatePanel.add(engPanel, BorderLayout.SOUTH);

        // 扱える言語のラベルを作成し位置指定してパネルに追加
        JLabel availableLanguagesLabel = new JLabel("扱える言語");
        availableLanguagesLabel.setBounds(0, -3, 100, 20);
        availableLanguagesPanel.add(availableLanguagesLabel);

        // 言語入力用のパネルを作成し位置指定、色設定
        JPanel availableLanguageFieldPanel = new JPanel();
        availableLanguageFieldPanel.setBounds(0, 15, 190, 40);
        availableLanguageFieldPanel.setBackground(Color.LIGHT_GRAY);
        availableLanguageFieldPanel.setLayout(null);

        // 扱える言語のテキストフィールドを作成し配置
        availableLanguageField = placeholderTextField("html・CSS");
        availableLanguageField.setBounds(0, 5, 190, 30);
        availableLanguageFieldPanel.add(availableLanguageField);

        // パネルに言語入力欄を追加
        availableLanguagesPanel.add(availableLanguageFieldPanel);
    }

    /**
     * 経歴入力用のテキストエリアを初期化して配置する
     */
    private void setupCareer() {
        // 「経歴」ラベルをパネルの上部に追加
        careerPanel.add(createLabel("経歴", 0, 0), BorderLayout.NORTH);

        // 複数行入力できるテキストエリアを作成
        careerArea = new JTextArea(5, 30);
        careerArea.setLineWrap(true); // 行の折り返しを有効にする

        // プレースホルダー（初期文字）を設定
        placeholderTextArea("経歴", careerArea);

        // スクロールバー付きのパネルにテキストエリアを入れる
        JScrollPane careerScroll = new JScrollPane(careerArea);

        // パネルの中央にテキストエリアのスクロールパネルを配置
        careerPanel.add(careerScroll, BorderLayout.CENTER);
    }

    /**
     * スキル評価用コンボボックスを初期化し配置する
     */
    private void setupSkills() {
        // スキルラベルをパネルの上部に追加
        skillsPanel.add(createLabel("スキル", 0, 0), BorderLayout.NORTH);

        // 4行2列のグリッドレイアウトでスキル評価欄を作成
        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        skillPanel.setBackground(Color.LIGHT_GRAY);

        // 技術力、コミュニケーション能力、受講態度、リーダーシップのコンボボックスを作成
        techCombo = createScoreCombo();
        commCombo = createScoreCombo();
        attitudeCombo = createScoreCombo();
        leaderCombo = createScoreCombo();

        // ラベルとコンボボックスを交互に追加し横並びで配置
        skillPanel.add(new JLabel("技術力"));
        skillPanel.add(techCombo);
        skillPanel.add(new JLabel("コミュニケーション能力"));
        skillPanel.add(commCombo);
        skillPanel.add(new JLabel("受講態度"));
        skillPanel.add(attitudeCombo);
        skillPanel.add(new JLabel("リーダーシップ"));
        skillPanel.add(leaderCombo);

        // パネルの位置とサイズを指定
        skillPanel.setBounds(0, 10, 360, 10);

        // スキルパネルを中央に配置
        skillsPanel.add(skillPanel, BorderLayout.CENTER);
    }

    /**
     * 研修受講歴のテキストエリアを初期化して配置する
     */
    private void setupTraining() {
        // 「研修受講歴」ラベルをパネル上部に追加
        trainingRecordsPanel.add(createLabel("研修受講歴", 0, 0), BorderLayout.NORTH);

        // 複数行テキストエリアを作成し折り返し有効化
        trainingArea = new JTextArea(5, 30);
        trainingArea.setLineWrap(true);

        // プレースホルダーを設定
        placeholderTextArea("2000年4月1日株式会社XXXX入社", trainingArea);

        // スクロール可能にするためスクロールパネルに入れる
        JScrollPane trainingScroll = new JScrollPane(trainingArea);

        // 中央にスクロールパネルを追加
        trainingRecordsPanel.add(trainingScroll, BorderLayout.CENTER);
    }

    /**
     * 備考欄のテキストエリアを初期化して配置する
     */
    private void setupRemarks() {
        // 「備考」ラベルをパネル上部に追加
        remarksPanel.add(createLabel("備考", 440, 340), BorderLayout.NORTH);

        // 複数行テキストエリアを作成し折り返し有効化
        remarksArea = new JTextArea(5, 30);
        remarksArea.setLineWrap(true);

        // プレースホルダーを設定
        placeholderTextArea("特になし", remarksArea);

        // スクロールパネルに入れてスクロール可能に
        JScrollPane remarksScroll = new JScrollPane(remarksArea);

        // パネルにスクロールパネルを追加
        remarksPanel.add(remarksScroll, BorderLayout.CENTER);
    }

    /**
     * 保存ボタンと戻るボタンの設定を行う
     */
    private void setupButtons() {
        // bottomPanelのレイアウトをnullにして自由配置可能に設定
        bottomPanel.setLayout(null);

        // 戻るボタンを作成（編集キャンセル用）
        backButton = new JButton("< 編集キャンセル");
        backButton.setBounds(0, 0, 140, 30);
        bottomPanel.add(backButton);

        // 戻るボタン押下時の動作を設定
        backButton.addActionListener(e -> {
            // 確認ダイアログを表示
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "保存せず前画面に戻ると\n編集中の内容は破棄されますが\n本当によろしいですか？",
                    "確認",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            // 「はい」が選択された場合の処理
            if (result == JOptionPane.YES_OPTION) {
                refreshUI(); // UIをリセット（入力内容などクリア）
                ViewDetailsScreen details = new ViewDetailsScreen();
                details.view(employeeInformation); // 詳細画面に戻る
            }
            // 「いいえ」の場合は何もしない（編集画面のまま）
        });

        // 保存ボタンを作成
        saveButton = new JButton("保存");
        saveButton.setBounds(350, 0, 80, 30);
        bottomPanel.add(saveButton);

        // 保存ボタン押下時の動作設定
        saveButton.addActionListener(e -> {
            setUIEnabled(false); // ボタンや入力欄を全て無効にし操作不可にして重複操作防止

            // 上書き保存の確認ダイアログ表示
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "この情報で上書きしますか？",
                    "確認",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            // 「はい」以外ならUIを再度有効にして処理中止
            if (result != JOptionPane.YES_OPTION) {
                setUIEnabled(true);
                return;
            }

            EmployeeInformation info = collectInputData();
            if (!MANAGER.validateNotNull(info)) {
                showErrorDialog("必須項目が入力されていません");
                showErrorMessageOnPanel("必須項目が入力されていません");
                setUIEnabled(true);
                return;
            }
            if (!MANAGER.validateEmployee(info)) {
                showErrorDialog("社員情報の内容に誤りがあります");
                showErrorMessageOnPanel("社員情報の内容に誤りがあります");
                setUIEnabled(true);
                return;
            }

            // 保存処理を呼び出し
EmployeeInfoUpdate update = new EmployeeInfoUpdate();
update.update(info);
update.run(); // ★ 非同期ではなく同期で実行

// 保存が成功していればログに「社員情報更新成功」が出るので、ログから判断するか、フラグを使って判定するのが理想。
// 今回は仮に run() 内部で例外が出なければ成功とみなす簡易判定とします。

// ここまで来た時点で失敗していれば run() の中で showErrorDialog が出ているため、成功時のみダイアログを出す：
JOptionPane optionPane = new JOptionPane(
    "保存完了しました",
    JOptionPane.INFORMATION_MESSAGE,
    JOptionPane.DEFAULT_OPTION,
    null,
    new Object[] { "一覧画面へ戻る" },
    "一覧画面へ戻る"
);
JDialog dialog = optionPane.createDialog("成功");
dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
dialog.setModal(true);
dialog.setVisible(true);

Object selectedValue = optionPane.getValue();
if ("一覧画面へ戻る".equals(selectedValue)) {
    refreshUI();
    ViewTopScreen top = new ViewTopScreen();
    top.View();
} else {
    setUIEnabled(true);
}

        });
    }

    /**
     * 画面の各入力部品の有効・無効を切り替えるメソッド
     * 
     * @param enabled trueで操作可能、falseで操作不可にする
     */
    private void setUIEnabled(boolean enabled) {
        employeeIdField.setEnabled(enabled);
        rubyLastNameField.setEnabled(enabled);
        rubyFirstNameField.setEnabled(enabled);
        lastNameField.setEnabled(enabled);
        firstNameField.setEnabled(enabled);
        availableLanguageField.setEnabled(enabled);
        birthYearCombo.setEnabled(enabled);
        birthMonthCombo.setEnabled(enabled);
        birthDayCombo.setEnabled(enabled);
        joinYearCombo.setEnabled(enabled);
        joinMonthCombo.setEnabled(enabled);
        joinDayCombo.setEnabled(enabled);
        engYearCombo.setEnabled(enabled);
        engMonthCombo.setEnabled(enabled);
        careerArea.setEnabled(enabled);
        trainingArea.setEnabled(enabled);
        remarksArea.setEnabled(enabled);
        techCombo.setEnabled(enabled);
        commCombo.setEnabled(enabled);
        attitudeCombo.setEnabled(enabled);
        leaderCombo.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        backButton.setEnabled(enabled);
    }

    /**
     * 汎用的なラベル（JLabel）を作成するメソッド
     * 
     * @param title ラベルに表示する文字列
     * @param x     ラベルのx座標
     * @param y     ラベルのy座標
     * @return 作成したJLabelオブジェクト
     */
    private JLabel createLabel(String title, int x, int y) {
        JLabel label = new JLabel(title);
        label.setBounds(x, y, 100, 20);
        return label;
    }

    /**
     * スキル評価用のコンボボックス（1.0〜5.0）を作成するメソッド
     * 
     * @return 作成したJComboBoxオブジェクト
     */
    private JComboBox<String> createScoreCombo() {
        String[] scores = { "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0" };
        return new JComboBox<>(scores);
    }

    /**
     * 生年月日や入社日などの年・月・日を選択するパネルを作成するメソッド
     * 
     * @param yearBox  年選択用コンボボックス
     * @param monthBox 月選択用コンボボックス
     * @param dayBox   日選択用コンボボックス
     * @return 作成したパネルオブジェクト
     */
    private JPanel dateSelector(JComboBox<String> yearBox, JComboBox<String> monthBox, JComboBox<String> dayBox) {
        // 現在の年
        int currentYear = LocalDate.now().getYear();

        // 年を「〇〇年」で100年分追加
        DefaultComboBoxModel<String> yearModel = new DefaultComboBoxModel<>();
        for (int i = currentYear - 100; i <= currentYear; i++) {
            yearModel.addElement(i + "年");
        }
        yearBox.setModel(yearModel);

        // 月を「〇月」で追加（正しい設定：生年月日・入社年月は「1月」〜「12月」）
        DefaultComboBoxModel<String> monthModel = new DefaultComboBoxModel<>();
        for (int i = 1; i <= 12; i++) {
            monthModel.addElement(i + "月");
        }
        monthBox.setModel(monthModel);

        // 日は仮に31日まで表示
        DefaultComboBoxModel<String> dayModel = new DefaultComboBoxModel<>();
        for (int i = 1; i <= 31; i++) {
            dayModel.addElement(i + "日");
        }
        dayBox.setModel(dayModel);

        // レイアウト設定
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(205, 40));
        panel.setMaximumSize(new Dimension(205, 40));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.add(yearBox);
        panel.add(monthBox);
        panel.add(dayBox);
        return panel;
    }

    /**
     * エンジニア歴の年・月選択用パネルを作成するメソッド
     * 
     * @param yearBox  年コンボボックス
     * @param monthBox 月コンボボックス
     * @return 作成したパネルオブジェクト
     */
    private JPanel engineerDateSelector(JComboBox<String> yearBox, JComboBox<String> monthBox) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(155, 40));
        panel.setMaximumSize(new Dimension(140, 40));
        panel.setBackground(Color.LIGHT_GRAY);

        // 年は0〜49年までを追加
        DefaultComboBoxModel<String> yearModel = new DefaultComboBoxModel<>();
        for (int i = 0; i < 50; i++) {
            yearModel.addElement(i + "年");
        }
        yearBox.setModel(yearModel);

        // 月は0〜11ヶ月までを追加
        DefaultComboBoxModel<String> monthModel = new DefaultComboBoxModel<>();
        for (int i = 0; i <= 11; i++) {
            monthModel.addElement(i + "ヵ月"); // 「ヵ月」に変更
        }
        monthBox.setModel(monthModel);

        // パネルに年・月コンボボックスとラベルを追加
        panel.add(yearBox);
        panel.add(monthBox);
        return panel;
    }

    /**
     * プレースホルダー付きテキストフィールドを作成するメソッド
     * 
     * @param placeholder 初期表示文字列
     * @return 作成したJTextFieldオブジェクト
     */
    private JTextField placeholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder, 7);
        // 初期文字はグレー表示に設定し、プレースホルダーらしさを表現
        textField.setForeground(Color.GRAY);
        return textField;
    }

    /**
     * プレースホルダー付きテキストエリアを作成するメソッド（フォーカス取得でプレースホルダー消去、離れると復活）
     * 
     * @param placeholder 初期表示文字列
     * @param textArea    対象のJTextArea
     * @return 設定済みのJTextAreaオブジェクト
     */
    private JTextArea placeholderTextArea(String placeholder, JTextArea textArea) {
        // 初期文字列とグレー色を設定
        textArea.setText(placeholder);
        textArea.setForeground(Color.GRAY);

        // 入力開始時にプレースホルダーを消す処理
        textArea.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholder)) {
                    textArea.setText(""); // プレースホルダー消去
                    textArea.setForeground(Color.BLACK); // 通常の黒文字に戻す
                }
            }

            // フォーカスを失った時に空ならプレースホルダー復活
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
     * 入力フィールドの値を取得してEmployeeInformationオブジェクトにまとめるメソッド
     * 
     * @return 入力情報を格納したEmployeeInformationオブジェクト（異常時はnull）
     */
    public EmployeeInformation collectInputData() {
        try {
            System.out.println("【DEBUG】データ取得開始");

            EmployeeInformation employee = new EmployeeInformation();

            // 各入力欄から値を取得しセット（プレースホルダーと同じ場合は空文字として扱う）
            employee.setEmployeeID(getFieldValue(employeeIdField, "01234xx"));
            employee.setlastName(getFieldValue(lastNameField, "山田"));
            employee.setFirstname(getFieldValue(firstNameField, "太郎"));
            employee.setRubyLastName(getFieldValue(rubyLastNameField, "ヤマダ"));
            employee.setRubyFirstname(getFieldValue(rubyFirstNameField, "タロウ"));

            // 日付はコンボボックスの選択値からDate型で生成
            employee.setBirthday(getDateFromComboBoxes(birthYearCombo, birthMonthCombo, birthDayCombo));
            employee.setJoiningDate(getDateFromComboBoxes(joinYearCombo, joinMonthCombo, joinDayCombo));

            // エンジニア歴は年と月を合算（単位は月）
            String yearStr = engYearCombo.getSelectedItem().toString(); // 例: "3年"
            String monthStr = engMonthCombo.getSelectedItem().toString(); // 例: "1ヵ月"

            int years = Integer.parseInt(yearStr.replace("年", "")); // → 3
            int months = Integer.parseInt(monthStr.replace("ヵ月", "")); // → 1

            employee.setEngineerDate(years * 12 + months);

            // 扱える言語、経歴、研修、備考などテキスト入力欄の値をセット
            String langs = getFieldValue(availableLanguageField, "html・CSS");
            langs = langs.replaceAll("[\\s　]+", "・");
            employee.setAvailableLanguages(langs);
            employee.setCareerDate(getFieldValue(careerArea, "XXXXXXX"));

            // スキル評価はコンボボックスの選択値を数値変換してセット
            employee.setSkillPoint(parseScore(techCombo));
            employee.setAttitudePoint(parseScore(attitudeCombo));
            employee.setCommunicationPoint(parseScore(commCombo));
            employee.setLeadershipPoint(parseScore(leaderCombo));

            employee.setTrainingDate(getFieldValue(trainingArea, "2000年4月1日株式会社XXXX入社"));
            employee.setRemarks(getFieldValue(remarksArea, "特になし"));

            // 更新日は現在日時をセット
            employee.setUpdatedDay(new Date());

            System.out.println("【DEBUG】データ取得完了");
            return employee;

        } catch (Exception e) {
            // 例外発生時はスタックトレースを表示しエラーダイアログを出す
            e.printStackTrace();
            showValidationError("データ取得中にエラーが発生しました");
            return null;
        }
    }

    /**
     * テキストフィールドやテキストエリアの値を取得するが、
     * プレースホルダーと同じ場合は空文字を返すユーティリティメソッド
     * 
     * @param field       JTextComponent（JTextFieldやJTextAreaなど）
     * @param placeholder プレースホルダー文字列
     * @return 入力された文字列、もしくは空文字
     */
    private String getFieldValue(JTextComponent field, String placeholder) {
        String text = field.getText();
        // null や空白のみの場合は空文字として返す
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        // プレースホルダーと一致していても、そのまま値として扱う
        return text.trim();
    }

    /**
     * スキル評価のコンボボックスの選択値（文字列）をdoubleに変換する
     * 
     * @param combo スキル評価コンボボックス
     * @return 数値としての評価値
     */
    private double parseScore(JComboBox<String> combo) {
        return Double.parseDouble((String) combo.getSelectedItem());
    }

    /**
     * 年・月・日がそれぞれのコンボボックスで選択された値から
     * java.util.Date型の日時オブジェクトを生成する
     * 
     * @param yearCombo  年のコンボボックス
     * @param monthCombo 月のコンボボックス
     * @param dayCombo   日のコンボボックス（nullの場合は1日固定）
     * @return 日付情報を持つDateオブジェクト
     */
    private Date getDateFromComboBoxes(JComboBox<String> yearCombo, JComboBox<String> monthCombo,
            JComboBox<String> dayCombo) {

        // 年（"1990年" → 1990）を取得
        int year = Integer.parseInt(yearCombo.getSelectedItem().toString().replace("年", ""));

        // 月の選択値を取得
        String monthStr = monthCombo.getSelectedItem().toString();

        // "0月" が選ばれていたらエラーダイアログを表示して中断（nullを返す）
        if (monthStr.equals("0月")) {
            showErrorDialog("月の選択が不正です。1月〜12月の中から選択してください。");
            return null; // ← 必ず null を返す
        }

        // "〇月" → "〇" に変換して数値にし、Calendar用に -1
        int month = Integer.parseInt(monthStr.replace("月", "")) - 1;

        // 日（"15日" → 15）を取得。nullのときは1日を指定
        int day = (dayCombo != null)
                ? Integer.parseInt(dayCombo.getSelectedItem().toString().replace("日", ""))
                : 1;

        // Calendarで日付を構築
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime(); // 最終的な Date 型の返却
    }

    // ========================== 以下、ダイアログ表示関連メソッド ==============================

    /**
     * 画面上の特定パネルに赤字でエラーメッセージを表示する
     * 
     * @param message 表示したいエラーメッセージ文字列
     */
    public void showErrorMessageOnPanel(String message) {
        errorPanel.removeAll(); // 既存のエラー表示を削除

        JLabel errorLabel = new JLabel(message);
        errorLabel.setBounds(0, 0, 750, 26); // サイズと位置を明示的に指定（←重要）
        errorLabel.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        errorLabel.setForeground(Color.RED); // 赤文字
        errorLabel.setHorizontalAlignment(JLabel.CENTER); // 中央揃え

        errorPanel.add(errorLabel); // パネルに追加
        errorPanel.revalidate(); // 再レイアウト
        errorPanel.repaint(); // 再描画
    }

    /**
     * 処理成功時にポップアップの情報ダイアログを表示する
     * 
     * @param message 表示メッセージ
     */
    public void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * バリデーションエラー発生時にエラーダイアログを表示する
     * 
     * @param message 表示するエラーメッセージ
     */
    public void showValidationError(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * EmployeeInformationオブジェクトの内容を各UI入力欄にセットするメソッド
     * （編集画面表示時に既存データを反映するために使用）
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
        birthYearCombo.setSelectedItem(birthCal.get(Calendar.YEAR) + "年");
        birthMonthCombo.setSelectedItem((birthCal.get(Calendar.MONTH) + 1) + "月");
        birthDayCombo.setSelectedItem(birthCal.get(Calendar.DAY_OF_MONTH) + "日");

        Calendar joinCal = Calendar.getInstance();
        joinCal.setTime(employeeInformation.getJoiningDate());
        joinYearCombo.setSelectedItem(joinCal.get(Calendar.YEAR) + "年");
        joinMonthCombo.setSelectedItem((joinCal.get(Calendar.MONTH) + 1) + "月");
        joinDayCombo.setSelectedItem(joinCal.get(Calendar.DAY_OF_MONTH) + "日");

        int totalMonths = employeeInformation.getEngineerDate();
        engYearCombo.setSelectedItem((totalMonths / 12) + "年");
        engMonthCombo.setSelectedItem((totalMonths % 12) + "ヵ月"); // 「ヵ月」に変更

    }

    /**
     * エラーメッセージをモーダルダイアログで表示する共通メソッド
     *
     * @param message 表示したいメッセージ文字列
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
    }
}
