import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/apiClient';
import { useAuth } from '../contexts/AuthContext';

const MyWritingsPage = () => {
    const navigate = useNavigate();
    const { auth } = useAuth(); // auth.token 을 사용
    const [myWritings, setMyWritings] = useState([]);

    useEffect(() => {
        const fetchMyWritings = async () => {
            try {
                const response = await api.getMyWritings({
                    headers: {
                        Authorization: `Bearer ${auth.token}`,
                    },
                });
                setMyWritings(response.data);
            } catch (error) {
                console.error('내 글 가져오기 실패:', error);
                alert('글을 불러오는데 실패했습니다.');
            }
        };

        if (auth.token) {
            fetchMyWritings();
        }
    }, [auth.token]);

    return (
        <div style={{ padding: '2rem' }}>
            <h2>📝 내가 쓴 글</h2>

            <button onClick={() => navigate('/write')} style={{ marginBottom: '1rem' }}>
                ✍ 새 글 작성하기
            </button>
            <button onClick={() => navigate(`/publish/:writingId`)} style={{ marginBottom: '1rem' }}>
                출간 신청하기</button>

            {myWritings.length === 0 ? (
                <p>작성한 글이 없습니다.</p>
            ) : (
                <ul>
                    {myWritings.map((writing) => (
                        <li key={writing.bookId} style={{ marginBottom: '0.5rem' }}>
                            <strong>{writing.title}</strong>
                            <button
                                onClick={() => navigate(`/edit/${writing.bookId}`)}
                                style={{ marginLeft: '1rem' }}
                            >
                                수정하기
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default MyWritingsPage;