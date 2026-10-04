package ds.heap;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * 최소 힙 (Min Heap) = java.util.PriorityQueue의 기본 동작
 *
 * 규칙: "부모 ≤ 자식" (모든 노드에서). 그러면 루트가 항상 최솟값이다.
 *       BST와 달리 형제끼리의 순서는 신경 쓰지 않는다. 그래서 "완전 정렬"은 아니고 "최솟값만 빠르게".
 *
 *               1
 *            /     \
 *           3       2
 *          / \     / \
 *         7   4   5   9
 *
 * ★ 트릭: 트리인데 배열로 저장한다 ★
 *   완전 이진 트리(왼쪽부터 빈틈없이 채운 트리)는 배열에 그대로 넣을 수 있다. 노드 객체도, 참조도 필요 없다.
 *
 *   배열:  [1, 3, 2, 7, 4, 5, 9]
 *   인덱스:  0  1  2  3  4  5  6
 *
 *   i의 부모      = (i - 1) / 2
 *   i의 왼쪽 자식 = 2i + 1
 *   i의 오른쪽    = 2i + 2
 *
 * 연산
 *  - offer (삽입): 배열 맨 끝에 넣고, 부모보다 작으면 교환하며 **위로** 올라간다 (sift up / heapify up)
 *  - poll  (최솟값 꺼내기): 루트를 빼고, 맨 끝 요소를 루트로 옮긴 뒤, 자식 중 작은 쪽과 교환하며 **아래로** 내려간다 (sift down)
 *  - peek: 배열[0]
 *
 * 시간 복잡도: offer, poll  O(log n)  (트리 높이만큼만 이동),  peek O(1)
 *
 * 어디에 쓰이나
 *  - 우선순위 큐: 응급실 환자 분류, OS 스케줄러, 작업 큐의 우선순위
 *  - Top-K 문제: "100만 개 중 가장 큰 10개" → 크기 10짜리 최소 힙 유지. 전체 정렬 O(n log n)보다 빠른 O(n log k)
 *  - 다익스트라 최단 경로, 허프만 압축
 *  - 힙 정렬, 여러 정렬된 리스트 합치기 (k-way merge)
 */
public class MyMinHeap<E extends Comparable<E>> {

    private Object[] heap;
    private int size;

    public MyMinHeap() {
        heap = new Object[16];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** 최솟값 보기. O(1) */
    @SuppressWarnings("unchecked")
    public E peek() {
        if (isEmpty()) throw new NoSuchElementException("힙이 비어 있습니다");
        return (E) heap[0];
    }

    /** 삽입. 맨 끝에 넣고 위로 올린다. O(log n) */
    public void offer(E value) {
        if (size == heap.length) heap = Arrays.copyOf(heap, size * 2);
        heap[size] = value;
        siftUp(size);
        size++;
    }

    /** 최솟값 꺼내기. 마지막 요소를 루트로 옮기고 아래로 내린다. O(log n) */
    @SuppressWarnings("unchecked")
    public E poll() {
        if (isEmpty()) throw new NoSuchElementException("힙이 비어 있습니다");
        E min = (E) heap[0];
        size--;
        heap[0] = heap[size];     // 마지막을 루트로
        heap[size] = null;
        if (size > 0) siftDown(0);
        return min;
    }

    // ───────────────────────── 핵심 두 메서드 ─────────────────────────

    /** i 위치의 요소를 부모와 비교하며 올린다 */
    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (compare(i, parent) >= 0) break;   // 부모 ≤ 나 → 규칙 만족, 멈춤
            swap(i, parent);
            i = parent;
        }
    }

    /** i 위치의 요소를 자식 중 작은 쪽과 비교하며 내린다 */
    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < size && compare(left, smallest) < 0) smallest = left;
            if (right < size && compare(right, smallest) < 0) smallest = right;

            if (smallest == i) break;   // 양쪽 자식 모두 나보다 크거나 같음 → 멈춤
            swap(i, smallest);
            i = smallest;
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(int i, int j) {
        return ((E) heap[i]).compareTo((E) heap[j]);
    }

    private void swap(int i, int j) {
        Object tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    @Override
    public String toString() {
        // 배열 순서 그대로 출력. 정렬돼 있지 않다는 걸 눈으로 확인할 수 있다
        return Arrays.toString(Arrays.copyOf(heap, size));
    }

    // ───────────────────────── 대표 활용 예제 ─────────────────────────

    /**
     * Top-K: 배열에서 가장 큰 k개를 구한다. O(n log k)
     * 크기 k짜리 최소 힙을 유지하면, 힙의 루트(최솟값)가 "현재까지의 k등"이다.
     * 새 값이 k등보다 크면 k등을 버리고 새 값을 넣는다.
     */
    public static int[] topK(int[] numbers, int k) {
        MyMinHeap<Integer> heap = new MyMinHeap<>();
        for (int n : numbers) {
            if (heap.size() < k) {
                heap.offer(n);
            } else if (n > heap.peek()) {
                heap.poll();
                heap.offer(n);
            }
        }
        int[] result = new int[heap.size()];
        for (int i = 0; i < result.length; i++) result[i] = heap.poll();   // poll은 작은 것부터 → 오름차순으로 채워진다
        return result;
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. MyMaxHeap 만들기 — compare 부호만 뒤집으면 된다. 또는 Comparator를 생성자로 받아 하나로 합치기.
    // TODO 2. 힙 정렬: 배열 전체를 offer한 뒤 poll을 n번 → 오름차순. 시간 복잡도는?
    // TODO 3. heapify: 배열 n개를 하나씩 offer하면 O(n log n)인데, 뒤에서부터 siftDown하면 O(n)에 힙이 된다. 구현해 보기.
    // TODO 4. 정렬된 배열 k개를 하나로 합치기 (k-way merge). 각 배열의 첫 요소를 힙에 넣고 시작.
}
