// Node 내장 테스트 러너 (node --test). 별도 라이브러리 없음.
import { test } from 'node:test';
import assert from 'node:assert/strict';
import { filterPosts } from './search.js';

const posts = [
  { id: 3, title: '공지', author: 'admin' },
  { id: 2, title: '질문 있어요', author: 'kim' },
  { id: 1, title: '공지', author: 'kim' },
];

const ids = (list) => list.map((p) => p.id);

test('빈 검색어면 전부 보여준다', () => {
  assert.deepEqual(filterPosts(posts, ''), posts);
});

test('제목 완전 일치', () => {
  assert.deepEqual(ids(filterPosts(posts, '공지')), [3, 1]);
});

test('작성자 완전 일치', () => {
  assert.deepEqual(ids(filterPosts(posts, 'kim')), [2, 1]);
});

test('부분 일치는 안 된다', () => {
  assert.deepEqual(filterPosts(posts, '공'), []);
  assert.deepEqual(filterPosts(posts, '질문'), []);
});

test('대소문자가 다르면 안 된다', () => {
  assert.deepEqual(filterPosts(posts, 'Kim'), []);
});

test('앞뒤 공백이 다르면 안 된다', () => {
  assert.deepEqual(filterPosts(posts, ' kim'), []);
  assert.deepEqual(filterPosts(posts, 'kim '), []);
});

test('해당 글이 없으면 빈 배열', () => {
  assert.deepEqual(filterPosts(posts, '없는값'), []);
});

test('서버가 준 순서(id 내림차순)를 유지한다', () => {
  const result = filterPosts(posts, 'kim');
  assert.ok(result[0].id > result[1].id);
});

test('글이 하나도 없어도 동작한다', () => {
  assert.deepEqual(filterPosts([], 'kim'), []);
  assert.deepEqual(filterPosts([], ''), []);
});
