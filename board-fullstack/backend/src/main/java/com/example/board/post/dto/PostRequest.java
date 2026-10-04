package com.example.board.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 글 작성/수정 요청 본문(JSON)을 받는 DTO.
 * 엔티티를 직접 받지 않는 이유: 클라이언트가 id, createdAt 같은 값을 멋대로 보내지 못하게 하기 위해.
 * record는 불변 데이터 묶음을 만드는 자바 16+ 문법.
 */
public record PostRequest(
        @NotBlank(message = "제목을 입력해 주세요.")
        @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
        String title,

        @NotBlank(message = "작성자를 입력해 주세요.")
        @Size(max = 30, message = "작성자는 30자 이하여야 합니다.")
        String author,

        @NotBlank(message = "내용을 입력해 주세요.")
        String content
) {
}
