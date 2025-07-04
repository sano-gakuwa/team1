public class App {

    public static void main(String[] args) throws Exception {
        AppLock appLock = new AppLock();
        appLock.tryAppLock();
        EmployeeManager manager = new EmployeeManager();
        manager.setUp();
    }
}
