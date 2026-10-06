package interview;

import org.junit.jupiter.api.Test;

import static interview.MostActiveUser.mostActiveUser;
import static interview.MostActiveUser.mostActiveUserClassic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MostActiveUserTest {

    @Test
    void 면접_예시() {
        String[] logs = {
                "kim /posts/1",
                "lee /posts/3",
                "kim /posts/1",     // 중복 — 1번으로 센다
                "park /posts/2",
                "kim /posts/5",
                "lee /posts/3"      // 중복
        };
        assertEquals("kim", mostActiveUser(logs));            // kim 2, lee 1, park 1
        assertEquals("kim", mostActiveUserClassic(logs));     // 옛날 스타일도 같은 답
    }

    @Test
    void 중복만_많고_실제_페이지는_적은_사용자는_이기지_못한다() {
        String[] logs = {
                "kim /posts/1", "kim /posts/1", "kim /posts/1", "kim /posts/1",   // 4번 봤지만 1페이지
                "lee /posts/1", "lee /posts/2"                                     // 2페이지
        };
        assertEquals("lee", mostActiveUser(logs));
    }

    // ── 엣지 케이스: 면접에서 "먼저 물어봤어야 할" 것들 ──

    @Test
    void 빈_배열이면_null() {
        assertNull(mostActiveUser(new String[]{}));
    }

    @Test
    void 사용자_한_명() {
        assertEquals("solo", mostActiveUser(new String[]{"solo /a", "solo /b"}));
    }

    @Test
    void 동점이면_둘_중_하나() {
        String[] logs = {"kim /a", "lee /b"};
        String result = mostActiveUser(logs);
        assertTrue(result.equals("kim") || result.equals("lee"));
    }
}
