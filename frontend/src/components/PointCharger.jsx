// =================================================================
// FILENAME: src/components/PointCharger.jsx (수정)
// 역할: '초기화' 버튼을 제거하고, 모든 버튼의 스타일을 노란색 계열로 통일합니다.
// =================================================================
import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';
import { Box, Button, TextField, Typography } from '@mui/material';

export const PointCharger = ({ onChargeSuccess }) => {
    const { auth } = useAuth();
    const [amount, setAmount] = useState(0);
    const [loading, setLoading] = useState(false);

    // 버튼에 적용할 공통 스타일을 정의합니다.
    const chargeButtonStyle = {
        backgroundColor: '#FFF7BF',
        color: 'grey.800',
        boxShadow: 'none',
        '&:hover': {
            backgroundColor: '#FFEB60',
        }
    };

    const handleCharge = async () => {
        if (!auth.user) {
            alert("로그인이 필요합니다.");
            return;
        }
        if (amount <= 0) {
            alert("충전할 금액을 올바르게 입력해주세요.");
            return;
        }
        setLoading(true);
        try {
            const response = await api.rechargePoint(auth.user.userId, { amount });
            alert(`${amount.toLocaleString()} 포인트가 충전되었습니다.`);
            
            if (onChargeSuccess) {
                onChargeSuccess(response.data);
            }
            setAmount(0);
        } catch (error) {
            alert("포인트 충전에 실패했습니다.");
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const addAmount = (value) => {
        setAmount(prevAmount => prevAmount + value);
    };

    return (
        <Box>
            <Typography variant="h6" fontWeight="bold" gutterBottom>포인트 충전</Typography>
            <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
                <Button variant="contained" onClick={() => addAmount(5000)} sx={chargeButtonStyle}>+ 5,000 P</Button>
                <Button variant="contained" onClick={() => addAmount(10000)} sx={chargeButtonStyle}>+ 10,000 P</Button>
                <Button variant="contained" onClick={() => addAmount(30000)} sx={chargeButtonStyle}>+ 30,000 P</Button>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <TextField 
                    type="text"
                    label="충전할 금액"
                    value={amount.toLocaleString('ko-KR')}
                    onChange={(e) => {
                        const numericValue = e.target.value.replace(/[^0-9]/g, '');
                        setAmount(Number(numericValue));
                    }}
                    fullWidth
                    variant="outlined"
                    inputProps={{ inputMode: 'numeric' }} 
                />
                <Button 
                    variant="contained" 
                    onClick={handleCharge} 
                    disabled={loading} 
                    sx={{ ...chargeButtonStyle, py: 1.5, px: 3, whiteSpace: 'nowrap' }}
                >
                    {loading ? '충전 중...' : '충전하기'}
                </Button>
            </Box>
        </Box>
    );
};
