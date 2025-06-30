package thminiprojthebook.service;

import java.util.Arrays;
import java.util.List;

public class GenreClassifier {
    
    public static final List<String> AVAILABLE_GENRES = Arrays.asList(
        "현대소설 (Contemporary Fiction)",
        "로맨스 (Romance)", 
        "판타지 / SF (Fantasy / Sci-Fi)",
        "추리 / 스릴러 / 범죄 (Mystery / Thriller / Crime)",
        "공포 / 호러 (Horror)",
        "역사소설 (Historical Fiction)",
        "청소년 / 청춘소설 (Young Adult)",
        "에세이 / 수필 (Essay / Memoir)",
        "인문 / 철학 / 종교 (Humanities / Philosophy / Religion)",
        "심리 / 자기계발 (Psychology / Self-help)",
        "사회 / 정치 / 시사 (Society / Politics)",
        "경제 / 경영 / 투자 (Business / Economics)",
        "과학 / 기술 / IT (Science / Technology)",
        "아동 / 그림책 (Children / Picture Books)",
        "라이프스타일 / 취미 / 여행 (Lifestyle / Hobby / Travel)"
    );
    
    /**
     * Create prompt for genre classification based on book summary
     */
    public static String createGenreClassificationPrompt(String title, String summary) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("다음 책의 제목과 요약된 내용을 분석하여 가장 적합한 장르를 분류해주세요.\n\n");
        
        prompt.append("책 제목: ").append(title).append("\n");
        prompt.append("책 요약: ").append(summary).append("\n\n");
        
        prompt.append("선택 가능한 장르:\n");
        for (int i = 0; i < AVAILABLE_GENRES.size(); i++) {
            prompt.append((i + 1)).append(". ").append(AVAILABLE_GENRES.get(i)).append("\n");
        }
        
        prompt.append("\n요구사항:\n");
        prompt.append("- 위 15개 장르 중에서 가장 적합한 하나만 선택\n");
        prompt.append("- 정확히 위 목록의 형식 그대로 답변 (예: '현대소설 (Contemporary Fiction)')\n");
        prompt.append("- 장르명만 답변하고 다른 설명은 포함하지 말 것\n");
        prompt.append("- 여러 장르에 해당할 경우 가장 주요한 장르 하나만 선택\n\n");
        
        prompt.append("분류된 장르:");
        
        return prompt.toString();
    }
    
    /**
     * Validate if the returned genre is valid
     */
    public static String validateGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return "현대소설 (Contemporary Fiction)"; // Default genre
        }
        
        String cleanedGenre = genre.trim();
        
        // Check if the genre is in our available list
        for (String availableGenre : AVAILABLE_GENRES) {
            if (availableGenre.equals(cleanedGenre)) {
                return cleanedGenre;
            }
        }
        
        // If not found, try partial matching
        for (String availableGenre : AVAILABLE_GENRES) {
            if (availableGenre.contains(cleanedGenre) || cleanedGenre.contains(availableGenre.split(" \\(")[0])) {
                return availableGenre;
            }
        }
        
        // Default fallback
        return "현대소설 (Contemporary Fiction)";
    }
}
