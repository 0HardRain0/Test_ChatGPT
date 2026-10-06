package interview;

import org.junit.jupiter.api.Test;

import static interview.BestSeller.bestSeller;
import static interview.BestSeller.bestSellerWithMerge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BestSellerTest {

    @Test
    void 면접_예시_합계_기준() {
        String[] orders = {"apple 3", "banana 1", "apple 2", "cherry 5", "banana 2"};
        // apple 5, banana 3, cherry 5 → 동점, 둘 중 하나
        String result = bestSeller(orders);
        assertTrue(result.equals("apple") || result.equals("cherry"), "got " + result);
        assertEquals(result, bestSellerWithMerge(orders));   // 두 구현이 같은 답 (HashMap 순회 순서가 같으므로)
    }

    @Test
    void 횟수가_아니라_수량_합이_기준() {
        // banana는 3번 주문됐지만 합계 3, apple은 1번인데 10
        String[] orders = {"banana 1", "banana 1", "banana 1", "apple 10"};
        assertEquals("apple", bestSeller(orders));
    }

    // ── 질문 ①: 없으면? ──

    @Test
    void null_이면_null() {
        assertNull(bestSeller(null));
        assertNull(bestSellerWithMerge(null));
    }

    @Test
    void 빈_배열이면_null() {
        assertNull(bestSeller(new String[]{}));
    }

    // ── 질문 ②: 하나면? ──

    @Test
    void 주문_하나면_그_상품() {
        assertEquals("apple", bestSeller(new String[]{"apple 3"}));
    }

    // ── 질문 ③: 경계 — 음수(반품)가 섞일 때 초기값 함정 ──

    @Test
    void 모든_합계가_음수이면_초기값_0_버전은_null을_돌려준다() {
        // 문제 조건(수량 ≥ 1)에서는 발생하지 않지만, 조건이 바뀌면 바로 터지는 함정을 기록해 둔다
        String[] returnsOnly = {"apple -3", "banana -1"};
        assertNull(bestSeller(returnsOnly));                          // 상품은 있는데 "없다"고 답함 → 버그
        assertEquals("banana", bestSellerWithMerge(returnsOnly));     // MIN_VALUE 초기값은 -1(banana)을 잡는다
    }

    @Test
    void 반품으로_합계가_0이_되는_상품() {
        String[] orders = {"apple 3", "apple -3", "banana 1"};
        assertEquals("banana", bestSellerWithMerge(orders));
    }
}
