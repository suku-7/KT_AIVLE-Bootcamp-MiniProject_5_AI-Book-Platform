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
    
    @Value("${openai.api.key:}")
    private String openaiApiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/images/generations";
    
    public DalleService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate book cover image using DALL-E
     * @param title Book title
     * @param context Book context/description
     * @return Image URL or null if generation fails
     */
    public String generateCoverImage(String title, String context) {
        try {
            if (openaiApiKey == null || openaiApiKey.trim().isEmpty()) {
                throw new RuntimeException("OpenAI API key not configured");
            }
            
            // Create prompt for book cover generation
            String prompt = createCoverPrompt(title, context);
            
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
            
            // Send request to OpenAI
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractImageUrl(response.getBody());
            } else {
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
     */
    private String createCoverPrompt(String title, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a professional book cover design for a book titled '").append(title).append("'. ");
        
        if (context != null && !context.trim().isEmpty()) {
            prompt.append("The book is about: ").append(context).append(". ");
        }
        
        prompt.append("The design should be: ")
              .append("- Professional and eye-catching ")
              .append("- Suitable for both digital and print formats ")
              .append("- Include the title prominently ")
              .append("- Use appropriate typography and color scheme ")
              .append("- Modern and marketable design ")
              .append("- High quality and visually appealing");
        
        return prompt.toString();
    }
    
    /**
     * Extract image URL from OpenAI API response
     */
    private String extractImageUrl(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");
            
            if (dataNode.isArray() && dataNode.size() > 0) {
                return dataNode.get(0).path("url").asText();
            }
            
            throw new RuntimeException("No image URL found in response");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response: " + e.getMessage());
        }
    }
}
