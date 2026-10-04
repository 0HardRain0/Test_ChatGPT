package ds.list;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyArrayListTest {

    @Test
    void 추가하고_인덱스로_꺼내기() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("c", list.get(2));
        assertEquals("[a, b, c]", list.toString());
    }

    @Test
    void 중간에_끼워_넣으면_뒤가_밀린다() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        list.add(3);
        list.add(1, 2);   // 1번 자리에 2를 끼움

        assertEquals("[1, 2, 3]", list.toString());
    }

    @Test
    void 삭제하면_뒤가_당겨진다() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assertEquals("b", list.remove(1));
        assertEquals("[a, c]", list.toString());
        assertEquals(2, list.size());

        assertTrue(list.remove("c"));
        assertFalse(list.remove("없는값"));
        assertEquals("[a]", list.toString());
    }

    @Test
    void 꽉_차면_자동으로_확장된다() {
        MyArrayList<Integer> list = new MyArrayList<>();
        assertEquals(10, list.capacity());

        for (int i = 0; i < 11; i++) list.add(i);   // 11개째에서 확장

        assertEquals(15, list.capacity());   // 10 × 1.5
        assertEquals(11, list.size());
        assertEquals(10, list.get(10));
    }

    @Test
    void 범위_밖_인덱스는_예외() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(5, "x"));
    }

    @Test
    void indexOf와_contains() {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("x");
        list.add(null);   // null도 담을 수 있어야 한다
        list.add("y");

        assertEquals(0, list.indexOf("x"));
        assertEquals(1, list.indexOf(null));
        assertEquals(-1, list.indexOf("z"));
        assertTrue(list.contains("y"));
    }
}
