package thminiprojthebook.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;

@Service
public class DalleService {
    
    @Value("${spring.ai.openai.api-key:${openai.api.key:}}")
    private String openaiApiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/images/generations";
    
    public DalleService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        // Spring 컨텍스트 외부에서 실행시 환경변수에서 직접 읽기
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
            openaiApiKey = System.getenv("OPENAI_API_KEY");
        }
    }
    
    /**
     * Generate book cover image using DALL-E
     * @param title Book title
     * @param summary AI-generated summary or book description for visual context
     * @return Image URL or null if generation fails
     */
    public String generateCoverImage(String title, String summary) {
        try {
            if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
                throw new RuntimeException("OpenAI API key not configured");
            }
            
            // Create prompt for book cover generation using title and summary
            String prompt = createCoverPrompt(title, summary);
            
            // Prepare request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openaiApiKey);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "dall-e-3");
            requestBody.put("prompt", prompt);
            requestBody.put("n", 1);
            requestBody.put("size", "1024x1024");
            requestBody.put("quality", "standard");
            requestBody.put("response_format", "url");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            System.out.println("=== Sending request to DALL-E API ===");
            System.out.println("URL: " + OPENAI_API_URL);
            System.out.println("Prompt: " + prompt);
            System.out.println("====================================");
            
            // Send request to OpenAI
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, request, String.class);
            
            System.out.println("=== DALL-E API Response Status ===");
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Headers: " + response.getHeaders());
            System.out.println("================================");
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractImageUrl(response.getBody());
            } else {
                System.err.println("API request failed with status: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
                throw new RuntimeException("Failed to generate image: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            System.err.println("Error generating cover image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create optimized prompt for book cover generation
     * @param title Book title
     * @param summary AI-generated summary or context description
     */
    private String createCoverPrompt(String title, String summary) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a professional book cover design for a book titled '").append(title).append("'. ");
        
        if (summary != null && !summary.trim().isEmpty()) {
            // Check if this appears to be an AI-generated summary vs raw content
            if (summary.length() < 500 && !summary.contains("\n\n")) {
                // Likely an AI summary - use it to inform visual style
                prompt.append("Based on this book summary: ").append(summary).append(". ");
                prompt.append("Design the cover to visually represent the themes, mood, and genre of this story. ");
            } else {
                // Likely raw content - use more general description
                prompt.append("This book's content includes: ").append(summary.substring(0, Math.min(300, summary.length()))).append("... ");
            }
        }
        
        prompt.append("The design requirements: ")
              .append("- Professional and eye-catching book cover ")
              .append("- Reflect the book's genre and mood through visual elements ")
              .append("- Include the title prominently with readable typography ")
              .append("- Use appropriate color scheme that matches the story's tone ")
              .append("- Modern, marketable design suitable for both digital and print ")
              .append("- High quality artwork that would attract readers ")
              .append("- Avoid including author names or additional text beyond the title");
        
        return prompt.toString();
    }
    
    /**
     * Extract image URL from OpenAI API response
     */
    private String extractImageUrl(String responseBody) {
        try {
            System.out.println("=== DALL-E API Response ===");
            System.out.println(responseBody);
            System.out.println("=========================");
            
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");
            
            System.out.println("Data node exists: " + !dataNode.isMissingNode());
            System.out.println("Data node is array: " + dataNode.isArray());
            System.out.println("Data node size: " + dataNode.size());
            
            if (dataNode.isArray() && dataNode.size() > 0) {
                JsonNode firstImage = dataNode.get(0);
                JsonNode urlNode = firstImage.path("url");
                
                System.out.println("URL node exists: " + !urlNode.isMissingNode());
                System.out.println("URL value: " + urlNode.asText());
                
                String imageUrl = urlNode.asText();
                
                if (imageUrl != null && !imageUrl.trim().isEmpty() && !imageUrl.equals("null")) {
                    System.out.println("Successfully extracted image URL: " + imageUrl);
                    return imageUrl;
                } else {
                    System.err.println("Image URL is empty or null");
                }
            }
            
            System.err.println("Failed to find valid image URL in response structure");
            throw new RuntimeException("No image URL found in response");
            
        } catch (Exception e) {
            System.err.println("Failed to parse OpenAI response: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse OpenAI response: " + e.getMessage());
        }
    }
}
