package ds.heap;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyMinHeapTest {

    @Test
    void 항상_최솟값이_먼저_나온다() {
        MyMinHeap<Integer> heap = new MyMinHeap<>();
        for (int v : new int[]{5, 3, 8, 1, 9, 2, 7}) heap.offer(v);

        assertEquals(1, heap.peek());
        assertEquals(7, heap.size());

        StringBuilder out = new StringBuilder();
        while (!heap.isEmpty()) out.append(heap.poll()).append(' ');
        assertEquals("1 2 3 5 7 8 9 ", out.toString());   // 꺼내는 순서는 정렬 순서 = 힙 정렬
    }

    @Test
    void 내부_배열은_정렬돼_있지_않다() {
        MyMinHeap<Integer> heap = new MyMinHeap<>();
        for (int v : new int[]{5, 3, 8, 1}) heap.offer(v);

        // 힙 규칙(부모 ≤ 자식)만 만족하면 된다. 정렬된 [1, 3, 5, 8]이 아님.
        assertEquals("[1, 3, 8, 5]", heap.toString());
        //      1
        //     / \
        //    3   8
        //   /
        //  5
    }

    @Test
    void 중복값도_정상() {
        MyMinHeap<Integer> heap = new MyMinHeap<>();
        for (int v : new int[]{3, 1, 3, 1, 2}) heap.offer(v);

        assertEquals(1, heap.poll());
        assertEquals(1, heap.poll());
        assertEquals(2, heap.poll());
        assertEquals(3, heap.poll());
        assertEquals(3, heap.poll());
    }

    @Test
    void 빈_힙은_예외() {
        MyMinHeap<Integer> heap = new MyMinHeap<>();
        assertThrows(NoSuchElementException.class, heap::poll);
        assertThrows(NoSuchElementException.class, heap::peek);
    }

    @Test
    void 많이_넣어도_확장되고_순서_유지() {
        MyMinHeap<Integer> heap = new MyMinHeap<>();
        for (int i = 1000; i > 0; i--) heap.offer(i);   // 역순으로 1000개

        int prev = Integer.MIN_VALUE;
        while (!heap.isEmpty()) {
            int cur = heap.poll();
            assertTrue(cur > prev, "순서가 깨짐: " + prev + " 다음에 " + cur);
            prev = cur;
        }
    }

    @Test
    void 문자열_우선순위() {
        MyMinHeap<String> heap = new MyMinHeap<>();
        heap.offer("banana");
        heap.offer("apple");
        heap.offer("cherry");
        assertEquals("apple", heap.poll());
    }

    @Test
    void TopK_가장_큰_3개() {
        int[] numbers = {4, 1, 9, 7, 3, 8, 2, 6, 5};
        assertArrayEquals(new int[]{7, 8, 9}, MyMinHeap.topK(numbers, 3));
    }

    @Test
    void TopK_k가_배열보다_크면_전부_정렬해서() {
        assertArrayEquals(new int[]{1, 2, 3}, MyMinHeap.topK(new int[]{3, 1, 2}, 10));
    }
}
