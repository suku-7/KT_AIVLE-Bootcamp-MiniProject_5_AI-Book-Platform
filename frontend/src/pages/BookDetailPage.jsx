// src/pages/BookDetailPage.jsx

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Typography, Button, Paper, CircularProgress, Alert, Card, CardMedia, CardContent } from '@mui/material';
import { getLibraryInfo, getUser, buyBook } from '../api/bookApi'; // 필요한 API 함수 임포트

const BookDetailPage = () => {
  const { bookId } = useParams(); // URL에서 bookId 파라미터 가져오기
  const navigate = useNavigate(); // 페이지 이동을 위한 훅

  const [book, setBook] = useState(null);
  const [user, setUser] = useState(null); // 사용자 정보 (포인트, 구매 목록 확인용)
  const [isLoading, setIsLoading] = useState(true);
  const [isBuying, setIsBuying] = useState(false); // 구매 중 로딩 상태
  const [error, setError] = useState(null);

  // TODO: 실제 앱에서는 로그인한 사용자의 ID를 사용해야 합니다.
  const CURRENT_USER_ID = 1; // 임시 사용자 ID

  // 도서 상세 정보와 사용자 정보를 불러오는 함수
  const fetchData = async () => {
    if (!bookId) {
      setError("도서 ID가 필요합니다.");
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      // 1. 도서 상세 정보 불러오기
      const bookResponse = await getLibraryInfo(bookId);
      if (bookResponse.data) {
        setBook(bookResponse.data);
      } else {
        setError("도서 정보를 불러오지 못했습니다.");
        setIsLoading(false);
        return;
      }

      // 2. 사용자 정보 불러오기 (포인트, 구매 목록 확인용)
      const userResponse = await getUser(CURRENT_USER_ID);
      if (userResponse.data) {
        setUser(userResponse.data);
      } else {
        // 사용자 정보 로드 실패는 치명적이지 않을 수 있음 (예: 비로그인 상태)
        console.warn("사용자 정보를 불러오지 못했습니다.");
      }

    } catch (err) {
      console.error("데이터 로딩 실패:", err);
      setError(err.response?.data?.message || "데이터를 불러오는 데 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [bookId]); // bookId가 변경될 때마다 데이터 다시 불러오기

  // 책 구매 처리 함수
  const handleBuyBook = async () => {
    if (!user) {
      alert("로그인이 필요합니다."); // 실제로는 로그인 페이지로 리다이렉트
      navigate('/user-auth');
      return;
    }
    if (!book) {
      alert("구매할 책 정보가 없습니다.");
      return;
    }

    // 이미 구매한 책인지 확인
    if (user.bookId && user.bookId.includes(parseInt(bookId))) {
      alert("이미 소장하고 있는 책입니다.");
      return;
    }

    if (!window.confirm(`'${book.title}'을(를) 구매하시겠습니까?`)) {
      return;
    }

    setIsBuying(true);
    setError(null);
    try {
      // 책 구매 API 호출
      const response = await buyBook(CURRENT_USER_ID, { bookId: parseInt(bookId) });
      console.log("책 구매 성공:", response.data);
      alert('책 구매가 완료되었습니다!');
      // 구매 성공 후 사용자 정보와 책 정보 다시 불러오기 (잔액, 구매 목록 업데이트)
      fetchData();
    } catch (err) {
      console.error("책 구매 실패:", err);
      setError(err.response?.data?.message || "책 구매에 실패했습니다.");
    } finally {
      setIsBuying(false);
    }
  };

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px', mt: 5 }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>도서 정보를 불러오는 중...</Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ my: 2, maxWidth: 800, mx: 'auto' }}>
        {error}
      </Alert>
    );
  }

  if (!book) {
    return (
      <Alert severity="info" sx={{ my: 2, maxWidth: 800, mx: 'auto' }}>
        해당 도서 정보를 찾을 수 없습니다.
      </Alert>
    );
  }

  const isBookOwned = user && user.bookId && user.bookId.includes(parseInt(bookId));
  const canAfford = user && user.pointBalance !== null && user.pointBalance >= 5000; // 책 가격을 5000으로 가정

  return (
    <Paper elevation={3} sx={{ p: 4, maxWidth: 800, mx: 'auto', mt: 5, borderRadius: 2 }}>
      <Box sx={{ display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, gap: 4 }}>
        {/* 책 표지 이미지 */}
        <Box sx={{ flexShrink: 0, width: { xs: '100%', sm: 200 }, height: { xs: 300, sm: 300 }, display: 'flex', justifyContent: 'center', alignItems: 'center', overflow: 'hidden', borderRadius: 1, boxShadow: 2 }}>
          <CardMedia
            component="img"
            image={book.imageUrl || 'https://placehold.co/200x300/e0e0e0/000000?text=No+Image'} // 이미지 URL이 없을 경우 대체 이미지
            alt={book.title}
            sx={{ width: '100%', height: '100%', objectFit: 'cover' }}
          />
        </Box>

        {/* 책 정보 */}
        <Box sx={{ flexGrow: 1 }}>
          <Typography variant="h4" component="h1" fontWeight={700} gutterBottom>
            {book.title}
          </Typography>
          <Typography variant="h6" color="text.secondary" gutterBottom>
            작가: {book.authorName}
          </Typography>
          <Typography variant="body1" sx={{ mt: 2, lineHeight: 1.6 }}>
            {book.summary || '이 책에 대한 요약 정보가 없습니다.'}
          </Typography>

          {/* 추가 정보 (예: 분류, 출판일) */}
          <Box sx={{ mt: 3, fontSize: '0.9em', color: 'text.secondary' }}>
            <Typography variant="body2">분류: {book.classficationType || '미정'}</Typography>
            <Typography variant="body2">출판일: {book.publishDate ? new Date(book.publishDate).toLocaleDateString() : '미정'}</Typography>
            <Typography variant="body2">조회수: {book.selectCount || 0}</Typography>
          </Box>
        </Box>
      </Box>

      {/* 구매 정보 및 버튼 */}
      <Box sx={{ mt: 4, pt: 3, borderTop: '1px dashed #eee', textAlign: 'center' }}>
        {user ? (
          <>
            <Typography variant="h6" gutterBottom>
              내 포인트: {user.pointBalance !== null ? `${user.pointBalance} P` : '정보 없음'}
            </Typography>
            {isBookOwned ? (
              <Button variant="contained" disabled sx={{ mt: 2, py: 1.5, px: 5, fontSize: '1.1em' }}>
                이미 소장 중
              </Button>
            ) : (
              <Button
                variant="contained"
                color="primary"
                onClick={handleBuyBook}
                disabled={isBuying || !canAfford}
                sx={{ mt: 2, py: 1.5, px: 5, fontSize: '1.1em', backgroundColor: '#3f51b5', '&:hover': { backgroundColor: '#303f9f' } }}
              >
                {isBuying ? '구매 중...' : `구매하기 (5,000 P)`}
              </Button>
            )}
            {!canAfford && !isBookOwned && user.pointBalance !== null && (
              <Typography variant="body2" color="error" sx={{ mt: 1 }}>
                포인트가 부족합니다.
              </Typography>
            )}
          </>
        ) : (
          <Typography variant="body1" color="text.secondary">
            로그인 후 구매 가능합니다.
            <Button onClick={() => navigate('/user-auth')} sx={{ ml: 1 }}>로그인</Button>
          </Typography>
        )}
      </Box>
    </Paper>
  );
};

export default BookDetailPage;
