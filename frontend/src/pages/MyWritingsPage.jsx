// =================================================================
// FILENAME: src/pages/MyWritingsPage.jsx (수정)
// 역할: '작업' 컬럼의 버튼 순서를 '출간', '수정', '삭제' 순으로 변경합니다.
// =================================================================
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/apiClient';
import { useAuth } from '../contexts/AuthContext';
import { 
    Box, Button, Typography, Paper, CircularProgress,
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import AutoFixHighIcon from '@mui/icons-material/AutoFixHigh'; // 출간 아이콘

export const MyWritingsPage = () => {
    const navigate = useNavigate();
    const { auth } = useAuth();
    const [myWritings, setMyWritings] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchMyWritings = async () => {
        if (!auth.user?.token) {
            setLoading(false);
            return;
        }
        try {
            setLoading(true);
            const response = await api.getMyWritings();
            const writings = response.data?._embedded?.writings || response.data || [];
            setMyWritings(writings);
        } catch (error) {
            console.error('내 글 가져오기 실패:', error);
        } finally {
            setLoading(false);
        }
    };
    
    useEffect(() => {
        fetchMyWritings();
    }, [auth]);

    const handleDelete = async (bookId) => {
        if (window.confirm(`Book ID: ${bookId} 글을 정말로 삭제하시겠습니까?`)) {
            try {
                await api.deleteContext(bookId);
                alert('글이 성공적으로 삭제되었습니다.');
                fetchMyWritings();
            } catch (error) {
                console.error('글 삭제 실패:', error);
                alert('글 삭제에 실패했습니다.');
            }
        }
    };

    if (loading) {
        return <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}><CircularProgress /></Box>;
    }

    return (
        <Box sx={{ p: { xs: 2, md: 4 } }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" component="h2" fontWeight="bold">작가 서재</Typography>
                <Button 
                    variant="contained" 
                    startIcon={<AddIcon />} 
                    onClick={() => navigate('/write')}
                    sx={{ backgroundColor: '#FFF7BF', color: 'grey.800', '&:hover': { backgroundColor: '#FFEB60' } }}
                >
                    새 글 작성하기
                </Button>
            </Box>

            <Paper variant="outlined" sx={{ backgroundColor: 'white' }}>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ '& th': { fontWeight: 'bold', backgroundColor: 'grey.50' } }}>
                                <TableCell sx={{ width: '5%' }}>No.</TableCell>
                                <TableCell sx={{ width: '25%' }}>제목</TableCell>
                                <TableCell>내용 (미리보기)</TableCell>
                                <TableCell align="center" sx={{ width: '10%' }}>출간 여부</TableCell>
                                <TableCell align="center" sx={{ width: '20%' }}>작업</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {myWritings.length > 0 ? (
                                myWritings.map((writing, index) => (
                                    <TableRow key={writing.bookId || index} hover>
                                        <TableCell>{index + 1}</TableCell>
                                        <TableCell component="th" scope="row">{writing.title}</TableCell>
                                        <TableCell>
                                            <Typography variant="body2" color="text.secondary" sx={{ display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                                                {writing.context}
                                            </Typography>
                                        </TableCell>
                                        <TableCell align="center">
                                            {writing.registration ? '✅ 출간됨' : '❌ 미출간'}
                                        </TableCell>
                                        <TableCell align="center">
                                            {/* ▼▼▼ 이 Box 안의 버튼 순서를 수정했습니다. ▼▼▼ */}
                                            <Box sx={{ display: 'flex', justifyContent: 'center', gap: 1 }}>
                                                {!writing.registration && (
                                                    <Tooltip title="AI로 표지/요약을 생성하고 출간합니다.">
                                                        <Button 
                                                            variant="contained"
                                                            size="small"
                                                            startIcon={<AutoFixHighIcon />}
                                                            onClick={() => navigate(`/publish/${writing.bookId}`, { state: { writingData: writing } })}
                                                            sx={{ backgroundColor: '#FFF7BF', color: 'grey.800', '&:hover': { backgroundColor: '#FFEB60' } }}
                                                        >
                                                            출간
                                                        </Button>
                                                    </Tooltip>
                                                )}
                                                <Button size="small" onClick={() => navigate(`/edit/${writing.bookId}`, { state: { writingData: writing } })}>수정</Button>
                                                <Button size="small" color="error" onClick={() => handleDelete(writing.bookId)}>삭제</Button>
                                            </Box>
                                        </TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={5} align="center" sx={{ py: 5 }}>
                                        작성한 글이 없습니다. '새 글 작성하기' 버튼을 눌러 첫 글을 시작해보세요!
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
export default MyWritingsPage;
