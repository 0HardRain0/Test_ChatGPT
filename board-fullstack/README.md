# 게시판 풀스택 (Java Spring Boot + React)

Java 백엔드와 React 프론트엔드가 REST API로 통신하는 구조를 공부하기 위한 최소 게시판.
글 목록 / 상세 / 작성 / 수정 / 삭제 (CRUD) 만 구현되어 있다.

```
board-fullstack/
├── backend/    Spring Boot 3, JPA, H2 인메모리 DB   → http://localhost:8080
└── frontend/   Vite + React 18 + react-router      → http://localhost:5173
```

## 실행 방법

터미널 2개가 필요하다. (Java 21, Node 18+ 설치 필요)

```bash
# 터미널 1 — 백엔드
cd board-fullstack/backend
./gradlew bootRun             # Windows: gradlew.bat bootRun

# 터미널 2 — 프론트엔드
cd board-fullstack/frontend
npm install
npm run dev
```

브라우저에서 http://localhost:5173 접속.

- API만 직접 확인: http://localhost:8080/api/posts
- DB 내용 확인: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:boarddb`, 사용자 `sa`, 비밀번호 없음)
- 백엔드 테스트: `cd backend && ./gradlew test`

## API

| 메서드 | URL | 설명 | 성공 응답 |
|---|---|---|---|
| GET | `/api/posts` | 목록 (최신순) | 200 |
| GET | `/api/posts/{id}` | 상세 | 200 / 404 |
| POST | `/api/posts` | 작성 | 201 + Location 헤더 |
| PUT | `/api/posts/{id}` | 수정 | 200 / 404 |
| DELETE | `/api/posts/{id}` | 삭제 | 204 / 404 |

요청 본문 (POST / PUT):

```json
{ "title": "제목", "author": "작성자", "content": "내용" }
```

검증 실패 시 400과 함께 필드별 메시지가 돌아온다:

```json
{ "status": 400, "message": "입력값이 올바르지 않습니다.", "errors": { "title": "제목을 입력해 주세요." } }
```

## 요청이 흘러가는 순서 (읽는 순서 추천)

```
[브라우저]  PostList.jsx  ──fetch('/api/posts')──▶  vite proxy  ──▶  :8080
                 │
[백엔드]   PostController ──▶ PostService ──▶ PostRepository ──▶ H2 DB
                 │               │
                 │               └─ PostNotFoundException → GlobalExceptionHandler → 404 JSON
                 └─ @Valid 실패 → GlobalExceptionHandler → 400 JSON
```

### backend (Java)

| 파일 | 역할 |
|---|---|
| `BoardApplication.java` | 진입점 |
| `post/Post.java` | 엔티티 — DB 테이블과 1:1 대응하는 클래스 |
| `post/PostRepository.java` | DB 접근 — 인터페이스만 선언하면 구현체 자동 생성 |
| `post/PostService.java` | 비즈니스 로직, 트랜잭션 |
| `post/PostController.java` | HTTP URL ↔ 자바 메서드 연결 |
| `post/dto/PostRequest.java` | 요청 JSON + 검증 규칙 |
| `post/dto/PostResponse.java` | 응답 JSON |
| `common/GlobalExceptionHandler.java` | 예외 → HTTP 상태코드 변환 |
| `common/CorsConfig.java` | 다른 포트(5173)에서 오는 요청 허용 |
| `test/.../PostControllerTest.java` | API 전체 흐름 자동 테스트 |

### frontend (React)

| 파일 | 역할 |
|---|---|
| `main.jsx` | 진입점, Router 설정 |
| `App.jsx` | URL → 페이지 매핑 |
| `api.js` | 백엔드 호출 함수 모음 |
| `pages/PostList.jsx` | 목록 — `useEffect`로 데이터 로딩, `map`으로 렌더링 |
| `pages/PostDetail.jsx` | 상세 — `useParams`, 삭제 후 `useNavigate` |
| `pages/PostForm.jsx` | 작성·수정 공용 폼 — 제어 컴포넌트, 서버 검증 메시지 표시 |

## 다음에 해볼 만한 확장

1. **댓글** — `Comment` 엔티티 추가, `@ManyToOne`으로 Post와 연결 (JPA 연관관계 학습)
2. **페이징** — `Pageable` 사용, 프론트에 페이지 번호 버튼
3. **검색** — 제목/내용 키워드 검색 (`findByTitleContaining`)
4. **DB 영속화** — H2 파일 모드 또는 MySQL로 교체 (`application.yml`만 수정)
5. **로그인** — Spring Security + JWT, 본인 글만 수정/삭제
