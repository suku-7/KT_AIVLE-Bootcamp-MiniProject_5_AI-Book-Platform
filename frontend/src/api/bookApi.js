// src/api/bookApi.js

import axios from "axios";

// Axios 인스턴스를 생성하고 기본 URL 및 헤더를 설정합니다.
// baseURL은 API 게이트웨이 주소로 설정되어 개별 서비스로의 라우팅을 담당합니다.
const api = axios.create({
  baseURL: "https://8088-cherish2pro-thminiprojt-2fbozbr7ku0.ws-us120.gitpod.io", // 중요: 개별 서비스(8085)가 아닌 게이트웨이(8088) 주소입니다.
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // ✅ 쿠키를 백엔드로 전달하게 설정하여 세션 관리를 용이하게 합니다.
});

// --- authormanage 바운더리 컨텍스트 API ---

/**
 * 작가 회원가입을 요청합니다.
 * @param {object} authorData - 작가 가입 정보 (name, loginId, password, portfolioUrl)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const joinAuthor = (authorData) => api.post(`/authors`, authorData);

/**
 * 특정 작가의 회원 정보를 수정합니다.
 * @param {number} authorId - 수정할 작가의 ID
 * @param {object} updateData - 업데이트할 작가 정보 (부분 업데이트 가능)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const modifyAuthor = (authorId, updateData) => api.patch(`/authors/${authorId}`, updateData);

/**
 * 특정 작가의 회원 정보를 삭제합니다.
 * @param {number} authorId - 삭제할 작가의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const deleteAuthor = (authorId) => api.delete(`/authors/${authorId}`);

/**
 * 특정 작가의 정보를 조회합니다.
 * @param {number} authorId - 조회할 작가의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getAuthor = (authorId) => api.get(`/authors/${authorId}`);

/**
 * 모든 작가 목록을 조회합니다.
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getAuthors = () => api.get(`/authors`); // <-- 이 함수가 명확히 export 되어 있습니다.

/**
 * 특정 작가의 등록을 승인합니다.
 * @param {number} authorId - 승인할 작가의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const approveAuthor = (authorId) => api.put(`/authors/${authorId}/approve`);

/**
 * 특정 작가의 등록을 거절합니다.
 * @param {number} authorId - 거절할 작가의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const disapproveAuthor = (authorId) => api.put(`/authors/${authorId}/disapprove`);

// --- writemanage 바운더리 컨텍스트 API ---

/**
 * 새로운 글을 작성합니다.
 * @param {object} writingData - 글 작성 정보 (authorId, authorName, title, context)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const writeContext = (writingData) => api.post(`/writings`, writingData);

/**
 * 특정 글의 내용을 수정합니다.
 * @param {number} bookId - 수정할 글의 ID
 * @param {object} updateData - 업데이트할 글 정보 (title, context)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const modifyContext = (bookId, updateData) => api.put(`/writings/${bookId}`, updateData);

/**
 * 특정 글을 삭제합니다.
 * @param {number} bookId - 삭제할 글의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const deleteContext = (bookId) => api.delete(`/writings/${bookId}`);

/**
 * 특정 글을 책으로 등록(출간 신청)합니다.
 * @param {number} bookId - 등록할 글의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const registBook = (bookId) => api.put(`/writings/${bookId}/registbook`);

/**
 * 특정 글의 상세 정보를 조회합니다.
 * @param {number} bookId - 조회할 글의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getWriting = (bookId) => api.get(`/writings/${bookId}`);

/**
 * 특정 작가의 승인 여부 정보를 조회합니다. (ReadModel)
 * @param {number} authorId - 조회할 작가의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getApprovalAuthor = (authorId) => api.get(`/approvalauthors/${authorId}`);

/**
 * 모든 승인된 작가 목록을 조회합니다. (ReadModel)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getApprovalAuthors = () => api.get(`/approvalauthors`);

// --- ai 바운더리 컨텍스트 API ---

/**
 * AI에게 책 표지 이미지 생성을 요청합니다.
 * @param {object} coverRequestData - 표지 생성 요청 정보 (bookId, authorId, title, authorName)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const requestCoverGeneration = (coverRequestData) => api.post(`/coverdesigns`, coverRequestData);

/**
 * 특정 책의 생성된 표지 디자인 정보를 조회합니다.
 * @param {number} bookId - 조회할 책의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getCoverDesign = (bookId) => api.get(`/coverdesigns/${bookId}`);

/**
 * AI에게 콘텐츠 요약 및 분석을 요청합니다.
 * @param {object} contentRequestData - 콘텐츠 분석 요청 정보 (bookId, authorId, context, maxLength, language, classificationType, requestedBy)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const requestContentAnalysis = (contentRequestData) => api.post(`/contentanalyzers`, contentRequestData);

/**
 * 특정 책의 분석된 콘텐츠 정보를 조회합니다.
 * @param {number} bookId - 조회할 책의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getContentAnalyzer = (bookId) => api.get(`/contentanalyzers/${bookId}`);

// --- subscribemanage 바운더리 컨텍스트 API ---

/**
 * 사용자 회원가입을 요청합니다.
 * @param {object} userData - 사용자 가입 정보 (loginId, loginPassword, name, isKt)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const registerUser = (userData) => api.post(`/users`, userData);

/**
 * 특정 사용자의 회원 정보를 수정합니다.
 * @param {number} userId - 수정할 사용자의 ID
 * @param {object} updateData - 업데이트할 사용자 정보 (name, loginPassword 등)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const updateUser = (userId, updateData) => api.put(`/users/${userId}`, updateData);

/**
 * 특정 사용자의 회원 정보를 삭제합니다.
 * @param {number} userId - 삭제할 사용자의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const deleteUser = (userId) => api.delete(`/users/${userId}`);

/**
 * 특정 사용자의 월간 구독 서비스 가입을 요청합니다.
 * @param {number} userId - 구독 가입할 사용자의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const subscribeToBookService = (userId) => api.put(`/users/${userId}/subscribetobookservice`);

/**
 * 특정 사용자의 월간 구독 서비스 취소를 요청합니다.
 * @param {number} userId - 구독 취소할 사용자의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const cancelSubscribeToBookService = (userId) => api.put(`/users/${userId}/cancelsubscribetobookservice`);

/**
 * 특정 사용자가 책을 소장(구매)하도록 요청합니다.
 * @param {number} userId - 책을 구매할 사용자의 ID
 * @param {object} bookData - 구매할 책의 정보 (bookId)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const buyBook = (userId, bookData) => api.put(`/users/${userId}/buybook`, bookData);

/**
 * 특정 사용자의 정보를 조회합니다.
 * @param {number} userId - 조회할 사용자의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getUser = (userId) => api.get(`/users/${userId}`);

// --- point 바운더리 컨텍스트 API ---

/**
 * 특정 사용자의 포인트를 충전합니다.
 * @param {number} userId - 포인트를 충전할 사용자의 ID
 * @param {object} rechargeData - 충전할 금액 정보 (amount)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const rechargePoint = (userId, rechargeData) => api.put(`/points/${userId}/pointrecharge`, rechargeData);

/**
 * 특정 사용자의 포인트 잔액을 조회합니다.
 * @param {number} userId - 조회할 사용자의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getPoint = (userId) => api.get(`/points/${userId}`);

// --- libraryplatform 바운더리 컨텍스트 API ---

/**
 * 특정 도서의 상세 정보를 조회합니다. (ReadModel)
 * @param {number} bookId - 조회할 도서의 ID
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getLibraryInfo = (bookId) => api.get(`/libraryinfos/${bookId}`);

/**
 * 모든 도서 목록을 조회합니다. (ReadModel)
 * @returns {Promise<axios.Response>} API 응답 Promise
 */
export const getLibraryInfos = () => api.get(`/libraryinfos`);
