
import java.awt.*;
import javax.swing.*;

public abstract class SetUpTopScreen extends SetUpJframe {
    protected JPanel topPanel;
    protected JPanel centerPanel;
    protected JPanel functionButtonsPanel;
    protected JPanel employeeListPanel;
    protected JPanel bottomPanel;

    public SetUpTopScreen() {
        layoutPanals();
    }

    private void layoutPanals() {
        // レイアウトマネージャーをセット（BoxLayout.Y_AXIS = 縦方向配置）
        fullScreenPanel.setLayout(new BoxLayout(fullScreenPanel, BoxLayout.Y_AXIS));

        // 上段の上スペース（53px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 53)));

        // 上段パネル（715*23px）
        topPanel = createPanel(new Dimension(715, 23));
        fullScreenPanel.add(wrapCentered(topPanel));

        // 上段と中央の間（53px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 53)));

        // 中央パネル（715*395px）
        centerPanel = createPanel(new Dimension(750, 500));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // 中央パネル_上段（715*22px）
        functionButtonsPanel = new JPanel();
        functionButtonsPanel.setPreferredSize(new Dimension(750, 30));
        functionButtonsPanel.setMaximumSize(new Dimension(750, 30));
        functionButtonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 左寄せ
        centerPanel.add(functionButtonsPanel);
        
        // 中央パネル_上下段ギャップ（10px）
        centerPanel.add(Box.createRigidArea(new Dimension(0,10)));

        // 中央パネル_下段（715*363px）
        employeeListPanel = new JPanel();
        functionButtonsPanel.setPreferredSize(new Dimension(750, 30));
        functionButtonsPanel.setMaximumSize(new Dimension(750, 30));
        employeeListPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // 左寄せ
        centerPanel.add(employeeListPanel);

        // centerPanelをfullScreenPanelに追加（中央寄せ）
        fullScreenPanel.add(wrapCentered(centerPanel));

        // 中央と下段の間（27px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 27)));

        // 下段パネル（715*23px）
        bottomPanel = createPanel(new Dimension(715, 23));
        fullScreenPanel.add(wrapCentered(bottomPanel));

        // 下段の下スペース（26px）
        fullScreenPanel.add(Box.createRigidArea(new Dimension(0, 26)));
    }

    /**
     * パネル作成用ユーティリティメソッド。
     * <p>
     * 指定されたサイズで新しいJPanelを作成し、背景色も仮設定。
     * また、このパネルのサイズを意図しない引き伸ばしを避けるために最大サイズを設定。
     * </p>
     *
     * @author nishiyama
     * @param size パネルの「希望するサイズ」を指定する {@link Dimension} オブジェクト。 パネルの幅と高さを設定。
     * @return 作成されたJPanelオブジェクト
     */
    private JPanel createPanel(Dimension size) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(size); // コンポーネントが「希望するサイズ」を指定
        panel.setMaximumSize(size); // 意図しない引き伸ばしを避ける
        panel.setBackground(Color.LIGHT_GRAY); // 仮背景色
        return panel;
    }

    /**
     * 中央寄せのラッパーパネルを作成するユーティリティメソッド
     * <p>
     * 指定されたパネルを中央寄せでラップするための新しいJPanelを作成。
     * このラッパーパネルは `FlowLayout.CENTER`を使用して中央寄せを実現し、背景は透過設定。
     * </p>
     *
     * @author nishiyama
     * @param panel 中央に配置する元のパネル。ラップして中央寄せ。
     * @return 中央寄せされた元のパネルを持つ新しいJPanelラッパーパネル。
     */
    private JPanel wrapCentered(JPanel panel) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false); // 背景透過
        wrapper.add(panel);
        return wrapper;
    }
}
