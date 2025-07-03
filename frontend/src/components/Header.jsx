// =================================================================
// FILENAME: src/components/Header.jsx (수정)
// 역할: 로그인 시 '회원정보 수정'과 '로그아웃' 아이콘 버튼을 추가합니다.
// =================================================================
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

// 1. IconButton, Tooltip 및 아이콘들을 추가로 import 합니다.
import { AppBar, Toolbar, Box, Button, Typography, IconButton, Tooltip } from '@mui/material';
import ManageAccountsIcon from '@mui/icons-material/ManageAccounts'; // 회원정보 수정 아이콘
import LogoutIcon from '@mui/icons-material/Logout'; // 로그아웃 아이콘
import logoImage from '../assets/ai-in-logo.png';

export const Header = () => {
    const { auth, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const commonButtonStyle = {
        backgroundColor: '#FFF7BF',
        color: '#5E35B1',
        fontWeight: 'bold',
        borderRadius: '20px',
        padding: '6px 16px',
        boxShadow: '0 2px 5px rgba(0,0,0,0.15)',
        '&:hover': {
            backgroundColor: '#F9A825',
        }
    };
    
    return (
        <AppBar position="static" sx={{ backgroundColor: '#FFEB60', boxShadow: '0 2px 4px -1px rgba(0,0,0,0.2)' }}>
            <Toolbar sx={{ justifyContent: 'space-between' }}>
                
                <Link to={auth.user ? "/main" : "/"} style={{ display: 'flex', alignItems: 'center' }}>
                    <Box component="img" src={logoImage} alt="AI IN 서재 로고" sx={{ height: '70px', mr: 2 }} />
                </Link>

                <Box sx={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                    {auth.user ? (
                        <>
                            <Typography sx={{color: '#555'}}>환영합니다, {auth.user.name}님!</Typography>
                            {auth.role === 'user' && <Button component={Link} to="/my-library" variant="contained" sx={commonButtonStyle}>내 서재</Button>}
                            {auth.role === 'author' && <Button component={Link} to="/write/my" variant="contained" sx={commonButtonStyle}>작가 서재</Button>}
                            {auth.role === 'admin' && <Button component={Link} to="/admin/approvals" variant="contained" sx={commonButtonStyle}>관리자</Button>}
                            
                            {/* 2. 회원정보 수정 아이콘 버튼을 추가합니다. */}
                            <Tooltip title="회원정보 수정">
                                <IconButton onClick={() => navigate('/profile/edit')} color="primary">
                                    <ManageAccountsIcon />
                                </IconButton>
                            </Tooltip>

                            {/* 3. 로그아웃 버튼을 아이콘 버튼으로 변경합니다. */}
                            <Tooltip title="로그아웃">
                                <IconButton onClick={handleLogout} color="primary">
                                    <LogoutIcon />
                                </IconButton>
                            </Tooltip>
                        </>
                    ) : (
                        <>
                            <Button component={Link} to="/user-auth" variant="contained" sx={commonButtonStyle}>사용자</Button>
                            <Button component={Link} to="/author-auth" variant="contained" sx={commonButtonStyle}>작가</Button>
                        </>
                    )}
                </Box>
            </Toolbar>
        </AppBar>
    );
};
