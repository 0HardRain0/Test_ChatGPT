package interview;

import org.junit.jupiter.api.Test;

import static interview.SimplifyPath.simplifyPath;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimplifyPathTest {

    @Test
    void 면접_예시() {
        assertEquals("/home/user/photos", simplifyPath("/home/user/./docs/../photos"));
        assertEquals("/a/d", simplifyPath("/a/b/c/../../d"));
    }

    @Test
    void 이미_정규_경로면_그대로() {
        assertEquals("/home/user", simplifyPath("/home/user"));
    }

    // ── 질문 ①: 없으면? ──

    @Test
    void 빈_문자열은_루트() {
        assertEquals("/", simplifyPath(""));
    }

    // ── 질문 ②: 하나면? ──

    @Test
    void 루트만_오면_루트() {
        assertEquals("/", simplifyPath("/"));
    }

    @Test
    void 폴더_하나() {
        assertEquals("/a", simplifyPath("/a"));
        assertEquals("/a", simplifyPath("/a/"));
    }

    // ── 질문 ③: 경계 ──

    @Test
    void 루트에서_상위로_가려_하면_무시() {
        // 이 문제의 핵심 함정. 빈 Stack에서 pop하면 NoSuchElementException
        assertEquals("/a", simplifyPath("/../a"));
        assertEquals("/", simplifyPath("/../../.."));
        assertEquals("/etc/passwd", simplifyPath("/static/../../../etc/passwd"));   // path traversal 시도 → 정규화 결과로 /static 밖임을 알 수 있다
    }

    @Test
    void 연속_슬래시와_끝_슬래시() {
        assertEquals("/a/b", simplifyPath("/a//b/"));
        assertEquals("/a/b", simplifyPath("///a///b///"));
    }

    @Test
    void 같은_이름_폴더가_반복돼도_위치로만_다룬다() {
        assertEquals("/a/a", simplifyPath("/a/a"));
        assertEquals("/a", simplifyPath("/a/a/.."));
    }

    @Test
    void 대소문자는_구분하고_변환하지_않는다() {
        assertEquals("/Docs/docs", simplifyPath("/Docs/docs"));
    }

    @Test
    void 점_하나는_무시_점_둘은_취소() {
        assertEquals("/a/b", simplifyPath("/a/./b/."));
        assertEquals("/a", simplifyPath("/a/b/.."));
        assertEquals("/", simplifyPath("/a/.."));
    }

    @Test
    void 점이_세_개_이상이면_그냥_폴더_이름() {
        // "..." 은 특수 의미가 없는 평범한 이름 — 이것도 물어볼 만한 ③ 경계
        assertEquals("/a/...", simplifyPath("/a/..."));
    }
}
