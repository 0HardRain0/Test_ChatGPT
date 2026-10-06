package ds.interview;

import org.junit.jupiter.api.Test;

import static ds.interview.MeetingOverlap.hasOverlap;
import static ds.interview.MeetingOverlap.hasOverlapBruteForce;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeetingOverlapTest {

    /** 두 구현이 항상 같은 답을 내는지 함께 검사 */
    private void assertBoth(boolean expected, int[][] r) {
        assertEquals(expected, hasOverlapBruteForce(r), "bruteForce");
        assertEquals(expected, hasOverlap(r), "sorted");
    }

    @Test
    void 면접_예시() {
        assertBoth(true, new int[][]{{9, 11}, {13, 15}, {10, 12}, {15, 16}});   // 9-11 과 10-12
    }

    @Test
    void 겹침_없음() {
        assertBoth(false, new int[][]{{9, 10}, {11, 12}, {14, 16}});
    }

    // ── 엣지 케이스: "없으면 / 하나면 / 경계가 닿으면" ──

    @Test
    void 빈_배열() {
        assertBoth(false, new int[][]{});
    }

    @Test
    void 예약_하나() {
        assertBoth(false, new int[][]{{9, 11}});
    }

    @Test
    void 딱_붙은_것은_겹치지_않는다() {
        assertBoth(false, new int[][]{{9, 11}, {11, 13}, {13, 15}});
    }

    @Test
    void 완전히_같은_예약은_겹친다() {
        assertBoth(true, new int[][]{{9, 11}, {9, 11}});
    }

    // ── 함정 ──

    @Test
    void 하나가_다른_하나를_통째로_감싸는_경우() {
        // "b1이 a 안에 있거나 b2가 a 안에 있으면" 조건으로는 놓치는 케이스
        assertBoth(true, new int[][]{{10, 11}, {9, 12}});
    }

    @Test
    void 역순으로_들어와도_된다() {
        assertBoth(true, new int[][]{{15, 16}, {10, 12}, {13, 15}, {9, 11}});
    }

    @Test
    void 정렬_버전은_입력_배열_순서를_바꾸지_않는다() {
        int[][] input = {{13, 15}, {9, 11}};
        hasOverlap(input);
        assertArrayEquals(new int[]{13, 15}, input[0]);   // clone 덕에 원본 유지
    }

    @Test
    void 큰_입력에서도_두_구현이_같은_답() {
        java.util.Random rnd = new java.util.Random(7);
        for (int trial = 0; trial < 200; trial++) {
            int n = rnd.nextInt(8);
            int[][] r = new int[n][];
            for (int i = 0; i < n; i++) {
                int start = rnd.nextInt(20);
                r[i] = new int[]{start, start + 1 + rnd.nextInt(5)};
            }
            assertEquals(hasOverlapBruteForce(r), hasOverlap(r), "trial " + trial);
        }
    }

    @Test
    void 조건식_함정_직접_확인() {
        // 틀린 조건: b의 끝점 중 하나가 a 안에 있는가
        int a1 = 10, a2 = 11, b1 = 9, b2 = 12;
        boolean wrong = (a1 < b1 && b1 < a2) || (a1 < b2 && b2 < a2);
        boolean right = a1 < b2 && b1 < a2;

        assertFalse(wrong);   // 감싸는 경우를 놓친다
        assertTrue(right);    // 표준 공식은 잡는다
    }
}
