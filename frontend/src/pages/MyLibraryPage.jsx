// =================================================================
// FILENAME: src/pages/MyLibraryPage.jsx (수정)
// 역할: 구독 및 구독 취소 성공 시, 전체 데이터를 새로고침하도록 수정합니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';
import { Link, useNavigate } from 'react-router-dom';
import { PointCharger } from '../components/PointCharger';
import { BookCard } from '../components/BookCard';
import { Typography, Box, Button, CircularProgress, IconButton } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';

export const MyLibraryPage = () => {
    const { auth, updateAuthUser } = useAuth();
    const [myBooks, setMyBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

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
            // ▼▼▼ [수정] 구독 취소 시에는 전체 새로고침 대신, API 응답으로 받은 데이터로만 화면을 업데이트합니다. ▼▼▼
            const response = await api.cancelSubscribeToBookService(auth.user.userId);
            alert("구독이 취소되었습니다.");
            updateAuthUser(response.data); // 상태만 바로 업데이트
        } catch (error) {
            alert("구독 취소에 실패했습니다.");
            console.error(error);
        }
    };

    const onChargeSuccess = async () => {
        alert("충전 완료! 최신 정보를 불러옵니다.");
        await fetchMyLibraryData();
    };

    if (loading) return <p>내 서재 정보를 불러오는 중...</p>;
    if (!auth.user) return <p>로그인이 필요합니다.</p>;

    const isSubscribed = auth.user.isSubscribe?.startsWith("월간 구독 중");

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h4" component="h2" gutterBottom fontWeight="bold">내 서재</Typography>
                <Button variant="outlined" startIcon={<RefreshIcon />} onClick={fetchMyLibraryData}>
                    내 정보 새로고침
                </Button>
            </Box>
            
            <Box sx={{ mb: 4, p: 2, backgroundColor: 'grey.100', borderRadius: 2 }}>
                <Typography variant="h6">내 정보</Typography>
                <Typography><strong>보유 포인트:</strong> {auth.user.pointBalance?.toLocaleString() || 0} P</Typography>
                <Typography><strong>구독 상태:</strong> {isSubscribed ? auth.user.isSubscribe : "현재 구독 중이 아닙니다."}</Typography>
                
                {isSubscribed ? (
                    <Button variant="contained" color="error" onClick={handleCancelSubscription} sx={{ mt: 1 }}>
                        구독 취소하기
                    </Button>
                ) : (
                    <Button variant="contained" onClick={handleSubscribe} sx={{ mt: 1 }}>
                        월간 구독하기 (11,900P)
                    </Button>
                )}
            </Box>

            <Box sx={{ mb: 4 }}>
                 <PointCharger onChargeSuccess={onChargeSuccess} />
            </Box>

            <Box>
                <Typography variant="h6">내가 소장한 책 목록 ({myBooks.length}권)</Typography>
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: '20px', mt: 2 }}>
                    {myBooks.length > 0 ? (
                        myBooks.map(book => <BookCard key={book.bookId} book={book} />)
                    ) : (
                        <Typography>아직 구매한 책이 없습니다.</Typography>
                    )}
                </Box>
            </Box>
        </Box>
    );
};
