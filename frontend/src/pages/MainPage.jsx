// =================================================================
// FILENAME: src/pages/MainPage.jsx (수정)
// 역할: 모든 섹션의 배경을 흰색으로 변경하고 테두리를 추가하여 디자인을 통일합니다.
// =================================================================
import React, { useState, useEffect, useMemo } from 'react';
import { api } from '../api/apiClient';
import { BookCard } from '../components/BookCard';
import { ImageSlideshow } from '../components/ImageSlideshow';
import { Typography, Box, CircularProgress, TextField, InputAdornment, Paper } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

import slide1 from '../assets/slide-1.png';
import slide2 from '../assets/slide-2.png';

const SectionHeader = ({ title, subtitle }) => (
    <Box sx={{ mb: 3 }}>
        <Typography variant="h4" component="h2" fontWeight="bold">
            {title}
        </Typography>
        {subtitle && (
            <Typography variant="body1" color="text.secondary">
                {subtitle}
            </Typography>
        )}
    </Box>
);

const BookCarouselSection = ({ title, subtitle, books }) => (
    // 1. Paper 컴포넌트의 스타일을 수정합니다.
    <Paper 
        variant="outlined" // elevation={0} 대신 variant="outlined" 사용
        sx={{ 
            p: 3, 
            mb: 4, 
            borderRadius: '16px', 
            backgroundColor: 'white' // 배경색을 흰색으로 변경
        }}
    >
        <SectionHeader title={title} subtitle={subtitle} />
        {books.length > 0 ? (
            <Box sx={{ display: 'flex', gap: '20px', overflowX: 'auto', pb: 2 }}>
                {books.map(book => (
                    <BookCard key={book.bookId} book={book} />
                ))}
            </Box>
        ) : (
            <Typography>해당하는 도서가 없습니다.</Typography>
        )}
    </Paper>
);

const AllBooksSection = ({ books }) => {
    const [searchTerm, setSearchTerm] = useState('');

    const filteredBooks = useMemo(() => {
        if (!searchTerm) return books;
        return books.filter(book => 
            book.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            book.authorName?.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [books, searchTerm]);

    return (
        // 2. 전체 도서 목록 섹션의 Paper 스타일도 동일하게 수정합니다.
        <Paper 
            variant="outlined" 
            sx={{ 
                p: 3, 
                borderRadius: '16px', 
                backgroundColor: 'white' 
            }}
        >
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Typography variant="h4" component="h2" fontWeight="bold" sx={{ mr: 2 }}>
                    📚 전체 도서
                </Typography>
                <TextField
                    variant="outlined"
                    size="small"
                    placeholder="제목 또는 작가로 검색"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    InputProps={{
                        startAdornment: (<InputAdornment position="start"><SearchIcon /></InputAdornment>),
                    }}
                />
            </Box>
            {filteredBooks.length > 0 ? (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: '20px', justifyContent: 'flex-start' }}>
                    {filteredBooks.map(book => (
                        <BookCard key={book.bookId} book={book} />
                    ))}
                </Box>
            ) : (
                <Typography>"{searchTerm}"에 대한 검색 결과가 없습니다.</Typography>
            )}
        </Paper>
    );
};

export const MainPage = () => {
    const [allBooks, setAllBooks] = useState([]);
    const [bestsellers, setBestsellers] = useState([]);
    const [newReleases, setNewReleases] = useState([]);
    const [loading, setLoading] = useState(true);

    const slideImages = [slide1, slide2];

    useEffect(() => {
        const fetchBooks = async () => {
            try {
                const response = await api.getLibraryInfos();
                const books = response.data._embedded?.libraryInfos || [];
                setAllBooks(books);

                const sortedByBest = [...books].sort((a, b) => (b.selectCount || 0) - (a.selectCount || 0));
                setBestsellers(sortedByBest.slice(0, 5));

                const sortedByNew = [...books].sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate));
                setNewReleases(sortedByNew.slice(0, 5));
            } catch (error) {
                console.error("도서 목록을 불러오는 데 실패했습니다.", error);
            } finally {
                setLoading(false);
            }
        };
        fetchBooks();
    }, []);

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ p: { xs: 2, md: 4 } }}>
            <Box sx={{ mb: 4 }}>
                <ImageSlideshow images={slideImages} />
            </Box>

            <BookCarouselSection 
                title="👑 AI IN 서재 랭킹"
                subtitle="지금 가장 많은 선택을 받은 책들을 만나보세요."
                books={bestsellers} 
            />
            <BookCarouselSection 
                title="✨ 따끈따끈, 새로 들어온 책"
                subtitle="기다리던 독서 취향, 지금 바로 펼쳐보세요!"
                books={newReleases} 
            />
            <AllBooksSection books={allBooks} />
        </Box>
    );
};
