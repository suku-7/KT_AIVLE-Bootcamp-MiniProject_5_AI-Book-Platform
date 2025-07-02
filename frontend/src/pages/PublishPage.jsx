// =================================================================
// FILENAME: src/pages/PublishPage.jsx
// 역할: 작가가 작성한 글을 AI로 분석하고 최종 출간하는 페이지입니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../api/apiClient';
import { useAuth } from '../contexts/AuthContext';

export const PublishPage = () => {
    const { bookId } = useParams();
    const { auth } = useAuth();
    const navigate = useNavigate();

    const [writing, setWriting] = useState(null);
    const [coverImageUrl, setCoverImageUrl] = useState('');
    const [summary, setSummary] = useState('');
    const [isCoverLoading, setIsCoverLoading] = useState(false);
    const [isSummaryLoading, setIsSummaryLoading] = useState(false);

    // 1. 페이지 로드 시 원고 내용 불러오기
    useEffect(() => {
        const fetchWriting = async () => {
            try {
                const response = await api.getWriting(bookId);
                setWriting(response.data);
            } catch (error) {
                console.error("원고 정보를 불러오는 데 실패했습니다.", error);
                alert("원고 정보를 불러올 수 없습니다.");
            }
        };
        fetchWriting();
    }, [bookId]);

    // 2. AI 표지 생성 요청
    const handleGenerateCover = async () => {
        if (!writing) return;
        setIsCoverLoading(true);
        try {
            // AI 표지 생성을 요청합니다. (백엔드에서 비동기 처리됨)
            await api.requestCoverGeneration({
                bookId: writing.bookId,
                authorId: writing.authorId,
                title: writing.title,
                authorName: writing.authorName
            });
            // 실제로는 Web-Socket이나 반복 폴링으로 완료 여부를 확인해야 하지만,
            // 여기서는 5초 후 생성된 정보를 조회하는 것으로 대체합니다.
            setTimeout(async () => {
                const response = await api.getCoverDesign(writing.bookId);
                setCoverImageUrl(response.data.imageUrl);
                setIsCoverLoading(false);
            }, 5000); // 5초 대기
        } catch (error) {
            alert("AI 표지 생성에 실패했습니다.");
            setIsCoverLoading(false);
        }
    };
    
    // 3. AI 요약 생성 요청
    const handleGenerateSummary = async () => {
        if (!writing) return;
        setIsSummaryLoading(true);
        try {
            await api.requestContentAnalysis({
                bookId: writing.bookId,
                authorId: writing.authorId,
                context: writing.context,
                maxLength: 150, // 150자 요약 요청
            });
            // 표지와 마찬가지로 5초 후 결과를 조회합니다.
            setTimeout(async () => {
                const response = await api.getContentAnalyzer(writing.bookId);
                setSummary(response.data.summary); // ContentAnalyzer에 summary 필드가 있다고 가정
                setIsSummaryLoading(false);
            }, 5000); // 5초 대기
        } catch (error) {
            alert("AI 요약 생성에 실패했습니다.");
            setIsSummaryLoading(false);
        }
    };

    // 4. 최종 출간 등록
    const handlePublish = async () => {
        if (!coverImageUrl || !summary) {
            alert("AI 표지와 요약을 먼저 생성해주세요.");
            return;
        }
        try {
            await api.registBook(bookId);
            alert("책이 성공적으로 출간되었습니다! 메인 화면에서 확인하세요.");
            navigate('/');
        } catch (error) {
            alert("출간에 실패했습니다.");
        }
    };

    if (!writing) return <p>원고 로딩 중...</p>;

    return (
        <div>
            <h2>출간 신청하기</h2>
            <h3>{writing.title}</h3>
            <p style={{ whiteSpace: 'pre-wrap', background: '#f9f9f9', padding: '1rem' }}>{writing.context}</p>
            <hr />
            
            <h4>AI 기능</h4>
            <div style={{ display: 'flex', gap: '2rem' }}>
                <div>
                    <button onClick={handleGenerateCover} disabled={isCoverLoading}>
                        {isCoverLoading ? '표지 생성 중...' : 'AI 표지 생성'}
                    </button>
                    {coverImageUrl && <img src={coverImageUrl} alt="AI-generated cover" style={{ width: '200px', marginTop: '1rem' }} />}
                </div>
                <div>
                    <button onClick={handleGenerateSummary} disabled={isSummaryLoading}>
                        {isSummaryLoading ? '요약 생성 중...' : 'AI 요약 생성'}
                    </button>
                    {summary && <p style={{ marginTop: '1rem', background: '#f0f0f0', padding: '1rem' }}>{summary}</p>}
                </div>
            </div>

            <hr />
            <button onClick={handlePublish} style={{ marginTop: '2rem', padding: '1rem 2rem', fontSize: '1.2rem' }}>최종 출간하기</button>
        </div>
    );
};