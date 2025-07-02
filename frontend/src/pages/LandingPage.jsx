// =================================================================
// FILENAME: src/pages/LandingPage.jsx (수정)
// 역할: 화면 너비에 따른 이미지와 콘텐츠 영역의 비율을 조정합니다.
// =================================================================
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Button, Paper } from '@mui/material';

// src/assets 폴더에 넣은 배경 이미지를 불러옵니다.
import backgroundImage from '../assets/library-background.png'; 

export const LandingPage = () => {
    const navigate = useNavigate();

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
                    // ▼▼▼ 이 부분의 비율을 3에서 4로 늘렸습니다. ▼▼▼
                    flex: { xs: 0, md: 4 }, // 중간 크기 이상 화면에서 4의 비율을 차지
                    backgroundImage: `url(${backgroundImage})`,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    display: { xs: 'none', md: 'block' }
                }}
            />

            {/* 오른쪽 영역: 로그인 및 회원가입 */}
            <Box
                sx={{
                    // ▼▼▼ 이 부분의 비율은 그대로 1를 유지합니다. ▼▼▼
                    flex: { xs: 1, md: 1 }, // 중간 크기 이상 화면에서 2의 비율을 차지
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
                        <Button 
                            fullWidth 
                            variant="contained" 
                            color="primary" 
                            onClick={() => navigate('/user-auth')}
                            sx={{ padding: '12px', fontSize: '1rem' }}
                        >
                            사용자 로그인 / 가입
                        </Button>
                        <Button 
                            fullWidth 
                            variant="outlined" 
                            color="secondary" 
                            onClick={() => navigate('/author-auth')}
                            sx={{ padding: '12px', fontSize: '1rem' }}
                        >
                            작가 로그인 / 가입
                        </Button>
                        <Button 
                            fullWidth 
                            variant="text" 
                            onClick={() => navigate('/admin/approvals')}
                        >
                            관리자 페이지로 이동
                        </Button>
                    </Box>
                </Box>
            </Box>
        </Box>
    );
};
