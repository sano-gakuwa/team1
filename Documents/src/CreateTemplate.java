import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class CreateTemplate implements Runnable {

    private File selectedDir;
    private final EmployeeManager MANAGER = new EmployeeManager();
    private final ThreadsManager THREAD_MANAGER = new ThreadsManager();
    private static ReentrantLock createTemplateLock = new ReentrantLock();
    private ViewDialog dialog = new ViewDialog();

    /**
     * テンプレート作成のロックを取得
     * 
     * @return ロックの状態 true:ロック中, false:ロックされていない
     */
    public boolean validateCreateTemplateLock() {
        return createTemplateLock.isLocked();
    }

    public void createTemplate(File selectedDir) {
        // 選択されたディレクトリを設定
        this.selectedDir = selectedDir;
    }

    @Override
    public void run() {
        MANAGER.printInfoLog("テンプレートファイル作成開始");
        THREAD_MANAGER.startUsing(Thread.currentThread());
        createTemplateLock.lock(); // ロックを取得
        // 固定ファイル名で保存先ファイルを作成
        File file = new File(selectedDir, "employee_template.csv");
        try {
            existenceValidation(file);
            templateWriting(file);
            MANAGER.printInfoLog("テンプレートファイル作成完了");
        } catch (Exception e) {
            MANAGER.printExceptionLog(e, "テンプレートファイル作成失敗");
        } finally {
            createTemplateLock.unlock();
            THREAD_MANAGER.endUsing(Thread.currentThread());
            MANAGER.printInfoLog("テンプレート出力完了");
        }
    }

    private void existenceValidation(File file) {
        // 上書き確認（存在する場合のみ）
        if (file.exists()) {
            String message = "ファイル「employee_template.csv」は既に存在します。上書きしてもよろしいですか？";
            String title = "上書き確認";
            int overwriteConfirm = dialog.warningConfirmation(message, title);
            if (overwriteConfirm != 0) {
                return;
            }
        }
    }

    private void templateWriting(File file) {
        // ファイル書き出し処理
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("社員ID,氏名,生年月日（yyyy/MM/dd）,入社年月（yyyy/MM）,エンジニア歴,扱える言語,職歴,研修歴,"
                    + "技術力,研修時の姿勢,コミュニケーション力,リーダーシップ,備考\n");
            dialog.viewEndDialog("テンプレートファイル「employee_template.csv」を出力しました。");
        } catch (IOException ex) {
            ex.printStackTrace();
            dialog.viewErrorDialog("テンプレートファイルの出力中にエラーが発生しました。");
            if (file.exists()) {
                if (!file.delete()) {
                    System.err.println("作成失敗したテンプレートファイルの削除に失敗しました: " + file.getAbsolutePath());
                }
            }
        }
    }
}
