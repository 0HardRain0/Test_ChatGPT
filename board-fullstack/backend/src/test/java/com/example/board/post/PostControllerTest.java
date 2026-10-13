package com.example.board.post;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 실제 서버를 띄우지 않고 MockMvc로 HTTP 요청을 흉내 내어 API 전체 흐름을 검증한다.
 * (컨트롤러 → 서비스 → JPA → H2 까지 실제로 동작한다)
 */
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 글_작성_조회_수정_삭제_전체_흐름() throws Exception {
        // 1. 작성 → 201 Created
        MvcResult created = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "첫 글", "author": "홍길동", "content": "안녕하세요"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("첫 글"))
                .andReturn();

        String location = created.getResponse().getHeader("Location");

        // 2. 상세 조회
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("홍길동"));

        // 3. 목록에 1건
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 4. 수정
        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "수정된 글", "author": "홍길동", "content": "내용 수정"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 글"));

        // 5. 삭제 → 204, 다시 조회하면 404
        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void 목록은_검색_파라미터를_무시하고_전체를_id_내림차순으로_준다() throws Exception {
        // 결정 사항: 검색은 GET /api/posts, 파라미터 없음 → 검색어가 쿼리스트링으로 와도 서버는 걸러내지 않는다 (필터는 프론트)
        int before = JsonPath.read(
                mockMvc.perform(get("/api/posts")).andReturn().getResponse().getContentAsString(), "$.length()");

        String first = create("첫 글", "홍길동");
        String second = create("둘째 글", "김철수");

        mockMvc.perform(get("/api/posts").param("q", "홍길동"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(before + 2)))       // 파라미터로 걸러지지 않음
                .andExpect(jsonPath("$[0].id").value(idOf(second)))   // 최신(id 큰) 글이 먼저
                .andExpect(jsonPath("$[1].id").value(idOf(first)));

        mockMvc.perform(delete(first)).andExpect(status().isNoContent());
        mockMvc.perform(delete(second)).andExpect(status().isNoContent());
    }

    private String create(String title, String author) throws Exception {
        return mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"" + title + "\", \"author\": \"" + author + "\", \"content\": \"내용\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    private static int idOf(String location) {
        return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    void 제목이_비어있으면_400() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "", "author": "홍길동", "content": "내용"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("제목을 입력해 주세요."));
    }
}
