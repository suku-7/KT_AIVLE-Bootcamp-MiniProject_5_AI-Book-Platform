// =================================================================
// FILENAME: src/components/Header.jsx (수정)
// 역할: 텍스트 로고를 이미지로 교체하고, 전체 UI를 Material-UI로 개선합니다.
// =================================================================
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

// 1. Material-UI 컴포넌트와 로고 이미지를 불러옵니다.
import { AppBar, Toolbar, Box, Button, Typography } from '@mui/material';
import logoImage from '../assets/ai-in-logo.png'; // assets 폴더에 저장한 로고 이미지

export const Header = () => {
    const { auth, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        // 2. 기존 header 태그 대신 AppBar 컴포넌트를 사용합니다.
        <AppBar position="static" sx={{ backgroundColor: '#2c3e50' }}>
            <Toolbar sx={{ justifyContent: 'space-between' }}>
                
                {/* 3. 로고: 이미지를 Link로 감싸서 클릭 가능한 버튼으로 만듭니다. */}
                <Link to={auth.user ? "/main" : "/"} style={{ display: 'flex', alignItems: 'center' }}>
                    <Box
                        component="img"
                        src={logoImage}
                        alt="AI IN 서재 로고"
                        sx={{ height: '70px', // 로고 이미지 크기 조절
                              mr: 2 // 로고와 텍스트 사이 여백
                        }}
                    />
                </Link>

                {/* 4. 네비게이션: Box와 Button 컴포넌트로 재구성합니다. */}
                <Box sx={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                    {auth.role === 'user' && <Button component={Link} to="/my-library" sx={{ color: "white" }}>내 서재</Button>}
                    {auth.role === 'author' && <Button component={Link} to="/write" sx={{ color: "white" }}>글쓰기</Button>}
                    {auth.role === 'admin' && <Button component={Link} to="/admin/approvals" sx={{ color: "white" }}>관리자</Button>}
                    
                    {auth.user ? (
                        <>
                            <Typography sx={{color: '#bdc3c7'}}>환영합니다, {auth.user.name}님!</Typography>
                            <Button variant="outlined" color="inherit" onClick={handleLogout}>로그아웃</Button>
                        </>
                    ) : (
                        <>
                            <Button component={Link} to="/user-auth" sx={{ color: "white" }}>사용자</Button>
                            <Button component={Link} to="/author-auth" sx={{ color: "white" }}>작가</Button>
                        </>
                    )}
                </Box>
            </Toolbar>
        </AppBar>
    );
};
