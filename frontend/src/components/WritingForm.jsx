// =================================================================
// FILENAME: src/components/WritingForm.jsx (수정)
// 역할: 글 작성/수정 폼의 내부 로직을 개선하여 버그를 해결합니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { Box, TextField, Button, Paper, Typography } from '@mui/material';

export const WritingForm = ({ initialData, onSubmit, isSubmitting, pageTitle, submitButtonText }) => {
    // 1. title과 context를 하나의 state 객체로 통합하여 관리합니다.
    const [formData, setFormData] = useState({ title: '', context: '' });

    // 2. 수정 페이지에서 비동기로 데이터를 받아왔을 때, 폼 데이터를 채워줍니다.
    useEffect(() => {
        if (initialData) {
            setFormData({
                title: initialData.title || '',
                context: initialData.context || ''
            });
        }
    }, [initialData]);

    // 3. 입력 필드가 변경될 때마다 formData state를 업데이트합니다.
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(formData); // 전체 formData 객체를 부모 컴포넌트로 전달합니다.
    };

    return (
        <Box sx={{ p: { xs: 2, md: 4 } }}>
            <Paper component="form" onSubmit={handleSubmit} variant="outlined" sx={{ p: 3, backgroundColor: 'white' }}>
                <Typography variant="h4" component="h1" fontWeight="bold" sx={{ mb: 3 }}>
                    {pageTitle}
                </Typography>
                <TextField
                    label="제목"
                    name="title" // 4. name 속성을 추가합니다.
                    fullWidth
                    required
                    value={formData.title}
                    onChange={handleChange}
                    sx={{ mb: 3 }}
                />
                <TextField
                    label="내용"
                    name="context" // 4. name 속성을 추가합니다.
                    fullWidth
                    required
                    multiline
                    rows={20}
                    value={formData.context}
                    onChange={handleChange}
                    sx={{ mb: 3 }}
                />
                <Button
                    type="submit"
                    variant="contained"
                    disabled={isSubmitting}
                    size="large"
                    sx={{
                        backgroundColor: '#FFF7BF',
                        color: 'grey.800',
                        '&:hover': { backgroundColor: '#FFEB60' }
                    }}
                >
                    {isSubmitting ? '저장 중...' : submitButtonText}
                </Button>
            </Paper>
        </Box>
    );
};
