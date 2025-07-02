// =================================================================
// FILENAME: src/api/apiClient.js
// 역할: 모든 백엔드 API 통신을 담당하는 중앙 클라이언트입니다.
// =================================================================
import axios from "axios";

const apiClient = axios.create({
    baseURL: "https://8088-cherish2pro-thminiprojt-2fbozbr7ku0.ws-us120.gitpod.io",
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

// API 함수들을 객체로 묶어서 export 합니다.
export const api = {

    // --- 로그인 API ---
    loginUser: (loginData) => apiClient.post('/users/login', loginData),
    loginAuthor: (loginData) => apiClient.post('/authors/login', loginData),

    // --- authormanage ---
    joinAuthor: (authorData) => apiClient.post('/authors', authorData),
    modifyAuthor: (authorId, updateData) => apiClient.patch(`/authors/${authorId}`, updateData),
    deleteAuthor: (authorId) => apiClient.delete(`/authors/${authorId}`),
    getAuthor: (authorId) => apiClient.get(`/authors/${authorId}`),
    getAuthors: () => apiClient.get('/authors'),
    approveAuthor: (authorId) => apiClient.put(`/authors/${authorId}/approve`),
    disapproveAuthor: (authorId) => apiClient.put(`/authors/${authorId}/disapprove`),

    // --- writemanage ---
    writeContext: (writingData) => apiClient.post('/writings', writingData),
    modifyContext: (bookId, updateData) => apiClient.put(`/writings/${bookId}`, updateData),
    deleteContext: (bookId) => apiClient.delete(`/writings/${bookId}`),
    registBook: (bookId) => apiClient.put(`/writings/${bookId}/registbook`),
    getWriting: (bookId) => apiClient.get(`/writings/${bookId}`),
    getApprovalAuthor: (authorId) => apiClient.get(`/approvalauthors/${authorId}`),
    getApprovalAuthors: () => apiClient.get('/approvalauthors'),

    // --- ai ---
    requestCoverGeneration: (data) => apiClient.post('/coverDesigns', data),
    getCoverDesign: (bookId) => apiClient.get(`/coverDesigns/${bookId}`),
    requestContentAnalysis: (data) => apiClient.post('/contentAnalyzers', data),
    getContentAnalyzer: (bookId) => apiClient.get(`/contentAnalyzers/${bookId}`),

    // --- subscribemanage ---
    loginUser: (data) => apiClient.post('/users/login', data), // <-- 로그인 API 추가
    registerUser: (userData) => apiClient.post('/users', userData),
    updateUser: (userId, updateData) => apiClient.put(`/users/${userId}`, updateData),
    deleteUser: (userId) => apiClient.delete(`/users/${userId}`),
    subscribeToBookService: (userId) => apiClient.put(`/users/${userId}/subscribetobookservice`),
    cancelSubscribeToBookService: (userId) => apiClient.put(`/users/${userId}/cancelsubscribetobookservice`),
    buyBook: (userId, bookData) => apiClient.put(`/users/${userId}/buybook`, bookData),
    getUser: (userId) => apiClient.get(`/users/${userId}`),
    getUsers: () => apiClient.get('/users'), // 관리자 페이지 등에서 여전히 필요할 수

    // --- point ---
    rechargePoint: (userId, rechargeData) => apiClient.put(`/points/${userId}/pointrecharge`, rechargeData),
    getPoint: (userId) => apiClient.get(`/points/${userId}`),

    // --- libraryplatform ---
    getLibraryInfo: (bookId) => apiClient.get(`/libraryInfos/${bookId}`),
    getLibraryInfos: () => apiClient.get('/libraryInfos'),
};