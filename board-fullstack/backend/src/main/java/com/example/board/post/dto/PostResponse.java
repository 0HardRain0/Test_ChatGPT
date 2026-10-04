package com.example.board.post.dto;

import com.example.board.post.Post;

import java.time.LocalDateTime;

/**
 * 클라이언트에 돌려주는 응답 DTO. 엔티티를 그대로 노출하지 않고 필요한 필드만 담는다.
 */
public record PostResponse(
        Long id,
        String title,
        String author,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getAuthor(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
