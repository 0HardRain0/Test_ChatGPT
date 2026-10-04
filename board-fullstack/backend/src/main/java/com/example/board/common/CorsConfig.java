package com.example.board.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 설정.
 * 브라우저는 보안상 다른 출처(포트가 달라도 다른 출처)로의 요청을 막는다.
 * React 개발 서버(5173)에서 API 서버(8080)를 호출하려면 서버가 "허용한다"고 알려줘야 한다.
 * (vite.config.js의 proxy를 쓰면 이 설정 없이도 되지만, 개념 학습을 위해 둘 다 넣어 둔다.)
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
