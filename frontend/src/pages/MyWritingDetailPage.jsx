import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../api/apiClient';
import { useAuth } from '../contexts/AuthContext';

const MyWritingDetailPage = () => {
    const { bookId } = useParams();
    const { auth } = useAuth();
    const navigate = useNavigate();
    const [writing, setWriting] = useState(null);

    useEffect(() => {
        const fetchWriting = async () => {
            try {
                const response = await api.getWriting(bookId, {
                    headers: {
                        Authorization: `Bearer ${auth.token}`,
                    },
                });
                setWriting(response.data);
            } catch (error) {
                console.error('글 불러오기 실패:', error);
                alert('글을 불러올 수 없습니다.');
            }
        };

        if (auth.token) {
            fetchWriting();
        }
    }, [bookId, auth.token]);

    if (!writing) return <div>로딩 중...</div>;

    return (
        <div style={{ padding: '2rem' }}>
            <h2>{writing.title}</h2>
            <p><strong>작가:</strong> {writing.authorName}</p>
            <hr />
            <p style={{ whiteSpace: 'pre-line' }}>{writing.context}</p>

            <button onClick={() => navigate(-1)} style={{ marginTop: '1rem' }}>
                ← 돌아가기
            </button>
        </div>
    );
};

export default MyWritingDetailPage;