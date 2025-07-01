// src/pages/UserAuthPage.jsx

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthForm from '../components/AuthForm'; // 재사용 가능한 인증 폼 컴포넌트 임포트
import { registerUser } from '../api/bookApi'; // 사용자 회원가입 API 임포트
import { Box, Typography } from '@mui/material'; // MUI 컴포넌트 임포트

const UserAuthPage = () => {
  const [mode, setMode] = useState('signup'); // 'signup' 또는 'login'
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate(); // 페이지 이동을 위한 훅

  /**
   * AuthForm 제출 시 호출될 함수입니다.
   * @param {object} formData - 폼에서 입력된 데이터 (loginId, password, name, isKt)
   */
  const handleSubmit = async (formData) => {
    setIsLoading(true);
    setErrorMessage(''); // 새로운 시도 전에 에러 메시지 초기화

    try {
      if (mode === 'signup') {
        // 사용자 회원가입 API 호출
        const response = await registerUser(formData);
        console.log('User Signup Success:', response.data);
        alert('회원가입이 완료되었습니다! 이제 로그인해주세요.'); // 간단한 알림
        setMode('login'); // 회원가입 성공 시 로그인 모드로 전환
      } else {
        // 로그인 기능은 현재 API에 명시적인 API가 없으므로,
        // 여기서는 간단히 '로그인 성공' 메시지를 표시하고 홈으로 리다이렉트합니다.
        // 실제 구현 시에는 로그인 API를 호출하고 토큰 등을 처리해야 합니다.
        console.log('User Login Attempt:', formData);
        alert('로그인 성공! 홈으로 이동합니다.'); // 간단한 알림
        navigate('/'); // 로그인 성공 시 홈 페이지로 이동
      }
    } catch (error) {
      console.error('Authentication Error:', error);
      // API 응답에서 에러 메시지를 추출하여 표시
      setErrorMessage(error.response?.data?.message || '인증 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Box sx={{ p: { xs: 2, sm: 4 }, maxWidth: 500, mx: 'auto' }}>
      <AuthForm
        mode={mode}
        isAuthor={false} // 사용자 폼이므로 false
        onSubmit={handleSubmit}
        isLoading={isLoading}
        errorMessage={errorMessage}
      />
      <Typography variant="body2" align="center" sx={{ mt: 2 }}>
        {mode === 'signup' ? (
          <>
            이미 계정이 있으신가요?{' '}
            <span
              onClick={() => setMode('login')}
              style={{ cursor: 'pointer', color: '#5a9', textDecoration: 'underline' }}
            >
              로그인
            </span>
          </>
        ) : (
          <>
            계정이 없으신가요?{' '}
            <span
              onClick={() => setMode('signup')}
              style={{ cursor: 'pointer', color: '#5a9', textDecoration: 'underline' }}
            >
              회원가입
            </span>
          </>
        )}
      </Typography>
    </Box>
  );
};

export default UserAuthPage;
