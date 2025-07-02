// =================================================================
// FILENAME: src/contexts/AuthContext.jsx
// 역할: 로그인 상태를 전역으로 관리합니다.
// 변경점: 페이지 새로고침 시에도 로그인 상태를 유지하기 위해 localStorage를 사용하고,
// 로그인/로그아웃 시 데이터를 확실하게 덮어쓰거나 삭제하도록 수정합니다.
// =================================================================
import React, { createContext, useState, useContext, useEffect } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [auth, setAuth] = useState({ 
        user: null, // 로그인한 사용자/작가/관리자 정보 객체
        role: null, // 'user', 'author', 'admin'
    });

    // 1. 앱이 처음 시작될 때, localStorage에서 저장된 로그인 정보를 불러옵니다.
    useEffect(() => {
        try {
            const storedAuth = localStorage.getItem('auth');
            if (storedAuth) {
                setAuth(JSON.parse(storedAuth));
            }
        } catch (error) {
            console.error("저장된 인증 정보를 불러오는 데 실패했습니다.", error);
            localStorage.removeItem('auth'); // 문제가 있으면 깨끗하게 지웁니다.
        }
    }, []);

    const login = (userData, role) => {
        const authData = { user: userData, role };
        setAuth(authData);
        // 2. 로그인 성공 시, 새로운 정보로 localStorage를 완전히 덮어씁니다.
        localStorage.setItem('auth', JSON.stringify(authData));
    };

    const logout = () => {
        setAuth({ user: null, role: null });
        // 3. 로그아웃 시, localStorage에서 정보를 확실하게 삭제합니다.
        localStorage.removeItem('auth');
    };
    
    // 4. 구독, 포인트 충전 등 사용자 정보가 변경되었을 때,
    //    localStorage의 정보도 함께 업데이트하는 함수입니다.
    const updateAuthUser = (updatedUserData) => {
        setAuth(prevAuth => {
            const newAuth = { ...prevAuth, user: updatedUserData };
            localStorage.setItem('auth', JSON.stringify(newAuth));
            return newAuth;
        });
    };

    return (
        <AuthContext.Provider value={{ auth, login, logout, updateAuthUser }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);