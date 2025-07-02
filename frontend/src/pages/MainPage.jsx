// =================================================================
// FILENAME: src/pages/MainPage.jsx (신규 생성)
// 역할: 로그인 후에 사용자와 작가가 보게 될 실제 메인 페이지입니다.
// 내용은 기존의 HomePage.jsx와 거의 동일합니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { api } from '../api/apiClient';
import { BookCard } from '../components/BookCard';
import { Typography, Box, CircularProgress } from '@mui/material';

const BookSection = ({ title, books }) => (
    <Box component="section" sx={{ mb: 5 }}>
        <Typography variant="h4" component="h2" gutterBottom fontWeight="bold">
            {title}
        </Typography>
        {books.length > 0 ? (
            <Box sx={{ display: 'flex', gap: '20px', overflowX: 'auto', pb: 2 }}>
                {books.map(book => (
                    <BookCard key={book.bookId} book={book} />
                ))}
            </Box>
        ) : (
            <Typography>해당하는 도서가 없습니다.</Typography>
        )}
    </Box>
);

export const MainPage = () => {
    const [allBooks, setAllBooks] = useState([]);
    const [bestsellers, setBestsellers] = useState([]);
    const [newReleases, setNewReleases] = useState([]);
    const [loading, setLoading] = useState(true);

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
        <Box>
            <BookSection title="🔥 베스트셀러 TOP 5" books={bestsellers} />
            <BookSection title="✨ 신규 출간 도서" books={newReleases} />
            <BookSection title="📚 전체 도서 목록" books={allBooks} />
        </Box>
    );
};