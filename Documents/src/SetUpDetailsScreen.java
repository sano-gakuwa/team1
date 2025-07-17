import java.awt.*;
import javax.swing.*;

public abstract class SetUpDetailsScreen extends SetUpJframe {

    JPanel topPanel;// 上段パネル
    JPanel idPanel;
    JPanel namePanel;
    JPanel upperPanel;// upperパネル
    JPanel birthdDayPanel;
    JPanel joiningDatePanel;
    JPanel engineerDatePanel;
    JPanel availableLanguagesPanel;
    JPanel middlePanel;// middleパネル
    JPanel careerPanel;// 1段目 経歴
    JPanel skillsPanel;// 1段目 スキル
    JPanel trainingRecordsPanel;// 2段目 研修受講歴
    JPanel remarksPanel;// 2段目 備考
    JPanel errorPanel;// エラーパネル
    JPanel bottomPanel;// 下段パネル
    // 各パネルの間隔
    int verticalGap = 20;
    int horizontalGap = 35;

    protected SetUpDetailsScreen() {
        layoutPanels(); // 新しいレイアウトを設定
    }

    private void layoutPanels() {
        // レイアウトマネージャーをセット（BoxLayout.Y_AXIS = 縦方向配置）
        fullScreenPanel.setLayout(new BoxLayout(fullScreenPanel, BoxLayout.Y_AXIS));

        // 上段の上スペース
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 上段パネル
        topPanel = createPanel(new Dimension(750, 135));
        topPanel.setOpaque(false);
        topPanel.setLayout(null);

        // topパネル_社員ID
        idPanel = createPanel(new Dimension(180, 40), 0, 0);
        idPanel.setBounds(5,5,160, 40);
        idPanel.setBackground(Color.LIGHT_GRAY);
        idPanel.setLayout(null);
        topPanel.add(idPanel);

        // topパネル_上下段ギャップ
        JPanel gapPanel = createPanel(new Dimension(0, 5));
        topPanel.add(gapPanel);

        // topパネル_氏名
        namePanel = createPanel(new Dimension(420, 130), 0, 0);
        // topPanel.add(namePanel, BorderLayout.SOUTH);
        namePanel.setBounds(5,50,420, 110);
        namePanel.setBackground(Color.LIGHT_GRAY);
        namePanel.setLayout(null);
        topPanel.add(namePanel);

        // topPanelをfullScreenPanelに追加（中央寄せ）
        fullScreenPanel.add(wrapCentered(topPanel));

        // 上段と中央の間
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // upperパネル
        upperPanel = createPanel(new Dimension(750, 60));
        upperPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0)); // 横並び

        // upperパネル_生年月日
        birthdDayPanel = createPanel(new Dimension(200, 60), 0, 0);
        birthdDayPanel.setLayout(new BorderLayout());

        upperPanel.add(birthdDayPanel);

        // upperパネル_入社年月
        joiningDatePanel = createPanel(new Dimension(200, 60), 0, 0);
        joiningDatePanel.setLayout(new BorderLayout());
        upperPanel.add(joiningDatePanel);

        // upperパネル_エンジニア歴
        engineerDatePanel = createPanel(new Dimension(140, 60), 0, 0);
        engineerDatePanel.setLayout(new BorderLayout());
        upperPanel.add(engineerDatePanel);

        // upperパネル_扱える言語
        availableLanguagesPanel = createPanel(new Dimension(190, 60), 0, 0);
        availableLanguagesPanel.setLayout(null);
        upperPanel.add(availableLanguagesPanel);

        // upperPanelをfullScreenPanelに追加（中央寄せ）
        fullScreenPanel.add(wrapCentered(upperPanel));

        // upperとmiddleの間
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        middlePanel = new JPanel();
        middlePanel.setPreferredSize(new Dimension(750, 245));
        middlePanel.setLayout(null);

        // 1段目 経歴
        careerPanel = new JPanel();
        careerPanel.setPreferredSize(new Dimension(375, 120));
        careerPanel.setBounds(0, 0, 375, 120);
        careerPanel.setLayout(new BorderLayout());
        careerPanel.setBackground(Color.WHITE);
        middlePanel.add(careerPanel);

        // 1段目 スキル
        skillsPanel = new JPanel();
        skillsPanel.setPreferredSize(new Dimension(375, 120));
        skillsPanel.setBounds(410, 0, 335, 120);
        skillsPanel.setLayout(new BorderLayout());
        skillsPanel.setBackground(Color.WHITE);
        middlePanel.add(skillsPanel);

        // 2段目 研修受講歴
        trainingRecordsPanel = new JPanel();
        trainingRecordsPanel.setPreferredSize(new Dimension(375, 105));
        trainingRecordsPanel.setBounds(0, 140, 375, 120);
        trainingRecordsPanel.setLayout(new BorderLayout());
        trainingRecordsPanel.setBackground(Color.WHITE);
        middlePanel.add(trainingRecordsPanel);

        // 2段目 備考
        remarksPanel = new JPanel();
        remarksPanel.setPreferredSize(new Dimension(340, 105));
        remarksPanel.setBounds(410, 140, 340, 120);
        remarksPanel.setLayout(new BorderLayout());
        remarksPanel.setBackground(Color.WHITE);
        middlePanel.add(remarksPanel);

        // ラップして中央寄せ（外枠パネルに追加）
        fullScreenPanel.add(wrapCentered(middlePanel));

        // エラー出るパネル
        errorPanel = createPanel(new Dimension(750, 26));
        fullScreenPanel.add(wrapCentered(errorPanel));

        // 下段パネル
        bottomPanel = createPanel(new Dimension(750, 34));
        fullScreenPanel.add(wrapCentered(bottomPanel));

        // bottomの下スペース
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 25)));
    }

    /**
     * パネル作成用ユーティリティメソッド。
     * <p>
     * 指定されたサイズで新しいJPanelを作成し、背景色も仮設定。 また、このパネルのサイズを意図しない引き伸ばしを避けるために最大サイズを設定。
     * </p>
     *
     * @param size パネルの「希望するサイズ」を指定する {@link Dimension} オブジェクト。 パネルの幅と高さを設定。
     * @return 作成されたJPanelオブジェクト
     *
     * @author nishiyama
     */
    private JPanel createPanel(Dimension size) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(size); // コンポーネントが「希望するサイズ」を指定
        panel.setMaximumSize(size); // 意図しない引き伸ばしを避ける
        panel.setBackground(Color.WHITE); // 仮背景色
        return panel;
    }

    private JPanel createPanel(Dimension size, int rightPadding, int bottomPadding) {
        JPanel panel = new JPanel();
        int wrapperWidth = size.width + rightPadding;
        int wrapperHeight = size.height + bottomPadding;
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(size.width, size.height));
        panel.setMaximumSize(new Dimension(wrapperWidth, wrapperHeight));
        panel.setBounds(0, 0, size.width, size.height);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * 中央寄せのラッパーパネルを作成するユーティリティメソッド
     * <p>
     * 指定されたパネルを中央寄せでラップするための新しいJPanelを作成。 このラッパーパネルは
     * `FlowLayout.CENTER`を使用して中央寄せを実現し、背景は透過設定。
     * </p>
     *
     * @param panel 中央に配置する元のパネル。ラップして中央寄せ。
     * @return 中央寄せされた元のパネルを持つ新しいJPanelラッパーパネル。
     *
     * @author nishiyama
     */
    private JPanel wrapCentered(JPanel panel) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false); // 背景透過
        wrapper.add(panel);
        return wrapper;
    }
}
