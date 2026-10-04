package com.example.board.post;

import com.example.board.post.dto.PostRequest;
import com.example.board.post.dto.PostResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 비즈니스 로직 계층. 컨트롤러(HTTP)와 리포지토리(DB) 사이에서 "무엇을 할지"를 결정한다.
 * 지금은 단순하지만, 권한 검사·알림 발송 같은 로직이 생기면 여기에 들어간다.
 */
@Service
@Transactional(readOnly = true) // 기본은 읽기 전용 트랜잭션, 쓰기 메서드만 따로 덮어쓴다
public class PostService {

    private final PostRepository postRepository;

    /** 생성자 주입: Spring이 PostRepository 구현체를 자동으로 넣어 준다 */
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostResponse> findAll() {
        return postRepository.findAllByOrderByIdDesc().stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostResponse findById(Long id) {
        return PostResponse.from(getPostOrThrow(id));
    }

    @Transactional
    public PostResponse create(PostRequest request) {
        Post post = new Post(request.title(), request.author(), request.content());
        return PostResponse.from(postRepository.save(post));
    }

    @Transactional
    public PostResponse update(Long id, PostRequest request) {
        Post post = getPostOrThrow(id);
        // 트랜잭션 안에서 엔티티를 바꾸면 JPA가 커밋 시점에 UPDATE 쿼리를 자동으로 날린다 (변경 감지, dirty checking)
        post.update(request.title(), request.content());
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long id) {
        Post post = getPostOrThrow(id);
        postRepository.delete(post);
    }

    private Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }
}
