// =================================================================
// FILENAME: src/components/ProfileEditForm.jsx (수정)
// 역할: 정보 수정 후 데이터를 새로고침하고, KT 인증 스위치 UI를 개선합니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/apiClient';
import { useAuth } from '../contexts/AuthContext'; // 1. useAuth에서 login 함수를 가져옵니다.
import { 
    Box, Button, TextField, Typography, Paper, Alert, 
    Switch
} from '@mui/material';

export const ProfileEditForm = ({ user, userType }) => {
    const { login } = useAuth(); // 2. AuthContext의 login 함수를 사용해 사용자 정보를 업데이트합니다.
    const [formData, setFormData] = useState({
        name: '',
        isKt: false,
        portfolioUrl: ''
    });
    const [isKtVerified, setIsKtVerified] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        if (user) {
            const ktStatus = String(user.isKt) === 'true';
            setFormData({
                name: user.name || '',
                isKt: ktStatus,
                portfolioUrl: user.portfolioUrl || ''
            });
            if (userType === 'user' && ktStatus) {
                setIsKtVerified(true);
            }
        }
    }, [user, userType]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        if (type === 'checkbox') {
            setFormData(prev => ({ ...prev, [name]: checked }));
        } else {
            setFormData(prev => ({ ...prev, [name]: value }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        const dataToUpdate = {
            name: formData.name,
            isKt: String(formData.isKt),
            portfolioUrl: formData.portfolioUrl,
        };
        
        if (userType === 'author') delete dataToUpdate.isKt;
        if (userType === 'user') delete dataToUpdate.portfolioUrl;

        try {
            let updatedUserResponse;
            if (userType === 'user') {
                await api.updateUser(user.userId, dataToUpdate);
                // 3. 사용자 정보 수정 후, 최신 정보를 다시 불러옵니다.
                updatedUserResponse = await api.getUser(user.userId);
            } else {
                await api.modifyAuthor(user.authorId, dataToUpdate);
                // 작가 정보 조회 API가 있다면 여기에 추가합니다. (예시)
                // updatedUserResponse = await api.getAuthor(user.authorId);
                
                // 임시로 현재 사용자 정보에 변경된 이름만 반영합니다.
                updatedUserResponse = { data: { ...user, name: dataToUpdate.name } };
            }
            
            // 4. 전역 상태(AuthContext)를 최신 정보로 업데이트합니다.
            if(updatedUserResponse?.data) {
                login(updatedUserResponse.data, userType);
            }

            setSuccess('회원정보가 성공적으로 수정되었습니다.');
            alert('회원정보가 수정되었습니다.');
            navigate('/main'); 
        } catch (err) {
            console.error(err);
            setError('정보 수정에 실패했습니다. 다시 시도해주세요.');
        }
    };

    const pageTitle = `${userType === 'user' ? '사용자' : '작가'} 정보 수정`;

    return (
        <Paper 
            elevation={4} 
            sx={{ 
                padding: { xs: '2rem', md: '3rem' },
                width: '100%', 
                maxWidth: '600px',
                margin: '4rem auto',
                borderRadius: '12px',
            }}
        >
            <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column' }}>
                <Typography variant="h5" component="h1" fontWeight="bold" sx={{ mb: 3 }}>
                    {pageTitle}
                </Typography>

                {isKtVerified && (
                    <Alert severity="info" sx={{ mb: 2 }}>
                        KT 제휴 회원은 인증 상태를 변경할 수 없습니다.
                    </Alert>
                )}

                <TextField
                    label="이름"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    fullWidth
                    margin="normal"
                />
                
                {userType === 'user' && (
                    // 5. "KT 고객 인증" 텍스트와 스위치가 바로 옆에 붙도록 레이아웃을 수정합니다.
                    <Box sx={{ display: 'flex', alignItems: 'center', mt: 2, mb: 1 }}>
                        <Typography sx={{ mr: 2 }}>KT 고객 인증</Typography>
                        <Switch
                            checked={formData.isKt}
                            onChange={handleChange}
                            name="isKt"
                            disabled={isKtVerified}
                            sx={{
                                '& .MuiSwitch-switchBase.Mui-checked': {
                                    color: '#FFD600',
                                    '&:hover': {
                                        backgroundColor: 'rgba(255, 214, 0, 0.08)',
                                    },
                                },
                                '& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track': {
                                    backgroundColor: '#FFEB60',
                                },
                            }}
                        />
                    </Box>
                )}
                {userType === 'author' && (
                    <TextField 
                        label="포트폴리오 URL" 
                        name="portfolioUrl" 
                        value={formData.portfolioUrl} 
                        onChange={handleChange} 
                        fullWidth 
                        margin="normal" 
                    />
                )}

                {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
                {success && <Alert severity="success" sx={{ mt: 2 }}>{success}</Alert>}

                <Button 
                    type="submit" 
                    variant="contained" 
                    fullWidth
                    sx={{
                        mt: 3,
                        padding: '12px',
                        fontSize: '1rem',
                        fontWeight: 'bold',
                        backgroundColor: '#FFF7BF',
                        color: 'grey.700',
                        '&:hover': { backgroundColor: '#FFEB60' }
                    }}
                >
                    정보 수정 완료
                </Button>
            </Box>
        </Paper>
    );
};
