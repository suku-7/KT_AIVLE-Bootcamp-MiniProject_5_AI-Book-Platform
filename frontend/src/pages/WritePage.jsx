// =================================================================
// FILENAME: src/pages/WritePage.jsx (수정)
// =================================================================
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../api/apiClient';
import { WritingForm } from '../components/WritingForm';

export const WritePage = () => {
    const { auth } = useAuth();
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);

    // onSubmit으로 formData 객체를 받도록 수정합니다.
    const handleSave = async (formData) => {
        if (!auth.user) {
            alert("로그인이 필요합니다.");
            return;
        }
        setIsSubmitting(true);
        try {
            const writingData = {
                authorId: auth.user.authorId,
                authorName: auth.user.name,
                title: formData.title,
                context: formData.context
            };
            await api.writeContext(writingData);
            alert('글이 성공적으로 저장되었습니다.');
            navigate('/write/my');
        } catch (error) {
            alert('글 저장에 실패했습니다.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <WritingForm
            onSubmit={handleSave}
            isSubmitting={isSubmitting}
            pageTitle="새 글 작성하기"
            submitButtonText="저장하기"
        />
    );
};
export default WritePage;