// =================================================================
// FILENAME: src/pages/AdminApprovalPage.jsx (수정된 코드)
// =================================================================
import React, { useState, useEffect } from 'react';
import {api, extractIdFromHref} from '../api/apiClient';

export const AdminApprovalPage = () => {
    const [authors, setAuthors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchAuthors = async () => {
        try {
            const response = await api.getAuthors();
            // HATEOAS 응답 구조에 맞춰 실제 데이터 추출
            if (response.data && response.data._embedded && response.data._embedded.authors) {
                setAuthors(response.data._embedded.authors);
            } else {
                setAuthors([]); // 데이터가 없는 경우 빈 배열로 설정
            }
        } catch (err) {
            setError('작가 목록을 불러오는 데 실패했습니다.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAuthors();
    }, []);

    const handleApproval = async (authorId, isApproved) => {
        try {
            if (isApproved) {
                await api.approveAuthor(authorId);
                alert(`작가(ID: ${authorId})를 승인했습니다.`);
            } else {
                // 승인 거절과 승인 취소 모두 disapproveAuthor를 호출합니다.
                await api.disapproveAuthor(authorId);
                // 상황에 맞는 다른 메시지를 표시할 수 있습니다.
                alert(`작가(ID: ${authorId})의 요청을 처리했습니다.`);
            }
            // 목록을 새로고침하여 변경사항 반영
            fetchAuthors();
        } catch (err) {
            alert('작업에 실패했습니다.');
            console.error(err);
        }
    };

    if (loading) return <p>로딩 중...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div>
            <h2>작가 등록 관리</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                    <tr style={{ background: '#f2f2f2' }}>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>ID</th>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>이름</th>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>로그인ID</th>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>승인 상태</th>
                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>관리</th>
                    </tr>
                </thead>
                <tbody>
                    {authors.length > 0 ? (
                        authors.map(author => (
                            <tr key={extractIdFromHref(author)}>
                                <td style={{ padding: '8px', border: '1px solid #ddd' }}>{extractIdFromHref(author)}</td>
                                <td style={{ padding: '8px', border: '1px solid #ddd' }}>{author.name}</td>
                                <td style={{ padding: '8px', border: '1px solid #ddd' }}>{author.loginId}</td>
                                <td style={{ padding: '8px', border: '1px solid #ddd' }}>{author.isApproved ? '승인됨' : '대기중'}</td>
                                <td style={{ padding: '8px', border: '1px solid #ddd' }}>
                                    {/* ▼▼▼ 이 부분이 수정되었습니다 ▼▼▼ */}
                                    {!author.isApproved ? (
                                        <>
                                            <button onClick={() => handleApproval(extractIdFromHref(author), true)} style={{ marginRight: '5px' }}>승인</button>
                                            <button onClick={() => handleApproval(author.authorId, false)}>거절</button>
                                        </>
                                    ) : (
                                        <button onClick={() => handleApproval(extractIdFromHref(author), false)}>승인 취소</button>
                                    )}
                                    {/* ▲▲▲ 이 부분이 수정되었습니다 ▲▲▲ */}
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="5" style={{ padding: '8px', border: '1px solid #ddd', textAlign: 'center' }}>신청한 작가가 없습니다.</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
};