import thminiprojthebook.service.DalleService;
import thminiprojthebook.service.GptService;
import thminiprojthebook.domain.BookRegisted;
import thminiprojthebook.domain.CoverDesign;
import thminiprojthebook.domain.ContentAnalyzer;

/**
 * 간단한 AI 기능 테스트
 * 실제 OpenAI API를 호출하여 AI 기능이 작동하는지 확인
 */
public class SimpleAITest {
    
    public static void main(String[] args) {
        System.out.println("=== AI 어그리게이트 기능 테스트 시작 ===\n");
        
        try {
            // 1. DalleService 인스턴스 생성
            DalleService dalleService = new DalleService();
            System.out.println("✅ DalleService 인스턴스 생성 완료");
            
            // 2. GptService 인스턴스 생성
            GptService gptService = new GptService();
            System.out.println("✅ GptService 인스턴스 생성 완료");
            
            // 3. 테스트용 BookRegisted 이벤트 생성
            BookRegisted testEvent = new BookRegisted();
            testEvent.setBookId(1001L);
            testEvent.setTitle("디지털 혁신과 AI의 미래");
            testEvent.setContext("4차 산업혁명 시대에 인공지능과 디지털 기술이 어떻게 우리의 삶과 비즈니스를 변화시키고 있는지 살펴보는 실용적인 가이드북입니다. 최신 AI 기술 동향부터 실제 적용 사례까지 포괄적으로 다룹니다.");
            testEvent.setAuthorId(2001L);
            
            System.out.println("📚 테스트 이벤트 생성:");
            System.out.println("   - BookId: " + testEvent.getBookId());
            System.out.println("   - Title: " + testEvent.getTitle());
            System.out.println("   - AuthorId: " + testEvent.getAuthorId());
            System.out.println("   - Context: " + testEvent.getContext().substring(0, 50) + "...");
            
            // 4. DALL-E 커버 이미지 생성 테스트
            System.out.println("\n🎨 DALL-E 커버 이미지 생성 테스트:");
            String imageUrl = dalleService.generateCoverImage(
                testEvent.getTitle(), 
                testEvent.getContext()
            );
            
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                System.out.println("✅ 커버 이미지 생성 성공!");
                System.out.println("   - 생성된 이미지 URL: " + imageUrl);
            } else {
                System.out.println("❌ 커버 이미지 생성 실패 (API 키 확인 필요)");
            }
            
            // 5. GPT 요약 생성 테스트
            System.out.println("\n🤖 GPT 요약 생성 테스트:");
            String summary = gptService.generateSummary(
                testEvent.getContext(),
                500,
                "KO",
                "기술서적"
            );
            
            if (summary != null && !summary.trim().isEmpty()) {
                System.out.println("✅ 요약 생성 성공!");
                System.out.println("   - 생성된 요약: " + summary);
            } else {
                System.out.println("❌ 요약 생성 실패 (API 키 확인 필요)");
            }
            
            // 6. 장르 분류 테스트
            System.out.println("\n📖 장르 분류 테스트:");
            String genre = gptService.classifyGenre(
                testEvent.getTitle(),
                testEvent.getContext()
            );
            
            if (genre != null && !genre.trim().isEmpty()) {
                System.out.println("✅ 장르 분류 성공!");
                System.out.println("   - 분류된 장르: " + genre);
            } else {
                System.out.println("❌ 장르 분류 실패 (API 키 확인 필요)");
            }
            
            System.out.println("\n=== AI 기능 테스트 완료 ===");
            
            // 7. 결과 요약
            System.out.println("\n📋 테스트 결과 요약:");
            System.out.println("   • DALL-E 이미지 생성: " + (imageUrl != null ? "성공" : "실패"));
            System.out.println("   • GPT 요약 생성: " + (summary != null ? "성공" : "실패"));
            System.out.println("   • 장르 분류: " + (genre != null ? "성공" : "실패"));
            
            boolean allTestsPassed = imageUrl != null && summary != null && genre != null;
            System.out.println("   • 전체 테스트: " + (allTestsPassed ? "✅ 성공" : "❌ 일부 실패"));
            
        } catch (Exception e) {
            System.err.println("❌ 테스트 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
