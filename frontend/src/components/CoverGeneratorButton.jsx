// src/components/CoverGeneratorButton.jsx

import React, { useState } from 'react';
import { Button, CircularProgress, Box, Typography } from '@mui/material';
import { requestCoverGeneration } from '../api/bookApi'; // AI 표지 생성 API 임포트

/**
 * AI 표지 생성을 요청하는 버튼 컴포넌트입니다.
 * @param {object} props
 * @param {string} props.promptValue - AI에게 전달할 표지 생성 프롬프트 (책 제목 등)
 * @param {function(string): void} props.setCoverUrl - 생성된 이미지 URL을 부모 컴포넌트로 전달하는 콜백 함수
 * @param {number} props.bookId - 표지 생성 대상이 되는 책의 ID
 * @param {number} props.authorId - 표지 생성 요청을 하는 작가의 ID
 * @param {string} props.authorName - 표지 생성 요청을 하는 작가의 이름
 */
const CoverGeneratorButton = ({ promptValue, setCoverUrl, bookId, authorId, authorName }) => {
  const [isGenerating, setIsGenerating] = useState(false);
  const [error, setError] = useState(null);

  const handleGenerateClick = async () => {
    if (!promptValue) {
      alert('표지 생성을 위한 프롬프트(예: 책 제목)를 입력해주세요.');
      return;
    }
    if (!bookId || !authorId || !authorName) {
      alert('책 정보(ID)와 작가 정보가 필요합니다.');
      return;
    }

    setIsGenerating(true);
    setError(null);
    try {
      const response = await requestCoverGeneration({
        bookId: parseInt(bookId), // bookId는 숫자 타입이어야 함
        authorId: authorId,
        authorName: authorName,
        title: promptValue, // 프롬프트로 책 제목 사용
        // coverPrompt 필드를 백엔드에서 어떻게 처리하는지에 따라 여기를 조정해야 할 수 있습니다.
        // 현재 API 정의상 title이 프롬프트 역할을 할 수 있습니다.
      });

      if (response.data && response.data.imageUrl) {
        setCoverUrl(response.data.imageUrl); // 생성된 이미지 URL 전달
        alert('AI 표지 이미지가 성공적으로 생성되었습니다!');
      } else {
        setError('표지 생성에 실패했습니다: 유효한 URL을 받지 못했습니다.');
      }
    } catch (err) {
      console.error('AI 표지 생성 오류:', err);
      setError(err.response?.data?.message || 'AI 표지 생성 중 오류가 발생했습니다.');
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <Box>
      <Button
        variant="contained"
        onClick={handleGenerateClick}
        disabled={isGenerating || !promptValue || !bookId}
        sx={{
          backgroundColor: '#4CAF50', // 초록색 계열
          '&:hover': { backgroundColor: '#45a049' },
          py: 1,
          px: 2,
          fontSize: '0.9em',
        }}
      >
        {isGenerating ? (
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <CircularProgress size={20} color="inherit" sx={{ mr: 1 }} />
            생성 중...
          </Box>
        ) : (
          'AI 표지 생성 요청'
        )}
      </Button>
      {error && (
        <Typography variant="body2" color="error" sx={{ mt: 1 }}>
          {error}
        </Typography>
      )}
    </Box>
  );
};

export default CoverGeneratorButton;
