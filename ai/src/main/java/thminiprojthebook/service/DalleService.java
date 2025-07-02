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
