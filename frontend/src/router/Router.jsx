// src/router/Router.jsx

import React from 'react';
import { Routes, Route } from 'react-router-dom';

// 페이지 컴포넌트들을 임포트합니다.
import HomePage from '../pages/HomePage';
import BookDetailPage from '../pages/BookDetailPage';
import UserAuthPage from '../pages/UserAuthPage';
import AuthorAuthPage from '../pages/AuthorAuthPage';
import WritePage from '../pages/WritePage';
import PublishPage from '../pages/PublishPage';
import AdminApprovalPage from '../pages/AdminApprovalPage';

const Router = () => {
  return (
    <Routes>
      {/* 메인 화면 */}
      <Route path="/" element={<HomePage />} />
      <Route path="/home" element={<HomePage />} /> {/* /home 경로도 HomePage로 연결 */}

      {/* 도서 상세 보기 화면 */}
      <Route path="/books/:bookId" element={<BookDetailPage />} />

      {/* 사용자 회원가입/로그인 화면 */}
      <Route path="/user-auth" element={<UserAuthPage />} />

      {/* 작가 회원가입/로그인 화면 */}
      <Route path="/author-auth" element={<AuthorAuthPage />} />

      {/* 작가 글 쓰고 저장하는 화면 */}
      <Route path="/write" element={<WritePage />} />
      <Route path="/write/:bookId" element={<WritePage />} /> {/* 기존 글 수정 시 */}

      {/* 작가 출간 신청시 AI 이미지와 요약 표시하는 화면 */}
      <Route path="/publish/:bookId" element={<PublishPage />} />

      {/* 관리자 작가 승인 거절 화면 */}
      <Route path="/admin/approvals" element={<AdminApprovalPage />} />

      {/* 404 Not Found 페이지 (일치하는 경로가 없을 경우) */}
      <Route path="*" element={<div>404 Not Found</div>} />
    </Routes>
  );
};

export default Router;
