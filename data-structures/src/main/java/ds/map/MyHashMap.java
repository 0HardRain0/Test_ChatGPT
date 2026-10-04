package ds.map;

/**
 * 해시맵 — 키로 값을 O(1)에 찾는 자료구조 (java.util.HashMap을 바닥부터 구현)
 *
 * 핵심 아이디어: "키를 숫자로 바꿔서 배열 인덱스로 쓴다"
 *
 *   1. key.hashCode()  → 임의의 정수      (예: "apple".hashCode() = 93029210)
 *   2. 정수 % 배열길이  → 배열 인덱스      (예: 93029210 % 16 = 10)
 *   3. buckets[10]에 저장
 *
 *   찾을 때도 같은 계산을 하면 바로 그 칸으로 간다. 배열을 뒤질 필요가 없다 → O(1)
 *
 * 문제: 해시 충돌 (collision)
 *   다른 키인데 같은 인덱스가 나올 수 있다 (정수는 무한한데 배열 칸은 16개뿐이니 당연).
 *   해결책 중 하나가 "체이닝(chaining)": 각 칸을 연결 리스트로 만들어 같은 칸에 여러 개를 매단다.
 *
 *     buckets[0]  → null
 *     buckets[1]  → [key="b", val=2] → null
 *     buckets[2]  → null
 *     buckets[3]  → [key="x", val=9] → [key="y", val=4] → null     ← 충돌! 둘 다 3번 칸
 *     ...
 *
 *   충돌이 많아지면 연결 리스트가 길어져 O(n)에 가까워진다. 그래서 요소가 일정 비율(load factor 0.75)을
 *   넘으면 배열을 2배로 늘리고 전부 다시 배치한다 (rehash). 그러면 다시 O(1)에 가까워진다.
 *
 * 시간 복잡도: get, put, remove  평균 O(1), 최악 O(n) (모든 키가 한 칸에 몰릴 때)
 *
 * ★ equals와 hashCode를 같이 재정의해야 하는 이유가 바로 여기 있다 ★
 *   - hashCode가 다르면 아예 다른 칸을 보니까 equals가 같아도 못 찾는다.
 *   - hashCode가 같아도 같은 칸 안에서 equals로 최종 확인한다.
 *   → "equals가 true면 hashCode도 반드시 같아야 한다"는 규약은 이 구조 때문에 생긴 것.
 *
 * 어디에 쓰이나: 캐시, 중복 체크, 개수 세기(단어 빈도), DB의 해시 인덱스, JSON 객체, 세션 저장소…
 */
public class MyHashMap<K, V> {

    /** 한 칸(bucket)에 매달리는 연결 리스트의 노드 */
    private static class Entry<K, V> {
        final K key;
        V value;
        final int hash;        // 매번 hashCode()를 다시 부르지 않게 저장해 둔다 (rehash 때도 재사용)
        Entry<K, V> next;

        Entry(K key, V value, int hash, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] buckets;
    private int size;
    private int threshold;   // size가 이 값을 넘으면 resize. = capacity × 0.75

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        buckets = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // ───────────────────────── 핵심 3연산 ─────────────────────────

    /** 넣기. 같은 키가 이미 있으면 값을 덮어쓰고 이전 값을 돌려준다. */
    public V put(K key, V value) {
        int hash = hash(key);
        int index = indexFor(hash, buckets.length);

        // 1) 같은 키가 이미 그 칸에 매달려 있나? 있으면 값만 교체
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (e.hash == hash && keyEquals(e.key, key)) {   // hash 먼저 비교 (정수 비교가 equals보다 싸다)
                V old = e.value;
                e.value = value;
                return old;
            }
        }

        // 2) 없으면 새 Entry를 그 칸의 "맨 앞"에 끼운다 (연결 리스트 addFirst — O(1))
        buckets[index] = new Entry<>(key, value, hash, buckets[index]);
        size++;

        if (size > threshold) resize();
        return null;
    }

    /** 찾기. 없으면 null */
    public V get(K key) {
        Entry<K, V> e = findEntry(key);
        return e == null ? null : e.value;
    }

    public boolean containsKey(K key) {
        return findEntry(key) != null;
    }

    /** 지우기. 연결 리스트에서 노드 하나 떼어내기 */
    public V remove(K key) {
        int hash = hash(key);
        int index = indexFor(hash, buckets.length);

        Entry<K, V> prev = null;
        for (Entry<K, V> e = buckets[index]; e != null; prev = e, e = e.next) {
            if (e.hash == hash && keyEquals(e.key, key)) {
                if (prev == null) buckets[index] = e.next;   // 첫 노드였으면 칸의 시작점을 다음으로
                else prev.next = e.next;                     // 아니면 앞 노드가 다음을 가리키게
                size--;
                return e.value;
            }
        }
        return null;
    }

    /** 없으면 기본값. 단어 빈도 세기 같은 데서 자주 쓴다: map.put(w, map.getOrDefault(w, 0) + 1) */
    public V getOrDefault(K key, V defaultValue) {
        Entry<K, V> e = findEntry(key);
        return e == null ? defaultValue : e.value;
    }

    // ───────────────────────── 내부 도우미 ─────────────────────────

    private Entry<K, V> findEntry(K key) {
        int hash = hash(key);
        int index = indexFor(hash, buckets.length);
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (e.hash == hash && keyEquals(e.key, key)) return e;
        }
        return null;
    }

    /**
     * 해시 계산. null 키는 0번 칸에 넣는다 (java.util.HashMap도 null 키 하나를 허용).
     * (h >>> 16) ^ h : 상위 비트를 하위 비트에 섞어 준다. 배열이 작을 때 indexFor가 하위 비트만 쓰기 때문에
     * 상위 비트만 다른 해시들이 같은 칸에 몰리는 걸 막는다. java.util.HashMap과 같은 기법.
     */
    private static int hash(Object key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    /**
     * 해시 → 배열 인덱스. 음수가 나올 수 있어서 Math.abs 대신 비트 마스크를 쓴다.
     * capacity가 2의 제곱이면  hash & (capacity - 1)  ==  hash % capacity  (단, 항상 양수).
     * 예: capacity=16 → 15 = 0b1111 → 하위 4비트만 남긴다.
     */
    private static int indexFor(int hash, int capacity) {
        return hash & (capacity - 1);
    }

    private static boolean keyEquals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * 배열을 2배로 늘리고 모든 Entry를 새 위치에 다시 배치한다. O(n).
     * 자주 일어나지 않으므로(2배씩 커지니까) 평균 비용은 여전히 O(1).
     * 저장해 둔 e.hash를 재사용하므로 hashCode()를 다시 부르지 않는다.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] old = buckets;
        int newCapacity = old.length * 2;
        Entry<K, V>[] fresh = (Entry<K, V>[]) new Entry[newCapacity];

        for (Entry<K, V> head : old) {
            for (Entry<K, V> e = head; e != null; ) {
                Entry<K, V> next = e.next;              // 옮기면서 e.next가 바뀌니 미리 저장
                int index = indexFor(e.hash, newCapacity);
                e.next = fresh[index];                  // 새 칸의 맨 앞에 끼운다
                fresh[index] = e;
                e = next;
            }
        }
        buckets = fresh;
        threshold = (int) (newCapacity * LOAD_FACTOR);
    }

    /** 공부용: 현재 버킷 개수 */
    int capacity() {
        return buckets.length;
    }

    /** 공부용: 가장 긴 체인 길이. 충돌이 얼마나 심한지 볼 수 있다. 이상적으로는 1~2 */
    int longestChain() {
        int max = 0;
        for (Entry<K, V> head : buckets) {
            int len = 0;
            for (Entry<K, V> e = head; e != null; e = e.next) len++;
            max = Math.max(max, len);
        }
        return max;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Entry<K, V> head : buckets) {
            for (Entry<K, V> e = head; e != null; e = e.next) {
                if (!first) sb.append(", ");
                sb.append(e.key).append("=").append(e.value);
                first = false;
            }
        }
        return sb.append("}").toString();
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. keySet()을 MyArrayList<K>로 반환하는 메서드 만들기. 왜 HashMap은 순서가 없는지 출력해 보면 보인다.
    // TODO 2. hashCode()가 항상 1을 반환하는 클래스를 키로 써서 10만 개 put 후 get 시간 재 보기.
    //         그리고 정상 hashCode와 비교. "최악 O(n)"이 뭔지 숫자로 체감된다.
    // TODO 3. equals만 재정의하고 hashCode는 재정의하지 않은 클래스를 키로 써 보기.
    //         같은 내용의 객체를 새로 만들어 get하면 왜 null이 나오는지 설명해 보기.
    // TODO 4. (심화) 체이닝 대신 "개방 주소법(open addressing)"으로 바꿔 보기: 충돌 시 다음 빈 칸으로 이동.
}
