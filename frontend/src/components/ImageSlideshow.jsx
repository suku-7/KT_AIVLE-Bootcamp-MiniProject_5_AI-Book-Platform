// =================================================================
// FILENAME: src/components/ImageSlideshow.jsx (신규 생성)
// 역할: 자동으로 이미지를 전환하는 슬라이드 쇼 배너입니다.
// =================================================================
import React, { useState, useEffect } from 'react';
import { Box } from '@mui/material';

export const ImageSlideshow = ({ images, interval = 7000 }) => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const [nextIndex, setNextIndex] = useState(1);
    const [isFading, setIsFading] = useState(false);

    useEffect(() => {
        if (images.length <= 1) return;

        const timer = setInterval(() => {
            setIsFading(true);
            setTimeout(() => {
                setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
                setNextIndex((prevIndex) => (prevIndex + 2) % images.length);
                setIsFading(false);
            }, 1000); // 1초 동안 페이드 효과
        }, interval);

        return () => clearInterval(timer);
    }, [images, interval]);

    if (!images || images.length === 0) {
        return null;
    }

    return (
        <Box sx={{ 
            position: 'relative', 
            width: '100%', 
            height: '500px', // 슬라이드 쇼 높이
            overflow: 'hidden',
            borderRadius: '16px',
            backgroundColor: 'grey.200'
        }}>
            <Box
                component="img"
                src={images[currentIndex]}
                alt={`Slide ${currentIndex + 1}`}
                sx={{
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover',
                    position: 'absolute',
                    opacity: isFading ? 0 : 1,
                    transition: 'opacity 1s ease-in-out',
                }}
            />
            <Box
                component="img"
                src={images[nextIndex]}
                alt={`Slide ${nextIndex + 1}`}
                sx={{
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover',
                    position: 'absolute',
                    opacity: 0, // 다음 이미지는 숨겨 둡니다.
                }}
            />
        </Box>
    );
};