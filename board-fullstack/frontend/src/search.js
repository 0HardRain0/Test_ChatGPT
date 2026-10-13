/**
 * 제목 또는 작성자가 검색어와 "완전 일치"하는 글만 남긴다.
 *
 *  - 빈 검색어("")면 전부 보여준다
 *  - 대소문자를 구분하고, 앞뒤 공백도 그대로 비교한다 ("완전 일치" 결정에 따름)
 *  - 원래 순서(서버가 준 id 내림차순)를 그대로 유지한다
 *
 * 서버 API(GET /api/posts)는 파라미터를 받지 않기로 했으므로 필터링은 여기(브라우저)에서 한다.
 * 글이 수천 건이 되면 서버 쪽 검색으로 옮겨야 한다 — 그때는 이 함수를 지우고 API에 파라미터를 추가.
 */
export function filterPosts(posts, query) {
  if (query === '') return posts;
  return posts.filter((post) => post.title === query || post.author === query);
}
