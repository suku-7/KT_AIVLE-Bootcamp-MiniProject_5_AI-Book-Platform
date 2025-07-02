// =================================================================
// FILENAME: src/pages/PointChargePage.jsx
// 역할: 사용자가 포인트를 충전하는 페이지입니다.
// =================================================================
import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';

export const PointChargePage = () => {
    const { auth } = useAuth();
    const [amount, setAmount] = useState(0);

    const handleCharge = async () => {
        if (amount <= 0) {
            alert("충전할 금액을 올바르게 입력해주세요.");
            return;
        }
        try {
            await api.rechargePoint(auth.user.userId, { amount });
            alert(`${amount.toLocaleString()} 포인트가 충전되었습니다.`);
            // TODO: 충전 후 사용자 정보(포인트)를 다시 불러와서 화면에 반영하면 좋습니다.
            setAmount(0);
        } catch (error) {
            alert("포인트 충전에 실패했습니다.");
            console.error(error);
        }
    };

    return (
        <div style={{ maxWidth: '500px', margin: 'auto' }}>
            <h2>포인트 충전</h2>
            <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
                <button onClick={() => setAmount(10000)}>10,000P</button>
                <button onClick={() => setAmount(30000)}>30,000P</button>
                <button onClick={() => setAmount(50000)}>50,000P</button>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <input 
                    type="number" 
                    value={amount} 
                    onChange={(e) => setAmount(Number(e.target.value))} 
                    placeholder="충전할 금액 입력"
                    style={{ flex: 1, padding: '0.75rem' }}
                />
                <button onClick={handleCharge} style={{ padding: '0.75rem 1.5rem' }}>충전하기</button>
            </div>
        </div>
    );
};