// =================================================================
// FILENAME: src/components/AuthForm.jsx (수정)
// 역할: 사용자 회원가입 시, KT 인증 부분을 스위치 컴포넌트로 변경합니다.
// =================================================================
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';

// 1. Switch 컴포넌트를 추가로 import 합니다.
import { Box, Button, TextField, Typography, Paper, Switch } from '@mui/material';

export const AuthForm = ({ userType }) => {
    const [isLogin, setIsLogin] = useState(true);
    // 2. isKt의 초기값을 boolean 타입인 false로 변경합니다.
    const [formData, setFormData] = useState({ loginId: '', password: '', name: '', isKt: false, portfolioUrl: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        // 3. 이벤트 타겟의 타입이 'checkbox'(스위치)인지 확인하여 값을 처리합니다.
        if (type === 'checkbox') {
            setFormData(prev => ({ ...prev, [name]: checked }));
        } else {
            setFormData(prev => ({ ...prev, [name]: value }));
        }
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
                navigate('/main');
            } else {
                // 4. API로 보내기 전, isKt 값을 다시 문자열("true" 또는 "false")로 변환합니다.
                const signupData = userType === 'user'
                    ? { loginId: formData.loginId, loginPassword: formData.password, name: formData.name, isKt: String(formData.isKt) }
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

    const userTypeName = userType === 'user' ? '사용자' : '작가';
    const formTitle = `${userTypeName} ${isLogin ? '로그인' : '회원가입'}`;
    const buttonText = isLogin ? `${userTypeName} 로그인` : `${userTypeName} 회원가입`;

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
                        AI IN 서재안에서 인생책을 찾아보세요
                    </Typography>
                )}

                <TextField label="아이디" name="loginId" value={formData.loginId} onChange={handleChange} required fullWidth margin="normal" />
                <TextField label="비밀번호" name="password" type="password" value={formData.password} onChange={handleChange} required fullWidth margin="normal" />

                {!isLogin && (
                    <>
                        <TextField label="이름" name="name" value={formData.name} onChange={handleChange} required fullWidth margin="normal" />
                        
                        {/* 5. 기존 TextField를 Switch 컴포넌트로 교체하고, 레이아웃을 수정합니다. */}
                        {userType === 'user' && (
                            <Box sx={{ display: 'flex', alignItems: 'center', mt: 2, mb: 1 }}>
                                <Typography sx={{ mr: 2 }}>KT 고객 인증</Typography>
                                <Switch
                                    checked={formData.isKt}
                                    onChange={handleChange}
                                    name="isKt"
                                    sx={{
                                        '& .MuiSwitch-switchBase.Mui-checked': {
                                            color: '#FFD600',
                                        },
                                        '& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track': {
                                            backgroundColor: '#FFEB60',
                                        },
                                    }}
                                />
                            </Box>
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
                        backgroundColor: '#FFF7BF',
                        color: 'grey.700',
                        boxShadow: 'none',
                        '&:hover': {
                            backgroundColor: '#FFEB60',
                        }
                    }}
                >
                    {buttonText}
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
