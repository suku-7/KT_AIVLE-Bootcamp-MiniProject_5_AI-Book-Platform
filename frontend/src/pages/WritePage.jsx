// =================================================================
// FILENAME: src/pages/WritePage.jsx
// 역할: 승인된 작가가 글을 작성하고 저장하는 페이지입니다.
// =================================================================
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';

export const WritePage = () => {
    const { auth } = useAuth();
    const [title, setTitle] = useState('');
    const [context, setContext] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!auth.user) {
            alert("로그인이 필요합니다.");
            return;
        }

        try {
            const writingData = {
                authorId: auth.user.authorId, // 로그인된 작가 정보 사용
                authorName: auth.user.name,
                title,
                context
            };
            const response = await api.writeContext(writingData);
            alert('글이 성공적으로 저장되었습니다.');
            // 저장 후 출간 신청 페이지로 이동
            navigate(`/publish/${response.data.bookId}`);
        } catch (error) {
            alert('글 저장에 실패했습니다.');
            console.error(error);
        }
    };

    return (
        <div>
            <h2>글 작성하기</h2>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div>
                    <label htmlFor="title">제목</label>
                    <input id="title" type="text" value={title} onChange={(e) => setTitle(e.target.value)} required style={{ width: '100%', padding: '0.5rem', boxSizing: 'border-box' }} />
                </div>
                <div>
                    <label htmlFor="context">내용</label>
                    <textarea id="context" value={context} onChange={(e) => setContext(e.target.value)} required style={{ width: '100%', height: '400px', padding: '0.5rem', boxSizing: 'border-box', resize: 'vertical' }} />
                </div>
                <button type="submit" style={{ padding: '0.75rem', cursor: 'pointer' }}>저장하고 출간 신청하기</button>
            </form>
        </div>
    );
};