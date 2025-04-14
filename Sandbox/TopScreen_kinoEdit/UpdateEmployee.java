import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//新規追加テスト
public class UpdateEmployee extends JFrame {
    private JTextField idField, nameField, ageField, expField, langField;

    public UpdateEmployee(ViewTopScreen topScreen) {
        setTitle("簡易追加（仮）");
        setSize(300, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("社員ID:"));
        idField = new JTextField();
        add(idField);

        add(new JLabel("氏名:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("年齢:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("エンジニア歴:"));
        expField = new JTextField();
        add(expField);

        add(new JLabel("扱える言語:"));
        langField = new JTextField();
        add(langField);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            String id = idField.getText();
            String name = nameField.getText();
            String age = ageField.getText();
            String exp = expField.getText();
            String lang = langField.getText();

            if (EmployeeManager.addEmployee(id, name, age, exp, lang)) {
                topScreen.currentPage = (EmployeeManager.getEmployeeCount() + 9) / 10; // ← 追加：最後のページに移動
                topScreen.refreshTable();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "最大登録数に達しています。");
            }
        });
        add(saveButton);

        JButton cancelButton = new JButton("キャンセル");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        setVisible(true);
    }
}

