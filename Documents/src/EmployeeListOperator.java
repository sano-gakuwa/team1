import java.util.stream.Collectors;
import java.util.*;
import java.util.concurrent.*;
import java.text.Normalizer;

public class EmployeeListOperator {
    private final List<EmployeeInformation> masterList;   // 元データ（不変）
    private volatile List<EmployeeInformation> filteredList; // 検索結果（ディープコピー）
    private final Object lock = new Object();
    private volatile boolean isSearching = false;
    // 検索中のキャンセルはここでは未実装（必要ならFutureやinterrupt対応を追加）
    public EmployeeListOperator(List<EmployeeInformation> initialList) {
        // 最大1000件で保持
        this.masterList = new ArrayList<>(Math.min(initialList.size(), 1000));
        for (int i = 0; i < Math.min(initialList.size(), 1000); i++) {
            this.masterList.add(initialList.get(i));
        }
        // 初期は全件コピー
        this.filteredList = deepCopyEmployeeList(this.masterList);
    }
    // 検索実行（AND検索、複数項目対応）
    // 各引数はnullまたは空文字の場合は無視する
    // 入力は最大100文字
    public void searchAsync(
            String employeeIDQuery,
            String nameQuery,
            String ageQuery,
            String engineerDateQuery,
            String availableLanguagesQuery,
            SearchCallback callback) {
        if (isSearching) {
            callback.onSearchFinished(false, null, "検索中です。");
            return;
        }
        // 空検索なら何もしない（再検索対応）
        boolean allEmpty =
                isEmpty(employeeIDQuery) &&
                isEmpty(nameQuery) &&
                isEmpty(ageQuery) &&
                isEmpty(engineerDateQuery) &&
                isEmpty(availableLanguagesQuery);
        if (allEmpty) {
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
                        trimmedAvailableLanguagesQuery
                );
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
        if (!matchesAllWords(employeeIDWords, normalize(emp.employeeID))) continue;
        if (!matchesAllWords(nameWords, normalize(emp.lastName + emp.firstname + emp.rubyLastName + emp.rubyFirstname))) continue;
        if (!matchesAllWords(ageWords, String.valueOf(calcAge(emp.birthday)))) continue;
        if (!matchesAllWords(engineerDateWords, String.valueOf(emp.engineerDate))) continue;
        if (!matchesAllWords(availableLanguagesWords, normalize(emp.availableLanguages))) continue;
        results.add(copyEmployeeInformation(emp));
    }
    return results;
}
    // 半角全角・かなカナ変換含めた正規化
    private String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFKC);
        n = hiraToKana(n).toUpperCase(Locale.JAPAN);
        return n;
    }
    // ひらがな→カタカナ変換
    private String hiraToKana(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c >= 'ぁ' && c <= 'ん') {
                sb.append((char)(c + ('ァ' - 'ぁ')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    // AND条件で全単語を含むか判定（部分一致）
    private boolean matchesAllWords(List<String> words, String target) {
        if (words == null || words.isEmpty()) return true; // 空ならマッチとみなす
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
        if (s == null) return null;
        return s.length() > 100 ? s.substring(0, 100) : s;
    }
    // スペースで分割、空文字は除外（カンマ・読点は単語に含む）
    private List<String> splitBySpace(String s) {
        if (isEmpty(s)) return Collections.emptyList();
        String[] arr = s.trim().split("\\s+");
        List<String> result = new ArrayList<>();
        for (String a : arr) {
            if (!a.isEmpty()) result.add(a);
        }
        return result;
    }
    // コピー処理（フィールド単位）
    private EmployeeInformation copyEmployeeInformation(EmployeeInformation emp) {
        return new EmployeeInformation(
                emp.employeeID,
                emp.lastName,
                emp.firstname,
                emp.rubyLastName,
                emp.rubyFirstname,
                emp.birthday != null ? (Date) emp.birthday.clone() : null,
                emp.joiningDate != null ? (Date) emp.joiningDate.clone() : null,
                emp.engineerDate,
                emp.availableLanguages,
                emp.careerDate,
                emp.trainingDate,
                emp.skillPoint,
                emp.attitudePoint,
                emp.communicationPoint,
                emp.leadershipPoint,
                emp.remarks,
                emp.updatedDay != null ? (Date) emp.updatedDay.clone() : null
        );
    }
    // リストのディープコピー
    private List<EmployeeInformation> deepCopyEmployeeList(List<EmployeeInformation> source) {
        List<EmployeeInformation> copy = new ArrayList<>(source.size());
        for (EmployeeInformation emp : source) {
            copy.add(copyEmployeeInformation(emp));
        }
        return copy;
    }
    // 年齢計算
    private int calcAge(Date birthday) {
        if (birthday == null) return 0;
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthday);
        Calendar now = Calendar.getInstance();
        int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
    // ソートキー定義
    public enum SortKey {
        EMPLOYEE_ID,
        NAME,
        AGE,
        ENGINEER_DATE,
        AVAILABLE_LANGUAGES
    }
    // ソート実行
    public void sort(SortKey key, boolean ascending) {
        synchronized (lock) {
            Comparator<EmployeeInformation> comparator = getComparator(key);
            if (comparator == null) return;
            if (!ascending) {
                comparator = comparator.reversed();
            }
            filteredList.sort(comparator);
        }
    }
    // ソート用コンパレータ生成
    private Comparator<EmployeeInformation> getComparator(SortKey key) {
        Date now = new Date();
        switch (key) {
            case EMPLOYEE_ID:
                return Comparator.comparing(emp -> emp.employeeID, Comparator.nullsLast(String::compareTo));
            case NAME:
                return Comparator.comparing(emp -> emp.lastName + emp.firstname, Comparator.nullsLast(String::compareTo));
            case AGE:
                return Comparator.comparingInt(emp -> calcAge(emp.birthday));
            case ENGINEER_DATE:
                return Comparator.comparingInt(emp -> emp.engineerDate);
            case AVAILABLE_LANGUAGES:
                return Comparator.comparing(emp -> emp.availableLanguages, Comparator.nullsLast(String::compareTo));
            default:
                return null;
        }
    }
    public boolean isSearching() {
        return isSearching;
    }
    // 検索完了コールバック
    public interface SearchCallback {
        /**
         * @param success 成功したか
         * @param results 検索結果（成功時のみ、ディープコピー済み）
         * @param errorMessage エラーメッセージ（失敗時のみ）
         */
        void onSearchFinished(boolean success, List<EmployeeInformation> results, String errorMessage);
    }
    // 現在の検索結果取得（ディープコピー）
    public List<EmployeeInformation> getFilteredList() {
        synchronized (lock) {
            return deepCopyEmployeeList(filteredList);
        }
    }
}
