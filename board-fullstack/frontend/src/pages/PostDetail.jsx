import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { formatDate, postApi } from '../api.js';

export default function PostDetail() {
  const { id } = useParams(); // URL의 :id 부분
  const navigate = useNavigate(); // 코드로 페이지 이동할 때 사용
  const [post, setPost] = useState(null);
  const [error, setError] = useState(null);

  // id가 바뀔 때마다(다른 글로 이동) 다시 불러온다
  useEffect(() => {
    postApi.get(id).then(setPost).catch((e) => setError(e.message));
  }, [id]);

  const handleDelete = async () => {
    if (!window.confirm('정말 삭제할까요?')) return;
    try {
      await postApi.remove(id);
      navigate('/'); // 삭제 후 목록으로
    } catch (e) {
      alert(e.message);
    }
  };

  if (error) return <p className="status error">{error}</p>;
  if (!post) return <p className="status">불러오는 중...</p>;

  return (
    <article className="post-detail">
      <h1>{post.title}</h1>
      <p className="meta">
        {post.author} · {formatDate(post.createdAt)}
        {post.updatedAt !== post.createdAt && ` (수정됨 ${formatDate(post.updatedAt)})`}
      </p>
      {/* white-space: pre-wrap 으로 줄바꿈을 그대로 보여준다 (index.css) */}
      <div className="content">{post.content}</div>

      <div className="actions">
        <Link to="/" className="btn">목록</Link>
        <Link to={`/posts/${id}/edit`} className="btn">수정</Link>
        <button type="button" onClick={handleDelete} className="btn btn-danger">삭제</button>
      </div>
    </article>
  );
}
