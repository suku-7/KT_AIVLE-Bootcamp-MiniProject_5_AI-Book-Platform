// src/components/Footer.jsx

import React from 'react';
import { Box, Typography } from '@mui/material';

const Footer = () => {
  return (
    <Box
      component="footer"
      sx={{
        mt: 'auto', // 메인 콘텐츠가 적을 때도 하단에 붙도록 함
        py: 3,
        px: { xs: 2, sm: 5, md: 10, lg: 35 },
        backgroundColor: '#f0f0f0',
        textAlign: 'center',
        borderTop: '1px solid #ddd',
      }}
    >
      <Typography variant="body2" color="text.secondary">
        &copy; 2025 AI IN 서재. All rights reserved.
      </Typography>
    </Box>
  );
};

export default Footer;
