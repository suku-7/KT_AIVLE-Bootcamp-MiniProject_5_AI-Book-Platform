// =================================================================
// FILENAME: src/pages/MyLibraryPage.jsx (수정)
// 역할: '내 정보 새로고침' 버튼을 '내 정보' 카드 안으로 이동시킵니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';
import { PointCharger } from '../components/PointCharger';
import { BookCard } from '../components/BookCard';
import { Typography, Box, Button, CircularProgress, Paper, Stack, Chip } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import WorkspacePremiumIcon from '@mui/icons-material/WorkspacePremium';

export const MyLibraryPage = () => {
    const { auth, updateAuthUser } = useAuth();
    const [myBooks, setMyBooks] = useState([]);
    const [loading, setLoading] = useState(true);

    const buttonTextColor = 'grey.800';
    const subscribeButtonStyle = {
        backgroundColor: '#FFF7BF',
        color: buttonTextColor,
        fontWeight: 'bold',
        boxShadow: 'none',
        '&:hover': {
            backgroundColor: '#FFEB60',
        }
    };

    const fetchMyLibraryData = async () => {
        if (!auth.user?.userId) {
            setLoading(false);
            return;
        }
        setLoading(true);
        try {
            const userResponse = await api.getUser(auth.user.userId);
            updateAuthUser(userResponse.data);

            const ownedBookIds = userResponse.data.bookId || [];
            if (ownedBookIds.length > 0) {
                const bookPromises = ownedBookIds.map(id => api.getLibraryInfo(id));
                const bookResponses = await Promise.all(bookPromises);
                setMyBooks(bookResponses.map(res => res.data));
            } else {
                setMyBooks([]);
            }
        } catch (error) {
            console.error("내 서재 정보를 불러오는 데 실패했습니다.", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMyLibraryData();
    }, []);

    const handleSubscribe = async () => {
        try {
            await api.subscribeToBookService(auth.user.userId);
            alert("구독이 완료되었습니다. 최신 정보를 불러옵니다.");
            await fetchMyLibraryData();
        } catch (error) {
            alert(error.response?.data?.message || "구독에 실패했습니다. 포인트를 충전해주세요.");
        }
    };

    const handleCancelSubscription = async () => {
        if (!window.confirm("정말로 구독을 취소하시겠습니까?")) {
            return;
        }
        try {
            const response = await api.cancelSubscribeToBookService(auth.user.userId);
            alert("구독이 취소되었습니다.");
            updateAuthUser(response.data);
        } catch (error) {
            alert("구독 취소에 실패했습니다.");
            console.error(error);
        }
    };

    const onChargeSuccess = async () => {
        alert("충전 완료! 최신 정보를 불러옵니다.");
        await fetchMyLibraryData();
    };

    if (loading) return (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
            <CircularProgress />
        </Box>
    );
    if (!auth.user) return <p>로그인이 필요합니다.</p>;

    const isSubscribed = auth.user.isSubscribe?.startsWith("월간 구독 중");

    return (
        <Box sx={{ p: { xs: 2, md: 4 } }}>
            {/* 1. 페이지 전체 제목은 그대로 유지합니다. */}
            <Typography variant="h4" component="h2" gutterBottom fontWeight="bold" sx={{ mb: 3 }}>
                내 서재
            </Typography>
            
            <Paper 
                variant="outlined"
                sx={{ 
                    p: 3, 
                    mb: 4, 
                    borderRadius: '16px', 
                    backgroundColor: 'white',
                    maxWidth: '500px'
                }}
            >
                {/* 2. '내 정보' 제목과 '새로고침' 버튼을 함께 배치합니다. */}
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h6" fontWeight="bold">내 정보</Typography>
                    <Button variant="outlined" size="small" startIcon={<RefreshIcon />} onClick={fetchMyLibraryData}>
                        새로고침
                    </Button>
                </Box>

                <Stack spacing={1} sx={{ mb: 3 }}>
                    <Typography><strong>보유 포인트:</strong> {auth.user.pointBalance?.toLocaleString() || 0} P</Typography>
                    
                    {isSubscribed ? (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <Typography><strong>구독 상태:</strong></Typography>
                            <Chip 
                                icon={<WorkspacePremiumIcon />} 
                                label={auth.user.isSubscribe}
                                color="success"
                                variant="filled"
                            />
                        </Box>
                    ) : (
                        <Typography><strong>구독 상태:</strong> 현재 구독 중이 아닙니다.</Typography>
                    )}
                </Stack>

                {isSubscribed ? (
                    <Button variant="outlined" color="error" onClick={handleCancelSubscription}>
                        구독 취소하기
                    </Button>
                ) : (
                    <Button variant="contained" onClick={handleSubscribe} sx={subscribeButtonStyle}>
                        월간 구독하기 (9,900P)
                    </Button>
                )}
            </Paper>

            <Paper 
                variant="outlined" 
                sx={{ 
                    p: 3, 
                    mb: 4, 
                    borderRadius: '16px', 
                    backgroundColor: 'white',
                    maxWidth: '500px'
                }}
            >
                 <PointCharger onChargeSuccess={onChargeSuccess} />
            </Paper>

            <Paper 
                variant="outlined" 
                sx={{ 
                    p: 3, 
                    borderRadius: '16px', 
                    backgroundColor: 'white' 
                }}
            >
                <Typography variant="h6" fontWeight="bold">내가 소장한 책 목록 ({myBooks.length}권)</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: '20px', mt: 2 }}>
                    {myBooks.length > 0 ? (
                        myBooks.map(book => <BookCard key={book.bookId} book={book} />)
                    ) : (
                        <Typography sx={{ p: 3, color: 'text.secondary' }}>아직 구매한 책이 없습니다.</Typography>
                    )}
                </Box>
            </Paper>
        </Box>
    );
};
