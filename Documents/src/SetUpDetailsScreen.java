
import java.awt.*;
import javax.swing.*;

public abstract class SetUpDetailsScreen extends SetUpJframe {

    JPanel topPanel;// 上段パネル（750*135px）
    JPanel upperPanel;// upperパネル（750*60px）
    JPanel middlePanel;// middleパネル（750*245px）
    JPanel careerPanel;// 1段目 経歴（375*120px）
    JPanel skillsPanel;// 1段目 スキル（265*120px）
    JPanel skillsScorePanel;// 1段目 スキルスコア（75*120px）
    JPanel trainingRecordsPanel;// 2段目 研修受講歴（375*105px）
    JPanel remarksPanel;// 2段目 備考（340*105px）
    JPanel errorPanel;// エラーパネル（750*26px）
    JPanel bottomPanel;// 下段パネル（715*34px）
    // 各パネルの間隔
    int verticalGap = 20;
    int horizontalGap = 35;

    protected SetUpDetailsScreen() {
        fullScreenPanel.removeAll();
        layoutPanels();  // 新しいレイアウトを設定
        refreshUI();  // 画面の再描画
    }

    private void layoutPanels() {
        // レイアウトマネージャーをセット（BoxLayout.Y_AXIS = 縦方向配置）
        fullScreenPanel.setLayout(new BoxLayout(fullScreenPanel, BoxLayout.Y_AXIS));

        // 上段の上スペース（25px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 上段パネル（750*135px）
        topPanel = createPanel(new Dimension(750, 135));
        topPanel.setLayout(new BorderLayout());

        // topパネル_社員ID（130*40px）
        topPanel.add(createPaddedPanel(new Dimension(135, 40), 0, 0, Color.DARK_GRAY), BorderLayout.NORTH);

        // topパネル_上下段ギャップ（15px）
        JPanel gapPanel = createPanel(new Dimension(0, 15));
        gapPanel.setOpaque(false);  // あえて背景透過
        topPanel.add(gapPanel);

        // topパネル_氏名（405*80px）
        topPanel.add(createPaddedPanel(new Dimension(405, 80), 0, 0, Color.DARK_GRAY), BorderLayout.SOUTH);

        // topPanelをfullScreenPanelに追加（中央寄せ）
        fullScreenPanel.add(wrapCentered(topPanel));

        // 上段と中央の間（10px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // upperパネル（750*60px）
        upperPanel = createPanel(new Dimension(750, 60));
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS)); // 横並び

        // ★ まじでわからん 61-72行目まで「子パネルの高さが倍近く必要になる問題」
        // upperパネル_生年月日（185 + 15）
        upperPanel.add(createPaddedPanel(new Dimension(185, 115), 15, 0, Color.DARK_GRAY));

        // upperパネル_入社年月（185 + 15）
        upperPanel.add(createPaddedPanel(new Dimension(185, 115), 15, 0, Color.DARK_GRAY));

        // upperパネル_エンジニア歴（140 + 15）
        upperPanel.add(createPaddedPanel(new Dimension(140, 115), 15, 0, Color.DARK_GRAY));

        // upperパネル_扱える言語(195 + 0)
        upperPanel.add(createPaddedPanel(new Dimension(195, 115), 0, 0, Color.DARK_GRAY));

        // upperPanelをfullScreenPanelに追加（中央寄せ）
        fullScreenPanel.add(wrapCentered(upperPanel));

        // upperとmiddleの間（10px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        middlePanel = new JPanel();
        middlePanel.setPreferredSize(new Dimension(750, 245));
        middlePanel.setLayout(null); // 手動配置
        middlePanel.setBackground(Color.PINK); // 赤枠デバッグ用

        // 1段目 経歴
        careerPanel = createPaddedPanel(new Dimension(375, 120), 0, 0, Color.GREEN);
        careerPanel.setBounds(0, 0, 375, 120);
        middlePanel.add(careerPanel);

        // 1段目 スキル
        skillsPanel = createPaddedPanel(new Dimension(265, 120), 0, 0, Color.BLUE);
        skillsPanel.setBounds(375 + horizontalGap, 0, 340, 120);
        middlePanel.add(skillsPanel);

        // 1段目 スキルスコア
        skillsScorePanel = createPaddedPanel(new Dimension(75, 120), 0, 0, Color.GREEN);
        skillsScorePanel.setBounds(715, 0, 75, 120); // 750-75=675
        skillsScorePanel.setBounds(375 + horizontalGap + 265, 0, 75, 120);
        middlePanel.add(skillsScorePanel);

        // 2段目 研修受講歴
        trainingRecordsPanel = createPaddedPanel(new Dimension(375, 105), 0, 0, Color.GREEN);
        trainingRecordsPanel.setBounds(0, 120 + verticalGap, 375, 105);
        middlePanel.add(trainingRecordsPanel);

        // 2段目 備考
        remarksPanel = createPaddedPanel(new Dimension(340, 105), 0, 0, Color.GREEN);
        remarksPanel.setBounds(375 + horizontalGap, 120 + verticalGap, 340, 105);
        middlePanel.add(remarksPanel);

        // ラップして中央寄せ（外枠パネルに追加）
        fullScreenPanel.add(wrapCentered(middlePanel));

        // エラー出るパネル（26px）
        errorPanel = createPanel(new Dimension(750,26));
        fullScreenPanel.add(wrapCentered(errorPanel));

        // 下段パネル（715*34px）
        bottomPanel = createPanel(new Dimension(750, 34));
        fullScreenPanel.add(wrapCentered(bottomPanel));

        // bottomの下スペース（25px）
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
        panel.setBackground(Color.LIGHT_GRAY); // 仮背景色
        return panel;
    }

    /**
     * 指定されたサイズ・背景色のJPanelを作成し、右と下にパディングを追加するためのラッパーパネルを返す。
     * <p>
     * rightPadding または bottomPadding が 0 の場合は、ラッパーなしのパネルをそのまま返す。
     * パディングが必要な場合は、ラッパー用のパネルを作成し、レイアウトを null に設定して 内部パネル（コンテンツ）を手動配置する。
     * </p>
     *
     * @param size コンテンツパネルのサイズ（幅と高さ）
     * @param rightPadding 右側に追加する余白のピクセル数
     * @param bottomPadding 下側に追加する余白のピクセル数
     * @param bgColor コンテンツパネルの仮背景色
     * @return 必要なパディングを含んだJPanel。パディングが不要な場合はコンテンツパネル自体を返す
     *
     * @author nishiyama
     */
    private JPanel createPaddedPanel(Dimension size, int rightPadding, int bottomPadding, Color bgColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)); // レイアウトをここで明示
        panel.setPreferredSize(size);
        panel.setMaximumSize(size);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBackground(bgColor);

        int wrapperWidth = size.width + rightPadding;
        int wrapperHeight = size.height + bottomPadding;

        JPanel wrapper = new JPanel(null);
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(wrapperWidth, wrapperHeight));
        wrapper.setMaximumSize(new Dimension(wrapperWidth, wrapperHeight));

        panel.setBounds(0, 0, size.width, size.height);
        wrapper.add(panel);

        return wrapper;
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
