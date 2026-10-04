import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { postApi } from '../api.js';

/**
 * 작성과 수정을 한 컴포넌트로 처리한다.
 * URL에 :id가 있으면 수정 모드 → 기존 글을 불러와 폼을 채운다.
 */
export default function PostForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();

  // 폼 필드 3개를 객체 하나로 관리 (제어 컴포넌트 패턴)
  const [form, setForm] = useState({ title: '', author: '', content: '' });
  const [fieldErrors, setFieldErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!isEdit) return;
    postApi.get(id).then((post) =>
      setForm({ title: post.title, author: post.author, content: post.content })
    );
  }, [id, isEdit]);

  // input의 name 속성으로 어떤 필드인지 구분해서 하나의 핸들러로 처리
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); // 브라우저 기본 동작(페이지 새로고침) 막기
    setSubmitting(true);
    setFieldErrors({});
    try {
      const saved = isEdit ? await postApi.update(id, form) : await postApi.create(form);
      navigate(`/posts/${saved.id}`);
    } catch (err) {
      // 서버 @Valid 검증 실패 → 각 필드 아래에 메시지 표시
      if (err.fieldErrors) setFieldErrors(err.fieldErrors);
      else alert(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="post-form" onSubmit={handleSubmit}>
      <h1>{isEdit ? '글 수정' : '글쓰기'}</h1>

      <label>
        제목
        <input name="title" value={form.title} onChange={handleChange} maxLength={100} />
        {fieldErrors.title && <span className="field-error">{fieldErrors.title}</span>}
      </label>

      <label>
        작성자
        {/* 수정 시에는 작성자를 바꿀 수 없게 한다 (서버의 Post.update도 author는 안 바꿈) */}
        <input name="author" value={form.author} onChange={handleChange} maxLength={30} disabled={isEdit} />
        {fieldErrors.author && <span className="field-error">{fieldErrors.author}</span>}
      </label>

      <label>
        내용
        <textarea name="content" value={form.content} onChange={handleChange} rows={10} />
        {fieldErrors.content && <span className="field-error">{fieldErrors.content}</span>}
      </label>

      <div className="actions">
        <button type="button" className="btn" onClick={() => navigate(-1)}>취소</button>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? '저장 중...' : isEdit ? '수정 완료' : '등록'}
        </button>
      </div>
    </form>
  );
}
