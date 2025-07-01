// src/components/Header.jsx

import React from 'react';
import { Link } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';

const Header = () => {
  return (
    <AppBar position="static" sx={{ backgroundColor: '#333' }}>
      <Toolbar sx={{ justifyContent: 'space-between', padding: { xs: '0 16px', sm: '0 24px' } }}>
        {/* 로고/앱 이름 */}
        <Typography variant="h6" component={Link} to="/" sx={{ textDecoration: 'none', color: 'white', fontWeight: 'bold' }}>
          AI IN 서재
        </Typography>

        {/* 네비게이션 링크 */}
        <Box sx={{ display: 'flex', gap: { xs: '10px', sm: '20px' } }}>
          <Button color="inherit" component={Link} to="/user-auth">
            사용자
          </Button>
          <Button color="inherit" component={Link} to="/author-auth">
            작가
          </Button>
          <Button color="inherit" component={Link} to="/admin/approvals">
            관리자
          </Button>
          {/* 필요에 따라 더 많은 링크 추가 */}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
