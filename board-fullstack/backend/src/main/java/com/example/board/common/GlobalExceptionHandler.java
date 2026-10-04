package com.example.board.common;

import com.example.board.post.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 모든 컨트롤러에서 발생하는 예외를 한 곳에서 JSON 응답으로 바꿔 준다.
 * 컨트롤러마다 try-catch를 쓰지 않아도 되는 이유가 이 클래스다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 없는 글 조회 → 404 */
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(PostNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("status", 404, "message", e.getMessage()));
    }

    /** @Valid 검증 실패 → 400, 어떤 필드가 왜 틀렸는지 함께 반환 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(Map.of("status", 400, "message", "입력값이 올바르지 않습니다.", "errors", errors));
    }
}
