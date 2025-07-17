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
     * 警告表示用に用意したダイアログに文言表示させる
     * 
     * @param message 表示するエラーメッセージ
     * @author 下村
     */
    public void viewWarningDialog(String message) {
        if (SetUpJframe.frame != null && SetUpJframe.frame.isDisplayable()) {
            JOptionPane.showMessageDialog(null, message, "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 致命的なエラー表示用に用意したダイアログに文言表示させる
     * 
     * @param message
     * @author 下村
     */
    public void viewFatalErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "エラー", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    /**
     * 確認用に用意したダイアログに文言表示させる
     * 
     * @param message
     * @return YES_OPTION:0 , NO_OPTION:1
     * @author 下村
     */
    public int warningConfirmation(String message,String title) {
        int returnInt;
        returnInt = JOptionPane.showConfirmDialog(null, message, title, 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return returnInt;
    }

    /**
     * 最終確認用に用意したダイアログに文言表示させる
     * 
     * @param message
     * @return YES_OPTION:0 , NO_OPTION:1
     * @author 下村
     */
    public int questionConfirmation(String message,String title){
        int returnInt;
        returnInt = JOptionPane.showConfirmDialog(null, message, title, 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return returnInt;
    }

    /**
     * 入出力用に用意したダイアログに文言表示させる
     * 
     * @param message
     * @return YES:0 , NO:1 , Try again:2
     * @author 下村
     */
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
