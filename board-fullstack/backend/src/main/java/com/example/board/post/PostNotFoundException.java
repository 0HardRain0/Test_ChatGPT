package com.example.board.post;

/** 존재하지 않는 글 id로 조회했을 때 던지는 예외. GlobalExceptionHandler가 404로 바꿔 준다. */
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(Long id) {
        super("게시글을 찾을 수 없습니다. id=" + id);
    }
}
