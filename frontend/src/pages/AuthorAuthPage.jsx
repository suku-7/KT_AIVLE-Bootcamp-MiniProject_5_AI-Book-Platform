// =================================================================
// FILENAME: src/pages/AuthorAuthPage.jsx
// 역할: 작가 회원가입/로그인 페이지입니다. AuthForm 컴포넌트를 사용합니다.
// =================================================================
import React from 'react';
import { AuthForm } from '../components/AuthForm';

export const AuthorAuthPage = () => {
    return (
        <div>
            <AuthForm userType="author" />
        </div>
    );
};