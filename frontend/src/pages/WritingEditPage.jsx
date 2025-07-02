import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api/apiClient';

const EditPage = () => {
    const { bookId } = useParams();
    const [title, setTitle] = useState('');
    const [context, setContext] = useState('');

    useEffect(() => {
        const fetchWriting = async () => {
            const res = await api.getWriting(bookId);
            setTitle(res.data.title);
            setContext(res.data.context);
        };
        fetchWriting();
    }, [bookId]);

    const handleUpdate = async () => {
        try {
            await api.modifyContext(bookId, { title, context });
            alert('수정 성공');
        } catch (err) {
            console.error(err);
            alert('수정 실패');
        }
    };

    return (
        <div>
            <h2>🛠 글 수정</h2>
            <input value={title} onChange={(e) => setTitle(e.target.value)} />
            <textarea value={context} onChange={(e) => setContext(e.target.value)} />
            <button onClick={handleUpdate}>수정</button>
        </div>
    );
};

export default EditPage;