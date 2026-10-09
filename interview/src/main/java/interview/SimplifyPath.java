package interview;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * [모의 라이브 코딩 #4] 파일 경로 정리 (정규화)
 *
 * 문제
 *   유닉스 경로 문자열을 가장 짧은 정규 경로로. "." 은 제자리, ".." 은 바로 앞 폴더 취소.
 *   "/home/user/./docs/../photos" → "/home/user/photos"
 *
 *   정규화는 "잘못된 경로를 고치는 것"이 아니라 "같은 곳을 가리키는 가장 짧은 표현으로 줄이는 것".
 *   3 + 2 - 2 가 틀린 식이 아니라 3 으로 줄일 수 있는 식인 것과 같다.
 *
 * 엣지 케이스 (질문해서 정한 것)
 *   - 빈 문자열, "/"           → "/"
 *   - 루트에서 ".."  "/../a"   → 무시, "/a"   ← 이 문제의 핵심 함정. 웹 서버라면 path traversal 방어 지점
 *   - 연속 슬래시   "/a//b/"   → "/a/b"
 *   - 같은 이름 폴더 "/a/a"    → 정상. 위치로만 다룬다
 *   - 대소문자 구분, 변환 없음
 *
 * 핵심 자료구조: Stack
 *   ".." = "가장 최근에 들어간 폴더를 취소" = 가장 최근에 넣은 것을 꺼낸다 = LIFO = Stack.
 *   (Queue는 가장 먼저 넣은 것을 꺼내는 FIFO — 정반대. 면접에서 헷갈린 지점)
 *   자바에서 Stack은 java.util.Stack 대신 ArrayDeque 를 쓴다 (더 빠르고, Stack 클래스는 오래된 설계).
 *
 * 단골 함정: 빈 Stack에서 pop
 *   ArrayDeque.pop() → NoSuchElementException,  java.util.Stack.pop() → EmptyStackException.
 *   둘 다 NPE가 아니고, 둘 다 프로그램이 죽는다. if (!stack.isEmpty()) 로 막는다.
 *
 * 시간 복잡도 O(n) — split 한 번 + 조각마다 O(1) push/pop + 이어 붙이기 한 번. "O(2n)" 이라고 세어도 상수는 떼고 O(n).
 */
public class SimplifyPath {

    private SimplifyPath() {
    }

    public static String simplifyPath(String path) {
        Deque<String> stack = new ArrayDeque<>();

        for (String part : path.split("/")) {              // "/a//b/" → ["", "a", "", "b"]
            if (part.isEmpty() || part.equals(".")) {
                continue;                                  // 빈 조각(연속 슬래시, 앞뒤 슬래시) 과 "." 은 무시
            }
            if (part.equals("..")) {
                if (!stack.isEmpty()) stack.pop();         // 루트에서 .. 은 무시
            } else {
                stack.push(part);                          // 폴더 이름
            }
        }

        StringBuilder sb = new StringBuilder();
        Iterator<String> it = stack.descendingIterator();  // push 순서(아래→위)로. 그냥 iterator()는 위→아래라 거꾸로 나온다
        while (it.hasNext()) sb.append('/').append(it.next());

        return sb.length() == 0 ? "/" : sb.toString();     // 아무것도 안 남으면 루트
    }

    // ───────────────────────── 직접 해볼 과제 ─────────────────────────
    // TODO 1. 웹 서버 버전: 루트 위로 올라가는 ".." 을 무시하지 말고 예외를 던지도록 바꾸기 (path traversal 거부).
    //         그리고 "/static" 아래로만 허용하는 isUnder(base, path) 만들기 — 정규화 후 startsWith 로.
    // TODO 2. 상대 경로 지원: 앞에 "/" 가 없으면 ("a/../b") 결과도 "/" 로 시작하지 않아야 한다. 어디를 고치나?
    // TODO 3. Windows 경로 ("C:\\a\\..\\b") 로 확장해 보기. 구분자와 드라이브 문자 처리가 추가된다.
    // TODO 4. descendingIterator 대신 String.join("/", stack) 을 쓰면 왜 순서가 거꾸로 나오는지 테스트로 확인.
}
