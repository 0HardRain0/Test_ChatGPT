import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { formatDate, postApi } from '../api.js';
import { filterPosts } from '../search.js';

export default function PostList() {
  const [posts, setPosts] = useState([]);
  const [query, setQuery] = useState('');   // 검색창 입력값
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 컴포넌트가 처음 화면에 나타날 때 한 번만 목록을 불러온다 (의존성 배열 [])
  useEffect(() => {
    postApi
      .list()
      .then(setPosts)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="status">불러오는 중...</p>;
  if (error) return <p className="status error">오류: {error} (백엔드 서버가 켜져 있나요?)</p>;
  if (posts.length === 0) return <p className="status">아직 글이 없습니다. 첫 글을 작성해 보세요!</p>;

  // 서버는 전체 목록을 주고, 검색은 브라우저에서 거른다 (API에 파라미터 없음)
  const visible = filterPosts(posts, query);

  return (
    <>
      <div className="search-box">
        <input
          type="search"
          placeholder="제목 또는 작성자로 검색 (완전 일치)"
          value={query}
          onChange={(e) => setQuery(e.target.value)}   // 입력할 때마다 바로 걸러진다
          aria-label="검색"
        />
      </div>

      {visible.length === 0 ? (
        <p className="status">'{query}'에 해당하는 글이 없습니다.</p>
      ) : (
        <table className="post-table">
          <thead>
            <tr>
              <th className="col-id">번호</th>
              <th>제목</th>
              <th className="col-author">작성자</th>
              <th className="col-date">작성일</th>
            </tr>
          </thead>
          <tbody>
            {/* 배열을 화면 요소로 바꿀 때는 map + 고유한 key */}
            {visible.map((post) => (
              <tr key={post.id}>
                <td className="col-id">{post.id}</td>
                <td>
                  <Link to={`/posts/${post.id}`}>{post.title}</Link>
                </td>
                <td className="col-author">{post.author}</td>
                <td className="col-date">{formatDate(post.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  );
}
