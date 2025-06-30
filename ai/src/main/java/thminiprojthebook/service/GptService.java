package thminiprojthebook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class GptService {
    
    @Value("${openai.api.key:}")
    private String openaiApiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    public GptService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Classify genre using GPT model
     * @param title Book title
     * @param content Book content
     * @return Classified genre or default genre if classification fails
     */
    public String classifyGenre(String title, String content) {
        try {
            if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
                throw new RuntimeException("OpenAI API key not configured");
            }
            
            // Create prompt for genre classification
            String prompt = GenreClassifier.createGenreClassificationPrompt(title, content);
            
            // Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4-1106-preview");
            
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 50); // Short response expected
            requestBody.put("temperature", 0.1); // Low temperature for consistent classification
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Send request to OpenAI
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String classifiedGenre = extractGenreFromResponse(response.getBody());
                return GenreClassifier.validateGenre(classifiedGenre);
            } else {
                throw new RuntimeException("Failed to classify genre: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            System.err.println("Error classifying genre: " + e.getMessage());
            e.printStackTrace();
            return GenreClassifier.validateGenre(null); // Return default genre
        }
    }
    
    /**
     * Extract genre from OpenAI API response
     */
    private String extractGenreFromResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = rootNode.path("choices");
            
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode messageNode = choicesNode.get(0).path("message");
                return messageNode.path("content").asText().trim();
            }
            
            throw new RuntimeException("No genre found in response");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response: " + e.getMessage());
        }
    }

    /**
     * Generate summary using GPT model
     * @param content Book content to summarize
     * @param maxLength Maximum length of summary
     * @param language Language of content
     * @param classificationType Type of classification
     * @return Summary string or null if generation fails
     */
    public String generateSummary(String content, Integer maxLength, String language, String classificationType) {
        try {
            if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
                throw new RuntimeException("OpenAI API key not configured");
            }
            
            // Create prompt for summarization
            String prompt = createSummaryPrompt(content, maxLength, language, classificationType);
            
            // Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4-1106-preview"); // GPT-4 Turbo
            
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", Math.min(maxLength != null ? maxLength * 2 : 1000, 4000));
            requestBody.put("temperature", 0.3);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Send request to OpenAI
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractSummary(response.getBody());
            } else {
                throw new RuntimeException("Failed to generate summary: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            System.err.println("Error generating summary: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create optimized prompt for summarization
     */
    private String createSummaryPrompt(String content, Integer maxLength, String language, String classificationType) {
        StringBuilder prompt = new StringBuilder();
        
        // Language setting
        if ("KO".equals(language)) {
            prompt.append("다음 책 내용을 한국어로 요약해주세요.\n\n");
        } else {
            prompt.append("Please summarize the following book content in English.\n\n");
        }
        
        // Genre-specific summarization guidance
        if (classificationType != null && !classificationType.trim().isEmpty()) {
            if ("KO".equals(language)) {
                prompt.append("장르: ").append(classificationType).append("\n");
                prompt.append("이 장르의 특성을 고려하여 ");
                
                if (classificationType.contains("현대소설") || classificationType.contains("로맨스")) {
                    prompt.append("인물과 감정, 스토리의 핵심을 중심으로 ");
                } else if (classificationType.contains("판타지") || classificationType.contains("SF")) {
                    prompt.append("세계관과 설정, 주요 사건을 중심으로 ");
                } else if (classificationType.contains("추리") || classificationType.contains("스릴러")) {
                    prompt.append("사건과 추리 과정, 긴장감을 중심으로 ");
                } else if (classificationType.contains("에세이") || classificationType.contains("수필")) {
                    prompt.append("작가의 생각과 경험, 메시지를 중심으로 ");
                } else if (classificationType.contains("자기계발") || classificationType.contains("경영")) {
                    prompt.append("핵심 개념과 실용적 내용을 중심으로 ");
                }
                prompt.append("요약해주세요.\n");
            } else {
                prompt.append("Genre: ").append(classificationType).append("\n");
                prompt.append("Consider the characteristics of this genre when summarizing.\n");
            }
        }
        
        // Length constraint
        if (maxLength != null && maxLength > 0) {
            if ("KO".equals(language)) {
                prompt.append("요약 길이: 약 ").append(maxLength).append("자 이내\n\n");
            } else {
                prompt.append("Summary length: approximately ").append(maxLength).append(" characters\n\n");
            }
        }
        
        // Content
        prompt.append("내용:\n").append(content).append("\n\n");
        
        // Instructions
        if ("KO".equals(language)) {
            prompt.append("요구사항:\n");
            prompt.append("- 해당 장르의 특성을 반영한 요약\n");
            prompt.append("- 핵심 내용을 간결하고 명확하게 요약\n");
            prompt.append("- 주요 주제와 키워드 포함\n");
            prompt.append("- 읽기 쉽고 이해하기 쉬운 문체\n");
            prompt.append("- 원문의 톤과 스타일 유지\n");
        } else {
            prompt.append("Requirements:\n");
            prompt.append("- Reflect the characteristics of the genre\n");
            prompt.append("- Summarize key content concisely and clearly\n");
            prompt.append("- Include main topics and keywords\n");
            prompt.append("- Easy to read and understand\n");
            prompt.append("- Maintain the tone and style of the original\n");
        }
        
        return prompt.toString();
    }
    
    /**
     * Extract summary from OpenAI API response
     */
    private String extractSummary(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = rootNode.path("choices");
            
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode messageNode = choicesNode.get(0).path("message");
                return messageNode.path("content").asText().trim();
            }
            
            throw new RuntimeException("No summary found in response");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response: " + e.getMessage());
        }
    }
}
