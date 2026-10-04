package ds.stack;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * 스택 — LIFO (Last In, First Out). 마지막에 넣은 게 먼저 나온다.
 *
 * 접시 쌓기와 같다. 위에만 올리고(push), 위에서만 빼고(pop), 맨 위를 들여다볼(peek) 수 있다.
 *
 *          push(D) →  │ D │ ← pop() 하면 D가 나옴
 *                     │ C │
 *                     │ B │
 *                     │ A │
 *                     └───┘
 *
 * 구현: 배열 하나 + top 인덱스. 배열의 끝을 "위"로 쓰면 push/pop이 모두 O(1).
 *       (MyArrayList의 add/remove(size-1)과 완전히 같은 원리)
 *
 * 시간 복잡도: push, pop, peek 모두 O(1)
 *
 * 어디에 쓰이나
 *  - 괄호 짝 검사  "( [ { } ] )"           → 여는 괄호 push, 닫는 괄호 만나면 pop해서 짝 확인
 *  - 브라우저 뒤로가기, Ctrl+Z 실행 취소
 *  - 함수 호출 (콜 스택) — 재귀가 깊어지면 StackOverflowError가 나는 이유
 *  - 후위 표기법 계산, 수식 파싱
 *  - DFS(깊이 우선 탐색)의 반복문 버전
 */
public class MyStack<E> {

    private Object[] elements;
    private int size;   // = 다음에 push할 위치 = 현재 top의 인덱스 + 1

    public MyStack() {
        elements = new Object[10];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** 맨 위에 올린다. O(1) */
    public void push(E element) {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, size * 2);   // 꽉 차면 2배 확장
        }
        elements[size++] = element;
    }

    /** 맨 위 것을 빼서 돌려준다. O(1) */
    @SuppressWarnings("unchecked")
    public E pop() {
        if (isEmpty()) throw new EmptyStackException();
        E top = (E) elements[--size];
        elements[size] = null;   // GC가 수거할 수 있게
        return top;
    }

    /** 맨 위 것을 빼지 않고 보기만 한다. O(1) */
    @SuppressWarnings("unchecked")
    public E peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (E) elements[size - 1];
    }

    @Override
    public String toString() {
        // 위(top)가 오른쪽에 오도록 출력
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        return sb.append("] ← top").toString();
    }

    // ───────────────────────── 대표 활용 예제 ─────────────────────────

    /**
     * 괄호 짝 검사. 스택의 가장 유명한 활용.
     *   "([]{})" → true,  "([)]" → false,  "((" → false
     */
    public static boolean isBalanced(String s) {
        MyStack<Character> stack = new MyStack<>();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '(', '[', '{' -> stack.push(c);          // 여는 괄호는 쌓아 둔다
                case ')', ']', '}' -> {
                    if (stack.isEmpty()) return false;        // 닫는 괄호인데 짝이 없음
                    char open = stack.pop();
                    if (!matches(open, c)) return false;      // 종류가 다른 괄호끼리 만남
                }
                default -> { /* 괄호가 아니면 무시 */ }
            }
        }
        return stack.isEmpty();   // 끝났는데 여는 괄호가 남아 있으면 false
    }

    private static boolean matches(char open, char close) {
        return (open == '(' && close == ')')
                || (open == '[' && close == ']')
                || (open == '{' && close == '}');
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. MyLinkedList를 내부에 써서 스택을 다시 구현해 보기 (addFirst/removeFirst 사용). 배열 버전과 뭐가 다른가?
    // TODO 2. 후위 표기법 계산기: "3 4 + 2 *" → 14.  숫자는 push, 연산자를 만나면 두 번 pop해서 계산 후 push.
    // TODO 3. 두 개의 스택으로 큐를 만들어 보기 (유명한 면접 문제).
}
