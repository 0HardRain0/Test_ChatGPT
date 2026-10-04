package ds.list;

import java.util.Arrays;

/**
 * 동적 배열 (java.util.ArrayList를 바닥부터 구현)
 *
 * 핵심 아이디어
 *  - 내부에 "진짜 배열"을 하나 들고 있다. 배열은 크기가 고정이라서
 *    꽉 차면 더 큰 배열을 새로 만들어 복사한다 (이게 "동적"의 정체).
 *  - size(실제 담긴 개수)와 capacity(배열 길이)는 다르다.
 *
 * 시간 복잡도
 *  - get(i), set(i)          O(1)   인덱스로 바로 접근
 *  - add(마지막)              O(1)   (가끔 확장이 일어나면 O(n)이지만 평균은 O(1) — 분할 상환)
 *  - add(중간), remove(중간)  O(n)   뒤의 요소를 전부 한 칸씩 밀어야 함
 *  - contains, indexOf       O(n)   앞에서부터 하나씩 비교
 *
 * 언제 쓰나: "목록"이 필요한 거의 모든 경우. 인덱스 접근이 빠르고 메모리가 연속이라 캐시 효율이 좋다.
 */
public class MyArrayList<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;  // 제네릭 배열은 직접 못 만들어서 Object[]로 들고 형변환해서 꺼낸다
    private int size;           // 실제로 들어 있는 요소 개수

    public MyArrayList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    // ───────────────────────── 조회 ─────────────────────────

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];   // 배열 인덱스 접근 → O(1)
    }

    public int indexOf(Object target) {
        for (int i = 0; i < size; i++) {
            // null도 안전하게 비교하려고 Objects.equals 대신 직접 분기
            if (target == null ? elements[i] == null : target.equals(elements[i])) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(Object target) {
        return indexOf(target) >= 0;
    }

    // ───────────────────────── 추가 ─────────────────────────

    /** 맨 뒤에 추가. 평균 O(1) */
    public boolean add(E element) {
        ensureCapacity(size + 1);
        elements[size] = element;
        size++;
        return true;
    }

    /** 중간에 끼워 넣기. index 이후 요소를 모두 한 칸 뒤로 밀어야 하므로 O(n) */
    public void add(int index, E element) {
        if (index < 0 || index > size) {   // add는 index == size(맨 뒤) 허용
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
        ensureCapacity(size + 1);
        // [index .. size-1] 구간을 [index+1 .. size] 로 한 칸씩 이동
        //   src          dest        length
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    public E set(int index, E element) {
        checkIndex(index);
        E old = get(index);
        elements[index] = element;
        return old;
    }

    // ───────────────────────── 삭제 ─────────────────────────

    /** index 위치 삭제. 뒤의 요소를 모두 한 칸 앞으로 당기므로 O(n) */
    public E remove(int index) {
        checkIndex(index);
        E removed = get(index);
        int moveCount = size - index - 1;   // 당겨야 할 요소 개수
        if (moveCount > 0) {
            System.arraycopy(elements, index + 1, elements, index, moveCount);
        }
        size--;
        elements[size] = null;  // 마지막 칸을 비워야 가비지 컬렉터가 수거할 수 있다 (메모리 누수 방지)
        return removed;
    }

    /** 값으로 삭제 (첫 번째로 발견된 것만) */
    public boolean remove(Object target) {
        int index = indexOf(target);
        if (index < 0) return false;
        remove(index);
        return true;
    }

    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
    }

    // ───────────────────────── 내부 도우미 ─────────────────────────

    /**
     * 확장 전략: 꽉 차면 1.5배로 키운다 (java.util.ArrayList와 동일).
     * 1개씩 늘리면 add할 때마다 O(n) 복사가 일어나 전체가 O(n²)이 된다.
     * 배수로 늘리면 복사 횟수가 log(n)번으로 줄어 add의 "평균" 비용이 O(1)이 된다.
     */
    private void ensureCapacity(int required) {
        if (required <= elements.length) return;
        int newCapacity = elements.length + (elements.length >> 1);  // ×1.5  (>>1 은 /2)
        if (newCapacity < required) newCapacity = required;
        elements = Arrays.copyOf(elements, newCapacity);  // 새 배열 만들고 복사 → O(n)
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }

    /** 공부용: 현재 내부 배열 길이 (실제 ArrayList는 노출하지 않는다) */
    int capacity() {
        return elements.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        return sb.append("]").toString();
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. addAll(MyArrayList<E> other) — 다른 리스트를 뒤에 통째로 붙이기. ensureCapacity를 한 번만 호출하도록.
    // TODO 2. trimToSize() — 안 쓰는 capacity를 size에 맞게 줄이기.
    // TODO 3. 역순으로 순회하며 remove(i)를 호출하면 안전하고, 정순으로 하면 왜 요소를 건너뛰는지 테스트로 확인해 보기.
}
