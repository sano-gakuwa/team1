import java.util.*;
import java.text.Normalizer;
/**
 * 社員情報の検索およびソート機能のクラス。
 * 元データのリストを保持し、検索条件に基づくフィルタリングや
 * ソート操作を提供
 * @atuthor 木下
 */
public class EmployeeListOperator {
    private final EmployeeManager MANAGER = new EmployeeManager();//infoログ用
    private final List<EmployeeInformation> masterList; // 元データ（不変）
    private volatile List<EmployeeInformation> filteredList; // 検索結果（ディープコピー）
    private final Object lock = new Object();
    private volatile boolean isSearching = false;

    /**
     * コンストラクタ。初期リストから最大1000件の社員情報を保持
     * filteredListに全件コピーを作成
     * 検索中のキャンセルはまだ未実装、作成中（Futureやinterrupt追加検討）
     * 
     * @param initialList 初期社員情報リスト
     */
    public EmployeeListOperator(List<EmployeeInformation> initialList) {
        // 最大1000件で保持
        this.masterList = new ArrayList<>(Math.min(initialList.size(), 1000));
        for (int i = 0; i < Math.min(initialList.size(), 1000); i++) {
            this.masterList.add(initialList.get(i));
        }
        // 初期は全件コピー
        this.filteredList = deepCopyEmployeeList(this.masterList);
    }

    /**
     * 非同期で検索を実行※検索処理を別スレッドで実行
     * AND条件で複数の検索項目に対応、空文字やnullは無視
     * 最大100文字までの入力を受け付ける
     *
     * @param employeeIDQuery          社員IDの検索キーワード
     * @param nameQuery                氏名の検索キーワード
     * @param ageQuery                 年齢の検索キーワード
     * @param engineerDateQuery        エンジニア歴の検索キーワード
     * @param availableLanguagesQuery 扱える言語の検索キーワード
     * @param callback                 検索完了時のコールバック
     */
    public void searchAsync(
            String employeeIDQuery,
            String nameQuery,
            String ageQuery,
            String engineerDateQuery,
            String availableLanguagesQuery,
            SearchCallback callback) {
        if (isSearching) {
            MANAGER.LOGGER.info("検索中に検索ボタンが押されました");
            callback.onSearchFinished(false, null, "検索中です。");
            return;
        }
        // 空検索なら何もしない（再検索対応）
        boolean allEmpty = isEmpty(employeeIDQuery) &&
                isEmpty(nameQuery) &&
                isEmpty(ageQuery) &&
                isEmpty(engineerDateQuery) &&
                isEmpty(availableLanguagesQuery);
        if (allEmpty) {
            MANAGER.LOGGER.info("検索条件が入力されていません。検索処理は実行されませんでした。");
            callback.onSearchFinished(false, null, "検索条件が入力されていません。");
            return;
        }
        // 100文字
        final String trimmedEmployeeIDQuery = cutTo100(employeeIDQuery);
        final String trimmedNameQuery = cutTo100(nameQuery);
        final String trimmedAgeQuery = cutTo100(ageQuery);
        final String trimmedEngineerDateQuery = cutTo100(engineerDateQuery);
        final String trimmedAvailableLanguagesQuery = cutTo100(availableLanguagesQuery);
        isSearching = true;
        Runnable searchTask = () -> {
            try {
                List<EmployeeInformation> results = search(
                        trimmedEmployeeIDQuery,
                        trimmedNameQuery,
                        trimmedAgeQuery,
                        trimmedEngineerDateQuery,
                        trimmedAvailableLanguagesQuery);
                synchronized (lock) {
                    filteredList = results;
                }
                callback.onSearchFinished(true, deepCopyEmployeeList(results), null);
            } catch (Exception e) {
                callback.onSearchFinished(false, null, e.getMessage());
            } finally {
                isSearching = false;
            }
        };
        Thread thread = new Thread(searchTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 指定された検索条件に基づき社員情報リストを検索
     * AND条件で全ての単語を部分一致で判定
     * 
     * @param employeeIDQuery         社員IDの検索キーワード
     * @param nameQuery               氏名の検索キーワード
     * @param ageQuery                年齢の検索キーワード
     * @param engineerDateQuery       エンジニア歴の検索キーワード
     * @param availableLanguagesQuery 扱える言語の検索キーワード
     * @return 条件に合致した社員情報のリスト
     */
    private List<EmployeeInformation> search(
            String employeeIDQuery,
            String nameQuery,
            String ageQuery,
            String engineerDateQuery,
            String availableLanguagesQuery) {
        // 入力の単語リスト化（スペース区切り）
        List<String> employeeIDWords = splitBySpace(employeeIDQuery);
        List<String> nameWords = splitBySpace(nameQuery);
        List<String> ageWords = splitBySpace(ageQuery);
        List<String> engineerDateWords = splitBySpace(engineerDateQuery);
        List<String> availableLanguagesWords = splitBySpace(availableLanguagesQuery);
        List<EmployeeInformation> results = new ArrayList<>();
        for (EmployeeInformation emp : masterList) {
            // AND条件: 5項目のみ対象
            if (!matchesAllWords(employeeIDWords, normalize(emp.getEmployeeID())))
                continue;
            if (!matchesAllWords(nameWords,
                    normalize(emp.getLastName() + emp.getFirstname() + emp.getRubyLastName() + emp.getRubyFirstname())))
                continue;
            if (!matchesAllWords(ageWords, String.valueOf(calcAge(emp.getBirthday()))))
                continue;
            if (!matchesAllWords(engineerDateWords, String.valueOf(emp.getEngineerDate())))
                continue;
            if (!matchesAllWords(availableLanguagesWords, normalize(emp.getAvailableLanguages())))
                continue;
            results.add(copyEmployeeInformation(emp));
        }
        return results;
    }

    /**
     * 与えられた文字列を正規化
     * 
     * 半角全角の統一、ひらがなをカタカナに変換
     * 大文字に変換
     *
     * @param s 入力文字列
     * @return 正規化された文字列
     */
    // 半角全角・かなカナ変換含めた正規化
    private String normalize(String s) {
        if (s == null)
            return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFKC);
        n = hiraToKana(n).toUpperCase(Locale.JAPAN);
        return n;
    }
    // ひらがな→カタカナ変換
    private String hiraToKana(String s) {
        if (s == null)
            return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c >= 'ぁ' && c <= 'ん') {
                sb.append((char) (c + ('ァ' - 'ぁ')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 検索ボタン押下時、項目欄内に入力された文字列の判定
     * AND条件で全ての単語が対象文字列に部分一致するか判定
     * ※文字列制限も入れたが、不要なら書き換え
     *
     * @param words  検索単語リスト
     * @param target 判定対象文字列
     * @return すべての単語が含まれていればtrue、そうでなければfalse
     */
    // AND条件で全単語を含むか判定（部分一致）
    private boolean matchesAllWords(List<String> words, String target) {
        if (words == null || words.isEmpty())
            return true; // 空ならマッチとみなす
        String normTarget = normalize(target);
        for (String w : words) {
            String nw = normalize(w);
            if (!normTarget.contains(nw)) {
                return false;
            }
        }
        return true;
    }
    // 空判定（nullまたはtrim後空文字）
    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
    // 100文字超えカット
    private String cutTo100(String s) {
        if (s == null)
            return null;
        return s.length() > 100 ? s.substring(0, 100) : s;
    }

    // スペースで分割、空文字は除外（カンマ・読点は単語に含む）
    private List<String> splitBySpace(String s) {
        if (isEmpty(s))
            return Collections.emptyList();
        String[] arr = s.trim().split("\\s+");
        List<String> result = new ArrayList<>();
        for (String a : arr) {
            if (!a.isEmpty())
                result.add(a);
        }
        return result;
    }

    /**
     * EmployeeInformationのディープコピーを作成
     *
     * @param emp コピー元のEmployeeInformation
     * @return コピーしたEmployeeInformationインスタンス
     */
    // コピー処理（フィールド単位）
    private EmployeeInformation copyEmployeeInformation(EmployeeInformation emp) {
        EmployeeInformation employee = new EmployeeInformation();
        employee.setEmployeeID(emp.getEmployeeID());
        employee.setlastName(emp.getLastName());
        employee.setFirstname(emp.getFirstname());
        employee.setRubyLastName(emp.getRubyLastName());
        employee.setRubyFirstname(emp.getRubyFirstname());
        employee.setBirthday(emp.getBirthday() != null ? (Date) emp.getBirthday().clone() : null);
        employee.setJoiningDate(emp.getJoiningDate() != null ? (Date) emp.getJoiningDate().clone() : null);
        employee.setEngineerDate(emp.getEngineerDate());
        employee.setAvailableLanguages(emp.getAvailableLanguages());
        employee.setCareerDate(emp.getCareerDate());
        employee.setTrainingDate(emp.getTrainingDate());
        employee.setSkillPoint(emp.getSkillPoint());
        employee.setAttitudePoint(emp.getAttitudePoint());
        employee.setCommunicationPoint(emp.getCommunicationPoint());
        employee.setLeadershipPoint(emp.getLeadershipPoint());
        employee.setRemarks(emp.getRemarks());
        employee.setUpdatedDay(emp.getUpdatedDay() != null ? (Date) emp.getUpdatedDay().clone() : null);
        return employee;
    }
    // リストのディープコピー
    private List<EmployeeInformation> deepCopyEmployeeList(List<EmployeeInformation> source) {
        List<EmployeeInformation> copy = new ArrayList<>(source.size());
        for (EmployeeInformation emp : source) {
            copy.add(copyEmployeeInformation(emp));
        }
        return copy;
    }

    /**
     * 指定された誕生日から年齢を計算
     *
     * @param birthday 生年月日
     * @return 年齢（誕生日がnullの場合は0）
     */
    // 年齢計算
    private int calcAge(Date birthday) {
        if (birthday == null)
            return 0;
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);
        Calendar now = Calendar.getInstance();
        int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
    /**
     * ソートキー定義
     */
    public enum SortKey {
        EMPLOYEE_ID,
        NAME,
        AGE,
        ENGINEER_DATE,
        AVAILABLE_LANGUAGES
    }
    // ソート実行

    /**
     * 検索完了時のコールバックインターフェース。
     */
    public interface SearchCallback {
        /**
         * @param success      成功したか
         * @param results      検索結果（成功時のみ、ディープコピー済み）
         * @param errorMessage エラーメッセージ（失敗時のみ）
         */
        void onSearchFinished(boolean success, List<EmployeeInformation> results, String errorMessage);
    }

    /**
     * 現在の検索結果をディープコピーで取得する。
     *
     * @return 現在の検索結果リストのコピー
     */
    public List<EmployeeInformation> getFilteredList() {
        
        synchronized (lock) {
            return deepCopyEmployeeList(filteredList);
        }
    }
    public void sort(SortKey key, boolean ascending) {
        synchronized (lock) {
            Comparator<EmployeeInformation> comparator = switch (key) {
                case EMPLOYEE_ID -> Comparator.comparing(EmployeeInformation::getEmployeeID, Comparator.nullsFirst(String::compareTo));
                case NAME -> Comparator.comparing(emp -> normalize(emp.getRubyLastName() + emp.getRubyFirstname()), Comparator.nullsFirst(String::compareTo));
                case AGE -> Comparator.comparingInt(emp -> calcAge(emp.getBirthday()));
                case ENGINEER_DATE -> Comparator.comparingInt(EmployeeInformation::getEngineerDate);
                default -> null;
            };
            if (comparator == null) return;
            if (!ascending) comparator = comparator.reversed();
            // 登録順を保持するために安定ソート（JavaのCollections.sortは安定）
            filteredList.sort(comparator);
        }
    }

}
