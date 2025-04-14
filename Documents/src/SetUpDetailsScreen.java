
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public abstract class SetUpDetailsScreen extends SetUpJframe {

    public SetUpDetailsScreen() {
        layoutPanels();
    }

    private void layoutPanels() {
        // レイアウトマネージャーをセット（BoxLayout.Y_AXIS = 縦方向配置）
        fullScreenPanel.setLayout(new BoxLayout(fullScreenPanel, BoxLayout.Y_AXIS));

        // 上段の上スペース（25px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // 上段パネル（715*135px）
        JPanel topPanel = createPanel(new Dimension(750, 135));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // 縦並び

        // topパネル_社員ID（130*35px）
        topPanel.add(createPaddedPanel(new Dimension(135, 35), 0, 0, Color.DARK_GRAY));

        // topパネル_上下段ギャップ（25px）
        topPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // topパネル_氏名（405*75px）
        topPanel.add(createPaddedPanel(new Dimension(405, 75), 0, 0, Color.DARK_GRAY));

        // topPanelをfullScreenPanelに追加（中央寄せ）
        fullScreenPanel.add(wrapCentered(topPanel));

        // 上段と中央の間（25px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // upperパネル（750*60px）
        JPanel upperPanel = createPanel(new Dimension(750, 60));
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS)); // 横並び

        // upperパネル_生年月日（185 + 15）
        upperPanel.add(createPaddedPanel(new Dimension(185, 60), 15, 0, Color.DARK_GRAY));

        // upperパネル_入社年月（185 + 15）
        upperPanel.add(createPaddedPanel(new Dimension(185, 60), 15, 0, Color.DARK_GRAY));

        // upperパネル_エンジニア歴（140 + 15）
        upperPanel.add(createPaddedPanel(new Dimension(140, 60), 15, 0, Color.DARK_GRAY));

        // upperパネル_扱える言語(190, 60)
        upperPanel.add(createPaddedPanel(new Dimension(190, 60), 0, 0, Color.DARK_GRAY));

        // upperPanelをfullScreenPanelに追加（中央寄せ）
        fullScreenPanel.add(wrapCentered(upperPanel));

        // upperとmiddleの間（25px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // middleパネル（750*245px）
        JPanel middlePanel = createPanel(new Dimension(750, 245));
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS)); // 横並び

        // middleパネル_経歴（375*120px）+ 35px + 20px
        middlePanel.add(createPaddedPanel(new Dimension(375, 120), 35, 20, Color.DARK_GRAY));

        // middleパネル_スキル（265*120px）+ 0px + 20px
        middlePanel.add(createPaddedPanel(new Dimension(265, 120), 0, 20, Color.DARK_GRAY));

        // middleパネル_スキル点数（75*120px）+ 0px + 20px
        middlePanel.add(createPaddedPanel(new Dimension(75, 120), 0, 20, Color.DARK_GRAY));

        // middleパネル_研修受講歴（375*105px）+ 35px + 0px
        middlePanel.add(createPaddedPanel(new Dimension(375, 105), 35, 0, Color.DARK_GRAY));

        // middleパネル_備考（340*105px）+ 0px + 0px
        middlePanel.add(createPaddedPanel(new Dimension(340, 105), 0, 0, Color.DARK_GRAY));

        // middlePanelをfullScreenPanelに追加（中央寄せ）
        fullScreenPanel.add(wrapCentered(middlePanel));

        // middleとbottomの間（26px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 26)));

        // 下段パネル（715*34px）
        JPanel bottomPanel = createPanel(new Dimension(750, 34));
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
        JPanel panel = new JPanel();
        panel.setPreferredSize(size);
        panel.setMaximumSize(size);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBackground(bgColor);

        // パディングが不要ならラップせずそのまま返す
        if (rightPadding == 0 && bottomPadding == 0) {
            return panel;
        }

        int wrapperWidth = size.width + rightPadding;
        int wrapperHeight = size.height + bottomPadding;

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(wrapperWidth, wrapperHeight));
        wrapper.setMaximumSize(new Dimension(wrapperWidth, wrapperHeight));
        wrapper.setLayout(null); // レイアウトを null にして手動配置

        // innerPanel の位置を固定
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
