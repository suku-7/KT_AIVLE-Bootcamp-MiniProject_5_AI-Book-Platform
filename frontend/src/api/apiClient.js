// =================================================================
// FILENAME: src/api/apiClient.js
// 역할: 모든 백엔드 API 통신을 담당하는 중앙 클라이언트입니다.
// =================================================================
import axios from "axios";

const apiClient = axios.create({
    baseURL: "http://localhost:8088",
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

apiClient.interceptors.request.use(
    (config) => {
        const storedAuth = localStorage.getItem('auth');
        if (storedAuth) {
            const token = JSON.parse(storedAuth).user?.token;
            if (token) {
                config.headers['Authorization'] = `Bearer ${token}`;
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// API 함수들을 객체로 묶어서 export 합니다.
export const api = {

    // --- 로그인 API ---
    loginUser: (loginData) => apiClient.post('/users/login', loginData),
    loginAuthor: (loginData) => apiClient.post('/auth/login', loginData),

    // --- authormanage ---
    joinAuthor: (authorData) => apiClient.post('/authors', authorData),
    modifyAuthor: (authorId, updateData) => apiClient.patch(`/authors/${authorId}`, updateData),
    deleteAuthor: (authorId) => apiClient.delete(`/authors/${authorId}`),
    getAuthor: (authorId) => apiClient.get(`/authors/${authorId}`),
    getAuthors: () => apiClient.get('/authors'),
    approveAuthor: (authorId) => apiClient.post(`/authors/${authorId}/approve`),
    disapproveAuthor: (authorId) => apiClient.post(`/authors/${authorId}/disapprove`),

    // --- writemanage ---
    writeContext: (writingData) => apiClient.post('/writings', writingData),
    modifyContext: (bookId, updateData) => apiClient.put(`/writings/${bookId}`, updateData),
    deleteContext: (bookId) => apiClient.delete(`/writings/${bookId}`),
    registBook: (bookId) => apiClient.put(`/writings/${bookId}/registbook`),
    getWriting: (bookId) => apiClient.get(`/writings/${bookId}`),
    getMyWritings: () => apiClient.get(`/writings/my`),
    getApprovalAuthor: (authorId) => apiClient.get(`/approvalauthors/${authorId}`),
    getApprovalAuthors: () => apiClient.get('/approvalauthors'),

    // --- ai ---
    requestCoverGeneration: (data) => apiClient.post('/coverDesigns', data),
    getCoverDesign: (bookId) => apiClient.get(`/coverDesigns/${bookId}`),
    requestContentAnalysis: (data) => apiClient.post('/contentAnalyzers', data),
    getContentAnalyzer: (bookId) => apiClient.get(`/contentAnalyzers/${bookId}`),

    // --- subscribemanage ---
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

export const extractIdFromHref = (object) => {
    const parts = object._links.self.href.split('/');
    return parts[parts.length - 1]; // 마지막 경로 조각이 id
};