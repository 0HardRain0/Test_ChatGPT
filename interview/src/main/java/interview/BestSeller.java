package interview;

import java.util.HashMap;
import java.util.Map;

/**
 * [모의 라이브 코딩 #3] 가장 많이 팔린 상품
 *
 * 문제
 *   "상품명 수량" 형식의 주문 로그 배열. 수량 합계가 가장 큰 상품명을 반환.
 *   - null 또는 빈 배열 → null
 *   - 수량은 1 이상의 정수, 합계는 int 범위 안
 *   - 동점이면 아무거나
 *
 * 핵심 자료구조
 *   Map<String, Integer>  — "개수 세기 / 빈도 / 합계" 는 반사적으로 Map<K, Integer>
 *   값이 int가 아니라 Integer인 이유: 제네릭은 기본형을 못 받고, get()이 없는 키에 null을 돌려주기 때문.
 *
 * Map에 "더하기"의 세 가지 표현 (전부 같은 뜻)
 *   total.put(name, total.getOrDefault(name, 0) + qty);     // Java 8 이전부터 가능. 두 번 호출
 *   total.merge(name, qty, Integer::sum);                   // Java 8+. "없으면 qty, 있으면 기존값+qty"
 *   total.compute(name, (k, v) -> v == null ? qty : v + qty); // 가장 일반적인 형태. 과하게 유연
 *
 * 시간 복잡도 O(n) — 로그를 한 번 훑고(Map 연산 O(1)), 상품 수(≤ n)만큼 한 번 더
 */
public class BestSeller {

    private BestSeller() {
    }

    /** 면접에서 말한 접근 그대로: getOrDefault + put */
    public static String bestSeller(String[] orders) {
        if (orders == null || orders.length == 0) return null;   // 질문 ①: 없으면?

        Map<String, Integer> total = new HashMap<>();
        for (String order : orders) {
            String[] parts = order.split(" ");
            String name = parts[0];
            int qty = Integer.parseInt(parts[1]);
            total.put(name, total.getOrDefault(name, 0) + qty);
        }

        // 수량이 1 이상이라고 확인했으므로 초기값 0이 안전하다.
        // 음수(반품)가 섞일 수 있었다면 모든 합계가 음수일 때 best가 null로 남는 버그 → Integer.MIN_VALUE 또는 "첫 번째는 무조건 채택"
        String best = null;
        int bestQty = 0;
        for (Map.Entry<String, Integer> e : total.entrySet()) {
            if (e.getValue() > bestQty) {
                bestQty = e.getValue();
                best = e.getKey();
            }
        }
        return best;
    }

    /** 같은 로직, merge 사용 + 음수 수량(반품)까지 안전한 초기값 */
    public static String bestSellerWithMerge(String[] orders) {
        if (orders == null || orders.length == 0) return null;

        Map<String, Integer> total = new HashMap<>();
        for (String order : orders) {
            String[] parts = order.split(" ");
            total.merge(parts[0], Integer.parseInt(parts[1]), Integer::sum);
        }

        String best = null;
        int bestQty = Integer.MIN_VALUE;                        // 어떤 합계보다도 작은 값에서 시작
        for (Map.Entry<String, Integer> e : total.entrySet()) {
            if (e.getValue() > bestQty) {
                bestQty = e.getValue();
                best = e.getKey();
            }
        }
        return best;
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. 동점이면 사전순으로 앞선 상품명 반환. 비교 조건에 || (같고 && 이름이 더 작다) 를 추가.
    // TODO 2. 상위 3개 상품을 수량 내림차순으로 반환하는 topSellers(orders, 3). PriorityQueue 또는 정렬.
    // TODO 3. "상품명 수량" 에서 수량이 숫자가 아닌 줄("apple x")이 섞여 있으면? parseInt가 던지는 예외 이름은?
    //         건너뛸지, 전체를 실패시킬지는 요구사항 — 면접이라면 이것도 질문거리.
    // TODO 4. 수량 합계가 int를 넘을 수 있다면 Map<String, Long>으로 바꾸고 Long::sum 사용. 어디가 바뀌는지 세어 보기.
}
