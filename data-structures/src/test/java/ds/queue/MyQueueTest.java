package ds.queue;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyQueueTest {

    @Test
    void 먼저_넣은_것이_먼저_나온다() {
        MyQueue<String> queue = new MyQueue<>();
        queue.offer("a");
        queue.offer("b");
        queue.offer("c");

        assertEquals("a", queue.peek());
        assertEquals("a", queue.poll());
        assertEquals("b", queue.poll());
        assertEquals("c", queue.poll());
        assertTrue(queue.isEmpty());
    }

    @Test
    void 빈_큐에서_poll은_null_remove는_예외() {
        MyQueue<Integer> queue = new MyQueue<>();
        assertNull(queue.poll());
        assertNull(queue.peek());
        assertThrows(NoSuchElementException.class, queue::remove);
    }

    @Test
    void 원형_배열이_끝에서_처음으로_돌아간다() {
        // 초기 capacity 8. 넣고 빼기를 반복해서 rear가 배열 끝을 넘어 0으로 돌아가게 만든다.
        MyQueue<Integer> queue = new MyQueue<>();
        for (int i = 0; i < 6; i++) queue.offer(i);     // rear=6
        for (int i = 0; i < 4; i++) queue.poll();       // front=4, 남은 것: 4, 5
        for (int i = 6; i < 10; i++) queue.offer(i);    // rear가 8을 넘어 0,1로 돌아감. 확장 없이 들어가야 함

        assertEquals(6, queue.size());
        assertEquals("front → [4, 5, 6, 7, 8, 9] ← rear", queue.toString());
        assertEquals(4, queue.poll());
        assertEquals(5, queue.poll());
        assertEquals(6, queue.poll());
    }

    @Test
    void 확장할_때_순서가_유지된다() {
        MyQueue<Integer> queue = new MyQueue<>();
        for (int i = 0; i < 3; i++) queue.offer(i);
        for (int i = 0; i < 3; i++) queue.poll();       // front를 3으로 옮겨 둠 (일부러 꼬인 상태로 만든다)
        for (int i = 0; i < 20; i++) queue.offer(i);    // 8 → 16 → 32 두 번 확장

        assertEquals(20, queue.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, queue.poll());              // 넣은 순서 그대로 나와야 한다
        }
    }
}
