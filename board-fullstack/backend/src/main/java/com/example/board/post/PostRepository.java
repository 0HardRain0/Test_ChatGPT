package com.example.board.post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * DB 접근 계층. 인터페이스만 선언하면 Spring Data JPA가 구현체를 자동 생성한다.
 * JpaRepository 덕분에 save, findById, findAll, deleteById 등을 바로 쓸 수 있다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /** 메서드 이름 규칙만 지키면 SQL 없이 쿼리가 만들어진다: ORDER BY id DESC */
    List<Post> findAllByOrderByIdDesc();
}
