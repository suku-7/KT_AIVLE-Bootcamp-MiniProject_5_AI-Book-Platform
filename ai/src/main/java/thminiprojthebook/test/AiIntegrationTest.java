package thminiprojthebook.test;

import thminiprojthebook.service.DalleService;
import thminiprojthebook.service.GptService;
import thminiprojthebook.domain.BookRegisted;

/**
 * AI 기능 통합 테스트
 * OpenAI API 연동 확인
 */
public class AiIntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("=== AI 기능 통합 테스트 시작 ===\n");
        
        try {
            // 환경변수 확인
            String apiKey = System.getenv("OPENAI_API_KEY");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.out.println("❌ OPENAI_API_KEY 환경변수가 설정되지 않았습니다.");
                System.out.println("   Mock 테스트만 실행합니다.\n");
            } else {
                System.out.println("✅ OPENAI_API_KEY 환경변수 확인됨");
                System.out.println("   실제 OpenAI API 테스트를 실행합니다.\n");
            }
            
            // 1. DalleService 테스트
            System.out.println("🎨 1. DALL-E 서비스 테스트");
            DalleService dalleService = new DalleService();
            
            String title = "디지털 혁신과 AI의 미래";
            String context = "4차 산업혁명 시대에 인공지능과 디지털 기술이 어떻게 우리의 삶과 비즈니스를 변화시키고 있는지 살펴보는 실용적인 가이드북입니다.";
            
            System.out.println("   입력:");
            System.out.println("   - 제목: " + title);
            System.out.println("   - 내용: " + context.substring(0, 30) + "...");
            
            System.out.println("   DALL-E API 호출 중...");
            String imageUrl = dalleService.generateCoverImage(title, context);
            
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                System.out.println("   ✅ 커버 이미지 생성 성공!");
                System.out.println("   - 생성된 URL: " + imageUrl);
            } else {
                System.out.println("   ❌ 커버 이미지 생성 실패");
                if (apiKey == null) {
                    System.out.println("   (원인: API 키 미설정)");
                }
            }
            
            // 2. GptService 요약 테스트
            System.out.println("\n🤖 2. GPT 요약 서비스 테스트");
            GptService gptService = new GptService();
            
            System.out.println("   GPT API 호출 중...");
            String summary = gptService.generateSummary(context, 300, "KO", "기술서적");
            
            if (summary != null && !summary.trim().isEmpty()) {
                System.out.println("   ✅ 요약 생성 성공!");
                System.out.println("   - 생성된 요약: " + summary);
            } else {
                System.out.println("   ❌ 요약 생성 실패");
                if (apiKey == null) {
                    System.out.println("   (원인: API 키 미설정)");
                }
            }
            
            // 3. 장르 분류 테스트
            System.out.println("\n📚 3. 장르 분류 서비스 테스트");
            System.out.println("   GPT API 호출 중...");
            String genre = gptService.classifyGenre(title, context);
            
            if (genre != null && !genre.trim().isEmpty()) {
                System.out.println("   ✅ 장르 분류 성공!");
                System.out.println("   - 분류된 장르: " + genre);
            } else {
                System.out.println("   ❌ 장르 분류 실패");
                if (apiKey == null) {
                    System.out.println("   (원인: API 키 미설정)");
                }
            }
            
            // 4. BookRegisted 이벤트 시뮬레이션
            System.out.println("\n📖 4. BookRegisted 이벤트 시뮬레이션");
            BookRegisted event = new BookRegisted();
            event.setBookId(1001L);
            event.setTitle(title);
            event.setContext(context);
            event.setAuthorId(2001L);
            
            System.out.println("   이벤트 생성:");
            System.out.println("   - BookId: " + event.getBookId());
            System.out.println("   - Title: " + event.getTitle());
            System.out.println("   - AuthorId: " + event.getAuthorId());
            System.out.println("   - Context Length: " + event.getContext().length() + " 문자");
            
            // 결과 요약
            System.out.println("\n=== 테스트 결과 요약 ===");
            boolean hasApiKey = apiKey != null && !apiKey.trim().isEmpty();
            boolean imageSuccess = imageUrl != null && !imageUrl.trim().isEmpty();
            boolean summarySuccess = summary != null && !summary.trim().isEmpty();
            boolean genreSuccess = genre != null && !genre.trim().isEmpty();
            
            System.out.println("🔑 API 키 설정: " + (hasApiKey ? "✅ 설정됨" : "❌ 미설정"));
            System.out.println("🎨 이미지 생성: " + (imageSuccess ? "✅ 성공" : "❌ 실패"));
            System.out.println("📝 요약 생성: " + (summarySuccess ? "✅ 성공" : "❌ 실패"));
            System.out.println("📚 장르 분류: " + (genreSuccess ? "✅ 성공" : "❌ 실패"));
            
            if (hasApiKey) {
                int successCount = (imageSuccess ? 1 : 0) + (summarySuccess ? 1 : 0) + (genreSuccess ? 1 : 0);
                System.out.println("🏆 전체 성공률: " + successCount + "/3 (" + (successCount * 100 / 3) + "%)");
                
                if (successCount == 3) {
                    System.out.println("🎉 모든 AI 기능이 정상적으로 작동합니다!");
                } else if (successCount > 0) {
                    System.out.println("⚠️  일부 AI 기능에 문제가 있습니다.");
                } else {
                    System.out.println("🚨 모든 AI 기능에 문제가 있습니다.");
                }
            } else {
                System.out.println("📌 실제 테스트를 위해 OPENAI_API_KEY 환경변수를 설정하세요.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 테스트 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== AI 기능 통합 테스트 완료 ===");
    }
}
