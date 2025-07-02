import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {api} from '../api/apiClient';
import { useAuth } from '../contexts/AuthContext';

const MyWritingsPage = () => {
    const navigate = useNavigate();
    const { auth } = useAuth();
    const [myWritings, setMyWritings] = useState([]);

    useEffect(() => {
        const fetchMyWritings = async () => {
            try {
                console.log("Fetching MyWritings");
                const response = await api.getMyWritings();
                console.log(response.data)
                setMyWritings(response.data);
            } catch (error) {
                console.error('내 글 가져오기 실패:', error);
                alert('글을 불러오는데 실패했습니다.');
            }
        };
        console.log(auth)
        if (auth && auth.user && auth.user.token) {
            fetchMyWritings();
        }
    }, [auth]);

    return (
        <div style={{ padding: '2rem' }}>
            <h2>📚 내가 쓴 글</h2>

            <button onClick={() => navigate('/write')} style={{ marginBottom: '1rem' }}>
                ✍ 새 글 작성하기
            </button>

            {myWritings.length === 0 ? (
                <p>작성한 글이 없습니다.</p>
            ) : (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead>
                    <tr>
                        <th style={thStyle}>제목</th>
                        <th style={thStyle}>작성자</th>
                        <th style={thStyle}>작업</th>
                        <th style={thStyle}>출간 여부</th>
                    </tr>
                    </thead>
                    <tbody>
                    {myWritings.map((writing) => (
                        <tr key={writing.bookId}>
                            <td style={tdStyle}>{writing.title}</td>
                            <td style={tdStyle}>{writing.authorName}</td>
                            <td style={tdStyle}>
                                <button onClick={() => navigate(`/write/${writing.bookId}`)}>상세보기</button>{' '}
                                <button onClick={() => navigate(`/edit/${writing.bookId}`)}>수정하기</button>{' '}
                                {!writing.registration && (
                                    <button onClick={() => navigate(`/publish/${writing.bookId}`)}>출간 신청</button>
                                )}
                            </td>
                            <td style={tdStyle}>
                                {writing.registration ? '✅ 출간됨' : '❌ 미출간'}
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

const thStyle = {
    borderBottom: '1px solid #ccc',
    textAlign: 'left',
    padding: '8px',
    background: '#f2f2f2',
};

const tdStyle = {
    borderBottom: '1px solid #eee',
    padding: '8px',
};

export default MyWritingsPage;