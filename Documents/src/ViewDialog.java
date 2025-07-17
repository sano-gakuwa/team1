import javax.swing.JOptionPane;

public class ViewDialog {
    public ViewDialog() {
        // インスタンス生成時のメソッド等無し
    }

    /**
     * 成功表示用に用意したダイアログに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author nishiyama
     */
    public void viewEndDialog(String message) {
        if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
            JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * エラー表示用に用意したダイアログに文言表示させる
     *
     * @param message 表示するエラーメッセージ
     * @author 下村
     */
    public void viewErrorDialog(String message) {
        if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
            JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 
     * @param message
     */
    public void viewWarningDialog(String message) {
        if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
            JOptionPane.showMessageDialog(null, message, "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void viewFatalErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public int warningConfirmation(String message,String title) {
        int returnInt;
        returnInt = JOptionPane.showConfirmDialog(null, message, title, 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return returnInt;
    }

    public int questionConfirmation(String message,String title){
        int returnInt;
        returnInt = JOptionPane.showConfirmDialog(null, message, title, 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return returnInt;
    }

    public int utputConfirmation(String directory, String[] label) {
        int returnInt;
        returnInt = JOptionPane.showOptionDialog(
                null,
                "出力先を選択してください\n"
                        + "選択中" + directory,
                "確認ダイアログ",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                label,
                null);
        return returnInt;
    }
    public int ioConfirmation(String message,String directory, String[] label){
        String messagesString=message+"\n選択中"+directory;
        int returnInt;
        returnInt=JOptionPane.showOptionDialog(
                null,
                messagesString,
                "確認ダイアログ",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                label,
                null);
        return returnInt;
    }
}
