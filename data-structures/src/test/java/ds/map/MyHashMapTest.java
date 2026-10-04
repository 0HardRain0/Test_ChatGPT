package ds.map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyHashMapTest {

    @Test
    void 넣고_키로_찾기() {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        map.put("apple", 1);
        map.put("banana", 2);

        assertEquals(1, map.get("apple"));
        assertEquals(2, map.get("banana"));
        assertNull(map.get("cherry"));
        assertEquals(2, map.size());
        assertTrue(map.containsKey("apple"));
        assertFalse(map.containsKey("cherry"));
    }

    @Test
    void 같은_키로_다시_넣으면_덮어쓴다() {
        MyHashMap<String, String> map = new MyHashMap<>();
        assertNull(map.put("k", "v1"));              // 처음은 이전 값 없음
        assertEquals("v1", map.put("k", "v2"));      // 이전 값 v1 반환

        assertEquals("v2", map.get("k"));
        assertEquals(1, map.size());                 // 개수는 그대로
    }

    @Test
    void 삭제() {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        assertEquals(1, map.remove("a"));
        assertNull(map.remove("a"));                 // 두 번 지우면 null
        assertNull(map.get("a"));
        assertEquals(1, map.size());
    }

    @Test
    void null_키도_허용() {
        MyHashMap<String, String> map = new MyHashMap<>();
        map.put(null, "널값");
        assertEquals("널값", map.get(null));
    }

    @Test
    void 많이_넣으면_자동으로_resize_되고_값은_유지된다() {
        MyHashMap<Integer, String> map = new MyHashMap<>();
        assertEquals(16, map.capacity());

        for (int i = 0; i < 100; i++) map.put(i, "v" + i);

        assertEquals(100, map.size());
        // threshold = capacity × 0.75 를 넘을 때마다 2배: 16(12) → 32(24) → 64(48) → 128(96) → 256
        assertEquals(256, map.capacity());
        for (int i = 0; i < 100; i++) {
            assertEquals("v" + i, map.get(i));      // resize 후에도 전부 찾아져야 한다
        }
        assertTrue(map.longestChain() <= 4, "체인이 너무 길다: " + map.longestChain());
    }

    @Test
    void 해시_충돌이_나도_정상_동작한다() {
        // hashCode가 전부 같은 키 → 모두 한 칸에 체이닝된다. 느리지만 "맞게" 동작해야 한다.
        record BadKey(String name) {
            @Override
            public int hashCode() {
                return 42;
            }
        }

        MyHashMap<BadKey, Integer> map = new MyHashMap<>();
        map.put(new BadKey("a"), 1);
        map.put(new BadKey("b"), 2);
        map.put(new BadKey("c"), 3);

        assertEquals(3, map.size());
        assertEquals(3, map.longestChain());        // 셋 다 한 체인에
        assertEquals(2, map.get(new BadKey("b")));  // equals로 구분해서 찾는다
        assertEquals(1, map.remove(new BadKey("a")));
        assertEquals(3, map.get(new BadKey("c")));
    }

    @Test
    void 단어_빈도_세기_활용_예제() {
        String text = "the quick brown fox jumps over the lazy dog the end";
        MyHashMap<String, Integer> freq = new MyHashMap<>();

        for (String word : text.split(" ")) {
            freq.put(word, freq.getOrDefault(word, 0) + 1);
        }

        assertEquals(3, freq.get("the"));
        assertEquals(1, freq.get("fox"));
        assertNull(freq.get("cat"));
    }
}
