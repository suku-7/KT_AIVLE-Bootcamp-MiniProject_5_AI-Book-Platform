// =================================================================
// FILENAME: src/components/AuthForm.jsx (수정)
// 역할: UI를 세련된 카드 디자인으로 전면 개편합니다.
// =================================================================
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';

// 1. Material-UI 컴포넌트를 추가로 불러옵니다.
import { Box, Button, TextField, Typography, Paper } from '@mui/material';

export const AuthForm = ({ userType }) => {
    // KT 인증값을 받기 위해 isKt 필드를 state에 다시 추가합니다.
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({ loginId: '', password: '', name: '', isKt: 'false', portfolioUrl: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        
        try {
            if (isLogin) {
                const loginData = { loginId: formData.loginId, password: formData.password };
                const response = userType === 'user' 
                    ? await api.loginUser(loginData) 
                    : await api.loginAuthor(loginData);
                alert('로그인 성공!');
                login(response.data, userType);
                userType ==='user' ? navigate('/main') : navigate('/write/my');
                // navigate('/main')
            } else {
                // 회원가입 시 isKt 값을 포함하도록 수정합니다.
                const signupData = userType === 'user'
                    ? { loginId: formData.loginId, loginPassword: formData.password, name: formData.name, isKt: formData.isKt }
                    : { loginId: formData.loginId, password: formData.password, name: formData.name, portfolioUrl: formData.portfolioUrl };

                if (userType === 'user') {
                    await api.registerUser(signupData);
                } else {
                    await api.joinAuthor(signupData);
                }
                alert('회원가입 성공! 로그인 해주세요.');
                setIsLogin(true);
            }
        } catch (err) {
            console.error(err);
            const message = err.response?.data?.message || err.response?.data || '오류가 발생했습니다. 다시 시도해주세요.';
            setError(message);
        }
    };

    const formTitle = `${userType === 'user' ? '사용자' : '작가'} ${isLogin ? '로그인' : '회원가입'}`;

    return (
        <Paper 
            elevation={4} 
            sx={{ 
                padding: { xs: '2rem', md: '3rem' },
                width: '100%', 
                maxWidth: '420px',
                margin: '4rem auto',
                borderRadius: '12px',
            }}
        >
            <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column' }}>
                <Typography variant="h5" component="h1" fontWeight="bold" sx={{ mb: 1 }}>
                    {isLogin ? '독서와 무제한 친해지리' : formTitle}
                </Typography>
                {isLogin && (
                    <Typography variant="body2" color="text.secondary" sx={{ mb: 4 }}>
                        20만 권 속에서 인생책을 찾아보세요
                    </Typography>
                )}

                <TextField
                    label="아이디"
                    name="loginId"
                    value={formData.loginId}
                    onChange={handleChange}
                    required
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="비밀번호"
                    name="password"
                    type="password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                    fullWidth
                    margin="normal"
                />

                {!isLogin && (
                    <>
                        <TextField label="이름" name="name" value={formData.name} onChange={handleChange} required fullWidth margin="normal" />
                        {/* 사용자 회원가입 시에만 KT 고객 여부 필드를 표시합니다. */}
                        {userType === 'user' && (
                            <TextField 
                                label="KT 고객 여부 (true/false)" 
                                name="isKt" 
                                value={formData.isKt} 
                                onChange={handleChange} 
                                required 
                                fullWidth 
                                margin="normal" 
                            />
                        )}
                        {userType === 'author' && <TextField label="포트폴리오 URL" name="portfolioUrl" value={formData.portfolioUrl} onChange={handleChange} fullWidth margin="normal" />}
                    </>
                )}

                {error && <Typography color="error" sx={{ mt: 2, textAlign: 'center' }}>{error}</Typography>}

                <Button 
                    type="submit" 
                    variant="contained" 
                    fullWidth
                    sx={{
                        mt: 2,
                        mb: 2,
                        padding: '12px',
                        fontSize: '1rem',
                        fontWeight: 'bold',
                        backgroundColor: '#FFEB3B',
                        color: '#333',
                        '&:hover': {
                            backgroundColor: '#FBC02D',
                        }
                    }}
                >
                    {isLogin ? '로그인' : '회원가입'}
                </Button>

                <Button 
                    variant="text" 
                    onClick={() => setIsLogin(!isLogin)}
                    sx={{ alignSelf: 'center' }}
                >
                    {isLogin ? '계정이 없으신가요? 회원가입' : '이미 계정이 있으신가요? 로그인'}
                </Button>
            </Box>
        </Paper>
    );
};
