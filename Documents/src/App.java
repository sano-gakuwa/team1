public class App {

    public static void main(String[] args) throws Exception {
        //↓本来のコード、999人テスト時はコメントアウトしてください（木下）
        EmployeeManager manager = new EmployeeManager();
        //↓★999人テスト用ダミーコード↓
        //TestEmployeeManager manager = new TestEmployeeManager(); //テスト時のみ使用、999人登録コード
        manager.setUp();
    }
}
