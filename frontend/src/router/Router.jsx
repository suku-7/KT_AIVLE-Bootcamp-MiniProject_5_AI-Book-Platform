// =================================================================
// FILENAME: src/router/Router.jsx (수정)
// 역할: 새로운 페이지 흐름에 맞게 라우팅 규칙을 전면 수정합니다.
// =================================================================
import React from 'react';
import { Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

// 페이지 컴포넌트 임포트
import { LandingPage } from '../pages/LandingPage';
import { MainPage } from '../pages/MainPage';
import { UserAuthPage } from '../pages/UserAuthPage';
import { AuthorAuthPage } from '../pages/AuthorAuthPage';
import { AdminApprovalPage } from '../pages/AdminApprovalPage';
import { WritePage } from '../pages/WritePage';
import { BookDetailPage } from '../pages/BookDetailPage';
import { MyLibraryPage } from '../pages/MyLibraryPage';
import { PublishPage } from '../pages/PublishPage';
import EditPage from "../pages/WritingEditPage.jsx";
import MyWritingsPage from "../pages/MyWritingsPage.jsx";
import MyWritingDetailPage from "../pages/MyWritingDetailPage.jsx";

const ProtectedRoute = ({ allowedRoles }) => {
    const { auth } = useAuth();
    if (!auth.role || !allowedRoles.includes(auth.role)) {
        return <Navigate to="/" replace />;
    }
    return <Outlet />;
};

export const Router = () => {
    return (
        <Routes>
            {/* --- 로그인 전 경로 --- */}
            <Route path="/" element={<LandingPage />} />
            <Route path="/user-auth" element={<UserAuthPage />} />
            <Route path="/author-auth" element={<AuthorAuthPage />} />
            
            {/* --- 로그인 후 공통 경로 (모든 로그인 사용자가 접근 가능) --- */}
            <Route element={<ProtectedRoute allowedRoles={['user', 'author', 'admin']} />}>
                <Route path="/main" element={<MainPage />} />
                <Route path="/book/:bookId" element={<BookDetailPage />} />
            </Route>

            {/* --- 사용자 전용 경로 --- */}
            <Route element={<ProtectedRoute allowedRoles={['user']} />}>
                <Route path="/my-library" element={<MyLibraryPage />} />
            </Route>

            {/* --- 작가 전용 경로 --- */}
            {/* <Route element={<ProtectedRoute allowedRoles={['author']} />}> */}
                <Route path="/write" element={<WritePage />} />
                <Route path="/write/my" element={<MyWritingsPage />} />
                <Route path="/edit/:bookId" element={<EditPage />} />
                <Route path="/publish/:bookId" element={<PublishPage />} />
                <Route path="/write/:bookId" element={<MyWritingDetailPage />} />
            {/* </Route> */}

            {/* --- 관리자 전용 경로 --- */}
            {/* <Route element={<ProtectedRoute allowedRoles={['admin']} />}> */}
                <Route path="/admin/approvals" element={<AdminApprovalPage />} />
            {/* </Route> */}
            
            <Route path="*" element={<Navigate to="/" />} />
        </Routes>
    );
};