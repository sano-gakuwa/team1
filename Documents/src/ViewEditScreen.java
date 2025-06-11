import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class ViewEditScreen extends SetUpDetailsScreen {

    private EmployeeInformation editedEmployee;
    JTextField employeeIdField;
    private JTextField rubyLastNameField, rubyFirstNameField;
    private JTextField lastNameField, firstNameField;
    private JComboBox<String> birthYearBox, birthMonthBox, birthDayBox;
    private JComboBox<String> joinYearBox, joinMonthBox, joinDayBox;
    private JComboBox<String> engYearBox, engMonthBox;
    private JTextField languageField;
    private JTextArea careerArea, trainingArea, remarksArea;
    private JComboBox<String> techCombo, commCombo, attitudeCombo, leaderCombo;
    private JButton saveButton, cancelButton;

    private EmployeeInformation employeeInformation;

    public ViewEditScreen(EmployeeInformation employeeInformation) {
        super();
        this.employeeInformation = employeeInformation;
    }

    public void view() {
        System.out.println("編集画面に遷移しました");
        System.out.println("社員ID: " + employeeInformation.employeeID);
        System.out.println("名前: " + employeeInformation.lastName + " " + employeeInformation.firstname);

        fullScreenPanel.removeAll();
        fullScreenPanel.setLayout(null);

        frame.setTitle("エンジニア情報 編集画面");
        frame.setSize(850, 600);
        frame.setResizable(false);

        JPanel container = new JPanel(null);
        container.setBounds(25, 25, 800, 550);

        setupEmployeeId(container);
        setupNameFields(container);
        setupDateAndLanguageFields(container);
        setupCareerAndSkills(container);
        setupTrainingAndRemarks(container);
        setupButtons(container);

        fullScreenPanel.add(container);
        frame.setContentPane(fullScreenPanel);
        fullScreenPanel.revalidate();
        fullScreenPanel.repaint();
        frame.setVisible(true);

        setValues();
        setEditableFields(true);
    }

    private void setupEmployeeId(JPanel panel) {
        employeeIdField = new JTextField();
        employeeIdField.setBounds(0, 0, 130, 30);
        employeeIdField.setEditable(false);
        panel.add(employeeIdField);
    }

    private void setupNameFields(JPanel panel) {
        rubyLastNameField = new JTextField();
        rubyLastNameField.setBounds(0, 40, 195, 30);
        panel.add(rubyLastNameField);

        rubyFirstNameField = new JTextField();
        rubyFirstNameField.setBounds(205, 40, 195, 30);
        panel.add(rubyFirstNameField);

        lastNameField = new JTextField();
        lastNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        lastNameField.setBounds(0, 80, 195, 40);
        panel.add(lastNameField);

        firstNameField = new JTextField();
        firstNameField.setFont(new Font("SansSerif", Font.BOLD, 18));
        firstNameField.setBounds(205, 80, 195, 40);
        panel.add(firstNameField);
    }

    private void setupDateAndLanguageFields(JPanel panel) {
        panel.add(createLabel("生年月日", 0, 130));
        panel.add(createLabel("入社年月", 220, 130));
        panel.add(createLabel("エンジニア歴", 440, 130));
        panel.add(createLabel("扱える言語", 585, 130));

        birthYearBox = new JComboBox<>();
        birthYearBox.setBounds(0, 150, 70, 25);
        panel.add(birthYearBox);

        birthMonthBox = new JComboBox<>();
        birthMonthBox.setBounds(75, 150, 60, 25);
        panel.add(birthMonthBox);

        birthDayBox = new JComboBox<>();
        birthDayBox.setBounds(140, 150, 60, 25);
        panel.add(birthDayBox);

        joinYearBox = new JComboBox<>();
        joinYearBox.setBounds(220, 150, 70, 25);
        panel.add(joinYearBox);

        joinMonthBox = new JComboBox<>();
        joinMonthBox.setBounds(295, 150, 60, 25);
        panel.add(joinMonthBox);

        joinDayBox = new JComboBox<>();
        joinDayBox.setBounds(360, 150, 60, 25);
        panel.add(joinDayBox);

        engYearBox = new JComboBox<>();
        engYearBox.setBounds(440, 150, 60, 25);
        panel.add(engYearBox);

        engMonthBox = new JComboBox<>();
        engMonthBox.setBounds(505, 150, 60, 25);
        panel.add(engMonthBox);

        languageField = new JTextField();
        languageField.setBounds(585, 150, 155, 25);
        panel.add(languageField);
    }

    private void setupCareerAndSkills(JPanel panel) {
        panel.add(createLabel("経歴", 0, 190));
        careerArea = new JTextArea();
        JScrollPane scroll1 = new JScrollPane(careerArea);
        scroll1.setBounds(0, 210, 375, 120);
        panel.add(scroll1);

        panel.add(createLabel("スキル", 400, 190));

        JPanel skillPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        skillPanel.setBounds(400, 210, 265, 120);

        skillPanel.add(new JLabel("技術力"));
        techCombo = new JComboBox<>();
        skillPanel.add(techCombo);

        skillPanel.add(new JLabel("コミュニケーション能力"));
        commCombo = new JComboBox<>();
        skillPanel.add(commCombo);

        skillPanel.add(new JLabel("受講態度"));
        attitudeCombo = new JComboBox<>();
        skillPanel.add(attitudeCombo);

        skillPanel.add(new JLabel("リーダーシップ"));
        leaderCombo = new JComboBox<>();
        skillPanel.add(leaderCombo);

        panel.add(skillPanel);
    }

    private void setupTrainingAndRemarks(JPanel panel) {
        panel.add(createLabel("研修受講歴", 0, 340));
        trainingArea = new JTextArea();
        JScrollPane scroll2 = new JScrollPane(trainingArea);
        scroll2.setBounds(0, 360, 375, 100);
        panel.add(scroll2);

        panel.add(createLabel("備考", 400, 340));
        remarksArea = new JTextArea();
        JScrollPane scroll3 = new JScrollPane(remarksArea);
        scroll3.setBounds(400, 360, 340, 100);
        panel.add(scroll3);
    }

    private void setupButtons(JPanel panel) {
        cancelButton = new JButton("＜編集キャンセル");
        cancelButton.setBounds(0, 470, 140, 30);
        cancelButton.addActionListener(e -> {
            int confirmCancel = JOptionPane.showConfirmDialog(
                    frame,
                    "保存せずに前の画面に戻ると編集中の内容は破棄されますが本当によろしいですか？",
                    "確認",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirmCancel == JOptionPane.YES_OPTION) {
                frame.dispose(); // 編集画面を閉じる
                ViewDetailsScreen detailsScreen = new ViewDetailsScreen(editedEmployee);
                detailsScreen.view();
            }
        });
        panel.add(cancelButton);

        saveButton = new JButton("保存");
        saveButton.setBounds(350, 470, 80, 30);
        saveButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "編集内容を保存してよろしいですか？",
                    "保存確認",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                saveEmployeeInfo();
            }
        });
        panel.add(saveButton);
    }

    private void saveEmployeeInfo() {
        try {
            EmployeeInformation updatedInfo = new EmployeeInformation(
                    employeeIdField.getText(),
                    rubyLastNameField.getText(),
                    rubyFirstNameField.getText(),
                    lastNameField.getText(),
                    firstNameField.getText(),
                    parseDateFromComboBox(birthYearBox, birthMonthBox, birthDayBox),
                    parseDateFromComboBox(joinYearBox, joinMonthBox, joinDayBox),
                    getEngineerExperience(),
                    languageField.getText(),
                    careerArea.getText(),
                    trainingArea.getText(),
                    Double.parseDouble(techCombo.getSelectedItem().toString()),
                    Double.parseDouble(commCombo.getSelectedItem().toString()),
                    Double.parseDouble(attitudeCombo.getSelectedItem().toString()),
                    Double.parseDouble(leaderCombo.getSelectedItem().toString()),
                    remarksArea.getText(),
                    parseDateFromComboBox(engYearBox, engMonthBox));

            EmployeeUpdater updater = new EmployeeUpdater();
            boolean success = updater.updateEmployee(updatedInfo);

            if (success) {
                JOptionPane.showMessageDialog(frame, "保存が完了しました。", "成功", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                ViewTopScreen topScreen = new ViewTopScreen();
                topScreen.View();
            } else {
                JOptionPane.showMessageDialog(frame, "保存に失敗しました。", "エラー", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "保存処理中にエラーが発生しました:\n" + ex.getMessage(), "エラー",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Date parseDateFromComboBox(JComboBox<String> yearBox, JComboBox<String> monthBox, JComboBox<String> dayBox)
            throws Exception {
        String y = yearBox.getSelectedItem().toString();
        String m = monthBox.getSelectedItem().toString();
        String d = dayBox.getSelectedItem().toString();
        return new SimpleDateFormat("yyyy-MM-dd").parse(y + "-" + m + "-" + d);
    }

    private Date parseDateFromComboBox(JComboBox<String> yearBox, JComboBox<String> monthBox) throws Exception {
        String y = yearBox.getSelectedItem().toString();
        String m = monthBox.getSelectedItem().toString();
        return new SimpleDateFormat("yyyy-MM").parse(y + "-" + m);
    }

    private int getEngineerExperience() {
        int y = Integer.parseInt(engYearBox.getSelectedItem().toString());
        int m = Integer.parseInt(engMonthBox.getSelectedItem().toString());
        return y * 12 + m;
    }

    private void setValues() {
        employeeIdField.setText(employeeInformation.employeeID);
        rubyLastNameField.setText(employeeInformation.rubyLastName);
        rubyFirstNameField.setText(employeeInformation.rubyFirstname);
        lastNameField.setText(employeeInformation.lastName);
        firstNameField.setText(employeeInformation.firstname);
        languageField.setText(employeeInformation.availableLanguages);
        careerArea.setText(employeeInformation.careerDate);
        trainingArea.setText(employeeInformation.trainingDate);
        remarksArea.setText(employeeInformation.remarks);
        techCombo.setSelectedItem(String.format("%.1f", employeeInformation.skillPoint));
        commCombo.setSelectedItem(String.format("%.1f", employeeInformation.communicationPoint));
        attitudeCombo.setSelectedItem(String.format("%.1f", employeeInformation.attitudePoint));
        leaderCombo.setSelectedItem(String.format("%.1f", employeeInformation.leadershipPoint));
    }

    private void setEditableFields(boolean editable) {
        rubyLastNameField.setEditable(editable);
        rubyFirstNameField.setEditable(editable);
        lastNameField.setEditable(editable);
        firstNameField.setEditable(editable);
        birthYearBox.setEnabled(editable);
        birthMonthBox.setEnabled(editable);
        birthDayBox.setEnabled(editable);
        joinYearBox.setEnabled(editable);
        joinMonthBox.setEnabled(editable);
        joinDayBox.setEnabled(editable);
        engYearBox.setEnabled(editable);
        engMonthBox.setEnabled(editable);
        languageField.setEditable(editable);
        careerArea.setEditable(editable);
        trainingArea.setEditable(editable);
        remarksArea.setEditable(editable);
        techCombo.setEnabled(editable);
        commCombo.setEnabled(editable);
        attitudeCombo.setEnabled(editable);
        leaderCombo.setEnabled(editable);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 100, 20);
        return label;
    }
}
