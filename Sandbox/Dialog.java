
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Dialog {

    public static void main(String[] args) {
        // メインフレーム
        JFrame frame = new JFrame("Custom Dialog Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 300);
        frame.setResizable(false); // サイズ固定

        // ボタンを作成してダイアログを開く
        JButton button = new JButton("ダイアログを開く");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // カスタムダイアログの作成
                JDialog dialog = new JDialog(frame, "カスタムダイアログ", true);
                dialog.setSize(300, 150);
                dialog.setLayout(new BorderLayout());

                // メッセージラベル
                JLabel message = new JLabel("これはカスタムダイアログです。", SwingConstants.CENTER);
                dialog.add(message, BorderLayout.CENTER);

                /**↓メッセージサンプルです。試しに使ってみてください。
                 *
                 * //情報message
                 *  JOptionPane.showMessageDialog(null,
                 * "これは情報メッセージです", "情報", JOptionPane.INFORMATION_MESSAGE);
                 *
                 * //エラーmessage
                 *  JOptionPane.showMessageDialog(null,
                 * "これはエラーメッセージです", "エラー", JOptionPane.ERROR_MESSAGE);
                 *
                 * //確認message
                 *  int result = JOptionPane.showConfirmDialog(null,
                 * "本当に続行しますか？", "確認", JOptionPane.YES_NO_OPTION); if (result ==
                 * JOptionPane.YES_OPTION) { System.out.println("Yesが選択されました");
                 * } else { System.out.println("Noが選択されました"); }
                 */

                
                // ボタンパネル
                JPanel buttonPanel = new JPanel();
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose(); // ダイアログを閉じる
                    }
                });
                buttonPanel.add(okButton);
                dialog.add(buttonPanel, BorderLayout.SOUTH);

                // ダイアログを表示
                dialog.setLocationRelativeTo(frame); // 親フレームの中央に表示
                dialog.setVisible(true);
            }
        });

        // フレームにボタンを追加
        frame.add(button);
        frame.setVisible(true);
    }
}
