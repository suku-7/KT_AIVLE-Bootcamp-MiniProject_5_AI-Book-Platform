// src/components/AuthForm.jsx

import React, { useState } from 'react';
import { Box, TextField, Button, Typography, Paper } from '@mui/material';

/**
 * 사용자/작가 인증을 위한 재사용 가능한 폼 컴포넌트입니다.
 * @param {object} props
 * @param {'login' | 'signup'} props.mode - 폼의 모드 (로그인 또는 회원가입)
 * @param {boolean} props.isAuthor - 작가 인증 폼인지 여부
 * @param {function(object): void} props.onSubmit - 폼 제출 시 호출될 콜백 함수
 * @param {boolean} [props.isLoading=false] - API 호출 중 로딩 상태인지 여부
 * @param {string} [props.errorMessage=''] - API 호출 실패 시 표시할 에러 메시지
 */
const AuthForm = ({ mode, isAuthor, onSubmit, isLoading = false, errorMessage = '' }) => {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [portfolioUrl, setPortfolioUrl] = useState(''); // 작가 전용
  const [isKt, setIsKt] = useState('false'); // 사용자 전용 (기본값 false)

  const handleSubmit = (event) => {
    event.preventDefault();
    const formData = { loginId, password };

    if (mode === 'signup') {
      formData.name = name;
      if (isAuthor) {
        formData.portfolioUrl = portfolioUrl;
      } else {
        formData.isKt = isKt; // 사용자 회원가입 시 KT 여부
      }
    }
    onSubmit(formData);
  };

  const title = mode === 'login' ? '로그인' : '회원가입';
  const submitButtonText = mode === 'login' ? '로그인' : '가입하기';

  return (
    <Paper elevation={3} sx={{ p: 4, maxWidth: 400, mx: 'auto', mt: 5, borderRadius: 2 }}>
      <Typography variant="h5" component="h1" gutterBottom align="center" fontWeight={600}>
        {isAuthor ? '작가' : '사용자'} {title}
      </Typography>
      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
        <TextField
          label="아이디"
          variant="outlined"
          fullWidth
          margin="normal"
          value={loginId}
          onChange={(e) => setLoginId(e.target.value)}
          required
        />
        <TextField
          label="비밀번호"
          variant="outlined"
          fullWidth
          margin="normal"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        {mode === 'signup' && (
          <>
            <TextField
              label="이름"
              variant="outlined"
              fullWidth
              margin="normal"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            {isAuthor ? (
              <TextField
                label="포트폴리오 URL (선택 사항)"
                variant="outlined"
                fullWidth
                margin="normal"
                value={portfolioUrl}
                onChange={(e) => setPortfolioUrl(e.target.value)}
              />
            ) : (
              <TextField
                label="KT 고객 여부 (true/false)"
                variant="outlined"
                fullWidth
                margin="normal"
                value={isKt}
                onChange={(e) => setIsKt(e.target.value)}
                helperText="true 또는 false로 입력해주세요."
              />
            )}
          </>
        )}

        {errorMessage && (
          <Typography color="error" sx={{ mt: 2, textAlign: 'center' }}>
            {errorMessage}
          </Typography>
        )}

        <Button
          type="submit"
          variant="contained"
          fullWidth
          sx={{ mt: 3, py: 1.5, backgroundColor: '#5a9', '&:hover': { backgroundColor: '#487' } }}
          disabled={isLoading}
        >
          {isLoading ? '처리 중...' : submitButtonText}
        </Button>
      </Box>
    </Paper>
  );
};

export default AuthForm;
