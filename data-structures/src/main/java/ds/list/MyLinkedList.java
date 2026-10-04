package ds.list;

import java.util.NoSuchElementException;

/**
 * 이중 연결 리스트 (java.util.LinkedList를 바닥부터 구현)
 *
 * 핵심 아이디어
 *  - 배열이 아니다. 요소마다 "노드"라는 작은 상자를 만들고, 상자끼리 앞/뒤 참조로 연결한다.
 *  - 메모리상 연속되어 있지 않다. 그래서 "3번째"로 바로 갈 수 없고 앞에서부터 따라가야 한다.
 *
 *     head                                    tail
 *      ↓                                       ↓
 *    [null|A|•] ⇄ [•|B|•] ⇄ [•|C|•] ⇄ [•|D|null]
 *     prev val next
 *
 * 시간 복잡도
 *  - addFirst / addLast / removeFirst / removeLast   O(1)   참조 몇 개만 바꾸면 끝
 *  - get(i)                                           O(n)   앞(또는 뒤)에서부터 i번 따라가야 함
 *  - 중간 삽입·삭제 (노드를 이미 알고 있을 때)          O(1)   ← 이론상 장점
 *  - 중간 삽입·삭제 (인덱스로)                          O(n)   노드를 찾는 데 O(n)
 *
 * 현실: 자바 실무에서 LinkedList는 거의 안 쓴다. 노드마다 객체를 만들어 메모리를 더 쓰고,
 *       메모리가 흩어져 있어 CPU 캐시 효율이 나쁘다. 대부분 ArrayList가 더 빠르다.
 *       그래도 배우는 이유: Stack/Queue/Deque, LRU 캐시, 트리·그래프 구현의 기초가 되기 때문.
 */
public class MyLinkedList<E> {

    /** 노드 = 값 + 앞 참조 + 뒤 참조. 리스트 밖에서는 볼 필요가 없어서 private static */
    private static class Node<E> {
        E value;
        Node<E> prev;
        Node<E> next;

        Node(E value) {
            this.value = value;
        }
    }

    private Node<E> head;  // 첫 노드
    private Node<E> tail;  // 마지막 노드
    private int size;

    // ───────────────────────── 조회 ─────────────────────────

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E getFirst() {
        if (head == null) throw new NoSuchElementException("리스트가 비어 있습니다");
        return head.value;
    }

    public E getLast() {
        if (tail == null) throw new NoSuchElementException("리스트가 비어 있습니다");
        return tail.value;
    }

    /** O(n). 인덱스가 뒤쪽이면 tail에서 거꾸로 가는 최적화 — 그래도 평균 n/4 번은 따라가야 한다 */
    public E get(int index) {
        return node(index).value;
    }

    public int indexOf(Object target) {
        int i = 0;
        for (Node<E> cur = head; cur != null; cur = cur.next, i++) {
            if (target == null ? cur.value == null : target.equals(cur.value)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(Object target) {
        return indexOf(target) >= 0;
    }

    // ───────────────────────── 추가 ─────────────────────────

    /** O(1). 새 노드를 head 앞에 붙인다 */
    public void addFirst(E element) {
        Node<E> node = new Node<>(element);
        if (head == null) {              // 비어 있으면 head = tail = 새 노드
            head = tail = node;
        } else {
            node.next = head;            // 새 노드 → 기존 head
            head.prev = node;            // 기존 head ← 새 노드
            head = node;                 // head 갱신
        }
        size++;
    }

    /** O(1). 새 노드를 tail 뒤에 붙인다 */
    public void addLast(E element) {
        Node<E> node = new Node<>(element);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
    }

    /** ArrayList와 같은 이름의 add = addLast */
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    /** 인덱스 위치에 삽입. 노드 찾기 O(n) + 연결 바꾸기 O(1) */
    public void add(int index, E element) {
        if (index == size) {             // 맨 뒤 = addLast
            addLast(element);
            return;
        }
        Node<E> target = node(index);    // 이 노드 "앞"에 끼운다 (범위 검사 포함)
        if (target == head) {
            addFirst(element);
            return;
        }
        Node<E> node = new Node<>(element);
        Node<E> before = target.prev;
        // before ⇄ node ⇄ target  이 되도록 참조 4개를 바꾼다
        node.prev = before;
        node.next = target;
        before.next = node;
        target.prev = node;
        size++;
    }

    // ───────────────────────── 삭제 ─────────────────────────

    public E removeFirst() {
        if (head == null) throw new NoSuchElementException("리스트가 비어 있습니다");
        return unlink(head);
    }

    public E removeLast() {
        if (tail == null) throw new NoSuchElementException("리스트가 비어 있습니다");
        return unlink(tail);
    }

    public E remove(int index) {
        return unlink(node(index));
    }

    public boolean remove(Object target) {
        for (Node<E> cur = head; cur != null; cur = cur.next) {
            if (target == null ? cur.value == null : target.equals(cur.value)) {
                unlink(cur);
                return true;
            }
        }
        return false;
    }

    public void clear() {
        // 참조만 끊으면 노드들은 GC가 수거한다
        head = tail = null;
        size = 0;
    }

    // ───────────────────────── 내부 도우미 ─────────────────────────

    /**
     * 노드 하나를 리스트에서 떼어 낸다. O(1).
     * 연결 리스트의 진짜 장점은 바로 이 메서드다 — 어디에 있든 앞뒤 참조 2개만 고치면 끝.
     */
    private E unlink(Node<E> node) {
        E value = node.value;
        Node<E> before = node.prev;
        Node<E> after = node.next;

        if (before == null) head = after;     // 첫 노드였다면 head 갱신
        else before.next = after;

        if (after == null) tail = before;     // 마지막 노드였다면 tail 갱신
        else after.prev = before;

        node.prev = node.next = null;         // 떼어낸 노드의 참조를 끊어 GC 도움
        node.value = null;
        size--;
        return value;
    }

    /** index번째 노드를 찾는다. 앞쪽이면 head에서, 뒤쪽이면 tail에서 출발 */
    private Node<E> node(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
        Node<E> cur;
        if (index < size / 2) {
            cur = head;
            for (int i = 0; i < index; i++) cur = cur.next;
        } else {
            cur = tail;
            for (int i = size - 1; i > index; i--) cur = cur.prev;
        }
        return cur;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<E> cur = head; cur != null; cur = cur.next) {
            if (cur != head) sb.append(", ");
            sb.append(cur.value);
        }
        return sb.append("]").toString();
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. reverse() — 리스트를 뒤집기. 모든 노드의 prev/next를 교환하고 head/tail도 바꾼다.
    // TODO 2. 단일 연결 리스트(prev 없이 next만)로 바꿔 보기. removeLast가 왜 O(n)이 되는지 체감된다.
    // TODO 3. MyArrayList와 MyLinkedList에 각각 10만 개를 add(0, x)로 앞에 넣고 시간을 재 보기.
    //         그리고 get(i)로 전부 순회하는 시간도 재 보기. 어느 쪽이 어디서 이기는가?
}
