// =================================================================
// FILENAME: src/pages/LandingPage.jsx (수정)
// 역할: 초기화면의 버튼 색상을 요청에 맞게 수정합니다.
// =================================================================
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Button } from '@mui/material';

// src/assets 폴더에 넣은 배경 이미지를 불러옵니다.
import backgroundImage from '../assets/library-background.png'; 

export const LandingPage = () => {
    const navigate = useNavigate();
    const buttonTextColor = 'grey.700'; // 버튼에 사용할 공통 회색 텍스트

    return (
        <Box 
            sx={{
                display: 'flex',
                width: '100vw',
                height: 'calc(100vh - 128px)',
                position: 'relative',
                left: '50%',
                transform: 'translateX(-50%)',
            }}
        >
            {/* 왼쪽 영역: 배경 이미지 */}
            <Box
                sx={{
                    flex: { xs: 0, md: 4 },
                    backgroundImage: `url(${backgroundImage})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    display: { xs: 'none', md: 'block' }
                }}
            />

            {/* 오른쪽 영역: 로그인 및 회원가입 */}
            <Box
                sx={{
                    flex: { xs: 1, md: 1 },
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    padding: { xs: '2rem', md: '4rem' },
                    backgroundColor: 'white',
                }}
            >
                <Box sx={{ width: '100%', maxWidth: '380px' }}>
                    <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
                        AI IN 서재
                    </Typography>
                    <Typography variant="h6" color="text.secondary" sx={{ mb: 4 }}>
                        AI와 함께 만드는 당신의 이야기,
                        <br />
                        지금 시작해보세요.
                    </Typography>
                    
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        {/* ▼▼▼ 1. '사용자 로그인' 버튼 스타일 수정 ▼▼▼ */}
                        <Button 
                            fullWidth 
                            variant="contained" 
                            onClick={() => navigate('/user-auth')}
                            sx={{ 
                                padding: '12px', 
                                fontSize: '1rem',
                                backgroundColor: '#FFF7BF', // 요청하신 배경색
                                color: buttonTextColor,    // 회색 텍스트
                                boxShadow: 'none',
                                '&:hover': {
                                    backgroundColor: '#FFEB60', // 호버 시 약간 더 진한 색
                                }
                            }}
                        >
                            사용자 로그인 / 가입
                        </Button>
                        
                        {/* ▼▼▼ 2. '작가 로그인' 버튼 스타일 수정 ▼▼▼ */}
                        <Button 
                            fullWidth 
                            variant="outlined" 
                            onClick={() => navigate('/author-auth')}
                            sx={{ 
                                padding: '12px', 
                                fontSize: '1rem',
                                borderColor: '#FFF7BF', // 요청하신 테두리 색
                                color: buttonTextColor,  // 회색 텍스트
                                '&:hover': {
                                    borderColor: '#FFF1A8',
                                    backgroundColor: 'rgba(255, 241, 168, 0.1)' // 호버 시 약간의 배경색
                                }
                            }}
                        >
                            작가 로그인 / 가입
                        </Button>

                        {/* ▼▼▼ 3. '관리자 페이지' 버튼 스타일 수정 ▼▼▼ */}
                        <Button 
                            fullWidth 
                            variant="text" 
                            onClick={() => navigate('/admin/approvals')}
                            sx={{ color: buttonTextColor }} // 회색 텍스트
                        >
                            관리자 페이지로 이동
                        </Button>
                    </Box>
                </Box>
            </Box>
        </Box>
    );
};
