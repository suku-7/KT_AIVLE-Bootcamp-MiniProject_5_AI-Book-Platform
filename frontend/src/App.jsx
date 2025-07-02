// =================================================================
// FILENAME: src/App.jsx
// 역할: 애플리케이션의 최상위 진입점입니다.
// =================================================================
import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { Header } from './components/Header';
import { Footer } from './components/Footer';
import { Router } from './router/Router';

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
                    <Header />
                    <main style={{ flex: 1, padding: '2rem' }}>
                        <Router />
                    </main>
                    <Footer />
                </div>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;