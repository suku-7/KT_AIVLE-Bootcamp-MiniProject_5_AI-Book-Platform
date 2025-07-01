import { useState } from "react";
import CoverGeneratorButton from "./CoverGeneratorButton";

function BookForm({ initialValues = {}, onSubmit }) {
  const [title, setTitle] = useState(initialValues.title || "");
  const [author, setAuthor] = useState(initialValues.author || "");
  const [coverPrompt, setCoverPrompt] = useState(initialValues.cover_prompt || "");
  const [coverUrl, setCoverUrl] = useState(initialValues.cover_url || "");

  const handleSubmit = (e) => {
    e.preventDefault();
    const newBook = {
      title,
      author,
      cover_prompt: coverPrompt,
      cover_url: coverUrl,
    };
    onSubmit(newBook);
  };

  return (
    <form
      onSubmit={handleSubmit}
      style={{
        maxWidth: "600px",
        margin: "0 auto",
        fontFamily: "sans-serif",
        padding: "0 10px",
      }}
    >
      {/* 제목 */}
      <div style={{ marginBottom: "30px" }}>
        <label style={{ fontWeight: "bold", display: "block", marginBottom: "5px" }}>
          1. 작품 제목을 입력해주세요<span style={{ color: "red" }}> *</span>
        </label>
        <input
          name="title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="예: 클라라와 태양"
          maxLength={20}
          style={{
            width: "100%",
            padding: "10px",
            fontSize: "15px",
            borderRadius: "6px",
            border: "1px solid #ccc",
          }}
        />
        <div style={{ textAlign: "right", fontSize: "12px", color: "#888" }}>
          {title.length}/20
        </div>
      </div>

      {/* 작가 */}
      <div style={{ marginBottom: "30px" }}>
        <label style={{ fontWeight: "bold", display: "block", marginBottom: "5px" }}>
          2. 작가 이름을 입력해주세요
        </label>
        <input
          name="author"
          value={author}
          onChange={(e) => setAuthor(e.target.value)}
          placeholder="예: 가즈오 이시구로"
          maxLength={30}
          style={{
            width: "100%",
            padding: "10px",
            fontSize: "15px",
            borderRadius: "6px",
            border: "1px solid #ccc",
          }}
        />
        <div style={{ textAlign: "right", fontSize: "12px", color: "#888" }}>
          {author.length}/30
        </div>
      </div>

      {/* 프롬프트 */}
      <div style={{ marginBottom: "20px" }}>
        <label style={{ fontWeight: "bold", display: "block", marginBottom: "5px" }}>
          3. 표지에 대한 프롬프트를 입력해주세요
        </label>
        <textarea
          name="cover_prompt"
          value={coverPrompt}
          onChange={(e) => setCoverPrompt(e.target.value)}
          placeholder="예: AI 로봇이 태양을 바라보는 표지"
          maxLength={200}
          rows={6}
          style={{
            width: "100%",
            padding: "10px",
            fontSize: "14px",
            borderRadius: "6px",
            border: "1px solid #ccc",
            resize: "vertical",
          }}
        />
        <div style={{ textAlign: "right", fontSize: "12px", color: "#888" }}>
          {coverPrompt.length}/200
        </div>
      </div>

      {/* 이미지 생성 */}
      <div style={{ marginBottom: "20px" }}>
        <CoverGeneratorButton
          promptValue={coverPrompt}
          setCoverUrl={setCoverUrl}
        />
        {coverUrl && (
          <div style={{ marginTop: "10px" }}>
            <img
              src={coverUrl}
              alt="생성된 표지"
              style={{ width: "150px", borderRadius: "4px" }}
            />
          </div>
        )}
      </div>

      {/* 저장 */}
      <button
        type="submit"
        style={{
          padding: "10px 20px",
          backgroundColor: "#333",
          color: "#fff",
          border: "none",
          borderRadius: "6px",
        }}
      >
        저장
      </button>
    </form>
  );
}

export default BookForm;
