// frontend/src/App.jsx

import React from 'react';
import { BrowserRouter, Link } from 'react-router-dom';
import Router from './router/Router'; // 우리가 만든 라우터 컴포넌트 임포트
import Header from './components/Header'; // 공통 헤더 임포트
import Footer from './components/Footer'; // 공통 푸터 임포트
import { Box } from '@mui/material'; // MUI Box 컴포넌트 임포트

function App() {
  return (
    <BrowserRouter>
      {/* Box를 사용하여 전체 레이아웃 컨테이너를 만듭니다. */}
      {/* minHeight: '100vh'로 최소 높이를 뷰포트 높이로 설정하여 푸터가 항상 하단에 붙도록 합니다. */}
      <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
        {/* 공통 헤더 컴포넌트 */}
        <Header />

        {/* 메인 콘텐츠 영역: Router 컴포넌트가 모든 페이지 라우팅을 처리합니다. */}
        {/* flexGrow: 1을 주어 남은 공간을 모두 차지하게 하여 푸터를 하단으로 밀어냅니다. */}
        <Box component="main" sx={{ flexGrow: 1, padding: '20px' }}>
          <Router />
        </Box>

        {/* 공통 푸터 컴포넌트 */}
        <Footer />
      </Box>
    </BrowserRouter>
  );
}

export default App;
