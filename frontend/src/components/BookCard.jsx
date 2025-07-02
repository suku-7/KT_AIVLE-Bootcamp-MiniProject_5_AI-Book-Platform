// =================================================================
// FILENAME: src/components/BookCard.jsx (신규 생성)
// 역할: 책 한 권의 정보를 카드 형태로 예쁘게 표시하는 재사용 가능한 컴포넌트입니다.
// 이전 프로젝트의 코드를 기반으로, 현재 프로젝트의 데이터 구조에 맞게 수정했습니다.
// =================================================================
import React from 'react';
import { Card, CardMedia, CardContent, Typography, Box } from '@mui/material';
import { Link } from 'react-router-dom';

// Material-UI 아이콘을 사용하려면 아래 패키지를 설치해야 합니다.
// npm install @mui/icons-material @mui/material @emotion/react @emotion/styled

export const BookCard = ({ book }) => {
    if (!book) return null;

    return (
        <Link to={`/book/${book.bookId}`} style={{ textDecoration: 'none' }}>
            <Card
                sx={{
                    width: 200,
                    height: 350,
                    display: 'flex',
                    flexDirection: 'column',
                    boxShadow: 3,
                    borderRadius: 2,
                    transition: 'transform 0.3s ease-in-out, box-shadow 0.3s ease-in-out',
                    '&:hover': {
                        transform: 'translateY(-5px)',
                        boxShadow: 6,
                    }
                }}
            >
                <CardMedia
                    component="img"
                    sx={{
                        height: 250,
                        objectFit: 'cover',
                    }}
                    image={book.imageUrl || 'https://placehold.co/400x600?text=No+Image'} // 이미지가 없을 경우 대체 이미지
                    alt={book.title}
                />
                <CardContent sx={{ flexGrow: 1, padding: '16px' }}>
                    <Typography gutterBottom variant="h6" component="div" noWrap title={book.title}>
                        {book.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" noWrap title={book.authorName}>
                        {book.authorName}
                    </Typography>
                </CardContent>
            </Card>
        </Link>
    );
};