// =================================================================
// FILENAME: src/pages/BookDetailPage.jsx
// 역할: 도서 상세 정보 페이지 (구매 성공 후 2초 딜레이를 주고 화면 리프레시)
// =================================================================
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';
import { 
    Box, 
    Button, 
    Typography, 
    CircularProgress, 
    Stack,
    Grid,
    Divider,
    Alert
} from '@mui/material';

export const BookDetailPage = () => {
    const { bookId } = useParams();
    const { auth, setAuth } = useAuth(); 
    const navigate = useNavigate();
    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isBuying, setIsBuying] = useState(false);

    useEffect(() => {
        const fetchBook = async () => {
            setLoading(true);
            try {
                const response = await api.getLibraryInfo(bookId);
                setBook(response.data);
            } catch (error) {
                console.error("도서 정보를 불러오는 데 실패했습니다.", error);
            } finally {
                setLoading(false);
            }
        };
        fetchBook();
    }, [bookId]);
    
    /**
     * [수정] 책 구매 핸들러 함수에 2초 딜레이를 추가하고, 정보 갱신 실패 알림을 제거합니다.
     */
    const handleBuyBook = async () => {
        if (!auth.user) {
            alert('사용자로 로그인해야 구매할 수 있습니다.');
            navigate('/user-auth');
            return;
        }

        const userPoints = auth.user.pointBalance || 0;
        if (userPoints < 1000) {
            alert(`포인트가 부족합니다. (보유: ${userPoints}P, 필요: 1000P)`);
            return;
        }

        setIsBuying(true);
        
        try {
            // 1. 백엔드에 책 구매 요청
            await api.buyBook(auth.user.userId, { bookId: bookId });
            
            // 2. 사용자에게 즉시 성공 피드백 제공
            alert(`${book.title}을(를) 구매했습니다.`);
            
            // 3. [수정] 2초(2000ms) 딜레이 후 화면 리랜더링 실행
            setTimeout(async () => {
                try {
                    const updatedUserResponse = await api.getUser(auth.user.userId);
                    setAuth(prevAuth => ({
                        ...prevAuth,
                        user: updatedUserResponse.data,
                    }));
                } catch (error) {
                    // [수정] 사용자에게 알림을 보여주는 대신, 콘솔에만 에러를 기록합니다.
                    console.error("사용자 정보 갱신 중 오류 발생:", error);
                } finally {
                    // 정보 갱신 시도가 끝나면 버튼 활성화
                    setIsBuying(false);
                }
            }, 1000);

        } catch (error) {
            // 구매 요청 자체가 실패한 경우
            console.error("책 구매 요청 실패:", error);
            alert("구매 처리 중 문제가 발생했습니다. 다시 시도해주세요.");
            setIsBuying(false);
        }
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    if (!book) {
        return (
            <Box sx={{ p: 4, textAlign: 'center' }}>
                <Typography variant="h6">도서 정보를 찾을 수 없습니다.</Typography>
                <Button variant="contained" onClick={() => navigate('/')} sx={{ mt: 2 }}>
                    홈으로 돌아가기
                </Button>
            </Box>
        );
    }

    const isSubscribed = auth.user?.isSubscribe?.startsWith("월간 구독 중");
    const isOwned = auth.user?.bookId?.includes(book.bookId);

    return (
        <Box sx={{ maxWidth: '700px', margin: 'auto', p: { xs: 2, md: 4 } }}>
            <Grid container spacing={{ xs: 4, md: 6 }} alignItems="center">
                
                <Grid item xs={12} sm={5} md={4}>
                    <Box
                        component="img"
                        src={book.imageUrl || 'https://placehold.co/400x600?text=No+Image'}
                        alt={book.title}
                        sx={{
                            width: '100%',
                            height: 'auto',
                            borderRadius: 2,
                            boxShadow: '0 8px 24px rgba(0,0,0,0.12)'
                        }}
                    />
                </Grid>

                <Grid item xs={12} sm={7} md={8}>
                    <Stack spacing={2}>
                        <Box>
                            <Typography variant="h4" component="h1" fontWeight="bold">
                                {book.title}
                            </Typography>
                            <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
                                {book.authorName} · {book.publishDate ? new Date(book.publishDate).toLocaleDateString() : '정보 없음'} · 소장 {book.selectCount || 0}
                            </Typography>
                        </Box>
                        
                        <Divider />

                        <Box>
                            <Typography variant="h6" fontWeight="bold" sx={{ mb: 1 }}>
                                책 소개
                            </Typography>
                            <Typography variant="body1" color="text.secondary" sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.7 }}>
                                {book.summary || "요약 정보가 없습니다."}
                            </Typography>
                        </Box>

                        <Divider />
                        
                        {(isSubscribed || isOwned) ? (
                             <Alert severity="success">이미 소장하고 있거나 구독 중인 책입니다.</Alert>
                        ) : (
                            <Alert 
                                severity="info" 
                                action={
                                    <Button 
                                        variant="contained"
                                        size="small"
                                        onClick={handleBuyBook}
                                        disabled={isBuying}
                                        sx={{
                                            backgroundColor: '#FFF7BF',
                                            color: 'grey.800',
                                            fontWeight: 'bold',
                                            boxShadow: 'none',
                                            '&:hover': {
                                                backgroundColor: '#FFEB60',
                                            },
                                        }}
                                    >
                                        {isBuying ? '처리 중...' : '소장하기 (1000P)'}
                                    </Button>
                                }
                                sx={{ alignItems: 'center' }}
                            >
                                내용을 보려면 월간 구독 또는 책 소장이 필요합니다.
                            </Alert>
                        )}
                    </Stack>
                </Grid>

            </Grid>
        </Box>
    );
};
