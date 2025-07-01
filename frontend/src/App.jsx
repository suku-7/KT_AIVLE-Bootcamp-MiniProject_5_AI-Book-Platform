// frontend/src/App.jsx

import React from 'react'; // React 임포트
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'; // Routes와 Route도 필요하지만, Router 컴포넌트가 내부적으로 사용하므로 App.jsx에서는 직접 사용하지 않을 수 있습니다.
import Router from './router/Router'; // 우리가 만든 라우터 컴포넌트 임포트

function App() {
  return (
    <BrowserRouter>
      {/* 공통 헤더 부분 */}
      <header style={{ padding: "12px 24px", background: "#333", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <Link to="/" style={{ color: "#fff", textDecoration: "none", fontWeight: "bold", fontSize: "1.2em" }}>
          AI IN 서재
        </Link>
        {/* 여기에 추가적인 네비게이션 링크를 넣을 수 있습니다. */}
        <nav>
          <ul style={{ listStyle: "none", margin: 0, padding: 0, display: "flex", gap: "20px" }}>
            <li><Link to="/user-auth" style={{ color: "#fff", textDecoration: "none" }}>사용자</Link></li>
            <li><Link to="/author-auth" style={{ color: "#fff", textDecoration: "none" }}>작가</Link></li>
            <li><Link to="/admin/approvals" style={{ color: "#fff", textDecoration: "none" }}>관리자</Link></li>
            {/* 필요에 따라 더 많은 링크 추가 */}
          </ul>
        </nav>
      </header>

      {/* 메인 콘텐츠 영역: Router 컴포넌트가 모든 페이지 라우팅을 처리합니다. */}
      <main style={{ padding: "20px", flexGrow: 1 }}> {/* flexGrow를 추가하여 푸터가 항상 하단에 위치하도록 함 */}
        <Router />
      </main>

      {/* 공통 푸터 부분 (간단한 예시) */}
      <footer style={{ padding: "12px 24px", background: "#f0f0f0", textAlign: "center", borderTop: "1px solid #ddd" }}>
        <p style={{ margin: 0, color: "#555" }}>&copy; 2025 AI IN 서재. All rights reserved.</p>
      </footer>
    </BrowserRouter>
  );
}

export default App;