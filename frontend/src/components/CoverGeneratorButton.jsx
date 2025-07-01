import { useState } from "react";
import axios from "axios";
import { Switch, FormControlLabel } from "@mui/material";

const GPT_PROMPT_REFINEMENT_INSTRUCTION = `
당신은 책 표지 이미지 생성 프롬프트 전문가입니다.
사용자가 제공하는 간단한 키워드를 바탕으로, DALL-E 3가 이미지화하기 좋은 형태로 시각적으로 구체적인 묘사를 포함한 프롬프트를 생성하세요.
스타일, 배경, 중심 오브젝트, 분위기 등을 포함하세요. 너무 설명적이거나 모호한 단어는 피하세요.
`;

function CoverGeneratorButton({ promptValue, setCoverUrl }) {
  const [loading, setLoading] = useState(false);
  const [useGpt, setUseGpt] = useState(true);

  const handleGenerate = async () => {
    if (!promptValue.trim()) {
      alert("표지 컨셉 프롬프트를 입력하세요.");
      return;
    }

    setLoading(true);
    let finalPrompt = promptValue;

    try {
      if (useGpt) {
        const gptPayload = {
          model: "gpt-4.1-mini",
          messages: [
            { role: "system", content: GPT_PROMPT_REFINEMENT_INSTRUCTION },
            { role: "user", content: `사용자 입력:\n"${promptValue}"` }
          ],
          temperature: 0.7,
          max_tokens: 400,
        };

        const gptResponse = await axios.post(
          "https://api.openai.com/v1/chat/completions",
          gptPayload,
          {
            headers: {
              Authorization: `Bearer ${import.meta.env.VITE_OPENAI_API_KEY}`,
              "Content-Type": "application/json",
            },
          }
        );

        if (gptResponse.data.choices?.length > 0) {
          finalPrompt = gptResponse.data.choices[0].message.content.trim();
        }
      }

      const dalleResponse = await axios.post(
        "https://api.openai.com/v1/images/generations",
        {
          model: "dall-e-3",
          prompt: finalPrompt,
          n: 1,
          size: "1024x1792",
        },
        {
          headers: {
            Authorization: `Bearer ${import.meta.env.VITE_OPENAI_API_KEY}`,
            "Content-Type": "application/json",
          },
        }
      );

      const url = dalleResponse.data.data[0].url;
      setCoverUrl(url);
      alert("표지 이미지가 생성되었습니다!");
    } catch (error) {
      console.error("OpenAI 요청 실패:", error.response?.data || error.message);
      alert("이미지 생성 실패");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ marginBottom: "1rem" }}>
      <FormControlLabel
        control={
          <Switch
            checked={useGpt}
            onChange={(e) => setUseGpt(e.target.checked)}
            sx={{
              '& .MuiSwitch-switchBase.Mui-checked': {
                color: '#5a9',
              },
              '& .MuiSwitch-switchBase.Mui-checked + .MuiSwitch-track': {
                backgroundColor: '#5a9',
              },
            }}
          />
        }
        label="GPT로 프롬프트 보정"
        sx={{ mb: 1 }}
      />
      <button
        type="button"
        onClick={handleGenerate}
        disabled={loading}
        style={{
          padding: "10px 16px",
          backgroundColor: "#5a9",
          color: "white",
          border: "none",
          borderRadius: "6px",
          cursor: "pointer"
        }}
      >
        {loading ? "생성 중..." : "표지 이미지 생성"}
      </button>
    </div>
  );
}

export default CoverGeneratorButton;
