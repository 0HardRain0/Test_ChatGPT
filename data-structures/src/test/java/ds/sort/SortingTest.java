package ds.sort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SortingTest {

    /** 다섯 정렬을 같은 입력으로 전부 검증하는 도우미 */
    private void assertAllSortsWork(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);   // 자바 표준 정렬을 "정답"으로

        check("bubble", Sorting::bubbleSort, input, expected);
        check("selection", Sorting::selectionSort, input, expected);
        check("insertion", Sorting::insertionSort, input, expected);
        check("merge", Sorting::mergeSort, input, expected);
        check("quick", Sorting::quickSort, input, expected);
    }

    private void check(String name, Consumer<int[]> sort, int[] input, int[] expected) {
        int[] a = input.clone();
        sort.accept(a);
        assertArrayEquals(expected, a, name + " 정렬 실패");
    }

    @Test
    void 일반_배열() {
        assertAllSortsWork(new int[]{5, 2, 9, 1, 5, 6, 3});
    }

    @Test
    void 경계_케이스_빈배열_하나_둘() {
        assertAllSortsWork(new int[]{});
        assertAllSortsWork(new int[]{42});
        assertAllSortsWork(new int[]{2, 1});
    }

    @Test
    void 이미_정렬됨_역순_전부_같음() {
        assertAllSortsWork(new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        assertAllSortsWork(new int[]{8, 7, 6, 5, 4, 3, 2, 1});   // 퀵 정렬 최악 케이스 방지 확인
        assertAllSortsWork(new int[]{7, 7, 7, 7, 7});
    }

    @Test
    void 음수_포함() {
        assertAllSortsWork(new int[]{-3, 5, -10, 0, 2, -1});
    }

    @Test
    void 랜덤_큰_배열() {
        Random rnd = new Random(42);   // 시드 고정 → 매번 같은 결과
        int[] big = new int[5000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(100_000) - 50_000;
        assertAllSortsWork(big);
    }

    @Test
    void 이분_탐색() {
        int[] sorted = {1, 3, 5, 7, 9, 11};

        assertEquals(0, Sorting.binarySearch(sorted, 1));    // 첫 번째
        assertEquals(5, Sorting.binarySearch(sorted, 11));   // 마지막
        assertEquals(3, Sorting.binarySearch(sorted, 7));    // 중간
        assertEquals(-1, Sorting.binarySearch(sorted, 4));   // 없음 (사이값)
        assertEquals(-1, Sorting.binarySearch(sorted, 0));   // 없음 (범위 밖 작음)
        assertEquals(-1, Sorting.binarySearch(sorted, 99));  // 없음 (범위 밖 큼)
        assertEquals(-1, Sorting.binarySearch(new int[]{}, 1));
    }

    @Test
    void lowerBound는_끼워넣을_자리() {
        int[] sorted = {1, 3, 3, 3, 5, 7};

        assertEquals(1, Sorting.lowerBound(sorted, 3));   // 3이 처음 나오는 위치
        assertEquals(4, Sorting.lowerBound(sorted, 4));   // 4는 없음 → 5 앞 = 끼워 넣을 자리
        assertEquals(0, Sorting.lowerBound(sorted, 0));   // 전부보다 작음 → 0
        assertEquals(6, Sorting.lowerBound(sorted, 100)); // 전부보다 큼 → length

        // 응용: 3의 개수 = lowerBound(4) - lowerBound(3)
        assertEquals(3, Sorting.lowerBound(sorted, 4) - Sorting.lowerBound(sorted, 3));
    }
}
