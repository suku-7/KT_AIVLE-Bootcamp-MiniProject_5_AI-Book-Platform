// src/pages/WritePage.jsx

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, TextField, Button, Typography, Paper, CircularProgress, Alert } from '@mui/material';
import { writeContext, modifyContext, getWriting, deleteContext } from '../api/bookApi'; // API 함수 임포트

const WritePage = () => {
  const { bookId } = useParams(); // URL에서 bookId 파라미터 가져오기 (수정 모드일 경우)
  const navigate = useNavigate(); // 페이지 이동을 위한 훅

  const [title, setTitle] = useState('');
  const [context, setContext] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false); // 수정 모드인지 여부

  // TODO: 실제 앱에서는 로그인한 작가의 authorId와 authorName을 사용해야 합니다.
  // 현재는 임시로 하드코딩하거나, 폼에서 입력받는 방식으로 처리할 수 있습니다.
  const AUTHOR_ID = 1; // 임시 작가 ID
  const AUTHOR_NAME = "김작가"; // 임시 작가 이름

  useEffect(() => {
    // bookId가 존재하면 수정 모드로 진입하고 기존 글을 불러옵니다.
    if (bookId) {
      setIsEditing(true);
      const fetchWriting = async () => {
        setIsLoading(true);
        setError(null);
        try {
          const response = await getWriting(bookId);
          if (response.data) {
            setTitle(response.data.title || '');
            setContext(response.data.context || '');
          } else {
            setError("글 정보를 불러오지 못했습니다.");
          }
        } catch (err) {
          console.error("글 조회 실패:", err);
          setError("글 정보를 불러오는 데 실패했습니다.");
        } finally {
          setIsLoading(false);
        }
      };
      fetchWriting();
    } else {
      setIsEditing(false);
      setTitle('');
      setContext('');
    }
  }, [bookId]); // bookId가 변경될 때마다 useEffect 실행

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsLoading(true);
    setError(null);

    const writingData = {
      authorId: AUTHOR_ID, // 임시 작가 ID 사용
      authorName: AUTHOR_NAME, // 임시 작가 이름 사용
      title,
      context,
    };

    try {
      if (isEditing) {
        // 글 수정
        await modifyContext(bookId, writingData);
        alert('글이 성공적으로 수정되었습니다.');
      } else {
        // 글 작성
        await writeContext(writingData);
        alert('글이 성공적으로 작성되었습니다.');
      }
      navigate('/'); // 성공 후 홈 페이지로 이동 또는 글 목록 페이지로 이동
    } catch (err) {
      console.error('글 저장 실패:', err);
      setError(err.response?.data?.message || '글을 저장하는 데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('정말로 이 글을 삭제하시겠습니까?')) {
      return;
    }
    setIsLoading(true);
    setError(null);
    try {
      await deleteContext(bookId);
      alert('글이 성공적으로 삭제되었습니다.');
      navigate('/'); // 삭제 후 홈 페이지로 이동
    } catch (err) {
      console.error('글 삭제 실패:', err);
      setError(err.response?.data?.message || '글을 삭제하는 데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading && isEditing && !title && !context) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
        <CircularProgress />
        <Typography sx={{ ml: 2 }}>글 정보를 불러오는 중...</Typography>
      </Box>
    );
  }

  return (
    <Paper elevation={3} sx={{ p: 4, maxWidth: 800, mx: 'auto', mt: 5, borderRadius: 2 }}>
      <Typography variant="h5" component="h1" gutterBottom align="center" fontWeight={600}>
        {isEditing ? '글 수정' : '새 글 작성'}
      </Typography>

      {error && (
        <Alert severity="error" sx={{ my: 2 }}>
          {error}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
        <TextField
          label="제목"
          variant="outlined"
          fullWidth
          margin="normal"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <TextField
          label="내용"
          variant="outlined"
          fullWidth
          margin="normal"
          multiline
          rows={10}
          value={context}
          onChange={(e) => setContext(e.target.value)}
          required
        />

        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 3, gap: 2 }}>
          <Button
            type="submit"
            variant="contained"
            sx={{ py: 1.5, backgroundColor: '#5a9', '&:hover': { backgroundColor: '#487' }, flexGrow: 1 }}
            disabled={isLoading}
          >
            {isLoading ? '저장 중...' : (isEditing ? '글 수정' : '글 저장')}
          </Button>
          {isEditing && (
            <Button
              variant="outlined"
              color="error"
              onClick={handleDelete}
              sx={{ py: 1.5, flexGrow: 1 }}
              disabled={isLoading}
            >
              {isLoading ? '삭제 중...' : '글 삭제'}
            </Button>
          )}
        </Box>
        {isEditing && (
          <Button
            variant="text"
            onClick={() => navigate(`/publish/${bookId}`)}
            sx={{ mt: 2, width: '100%' }}
            disabled={isLoading}
          >
            출간 신청 및 AI 표지/요약 미리보기
          </Button>
        )}
      </Box>
    </Paper>
  );
};

export default WritePage;
