// =================================================================
// FILENAME: src/pages/PublishPage.jsx (최종 수정)
// 역할: 페이지 전체에 최대 너비를 설정하여, 넓은 화면에서도 레이아웃이 안정적으로 보이도록 수정합니다.
// =================================================================
import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { api } from '../api/apiClient';
import { 
    Box, Button, Typography, Paper, CircularProgress, Stack
} from '@mui/material';

export const PublishPage = () => {
    const { bookId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();

    const [writing, setWriting] = useState(null);
    const [coverImageUrl, setCoverImageUrl] = useState('');
    const [summary, setSummary] = useState('');
    const [isPublishing, setIsPublishing] = useState(false);
    const [isPublished, setIsPublished] = useState(false);

    const pollingRef = useRef(null);

    useEffect(() => {
        if (location.state?.writingData) {
            const initialWriting = location.state.writingData;
            setWriting(initialWriting);
            if (initialWriting.registration) {
                setIsPublished(true);
                fetchAiResults(true);
            }
        } else {
            alert("출간할 글의 정보를 가져오지 못했습니다.");
            navigate(-1);
        }

        return () => {
            if (pollingRef.current) {
                clearInterval(pollingRef.current);
            }
        };
    }, [location, navigate, bookId]);

    const fetchAiResults = async (isInitialFetch = false) => {
        if (!isInitialFetch) setIsPublishing(true);

        try {
            const [coverRes, summaryRes] = await Promise.all([
                api.getCoverDesign(bookId),
                api.getContentAnalyzer(bookId)
            ]);
            
            const newCoverUrl = coverRes.data?.imageUrl;
            const newSummary = summaryRes.data?.summary;

            let coverDone = false;
            let summaryDone = false;

            if (newCoverUrl) {
                setCoverImageUrl(newCoverUrl);
                coverDone = true;
            }
            if (newSummary) {
                setSummary(newSummary);
                summaryDone = true;
            }

            if (coverDone && summaryDone) {
                if (pollingRef.current) {
                    clearInterval(pollingRef.current);
                    pollingRef.current = null;
                }
                setIsPublishing(false);
            }
        } catch (error) {
            console.error("AI 결과물 로딩 실패:", error);
        }
    };

    const handlePublish = async () => {
        if (!writing || isPublished || isPublishing) return;

        setIsPublishing(true);
        setIsPublished(true);

        try {
            await api.registBook(bookId);
            alert("AI가 표지와 요약 생성을 시작했습니다. 결과가 표시될 때까지 잠시만 기다려주세요.");
            
            pollingRef.current = setInterval(() => {
                fetchAiResults();
            }, 5000);

        } catch (error) {
            alert("출간 요청에 실패했습니다.");
            setIsPublishing(false);
            setIsPublished(false);
        }
    };

    if (!writing) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}><CircularProgress /></Box>;
    }

    return (
        // 1. 전체 페이지를 감싸는 컨테이너에 maxWidth와 중앙 정렬을 적용합니다.
        <Box sx={{ maxWidth: '1400px', margin: 'auto', p: { xs: 2, md: 4 } }}>
            <Box sx={{
                display: 'flex',
                flexDirection: { xs: 'column', md: 'row' },
                gap: 4,
            }}>
                {/* 왼쪽 영역 */}
                <Box sx={{ flex: 1 }}>
                    <Stack spacing={4}>
                        <Paper variant="outlined" sx={{ p: 3, backgroundColor: 'white' }}>
                            <Typography variant="h5" component="h2" fontWeight="bold">
                                AI 최종 출간
                            </Typography>
                            <Button 
                                variant="contained"
                                size="large"
                                onClick={handlePublish}
                                disabled={isPublishing || isPublished}
                                sx={{ mt: 2, mb: 1, backgroundColor: '#FFF7BF', color: 'grey.800', '&:hover': { backgroundColor: '#FFEB60' } }}
                            >
                                {isPublishing ? "AI 작업 중..." : (isPublished ? "✅ 출간 완료" : "AI 출간 시작하기")}
                            </Button>
                            <Typography variant="body2" color="text.secondary">
                                버튼을 누르면 AI가 글을 분석하여 표지와 요약을 생성하고 책을 출간합니다.
                            </Typography>
                        </Paper>

                        <Paper variant="outlined" sx={{ p: 3 }}>
                            <Typography variant="subtitle1" color="text.secondary">제목</Typography>
                            <Box sx={{ p: 2, mb: 2, backgroundColor: 'grey.100', borderRadius: 1 }}>
                                <Typography variant="h6" fontWeight="bold">
                                    {writing.title}
                                </Typography>
                            </Box>
                            
                            <Typography variant="subtitle1" color="text.secondary">내용</Typography>
                            <Box sx={{ p: 2, backgroundColor: 'grey.100', borderRadius: 1, whiteSpace: 'pre-wrap', height: '55vh', overflowY: 'auto' }}>
                                <Typography>{writing.context}</Typography>
                            </Box>
                        </Paper>
                    </Stack>
                </Box>

                {/* 오른쪽 영역 */}
                <Box sx={{ flex: 1.3 }}>
                    <Stack spacing={4}>
                        <Paper variant="outlined" sx={{ p: 3 }}>
                            <Typography variant="h6" fontWeight="bold" gutterBottom>AI 생성 표지</Typography>
                            <Box sx={{ mt: 2, height: '30vh', backgroundColor: 'grey.100', borderRadius: 2, display: 'flex', alignItems: 'center', justifyContent: 'center', p: 2 }}>
                                {isPublishing && !coverImageUrl && <CircularProgress />}
                                {coverImageUrl && (
                                    <Box component="img" src={coverImageUrl} alt="AI-generated cover" sx={{ maxWidth: '100%', maxHeight: '100%', objectFit: 'contain', borderRadius: 2 }} />
                                )}
                            </Box>
                        </Paper>

                        <Paper variant="outlined" sx={{ p: 3 }}>
                            <Typography variant="h6" fontWeight="bold" gutterBottom>AI 생성 요약</Typography>
                            <Box sx={{ mt: 2, height: '35vh', p: 3, backgroundColor: 'grey.100', borderRadius: 2, overflowY: 'auto' }}>
                                {isPublishing && !summary && <CircularProgress />}
                                {summary && <Typography>{summary}</Typography>}
                            </Box>
                        </Paper>
                    </Stack>
                </Box>
            </Box>
        </Box>
    );
};
export default PublishPage;
