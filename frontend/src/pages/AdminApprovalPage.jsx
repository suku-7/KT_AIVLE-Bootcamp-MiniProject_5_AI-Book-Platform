// src/pages/AdminApprovalPage.jsx

import React, { useState, useEffect } from 'react';
import { Box, Typography, Button, Paper, CircularProgress, Alert, List, ListItem, ListItemText, ListItemSecondaryAction, Divider } from '@mui/material';
import { getAuthors, approveAuthor, disapproveAuthor } from '../api/bookApi'; // API 함수 임포트

const AdminApprovalPage = () => {
  const [authors, setAuthors] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionLoadingId, setActionLoadingId] = useState(null); // 특정 작가에 대한 액션 로딩 상태

  // 작가 목록을 불러오는 함수
  const fetchAuthors = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await getAuthors(); // 모든 작가 목록 조회
      if (response.data && Array.isArray(response.data)) {
        // isApproved 상태를 기준으로 필터링하거나 정렬하여 표시할 수 있습니다.
        // 여기서는 모든 작가를 가져와서 표시합니다.
        setAuthors(response.data);
      } else {
        setAuthors([]);
        setError("작가 목록을 불러오는 데 실패했습니다.");
      }
    } catch (err) {
      console.error("작가 목록 조회 실패:", err);
      setError(err.response?.data?.message || "작가 목록을 불러오는 데 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAuthors();
  }, []);

  // 작가 승인 처리
  const handleApprove = async (authorId) => {
    if (!window.confirm('이 작가를 승인하시겠습니까?')) {
      return;
    }
    setActionLoadingId(authorId);
    setError(null);
    try {
      await approveAuthor(authorId);
      alert('작가가 성공적으로 승인되었습니다.');
      fetchAuthors(); // 목록 새로고침
    } catch (err) {
      console.error("작가 승인 실패:", err);
      setError(err.response?.data?.message || "작가 승인에 실패했습니다.");
    } finally {
      setActionLoadingId(null);
    }
  };

  // 작가 거절 처리
  const handleDisapprove = async (authorId) => {
    if (!window.confirm('이 작가를 거절하시겠습니까?')) {
      return;
    }
    setActionLoadingId(authorId);
    setError(null);
    try {
      await disapproveAuthor(authorId);
      alert('작가가 성공적으로 거절되었습니다.');
      fetchAuthors(); // 목록 새로고침
    } catch (err) {
      console.error("작가 거절 실패:", err);
      setError(err.response?.data?.message || "작가 거절에 실패했습니다.");
    } finally {
      setActionLoadingId(null);
    }
  };

  // 승인이 필요한 작가들만 필터링
  const pendingAuthors = authors.filter(author => !author.isApproved);
  // 승인된 작가들만 필터링
  const approvedAuthors = authors.filter(author => author.isApproved);

  return (
    <Paper elevation={3} sx={{ p: 4, maxWidth: 800, mx: 'auto', mt: 5, borderRadius: 2 }}>
      <Typography variant="h5" component="h1" gutterBottom align="center" fontWeight={600}>
        작가 승인 관리
      </Typography>
      <Typography variant="body1" color="text.secondary" align="center" mb={3}>
        새로운 작가 등록 요청을 검토하고 승인 또는 거절할 수 있습니다.
      </Typography>

      {error && (
        <Alert severity="error" sx={{ my: 2 }}>
          {error}
        </Alert>
      )}

      {isLoading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
          <CircularProgress />
          <Typography sx={{ ml: 2 }}>작가 목록을 불러오는 중...</Typography>
        </Box>
      ) : (
        <>
          {/* 승인 대기 중인 작가 목록 */}
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" fontWeight={500} mb={2}>승인 대기 중인 작가 ({pendingAuthors.length})</Typography>
            {pendingAuthors.length === 0 ? (
              <Alert severity="info">승인 대기 중인 작가가 없습니다.</Alert>
            ) : (
              <List sx={{ border: '1px solid #eee', borderRadius: 1 }}>
                {pendingAuthors.map((author, index) => (
                  <React.Fragment key={author.authorId}>
                    <ListItem>
                      <ListItemText // <-- 수정: ListItemT -> ListItemText
                        primary={author.name}
                        secondary={`ID: ${author.loginId} | 포트폴리오: ${author.portfolioUrl || '없음'}`}
                      />
                      <ListItemSecondaryAction>
                        <Button
                          variant="contained"
                          color="success"
                          size="small"
                          onClick={() => handleApprove(author.authorId)}
                          disabled={actionLoadingId === author.authorId}
                          sx={{ mr: 1 }}
                        >
                          {actionLoadingId === author.authorId ? <CircularProgress size={20} /> : '승인'}
                        </Button>
                        <Button
                          variant="outlined"
                          color="error"
                          size="small"
                          onClick={() => handleDisapprove(author.authorId)}
                          disabled={actionLoadingId === author.authorId}
                        >
                          {actionLoadingId === author.authorId ? <CircularProgress size={20} /> : '거절'}
                        </Button>
                      </ListItemSecondaryAction>
                    </ListItem>
                    {index < pendingAuthors.length - 1 && <Divider component="li" />}
                  </React.Fragment>
                ))}
              </List>
            )}
          </Box>

          {/* 승인된 작가 목록 (선택 사항: 확인용) */}
          <Box>
            <Typography variant="h6" fontWeight={500} mb={2}>승인된 작가 ({approvedAuthors.length})</Typography>
            {approvedAuthors.length === 0 ? (
              <Alert severity="info">승인된 작가가 없습니다.</Alert>
            ) : (
              <List sx={{ border: '1px solid #eee', borderRadius: 1 }}>
                {approvedAuthors.map((author, index) => (
                  <React.Fragment key={author.authorId}>
                    <ListItem>
                      <ListItemText
                        primary={author.name}
                        secondary={`ID: ${author.loginId} | 승인됨`}
                      />
                    </ListItem>
                    {index < approvedAuthors.length - 1 && <Divider component="li" />}
                  </React.Fragment>
                ))}
              </List>
            )}
          </Box>
        </>
      )}
    </Paper>
  );
};

export default AdminApprovalPage;