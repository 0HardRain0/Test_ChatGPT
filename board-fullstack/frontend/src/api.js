/**
 * 백엔드 API 호출을 한 파일에 모아 둔다.
 * 컴포넌트에서 fetch를 직접 쓰지 않으면, URL이 바뀌거나 인증 헤더가 생겨도 여기만 고치면 된다.
 */
const BASE_URL = '/api/posts'; // vite.config.js의 proxy가 8080으로 넘겨 준다

async function request(url, options = {}) {
  const response = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });

  // 204 No Content(삭제 성공)는 본문이 없으므로 json()을 호출하면 안 된다
  if (response.status === 204) return null;

  const data = await response.json();

  // 서버가 400/404를 돌려주면 GlobalExceptionHandler가 만든 { status, message, errors } 형태
  if (!response.ok) {
    const error = new Error(data.message || '요청에 실패했습니다.');
    error.fieldErrors = data.errors; // 검증 실패 시 필드별 메시지
    throw error;
  }

  return data;
}

export const postApi = {
  list: () => request(BASE_URL),
  get: (id) => request(`${BASE_URL}/${id}`),
  create: (post) => request(BASE_URL, { method: 'POST', body: JSON.stringify(post) }),
  update: (id, post) => request(`${BASE_URL}/${id}`, { method: 'PUT', body: JSON.stringify(post) }),
  remove: (id) => request(`${BASE_URL}/${id}`, { method: 'DELETE' }),
};

/** "2026-10-04T12:34:56" → "2026. 10. 4. 오후 12:34" */
export function formatDate(isoString) {
  return new Date(isoString).toLocaleString('ko-KR', {
    year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit',
  });
}
