package com.example.board.post;

import com.example.board.post.dto.PostRequest;
import com.example.board.post.dto.PostResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * HTTP 요청을 받는 계층. URL + HTTP 메서드를 자바 메서드에 연결한다.
 *
 *  GET    /api/posts        목록
 *  GET    /api/posts/{id}   상세
 *  POST   /api/posts        작성  (201 Created)
 *  PUT    /api/posts/{id}   수정
 *  DELETE /api/posts/{id}   삭제  (204 No Content)
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostResponse> list() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public PostResponse detail(@PathVariable Long id) {
        return postService.findById(id);
    }

    /** @Valid: PostRequest의 @NotBlank 등을 검사하고, 실패하면 400으로 응답 (GlobalExceptionHandler 참고) */
    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {
        PostResponse created = postService.create(request);
        // REST 관례: 생성 성공 시 201 + Location 헤더에 새 리소스 주소
        return ResponseEntity
                .created(URI.create("/api/posts/" + created.id()))
                .body(created);
    }

    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        return postService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
