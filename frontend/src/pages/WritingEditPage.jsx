// =================================================================
// FILENAME: src/pages/WritingEditPage.jsx (수정)
// 역할: 목록에서 전달받은 데이터로 폼을 채워 API 호출 에러를 해결합니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { api } from '../api/apiClient';
import { WritingForm } from '../components/WritingForm';
import { Box, CircularProgress } from '@mui/material';

export const WritingEditPage = () => {
    const { bookId } = useParams();
    const navigate = useNavigate();
    const location = useLocation(); // 페이지 이동 시 전달된 state를 읽기 위해 사용

    const [initialData, setInitialData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        // 1. 목록 페이지에서 전달해 준 데이터(location.state.writingData)가 있는지 확인합니다.
        if (location.state?.writingData) {
            // 데이터가 있으면, 그 데이터를 초기값으로 설정하고 로딩을 끝냅니다.
            setInitialData(location.state.writingData);
            setLoading(false);
        } else {
            // 데이터가 없으면 (예: URL로 직접 접근), 기존처럼 API를 호출합니다.
            const fetchWriting = async () => {
                try {
                    const res = await api.getWriting(bookId);
                    setInitialData({ title: res.data.title, context: res.data.context });
                } catch (error) {
                    console.error('글 불러오기 실패:', error);
                    alert('글 정보를 불러오는 데 실패했습니다.');
                } finally {
                    setLoading(false);
                }
            };
            fetchWriting();
        }
    }, [bookId, location.state]);

    const handleUpdate = async (formData) => {
        setIsSubmitting(true);
        try {
            // 백엔드가 기대하는 필드명('newContext')에 맞게 데이터를 보냅니다.
            await api.modifyContext(bookId, { 
                newTitle: formData.title, 
                newContext: formData.context 
            });
            alert('글이 성공적으로 수정되었습니다.');
            navigate('/write/my');
        } catch (err) {
            console.error('수정 실패:', err);
            alert('수정 실패');
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <WritingForm
            initialData={initialData}
            onSubmit={handleUpdate}
            isSubmitting={isSubmitting}
            pageTitle="글 수정하기"
            submitButtonText="수정 완료"
        />
    );
};
export default WritingEditPage;
