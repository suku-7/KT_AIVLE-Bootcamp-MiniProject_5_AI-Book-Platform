// src/pages/PublishPage.jsx

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Button, Typography, Paper, CircularProgress, Alert, Card, CardMedia, CardContent } from '@mui/material';
import {
  getWriting,
  requestCoverGeneration,
  getCoverDesign,
  requestContentAnalysis,
  getContentAnalyzer,
  registBook
} from '../api/bookApi'; // 필요한 API 함수 임포트

const PublishPage = () => {
  const { bookId } = useParams(); // URL에서 bookId 파라미터 가져오기
  const navigate = useNavigate(); // 페이지 이동을 위한 훅

  const [writingData, setWritingData] = useState(null); // 글 내용 (AI 요청에 필요)
  const [coverDesign, setCoverDesign] = useState(null); // AI 생성 표지 정보
  const [contentAnalysis, setContentAnalysis] = useState(null); // AI 생성 요약 정보

  const [isLoading, setIsLoading] = useState(true);
  const [isGeneratingCover, setIsGeneratingCover] = useState(false);
  const [isGeneratingSummary, setIsGeneratingSummary] = useState(false);
  const [isPublishing, setIsPublishing] = useState(false);
  const [error, setError] = useState(null);

  // TODO: 실제 앱에서는 로그인한 작가의 authorId와 authorName을 사용해야 합니다.
  const AUTHOR_ID = 1; // 임시 작가 ID
  const AUTHOR_NAME = "김작가"; // 임시 작가 이름

  // 초기 데이터 로딩 (글 내용, 기존 AI 결과)
  useEffect(() => {
    const fetchData = async () => {
      if (!bookId) {
        setError("출간할 책 ID가 필요합니다.");
        setIsLoading(false);
        return;
      }

      setIsLoading(true);
      setError(null);
      try {
        // 1. 글 내용 불러오기 (AI 요청에 필요)
        const writingRes = await getWriting(bookId);
        if (writingRes.data) {
          setWritingData(writingRes.data);
        } else {
          setError("글 내용을 불러오지 못했습니다.");
          setIsLoading(false);
          return;
        }

        // 2. 기존 AI 표지 디자인 불러오기
        const coverRes = await getCoverDesign(bookId);
        if (coverRes.data) {
          setCoverDesign(coverRes.data);
        }

        // 3. 기존 AI 콘텐츠 분석 결과 불러오기
        const contentRes = await getContentAnalyzer(bookId);
        if (contentRes.data) {
          setContentAnalysis(contentRes.data);
        }

      } catch (err) {
        console.error("데이터 로딩 실패:", err);
        setError(err.response?.data?.message || "데이터를 불러오는 데 실패했습니다.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [bookId]);

  // AI 표지 생성 요청
  const handleGenerateCover = async () => {
    if (!writingData || !writingData.title) {
      alert("표지 생성을 위해 책 제목이 필요합니다.");
      return;
    }
    setIsGeneratingCover(true);
    setError(null);
    try {
      const requestData = {
        bookId: parseInt(bookId), // bookId는 숫자여야 함
        authorId: AUTHOR_ID,
        authorName: AUTHOR_NAME,
        title: writingData.title,
        // coverPrompt는 writingData에 직접 포함되어 있지 않으므로,
        // 필요하다면 별도의 입력 필드를 추가하거나, title을 기반으로 프롬프트 생성 로직을 추가해야 합니다.
        // 현재는 title을 프롬프트로 사용하거나, 임시 프롬프트를 사용합니다.
        coverPrompt: `${writingData.title} 책의 표지 이미지` // 임시 프롬프트
      };
      const response = await requestCoverGeneration(requestData);
      if (response.data) {
        setCoverDesign(response.data); // 생성된 표지 URL 포함
        alert('표지 이미지가 성공적으로 생성되었습니다!');
      } else {
        setError("표지 생성에 실패했습니다.");
      }
    } catch (err) {
      console.error("표지 생성 실패:", err);
      setError(err.response?.data?.message || "표지 생성 중 오류가 발생했습니다.");
    } finally {
      setIsGeneratingCover(false);
    }
  };

  // AI 요약 생성 요청
  const handleGenerateSummary = async () => {
    if (!writingData || !writingData.context) {
      alert("요약 생성을 위해 글 내용이 필요합니다.");
      return;
    }
    setIsGeneratingSummary(true);
    setError(null);
    try {
      const requestData = {
        bookId: parseInt(bookId), // bookId는 숫자여야 함
        authorId: AUTHOR_ID,
        authorName: AUTHOR_NAME,
        context: writingData.context,
        maxLength: 500, // 요약 최대 길이
        language: "Korean",
        classificationType: "소설", // 임시 분류
        requestedBy: "작가"
      };
      const response = await requestContentAnalysis(requestData);
      if (response.data) {
        setContentAnalysis(response.data); // 생성된 요약 포함
        alert('글 요약이 성공적으로 생성되었습니다!');
      } else {
        setError("요약 생성에 실패했습니다.");
      }
    } catch (err) {
      console.error("요약 생성 실패:", err);
      setError(err.response?.data?.message || "요약 생성 중 오류가 발생했습니다.");
    } finally {
      setIsGeneratingSummary(false);
    }
  };

  // 책 출간 신청
  const handlePublishBook = async () => {
    if (!window.confirm('이 글을 책으로 출간 신청하시겠습니까?')) {
      return;
    }
    setIsPublishing(true);
    setError(null);
    try {
      await registBook(bookId);
      alert('책 출간 신청이 완료되었습니다!');
      navigate('/'); // 출간 신청 후 홈 페이지로 이동
    } catch (err) {
      console.error("출간 신청 실패:", err);
      setError(err.response?.data?.message || "출간 신청 중 오류가 발생했습니다.");
    } finally {
      setIsPublishing(false);
    }
  };

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px', mt: 5 }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>데이터를 불러오는 중...</Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ my: 2, maxWidth: 800, mx: 'auto' }}>
        {error}
      </Alert>
    );
  }

  if (!writingData) {
    return (
      <Alert severity="info" sx={{ my: 2, maxWidth: 800, mx: 'auto' }}>
        출간할 글 정보를 찾을 수 없습니다.
      </Alert>
    );
  }

  return (
    <Paper elevation={3} sx={{ p: 4, maxWidth: 800, mx: 'auto', mt: 5, borderRadius: 2 }}>
      <Typography variant="h5" component="h1" gutterBottom align="center" fontWeight={600}>
        '{writingData.title}' 출간 준비
      </Typography>
      <Typography variant="body1" color="text.secondary" align="center" mb={3}>
        AI가 생성한 표지와 요약을 확인하고 출간을 신청하세요.
      </Typography>

      {/* AI 표지 디자인 섹션 */}
      <Box sx={{ my: 4, border: '1px dashed #ccc', p: 3, borderRadius: 2 }}>
        <Typography variant="h6" fontWeight={500} mb={2}>AI 표지 디자인</Typography>
        <Box sx={{ display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, alignItems: 'center', gap: 3 }}>
          <Box sx={{ flexShrink: 0, width: 150, height: 230, border: '1px solid #eee', display: 'flex', justifyContent: 'center', alignItems: 'center', overflow: 'hidden', borderRadius: 1 }}>
            {isGeneratingCover ? (
              <CircularProgress size={30} />
            ) : coverDesign?.imageUrl ? (
              <img src={coverDesign.imageUrl} alt="AI 생성 표지" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
            ) : (
              <Typography variant="body2" color="text.secondary">표지 없음</Typography>
            )}
          </Box>
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="body2" color="text.secondary" mb={1}>
              프롬프트: {coverDesign?.coverPrompt || '표지 프롬프트가 없습니다.'} {/* 수정: 끊긴 문자열 완성 */}
            </Typography>
            <Button
              variant="contained"
              onClick={handleGenerateCover}
              disabled={isGeneratingCover || !writingData.title}
              sx={{ backgroundColor: '#5a9', '&:hover': { backgroundColor: '#487' } }}
            >
              {isGeneratingCover ? '표지 생성 중...' : 'AI 표지 생성'}
            </Button>
          </Box>
        </Box>
      </Box>

      {/* AI 콘텐츠 요약 섹션 */}
      <Box sx={{ my: 4, border: '1px dashed #ccc', p: 3, borderRadius: 2 }}>
        <Typography variant="h6" fontWeight={500} mb={2}>AI 글 요약</Typography>
        <Typography variant="body2" mb={2}>
          {isGeneratingSummary ? (
            <Box sx={{ display: 'flex', alignItems: 'center' }}><CircularProgress size={20} sx={{ mr: 1 }} /> 요약 생성 중...</Box>
          ) : contentAnalysis?.summary ? (
            contentAnalysis.summary
          ) : (
            '아직 요약된 내용이 없습니다.'
          )}
        </Typography>
        <Button
          variant="contained"
          onClick={handleGenerateSummary}
          disabled={isGeneratingSummary || !writingData.context}
          sx={{ backgroundColor: '#5a9', '&:hover': { backgroundColor: '#487' } }}
        >
          {isGeneratingSummary ? '요약 생성 중...' : 'AI 글 요약 생성'}
        </Button>
      </Box>

      {/* 출간 신청 버튼 */}
      <Box sx={{ mt: 4, textAlign: 'center' }}>
        <Button
          variant="contained"
          color="primary"
          onClick={handlePublishBook}
          disabled={isPublishing || !writingData} // 글 데이터가 없으면 비활성화
          sx={{ py: 1.5, px: 5, fontSize: '1.1em', backgroundColor: '#3f51b5', '&:hover': { backgroundColor: '#303f9f' } }}
        >
          {isPublishing ? '출간 신청 중...' : '책 출간 신청'}
        </Button>
      </Box>
    </Paper>
  );
};

export default PublishPage;
