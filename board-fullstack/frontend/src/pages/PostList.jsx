import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { formatDate, postApi } from '../api.js';

export default function PostList() {
  const [posts, setPosts] = useState([]);
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

  return (
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
        {posts.map((post) => (
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
  );
}
