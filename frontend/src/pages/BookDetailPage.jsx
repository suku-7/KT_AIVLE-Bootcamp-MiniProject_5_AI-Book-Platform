// =================================================================
// FILENAME: src/pages/BookDetailPage.jsx
// 역할: 도서 한 권의 상세 정보를 보여주는 페이지입니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { api, extractIdFromHref } from '../api/apiClient';

export const BookDetailPage = () => {
    const { bookId } = useParams();
    console.log("bookId in URL:", bookId);  // 👈 로그 확인
    const { auth } = useAuth();
    const navigate = useNavigate();
    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchBook = async () => {
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
    
    const handleBuyBook = async () => {
        if (!auth.user) {
            alert('사용자로 로그인해야 구매할 수 있습니다.');
            navigate('/user-auth');
            return;
        }
        try {
            await api.buyBook(auth.user.userId, { bookId: extractIdFromHref(book)});
            alert(`${book.title}을(를) 구매했습니다. 내 서재에서 확인하세요.`);
            // TODO: 구매 후 auth 상태의 사용자 정보를 갱신하여 소장 목록을 반영하면 더 좋습니다.
        } catch (error) {
            alert(error.response?.data?.message || '구매에 실패했습니다. 포인트가 부족할 수 있습니다.');
        }
    };

    if (loading) return <p>로딩 중...</p>;
    if (!book) return <p>도서 정보를 찾을 수 없습니다.</p>;

    const isSubscribed = auth.user?.isSubscribe?.startsWith("월간 구독 중");
    const isOwned = auth.user?.bookId?.includes(book.bookId);

    return (
        <div style={{ display: 'flex', gap: '2rem' }}>
            <img src={book.imageUrl} alt={book.title} style={{ width: '300px', height: 'auto', objectFit: 'cover', borderRadius: '8px' }} />
            <div>
                <h1>{book.title}</h1>
                <h3>{book.authorName}</h3>
                <p>({book.publishDate ? new Date(book.publishDate).toLocaleDateString() : '출간일 정보 없음'})</p>
                <p>소장 횟수: {book.selectCount || 0}</p>
                <p><strong>요약:</strong> {book.summary}</p>
                <hr />
                {(isSubscribed || isOwned) ? (
                    <div>
                        <h4>책 내용 보기</h4>
                        <div style={{ background: '#f9f9f9', padding: '1rem', border: '1px solid #eee', whiteSpace: 'pre-wrap' }}>{book.context}</div>
                    </div>
                ) : (
                    <button onClick={handleBuyBook} style={{ padding: '0.75rem 1.5rem', cursor: 'pointer' }}>이 책 소장하기 (1000P)</button>
                )}
            </div>
        </div>
    );
};