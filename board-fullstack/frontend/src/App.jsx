import { Link, Route, Routes } from 'react-router-dom';
import PostList from './pages/PostList.jsx';
import PostDetail from './pages/PostDetail.jsx';
import PostForm from './pages/PostForm.jsx';

/**
 * URL에 따라 어떤 페이지를 보여줄지 정하는 곳.
 *
 *  /               목록
 *  /posts/new      작성
 *  /posts/:id      상세
 *  /posts/:id/edit 수정
 */
export default function App() {
  return (
    <div className="container">
      <header className="header">
        <Link to="/" className="logo">게시판</Link>
        <Link to="/posts/new" className="btn btn-primary">글쓰기</Link>
      </header>

      <main>
        <Routes>
          <Route path="/" element={<PostList />} />
          <Route path="/posts/new" element={<PostForm />} />
          <Route path="/posts/:id" element={<PostDetail />} />
          <Route path="/posts/:id/edit" element={<PostForm />} />
        </Routes>
      </main>
    </div>
  );
}
