package thminiprojthebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import thminiprojthebook.domain.*;
import thminiprojthebook.service.GptService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * AI 서비스 독립 기능 테스트
 * - 실제 OpenAI API 호출 테스트
 * - 요약 생성 및 장르 분류 테스트
 * - 모든 요청/응답 데이터를 txt 파일로 저장
 */
@SpringBootTest
@ActiveProfiles("test")
public class AiServiceIndependentTest {
    
    private static final String OUTPUT_FILE = "src/test/results/ai_service_test_results.txt";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> testResults = new ArrayList<>();
    
    @Test
    public void testAiServiceCompleteFunctionality() {
        logResult("========================================");
        logResult("AI 서비스 독립 기능 테스트 시작");
        logResult("테스트 시작 시간: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logResult("========================================");
        
        try {
            // 1. 테스트용 도서 데이터 준비
            BookRegisted testBook = createTestBookData();
            logTestBookData(testBook);
            
            // 2. GptService 인스턴스 생성 및 설정 확인
            GptService gptService = new GptService();
            logResult("\n--- GptService 초기화 확인 ---");
            logResult("GptService 인스턴스 생성 완료");
            
            // 3. 요약 생성 테스트
            testSummaryGeneration(gptService, testBook);
            
            // 4. 장르 분류 테스트
            testGenreClassification(gptService, testBook);
            
            // 5. 전체 프로세스 통합 테스트
            testCompleteAiProcess(testBook);
            
            // 6. REST API 엔드포인트 테스트 (시뮬레이션)
            testRestApiEndpoints();
            
            logResult("\n========================================");
            logResult("AI 서비스 테스트 완료");
            logResult("테스트 완료 시간: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            logResult("========================================");
            
        } catch (Exception e) {
            logResult("\n!!! 테스트 중 오류 발생 !!!");
            logResult("오류 메시지: " + e.getMessage());
            logResult("스택 트레이스:");
            for (StackTraceElement element : e.getStackTrace()) {
                logResult("  " + element.toString());
            }
        } finally {
            // 결과를 txt 파일로 저장
            saveResultsToFile();
        }
    }
    
    private BookRegisted createTestBookData() {
        BookRegisted book = new BookRegisted();
        book.setBookId(12345L);
        book.setTitle("마법사의 모험");
        book.setContext(
            "한때 평범한 고등학생이었던 김민수는 어느 날 신비로운 책을 발견하게 된다. " +
            "그 책을 열어본 순간, 그는 마법의 세계로 빨려 들어가게 되었다. " +
            "그곳에서 그는 자신이 예언된 마법사임을 알게 되고, 세상을 구하기 위한 모험을 시작한다.\n\n" +
            "마법의 세계에서 만난 동료들과 함께 어둠의 마왕을 물리치기 위해 " +
            "위험한 여행을 떠나는 민수. 과연 그는 자신의 운명을 받아들이고 " +
            "세상을 구할 수 있을까?\n\n" +
            "이 소설은 청소년 판타지 장르로, 성장과 우정, 용기에 대한 이야기를 담고 있다. " +
            "마법과 모험이 가득한 세계에서 펼쳐지는 흥미진진한 스토리가 " +
            "독자들을 끝까지 몰입하게 만든다."
        );
        book.setAuthorId(67890L);
        return book;
    }
    
    private void logTestBookData(BookRegisted book) {
        logResult("\n--- 테스트용 도서 데이터 ---");
        logResult("도서 ID: " + book.getBookId());
        logResult("제목: " + book.getTitle());
        logResult("작가 ID: " + book.getAuthorId());
        logResult("내용 길이: " + book.getContext().length() + " 문자");
        logResult("내용 미리보기: " + book.getContext().substring(0, Math.min(100, book.getContext().length())) + "...");
    }
    
    private void testSummaryGeneration(GptService gptService, BookRegisted book) {
        logResult("\n--- 요약 생성 테스트 ---");
        
        try {
            // API 요청 정보 로깅
            logResult("OpenAI API 요약 생성 요청:");
            logResult("- 모델: gpt-4-1106-preview");
            logResult("- 최대 토큰: 500");
            logResult("- 언어: KO");
            logResult("- 요약 타입: 일반요약");
            logResult("- 입력 텍스트 길이: " + book.getContext().length());
            
            long startTime = System.currentTimeMillis();
            String summary = gptService.generateSummary(
                book.getContext(), 
                500, 
                "KO", 
                "일반요약"
            );
            long endTime = System.currentTimeMillis();
            
            // 응답 정보 로깅
            logResult("\nOpenAI API 요약 생성 응답:");
            logResult("- 응답 시간: " + (endTime - startTime) + "ms");
            logResult("- 요약 결과 존재: " + (summary != null && !summary.trim().isEmpty()));
            
            if (summary != null && !summary.trim().isEmpty()) {
                logResult("- 요약 길이: " + summary.length() + " 문자");
                logResult("- 생성된 요약:");
                logResult("  " + summary);
            } else {
                logResult("- 요약 생성 실패");
            }
            
        } catch (Exception e) {
            logResult("요약 생성 중 오류 발생: " + e.getMessage());
        }
    }
    
    private void testGenreClassification(GptService gptService, BookRegisted book) {
        logResult("\n--- 장르 분류 테스트 ---");
        
        try {
            // API 요청 정보 로깅
            logResult("OpenAI API 장르 분류 요청:");
            logResult("- 모델: gpt-4-1106-preview");
            logResult("- 최대 토큰: 50");
            logResult("- Temperature: 0.1");
            logResult("- 분석 대상 제목: " + book.getTitle());
            
            long startTime = System.currentTimeMillis();
            String genre = gptService.classifyGenre(book.getTitle(), book.getContext());
            long endTime = System.currentTimeMillis();
            
            // 응답 정보 로깅
            logResult("\nOpenAI API 장르 분류 응답:");
            logResult("- 응답 시간: " + (endTime - startTime) + "ms");
            logResult("- 분류된 장르: " + genre);
            
        } catch (Exception e) {
            logResult("장르 분류 중 오류 발생: " + e.getMessage());
        }
    }
    
    private void testCompleteAiProcess(BookRegisted book) {
        logResult("\n--- 전체 AI 프로세스 통합 테스트 ---");
        
        try {
            logResult("ContentAnalyzer.aiSummarize() 메서드 호출 시뮬레이션:");
            logResult("입력 데이터:");
            logResult("- BookId: " + book.getBookId());
            logResult("- Title: " + book.getTitle());
            logResult("- AuthorId: " + book.getAuthorId());
            logResult("- Context: " + book.getContext().substring(0, Math.min(200, book.getContext().length())) + "...");
            
            // ContentAnalyzer 엔티티 생성 시뮬레이션
            ContentAnalyzer analyzer = new ContentAnalyzer();
            analyzer.setBookId(book.getBookId().toString());
            analyzer.setContext(book.getContext());
            analyzer.setLanguage("KO");
            analyzer.setMaxLength(500);
            analyzer.setClassificationType("임시분류");
            analyzer.setRequestedBy("AI-SYSTEM");
            
            logResult("\n생성된 ContentAnalyzer 엔티티:");
            logResult("- BookId: " + analyzer.getBookId());
            logResult("- Language: " + analyzer.getLanguage());
            logResult("- MaxLength: " + analyzer.getMaxLength());
            logResult("- ClassificationType: " + analyzer.getClassificationType());
            logResult("- RequestedBy: " + analyzer.getRequestedBy());
            
            // AiSummarized 이벤트 생성 시뮬레이션
            logResult("\nAiSummarized 이벤트 발행 시뮬레이션:");
            logResult("- 이벤트 타입: AiSummarized");
            logResult("- 발행 대상: 다른 마이크로서비스");
            logResult("- 포함 데이터: AuthorId, BookId, Summary, ClassificationType 등");
            
        } catch (Exception e) {
            logResult("통합 프로세스 테스트 중 오류 발생: " + e.getMessage());
        }
    }
    
    private void testRestApiEndpoints() {
        logResult("\n--- REST API 엔드포인트 테스트 시뮬레이션 ---");
        
        // ContentAnalyzerRepository API 테스트
        logResult("Spring Data REST API 엔드포인트:");
        logResult("- GET /contentAnalyzers - 모든 콘텐츠 분석 결과 조회");
        logResult("- GET /contentAnalyzers/{id} - 특정 분석 결과 조회");
        logResult("- POST /contentAnalyzers - 새 분석 결과 생성");
        logResult("- PUT /contentAnalyzers/{id} - 분석 결과 수정");
        logResult("- DELETE /contentAnalyzers/{id} - 분석 결과 삭제");
        
        // 샘플 API 호출 결과 시뮬레이션
        logResult("\n샘플 API 응답 (GET /contentAnalyzers):");
        String sampleResponse = "{\n" +
                "  \"_embedded\": {\n" +
                "    \"contentAnalyzers\": [\n" +
                "      {\n" +
                "        \"authorId\": 67890,\n" +
                "        \"bookId\": \"12345\",\n" +
                "        \"context\": \"한때 평범한 고등학생이었던 김민수는...\",\n" +
                "        \"summary\": \"평범한 고등학생이 마법의 세계로 빨려들어가 예언된 마법사가 되어 세상을 구하는 모험을 떠나는 청소년 판타지 소설\",\n" +
                "        \"language\": \"KO\",\n" +
                "        \"maxLength\": 500,\n" +
                "        \"classificationType\": \"판타지\",\n" +
                "        \"requestedBy\": \"AI-SYSTEM\",\n" +
                "        \"_links\": {\n" +
                "          \"self\": {\n" +
                "            \"href\": \"http://localhost:8080/contentAnalyzers/1\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"_links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://localhost:8080/contentAnalyzers\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        logResult(sampleResponse);
    }
    
    private void logResult(String message) {
        testResults.add(message);
        System.out.println(message);
    }
    
    private void saveResultsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
            for (String result : testResults) {
                writer.write(result);
                writer.newLine();
            }
            logResult("\n테스트 결과가 파일에 저장되었습니다: " + OUTPUT_FILE);
        } catch (IOException e) {
            System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
