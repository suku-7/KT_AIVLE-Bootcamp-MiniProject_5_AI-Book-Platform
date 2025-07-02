// =================================================================
// FILENAME: src/pages/UserAuthPage.jsx
// 역할: 사용자 회원가입/로그인 페이지입니다. AuthForm 컴포넌트를 사용합니다.
// =================================================================
import React from 'react';
import { AuthForm } from '../components/AuthForm';

export const UserAuthPage = () => {
    return (
        <div>
            <AuthForm userType="user" />
        </div>
    );
};