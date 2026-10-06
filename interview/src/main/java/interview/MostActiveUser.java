package interview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * [모의 라이브 코딩 #1] 가장 많은 페이지를 본 사용자
 *
 * 문제
 *   접속 로그가 "사용자ID 페이지" 형식의 문자열 배열로 주어진다.
 *   가장 많은 (서로 다른) 페이지를 본 사용자를 반환하라. 같은 페이지를 여러 번 봐도 1번으로 센다.
 *   동점이면 아무나, 빈 배열이면 null.
 *
 * 핵심 자료구조 두 개
 *   Map<사용자, Set<페이지>>
 *     - Map : 사용자 이름으로 찾아가야 하니까              → "키로 찾기 = Map"
 *     - Set : 같은 페이지를 두 번 넣어도 하나만 남아야 하니까 → "중복 제거 = Set"
 *
 * 시간 복잡도 O(n)
 *   로그 n개를 한 번 훑고(Map/Set 연산은 O(1)), 사용자 수(≤ n)만큼 한 번 더 돈다.
 *   Set 대신 List + contains 를 썼다면 contains가 O(n)이라 전체 O(n²)이 됐을 것.
 */
public class MostActiveUser {

    private MostActiveUser() {
    }

    public static String mostActiveUser(String[] logs) {
        Map<String, Set<String>> userPages = new HashMap<>();

        // 1단계: 사용자별로 본 페이지를 모은다 (Set이라 중복은 저절로 사라진다)
        for (String log : logs) {
            String[] parts = log.split(" ");
            String user = parts[0];
            String page = parts[1];

            // "user 키가 없으면 새 HashSet을 만들어 넣고, 그걸 돌려준다" — Java 8+
            userPages.computeIfAbsent(user, k -> new HashSet<>()).add(page);
        }

        // 2단계: 다 모은 뒤에 가장 큰 Set을 가진 사용자를 찾는다.
        //        1단계 안에서 비교하면 안 되는 이유: 그 시점엔 각 사용자의 Set이 아직 다 안 채워져 있다.
        String best = null;
        int bestCount = 0;
        for (Map.Entry<String, Set<String>> entry : userPages.entrySet()) {
            int count = entry.getValue().size();
            if (count > bestCount) {
                bestCount = count;
                best = entry.getKey();
            }
        }
        return best;
    }

    /**
     * 같은 로직, Java 8 이전 스타일. 실무 레거시 코드에서 자주 보이는 모양.
     * computeIfAbsent 한 줄이 아래 네 줄과 같다.
     */
    public static String mostActiveUserClassic(String[] logs) {
        Map<String, Set<String>> userPages = new HashMap<>();

        for (String log : logs) {
            String[] parts = log.split(" ");
            String user = parts[0];
            String page = parts[1];

            if (!userPages.containsKey(user)) {
                userPages.put(user, new HashSet<>());
            }
            userPages.get(user).add(page);
        }

        String best = null;
        int bestCount = 0;
        for (String user : userPages.keySet()) {
            int count = userPages.get(user).size();
            if (count > bestCount) {
                bestCount = count;
                best = user;
            }
        }
        return best;
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. 동점이면 사전순으로 앞선 사용자를 반환하도록 바꾸기. 두 번째 반복문의 if 조건만 손대면 된다.
    // TODO 2. 상위 3명을 반환하는 topUsers(logs, 3) 만들기. MyMinHeap.topK 와 같은 아이디어.
    // TODO 3. 로그 형식이 "사용자ID 페이지 시각" 으로 바뀌어 split 결과가 3개가 되면 어디를 고쳐야 하나?
    //         그리고 형식이 깨진 줄(공백 없음)이 섞여 있으면 어떻게 방어할까?
}
