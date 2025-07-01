// src/components/BookCard.jsx

import React from 'react';
import { Card, CardMedia, CardContent, Typography, Box } from '@mui/material';

/**
 * 개별 책 정보를 표시하는 카드 컴포넌트입니다.
 * 메인 화면과 같은 목록에서 사용됩니다.
 * @param {object} props
 * @param {object} props.book - 책 정보 객체 (bookId, title, authorName, imageUrl, summary 등 포함)
 * @param {function(): void} [props.onClick] - 카드 클릭 시 호출될 콜백 함수
 */
const BookCard = ({ book, onClick }) => {
  // `book.imageUrl`이 없을 경우를 대비한 대체 이미지 URL
  const defaultImageUrl = 'https://placehold.co/160x250/e0e0e0/000000?text=No+Image';

  return (
    <Box
      sx={{
        width: 160,
        cursor: onClick ? 'pointer' : 'default', // onClick 프롭스가 있을 때만 커서 포인터
        transition: 'transform 0.2s ease-in-out',
        '&:hover': {
          transform: onClick ? 'scale(1.03)' : 'none', // 클릭 가능할 때만 호버 효과
        },
      }}
      onClick={onClick} // 카드 클릭 이벤트 핸들러
    >
      <Card
        sx={{
          width: 160,
          boxShadow: 3,
          borderRadius: 2,
          overflow: 'hidden',
        }}
      >
        <CardMedia
          component="img"
          height="250"
          image={book.imageUrl || defaultImageUrl} // imageUrl 사용, 없으면 대체 이미지
          alt={book.title}
          sx={{ objectFit: 'cover' }}
        />
        <CardContent>
          {/* 책 제목 */}
          <Typography variant="subtitle1" noWrap>
            {book.title}
          </Typography>
          {/* 작가 이름 */}
          <Typography variant="body2" color="text.secondary" noWrap>
            {book.authorName} {/* author 대신 authorName 사용 */}
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default BookCard;
