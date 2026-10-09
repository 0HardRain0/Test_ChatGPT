package interview;

import org.junit.jupiter.api.Test;

import static interview.WaitingTimes.waitingTimes;
import static interview.WaitingTimes.waitingTimesWithQueue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WaitingTimesTest {

    private void assertBoth(int[] expected, int[] durations) {
        assertArrayEquals(expected, waitingTimes(durations), "prefix sum");
        assertArrayEquals(expected, waitingTimesWithQueue(durations), "queue");
    }

    @Test
    void 면접_예시() {
        assertBoth(new int[]{0, 5, 8}, new int[]{5, 3, 8});
    }

    // ── 배열이 나오면 무조건: ① 비어 있으면? ② 하나면? ──

    @Test
    void 빈_배열() {
        assertBoth(new int[]{}, new int[]{});
    }

    @Test
    void 요청_하나는_대기_0() {
        assertBoth(new int[]{0}, new int[]{7});
    }

    // ── ③ 경계 ──

    @Test
    void 처리_시간이_모두_같아도_대기는_누적된다() {
        assertBoth(new int[]{0, 1, 2, 3}, new int[]{1, 1, 1, 1});
    }

    @Test
    void 큰_값_누적() {
        // 합계가 int 범위 안이라는 가정 하에
        assertBoth(new int[]{0, 1_000_000, 2_000_000}, new int[]{1_000_000, 1_000_000, 1_000_000});
    }

    @Test
    void 마지막_요청의_처리_시간은_결과에_영향이_없다() {
        // 마지막 사람의 처리 시간은 아무도 기다리지 않는다
        assertBoth(new int[]{0, 5}, new int[]{5, 999});
    }

    // ── Queue 객체가 진짜 필요한 상황 ──

    @Test
    void 처리_도중에_요청이_추가되는_시뮬레이션() {
        WaitingTimes.Simulator sim = new WaitingTimes.Simulator();

        sim.arrive(5);
        sim.arrive(3);
        assertEquals(0, sim.serveNext());    // 1번: 바로
        sim.arrive(8);                       // 처리 중에 새 요청 도착 — 배열로는 못 하는 것
        assertEquals(5, sim.serveNext());    // 2번: 5분 대기
        assertEquals(8, sim.serveNext());    // 3번: 5+3 대기
        assertEquals(-1, sim.serveNext());   // 대기 없음
        assertEquals(0, sim.pendingCount());
    }
}
