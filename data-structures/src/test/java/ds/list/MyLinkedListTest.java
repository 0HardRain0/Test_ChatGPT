package ds.list;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyLinkedListTest {

    @Test
    void 앞뒤로_추가() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.addLast("b");
        list.addLast("c");
        list.addFirst("a");

        assertEquals("[a, b, c]", list.toString());
        assertEquals("a", list.getFirst());
        assertEquals("c", list.getLast());
        assertEquals(3, list.size());
    }

    @Test
    void 인덱스로_접근은_되지만_앞에서부터_따라간다() {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        for (int i = 0; i < 10; i++) list.add(i);

        assertEquals(0, list.get(0));
        assertEquals(7, list.get(7));   // 뒤쪽이라 tail에서 거꾸로 2칸
        assertEquals(9, list.get(9));
    }

    @Test
    void 중간_삽입() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("a");
        list.add("c");
        list.add(1, "b");      // 중간
        list.add(0, "start");  // 맨 앞
        list.add(4, "end");    // 맨 뒤 (index == size)

        assertEquals("[start, a, b, c, end]", list.toString());
    }

    @Test
    void 앞뒤_삭제() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals("a", list.removeFirst());
        assertEquals("c", list.removeLast());
        assertEquals("[b]", list.toString());

        list.removeFirst();
        assertTrue(list.isEmpty());
        assertThrows(NoSuchElementException.class, list::removeFirst);
    }

    @Test
    void 중간_노드_삭제시_앞뒤가_이어진다() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals("b", list.remove(1));
        assertEquals("[a, c]", list.toString());
        assertEquals("c", list.get(1));   // a의 next가 c로 이어졌는지
        assertEquals("c", list.getLast());

        assertTrue(list.remove("a"));
        assertFalse(list.remove("없음"));
        assertEquals("[c]", list.toString());
        assertEquals("c", list.getFirst());   // head가 c로 갱신됐는지
    }

    @Test
    void 요소_하나만_있을_때_삭제하면_head와_tail_모두_null() {
        MyLinkedList<String> list = new MyLinkedList<>();
        list.add("only");
        list.remove(0);

        assertTrue(list.isEmpty());
        assertThrows(NoSuchElementException.class, list::getFirst);
        assertThrows(NoSuchElementException.class, list::getLast);

        list.add("again");   // 비운 뒤 다시 추가해도 정상 동작해야 한다
        assertEquals("[again]", list.toString());
    }
}
