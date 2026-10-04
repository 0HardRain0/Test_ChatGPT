package ds.tree;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyBinarySearchTreeTest {

    /**
     *          50
     *        /    \
     *      30      70
     *     /  \    /  \
     *   20   40  60   80
     */
    private MyBinarySearchTree<Integer> sample() {
        MyBinarySearchTree<Integer> t = new MyBinarySearchTree<>();
        for (int v : new int[]{50, 30, 70, 20, 40, 60, 80}) t.insert(v);
        return t;
    }

    @Test
    void 삽입과_검색() {
        MyBinarySearchTree<Integer> t = sample();

        assertEquals(7, t.size());
        assertTrue(t.contains(40));
        assertTrue(t.contains(80));
        assertFalse(t.contains(55));
        assertFalse(t.insert(50));   // 중복은 거부
        assertEquals(7, t.size());
    }

    @Test
    void 중위_순회는_오름차순() {
        assertEquals("[20, 30, 40, 50, 60, 70, 80]", sample().inOrder().toString());
    }

    @Test
    void 전위와_후위_순회() {
        MyBinarySearchTree<Integer> t = sample();
        assertEquals("[50, 30, 20, 40, 70, 60, 80]", t.preOrder().toString());   // 부모 먼저
        assertEquals("[20, 40, 30, 60, 80, 70, 50]", t.postOrder().toString());  // 자식 먼저, 루트 마지막
    }

    @Test
    void 최솟값_최댓값_높이() {
        MyBinarySearchTree<Integer> t = sample();
        assertEquals(20, t.min());
        assertEquals(80, t.max());
        assertEquals(3, t.height());
    }

    @Test
    void 삭제_잎_노드() {
        MyBinarySearchTree<Integer> t = sample();
        assertTrue(t.remove(20));
        assertEquals("[30, 40, 50, 60, 70, 80]", t.inOrder().toString());
        assertEquals(6, t.size());
    }

    @Test
    void 삭제_자식_하나() {
        MyBinarySearchTree<Integer> t = sample();
        t.remove(20);          // 30의 왼쪽 자식이 사라져 30은 자식 하나(40)만 남음
        assertTrue(t.remove(30));
        assertEquals("[40, 50, 60, 70, 80]", t.inOrder().toString());
        assertTrue(t.contains(40));   // 40이 30 자리로 올라왔는지
    }

    @Test
    void 삭제_자식_둘_루트() {
        MyBinarySearchTree<Integer> t = sample();
        assertTrue(t.remove(50));   // 루트. 후계자 60으로 교체돼야 한다
        assertEquals("[20, 30, 40, 60, 70, 80]", t.inOrder().toString());
        assertEquals(6, t.size());
        assertEquals("[60, 30, 20, 40, 70, 80]", t.preOrder().toString());   // 새 루트가 60
    }

    @Test
    void 없는_값_삭제는_false() {
        MyBinarySearchTree<Integer> t = sample();
        assertFalse(t.remove(999));
        assertEquals(7, t.size());
    }

    @Test
    void 정렬된_순서로_넣으면_한쪽으로_쏠린다() {
        MyBinarySearchTree<Integer> sorted = new MyBinarySearchTree<>();
        for (int i = 1; i <= 100; i++) sorted.insert(i);

        MyBinarySearchTree<Integer> shuffled = new MyBinarySearchTree<>();
        for (int v : new int[]{50, 25, 75, 12, 37, 62, 87, 6, 18, 31, 43, 56, 68, 81, 93}) shuffled.insert(v);

        assertEquals(100, sorted.height());    // 연결 리스트가 됐다 → 검색 O(n)
        assertEquals(4, shuffled.height());    // 15개인데 높이 4 = log₂(16) → 검색 O(log n)
    }

    @Test
    void 문자열도_된다() {
        MyBinarySearchTree<String> t = new MyBinarySearchTree<>();
        for (String s : new String[]{"banana", "apple", "cherry"}) t.insert(s);
        assertEquals("[apple, banana, cherry]", t.inOrder().toString());
    }
}
