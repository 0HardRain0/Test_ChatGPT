package ds.queue;

import java.util.NoSuchElementException;

/**
 * 큐 — FIFO (First In, First Out). 먼저 넣은 게 먼저 나온다.
 *
 * 줄 서기와 같다. 뒤로 들어오고(offer / enqueue), 앞에서 나간다(poll / dequeue).
 *
 *     poll() ← │ A │ B │ C │ D │ ← offer(E)
 *             front           rear
 *
 * 구현: 원형 배열 (circular buffer)
 *
 *  단순하게 배열 앞에서 빼면 뒤 요소를 전부 당겨야 해서 O(n)이다 (MyArrayList.remove(0)과 같음).
 *  해결책: 빼도 당기지 않고 front 인덱스만 한 칸 앞으로 옮긴다. 그러면 배열 앞쪽에 빈 칸이 생기는데,
 *  rear가 배열 끝에 닿으면 처음으로 "돌아가서" 그 빈 칸을 재사용한다. 그래서 "원형".
 *
 *     인덱스:  0   1   2   3   4
 *            [ E ][   ][   ][ C ][ D ]     front=3, rear=1  (A, B는 이미 나갔고 E가 0번으로 돌아옴)
 *
 *  인덱스를 돌리는 공식:  (i + 1) % capacity
 *
 * 시간 복잡도: offer, poll, peek 모두 O(1)
 *
 * 어디에 쓰이나
 *  - BFS(너비 우선 탐색) — 가까운 것부터 차례로 방문
 *  - 작업 대기열 (프린터 스풀, 메시지 큐, 스레드 풀의 작업 큐)
 *  - 캐시 버퍼, 키보드 입력 버퍼
 *  - 게시판에서 "요청 순서대로 처리"가 필요한 모든 곳
 */
public class MyQueue<E> {

    private Object[] elements;
    private int front;   // 다음에 poll할 위치
    private int rear;    // 다음에 offer할 위치
    private int size;    // front == rear 일 때 "비었나 꽉 찼나"를 구분하려면 size가 필요하다

    public MyQueue() {
        elements = new Object[8];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** 뒤에 넣는다. O(1) */
    public boolean offer(E element) {
        if (size == elements.length) grow();
        elements[rear] = element;
        rear = (rear + 1) % elements.length;   // 끝에 닿으면 0으로 돌아간다
        size++;
        return true;
    }

    /** 앞에서 뺀다. 비어 있으면 null. O(1) */
    @SuppressWarnings("unchecked")
    public E poll() {
        if (isEmpty()) return null;
        E value = (E) elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;
        size--;
        return value;
    }

    /** 앞에서 뺀다. 비어 있으면 예외 (poll과의 차이는 이것뿐 — java.util.Queue도 두 종류를 제공) */
    public E remove() {
        if (isEmpty()) throw new NoSuchElementException("큐가 비어 있습니다");
        return poll();
    }

    /** 앞을 보기만 한다. O(1) */
    @SuppressWarnings("unchecked")
    public E peek() {
        return isEmpty() ? null : (E) elements[front];
    }

    /**
     * 확장. 원형이라 그냥 copyOf를 쓰면 안 된다 — front부터 순서대로 새 배열의 0번부터 다시 깔아야 한다.
     *   기존: [ E ][   ][   ][ C ][ D ]  front=3
     *   새것: [ C ][ D ][ E ][   ][   ] ...  front=0, rear=3
     */
    private void grow() {
        Object[] bigger = new Object[elements.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = elements[(front + i) % elements.length];
        }
        elements = bigger;
        front = 0;
        rear = size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("front → [");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[(front + i) % elements.length]);
        }
        return sb.append("] ← rear").toString();
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. MyLinkedList로 큐 구현하기 (addLast + removeFirst). 원형 배열의 복잡한 인덱스 계산이 왜 필요 없는지 체감.
    // TODO 2. Deque(양쪽에서 넣고 뺄 수 있는 큐) 만들기: offerFirst, offerLast, pollFirst, pollLast.
    //         원형 배열에서 front를 한 칸 "뒤로" 옮기려면 (front - 1 + capacity) % capacity 를 써야 한다. 왜?
    // TODO 3. 요세푸스 문제: n명이 원으로 앉아 k번째마다 제거. 큐로 풀어 보기 (poll해서 k-1번은 다시 offer).
}
