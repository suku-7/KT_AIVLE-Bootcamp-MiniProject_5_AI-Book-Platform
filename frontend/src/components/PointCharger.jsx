// =================================================================
// FILENAME: src/components/PointCharger.jsx (수정)
// 역할: 충전 금액 입력 필드에 1,000단위 콤마를 추가하여 가독성을 높입니다.
// =================================================================
import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';
import { Box, Button, TextField, Typography } from '@mui/material';

export const PointCharger = ({ onChargeSuccess }) => {
    const { auth } = useAuth();
    const [amount, setAmount] = useState(0);
    const [loading, setLoading] = useState(false);

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
            // API로 보낼 때는 순수 숫자 값(amount)을 보냅니다.
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
        <Box sx={{ mt: 2, p: 2.5, border: '1px solid', borderColor: 'primary.main', borderRadius: 2 }}>
            <Typography variant="h6" gutterBottom>포인트 충전</Typography>
            <Box sx={{ display: 'flex', gap: 1, mb: 2 }}>
                <Button variant="outlined" onClick={() => addAmount(5000)}>+ 5,000 P</Button>
                <Button variant="outlined" onClick={() => addAmount(10000)}>+ 10,000 P</Button>
                <Button variant="outlined" onClick={() => addAmount(30000)}>+ 30,000 P</Button>
                <Button variant="text" onClick={() => setAmount(0)}>초기화</Button>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                {/* ▼▼▼ 이 TextField 부분이 수정되었습니다. ▼▼▼ */}
                <TextField 
                    type="text" // type을 "text"로 변경하여 콤마가 포함된 문자열을 받을 수 있게 합니다.
                    label="충전할 금액"
                    value={amount.toLocaleString('ko-KR')} // 화면에 표시될 때는 콤마를 추가합니다.
                    onChange={(e) => {
                        // 사용자가 입력한 값에서 콤마 등 숫자 외의 문자를 모두 제거합니다.
                        const numericValue = e.target.value.replace(/[^0-9]/g, '');
                        // 숫자만 남은 값을 상태(state)에 저장합니다.
                        setAmount(Number(numericValue));
                    }}
                    fullWidth
                    variant="outlined"
                    // 모바일 기기에서 숫자 키패드가 나타나도록 돕는 속성입니다.
                    inputProps={{ inputMode: 'numeric' }} 
                />
                {/* ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲ */}
                <Button 
                    variant="contained" 
                    onClick={handleCharge} 
                    disabled={loading} 
                    sx={{ py: 1.5, px: 3, whiteSpace: 'nowrap' }}
                >
                    {loading ? '충전 중...' : '충전하기'}
                </Button>
            </Box>
        </Box>
    );
};
