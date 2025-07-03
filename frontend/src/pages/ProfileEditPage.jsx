// =================================================================
// FILENAME: src/pages/ProfileEditPage.jsx (신규 생성)
// 역할: 회원정보 수정 폼을 감싸고 표시하는 페이지입니다.
// =================================================================
import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { ProfileEditForm } from '../components/ProfileEditForm';
import { Box, Typography } from '@mui/material';

export const ProfileEditPage = () => {
    const { auth } = useAuth();

    if (!auth.user) {
        return (
            <Box sx={{ p: 4 }}>
                <Typography>로그인이 필요합니다.</Typography>
            </Box>
        );
    }

    return (
        <Box>
            <ProfileEditForm user={auth.user} userType={auth.role} />
        </Box>
    );
};

export default ProfileEditPage;