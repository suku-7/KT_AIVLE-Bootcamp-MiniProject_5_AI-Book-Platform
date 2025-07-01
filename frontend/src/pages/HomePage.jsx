// src/pages/HomePage.jsx

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom'; // 라우팅을 위해 useNavigate 임포트
import { Grid, Typography, Box, TextField, CircularProgress, Alert } from '@mui/material';
// BookCard는 개별 책을 표시하는 컴포넌트입니다.
// 이전에 BookModal은 제거되었으므로 임포트하지 않습니다.
import BookCard from '../components/BookCard';
// 새로운 API 함수들을 임포트합니다.
import { getLibraryInfos, getUser } from '../api/bookApi'; // 메인 화면에서 필요한 API

const HomePage = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [keyword, setKeyword] = useState("");
  const [userPurchasedBooks, setUserPurchasedBooks] = useState([]); // 사용자가 구매한 책 ID 목록
  const navigate = useNavigate(); // 페이지 이동을 위한 훅

  // 도서 목록과 사용자 구매 정보를 불러오는 함수
  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);

      // 1. 전체 도서 목록 불러오기
      const booksResponse = await getLibraryInfos();
      if (booksResponse.data && Array.isArray(booksResponse.data)) {
        setBooks(booksResponse.data);
      } else {
        setBooks([]);
        console.warn("도서 목록 데이터 형식이 예상과 다릅니다:", booksResponse.data);
        setError("도서 목록을 불러오는 데 실패했습니다.");
      }

      // 2. 사용자 구매 목록 불러오기 (예시: userId가 1인 사용자의 구매 목록)
      // 실제 앱에서는 로그인한 사용자의 ID를 사용해야 합니다.
      const userId = 1; // 임시 사용자 ID. 실제로는 로그인 상태에서 가져와야 함.
      try {
        const userResponse = await getUser(userId);
        if (userResponse.data && Array.isArray(userResponse.data.bookId)) {
          setUserPurchasedBooks(userResponse.data.bookId);
        } else {
          setUserPurchasedBooks([]);
          console.warn("사용자 구매 목록 데이터 형식이 예상과 다릅니다:", userResponse.data);
        }
      } catch (userErr) {
        console.error("사용자 구매 목록 조회 중 오류:", userErr);
        // 사용자 구매 목록은 필수 기능이 아닐 수 있으므로 에러를 치명적으로 처리하지 않을 수 있습니다.
      }

    } catch (err) {
      console.error("데이터 조회 중 오류:", err);
      setError("서버 오류로 데이터를 불러올 수 없습니다.");
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트 마운트 시 데이터 불러오기
  useEffect(() => {
    fetchData();
  }, []);

  // 검색 키워드에 따라 도서 목록 필터링
  const filteredBooks = books.filter(book =>
    book.title.toLowerCase().includes(keyword.toLowerCase()) ||
    (book.authorName && book.authorName.toLowerCase().includes(keyword.toLowerCase())) // authorName으로 변경
  );

  // 최근 구매한 책 목록 필터링 (간단 예시)
  // 실제 구현에서는 구매 날짜 등을 고려하여 더 복잡한 로직이 필요할 수 있습니다.
  const recentPurchasedBooks = books.filter(book => userPurchasedBooks.includes(book.bookId));

  // 베스트셀러 및 신작 (간단 예시)
  // 실제로는 API에서 해당 정보를 제공하거나, 별도의 로직으로 분류해야 합니다.
  const bestsellers = books.filter(book => book.rank && book.rank <= 10); // rank 속성 가정
  const newReleases = books.filter(book => {
    // publishDate 속성을 가정하고 최근 30일 이내 출간된 책으로 필터링
    if (book.publishDate) {
      const publishDate = new Date(book.publishDate);
      const thirtyDaysAgo = new Date();
      thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
      return publishDate >= thirtyDaysAgo;
    }
    return false;
  });


  // 책 카드 클릭 시 상세 페이지로 이동
  const handleBookClick = (bookId) => {
    navigate(`/books/${bookId}`); // BookDetailPage로 이동
  };

  return (
    <Box sx={{ px: { xs: 2, sm: 5, md: 10, lg: 35 }, py: 5, mx: 'auto' }}>
      <Box
        sx={{
          mb: 3,
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          justifyContent: 'space-between',
          alignItems: { xs: 'flex-start', sm: 'center' },
          gap: 2
        }}
      >
        <Typography variant="h4" fontWeight={600}>도서 목록</Typography>
        <TextField
          fullWidth={window.innerWidth < 600}
          size="small"
          placeholder="제목 또는 작가 검색"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          sx={{ backgroundColor: '#fff' }}
        />
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
          <CircularProgress />
          <Typography sx={{ ml: 2 }}>도서 목록을 불러오는 중...</Typography>
        </Box>
      ) : error ? (
        <Alert severity="error" sx={{ my: 2 }}>{error}</Alert>
      ) : (
        <>
          {/* 최근 구매한 책 섹션 */}
          {recentPurchasedBooks.length > 0 && (
            <Box sx={{ my: 4 }}>
              <Typography variant="h5" fontWeight={600} mb={2}>최근 구매한 책</Typography>
              <Grid container spacing={2} justifyContent="flex-start">
                {recentPurchasedBooks.map(book => (
                  <Grid item xs={6} sm={4} md={3} lg={2} key={book.bookId}>
                    <BookCard book={book} onClick={() => handleBookClick(book.bookId)} />
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {/* 베스트셀러 섹션 */}
          {bestsellers.length > 0 && (
            <Box sx={{ my: 4 }}>
              <Typography variant="h5" fontWeight={600} mb={2}>베스트셀러</Typography>
              <Grid container spacing={2} justifyContent="flex-start">
                {bestsellers.map(book => (
                  <Grid item xs={6} sm={4} md={3} lg={2} key={book.bookId}>
                    <BookCard book={book} onClick={() => handleBookClick(book.bookId)} />
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {/* 신작 섹션 */}
          {newReleases.length > 0 && (
            <Box sx={{ my: 4 }}>
              <Typography variant="h5" fontWeight={600} mb={2}>신작</Typography>
              <Grid container spacing={2} justifyContent="flex-start">
                {newReleases.map(book => (
                  <Grid item xs={6} sm={4} md={3} lg={2} key={book.bookId}>
                    <BookCard book={book} onClick={() => handleBookClick(book.bookId)} />
                  </Grid>
                ))}
              </Grid>
            </Box>
          )}

          {/* 총 도서 목록 섹션 */}
          <Box sx={{ my: 4 }}>
            <Typography variant="h5" fontWeight={600} mb={2}>총 도서 목록</Typography>
            {filteredBooks.length === 0 ? (
              <Alert severity="info" sx={{ my: 2 }}>
                {keyword ? "검색 결과가 없습니다." : "등록된 도서가 없습니다."}
              </Alert>
            ) : (
              <Grid container spacing={2} justifyContent="flex-start">
                {filteredBooks.map(book => (
                  <Grid item xs={6} sm={4} md={3} lg={2} key={book.bookId}>
                    <BookCard book={book} onClick={() => handleBookClick(book.bookId)} />
                  </Grid>
                ))}
              </Grid>
            )}
          </Box>
        </>
      )}
    </Box>
  );
};

export default HomePage;
