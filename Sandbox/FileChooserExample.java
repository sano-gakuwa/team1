import java.awt.*;
import java.io.File;
import javax.swing.*;

public class FileChooserExample {
    public static void main(String[] args) {
        // SwingのGUIをイベントディスパッチスレッド上で実行
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ファイル選択");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLayout(new FlowLayout());

            JButton button = new JButton("ファイルを選択");

            button.addActionListener(e -> showFileChooser(frame));

            frame.add(button);
            frame.setVisible(true);
        });
    }

    private static void showFileChooser(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            showFileDialog(frame, selectedFile);
        }
    }

    private static void showFileDialog(JFrame frame, File selectedFile) {
        int choice = JOptionPane.showConfirmDialog(
            frame,
            "選択されたファイル: \n" + selectedFile.getAbsolutePath() + "\n\n" +
            "このファイルでよろしいですか？",
            "ファイル確認",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.NO_OPTION) {
            showFileChooser(frame);
        }
    }
}
