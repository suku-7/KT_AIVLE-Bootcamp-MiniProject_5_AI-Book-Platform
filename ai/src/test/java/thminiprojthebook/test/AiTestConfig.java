package thminiprojthebook.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import thminiprojthebook.service.DalleService;

/**
 * AI 어그리게이트 테스트용 구성 클래스
 * - 실제 OpenAI API 호출 없이 테스트할 수 있도록 Mock DalleService 제공
 */
@Configuration
@Profile("test")
public class AiTestConfig {

    @Bean
    @Primary
    public DalleService mockDalleService() {
        return new MockDalleService();
    }

    /**
     * 테스트용 Mock DalleService 구현
     */
    public static class MockDalleService extends DalleService {
        
        @Override
        public String generateCoverImage(String title, String context) {
            // 테스트용 가짜 이미지 URL 반환
            String mockImageUrl = "https://mock-generated-image.example.com/cover_" + 
                                 title.replaceAll("\\s+", "_").toLowerCase() + "_" + 
                                 System.currentTimeMillis() + ".png";
            
            System.out.println("=== Mock DALL-E Service Called ===");
            System.out.println("Title: " + title);
            System.out.println("Context: " + context);
            System.out.println("Generated Mock URL: " + mockImageUrl);
            
            // 실제 API 호출 시뮬레이션을 위한 약간의 지연
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return mockImageUrl;
        }
    }
}
