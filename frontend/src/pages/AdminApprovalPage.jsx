// =================================================================
// FILENAME: src/pages/AdminApprovalPage.jsx (수정)
// 역할: 포트폴리오 URL 표시 방식을 변경하고, 전체적인 UI를 개선합니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { api, extractIdFromHref } from '../api/apiClient';
import {
    Box, Button, Typography, Paper, CircularProgress, Link,
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip
} from '@mui/material';

export const AdminApprovalPage = () => {
    const [authors, setAuthors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchAuthors = async () => {
        try {
            const response = await api.getAuthors();
            if (response.data?._embedded?.authors) {
                setAuthors(response.data._embedded.authors);
            } else {
                setAuthors([]);
            }
        } catch (err) {
            setError('작가 목록을 불러오는 데 실패했습니다.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAuthors();
    }, []);

    const handleApproval = async (authorId, isApproved) => {
        try {
            if (isApproved) {
                await api.approveAuthor(authorId);
                alert(`작가(ID: ${authorId})를 승인했습니다.`);
            } else {
                await api.disapproveAuthor(authorId);
                alert(`작가(ID: ${authorId})의 요청을 처리했습니다.`);
            }
            fetchAuthors();
        } catch (err) {
            alert('작업에 실패했습니다.');
            console.error(err);
        }
    };

    if (loading) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}><CircularProgress /></Box>;
    }
    if (error) {
        return <Typography color="error" sx={{ p: 3 }}>{error}</Typography>;
    }

    return (
        <Box sx={{ p: { xs: 2, md: 4 } }}>
            <Typography variant="h4" component="h2" fontWeight="bold" sx={{ mb: 3 }}>
                작가 등록 관리
            </Typography>
            <Paper variant="outlined" sx={{ backgroundColor: 'white' }}>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ '& th': { fontWeight: 'bold', backgroundColor: 'grey.50' } }}>
                                {/* 1. 컬럼명을 '작가 ID'로 변경하고, 텍스트를 가운데 정렬합니다. */}
                                <TableCell align="center" sx={{ width: '10%' }}>No</TableCell>
                                <TableCell>로그인 ID</TableCell>
                                <TableCell>작가 이름</TableCell>
                                <TableCell sx={{ width: '30%' }}>포트폴리오 URL</TableCell>
                                <TableCell align="center">승인 상태</TableCell>
                                <TableCell align="center">관리</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {authors.length > 0 ? (
                                authors.map(author => {
                                    const authorId = extractIdFromHref(author);
                                    return (
                                        <TableRow key={authorId} hover>
                                            <TableCell align="center">{authorId}</TableCell>
                                            <TableCell>{author.name}</TableCell>
                                            <TableCell>{author.loginId}</TableCell>
                                            <TableCell>
                                                {/* 2. 포트폴리오 URL을 텍스트로 직접 표시하고, 클릭 가능한 링크로 만듭니다. */}
                                                {author.portfolioUrl ? (
                                                    <Tooltip title="새 탭에서 열기">
                                                        <Link 
                                                            href={author.portfolioUrl} 
                                                            target="_blank" 
                                                            rel="noopener noreferrer"
                                                            sx={{ 
                                                                display: 'block', 
                                                                whiteSpace: 'nowrap', 
                                                                overflow: 'hidden', 
                                                                textOverflow: 'ellipsis' 
                                                            }}
                                                        >
                                                            {author.portfolioUrl}
                                                        </Link>
                                                    </Tooltip>
                                                ) : (
                                                    <Typography color="text.secondary" variant="body2">N/A</Typography>
                                                )}
                                            </TableCell>
                                            <TableCell align="center">
                                                {author.isApproved ? '✅ 승인됨' : '🕒 대기중'}
                                            </TableCell>
                                            <TableCell align="center">
                                                {/* 3. 버튼 디자인을 다른 페이지와 통일합니다. */}
                                                <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1 }}>
                                                    {!author.isApproved ? (
                                                        <>
                                                            <Button 
                                                                variant="contained" 
                                                                size="small"
                                                                onClick={() => handleApproval(authorId, true)}
                                                                sx={{ backgroundColor: '#FFF7BF', color: 'grey.800', '&:hover': { backgroundColor: '#FFEB60' } }}
                                                            >
                                                                승인
                                                            </Button>
                                                            <Button variant="outlined" color="warning" size="small" onClick={() => handleApproval(authorId, false)}>
                                                                거절
                                                            </Button>
                                                        </>
                                                    ) : (
                                                        <Button variant="outlined" color="error" size="small" onClick={() => handleApproval(authorId, false)}>
                                                            승인 취소
                                                        </Button>
                                                    )}
                                                </Box>
                                            </TableCell>
                                        </TableRow>
                                    );
                                })
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={6} align="center" sx={{ py: 5 }}>
                                        신청한 작가가 없습니다.
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </Box>
    );
};
export default AdminApprovalPage;
