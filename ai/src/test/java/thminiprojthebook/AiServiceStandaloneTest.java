package thminiprojthebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import thminiprojthebook.service.GptService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 서비스 독립 실행 테스트 클래스
 * Spring 컨텍스트 없이 실행 가능한 단순 테스트
 * 실제 OpenAI API 호출 및 결과를 txt 파일로 저장
 */
public class AiServiceStandaloneTest {
    
    private static final String OUTPUT_FILE = "src/test/results/ai_service_standalone_test_results.txt";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final List<String> testResults;
    
    public AiServiceStandaloneTest() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.testResults = new ArrayList<>();
    }
    
    public static void main(String[] args) {
        AiServiceStandaloneTest test = new AiServiceStandaloneTest();
        test.runFullTest();
    }
    
    public void runFullTest() {
        logResult("========================================");
        logResult("AI Service Standalone Test Started");
        logResult("Test Start Time: " + getCurrentTimestamp());
        logResult("========================================");
        
        try {
            // 1. 환경 설정 확인
            checkEnvironmentSetup();
            
            // 2. 테스트 데이터 준비
            TestBookData testBook = prepareTestData();
            
            // 3. OpenAI API 직접 호출 테스트
            testDirectOpenAiApiCall(testBook);
            
            // 4. GptService 클래스 테스트
            testGptServiceClass(testBook);
            
            // 5. HTTP REST API 테스트 시뮬레이션
            testHttpApiSimulation();
            
            logResult("\\n========================================");
            logResult("AI Service Test Completed");
            logResult("Test Completion Time: " + getCurrentTimestamp());
            logResult("========================================");
            
        } catch (Exception e) {
            logResult("\\n!!! Error occurred during test !!!");
            logResult("Error Message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            saveResultsToFile();
        }
    }
    
    private void checkEnvironmentSetup() {
        logResult("\\n--- Environment Setup Check ---");
        
        String apiKey = System.getenv("OPENAI_API_KEY");
        logResult("OpenAI API Key environment variable: " + (apiKey != null && !apiKey.trim().isEmpty() ? "Set" : "Not set"));
        
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            // API key format validation
            if (apiKey.startsWith("sk-")) {
                logResult("API Key Format: Valid OpenAI API key format");
                logResult("API Key Length: " + apiKey.length() + " characters");
                logResult("API Key Prefix: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
                
                if (apiKey.contains("test") || apiKey.contains("demo") || apiKey.contains("example")) {
                    logResult("WARNING: Appears to be test/demo API key. Real API calls may fail.");
                } else {
                    logResult("✅ API key appears to be real.");
                }
            } else {
                logResult("WARNING: Invalid API key format. (Should start with sk-)");
            }
        } else {
            logResult("WARNING: OPENAI_API_KEY environment variable not set, skipping real API calls.");
            logResult("For real API testing, run:");
            logResult("  Windows: setup_api_key.bat");
            logResult("  Linux/Mac: ./setup_api_key.sh");
        }
        
        logResult("RestTemplate initialization: Complete");
        logResult("ObjectMapper initialization: Complete");
        
        // Network connection check (simple DNS check)
        try {
            java.net.InetAddress.getByName("api.openai.com");
            logResult("Network connection: OpenAI API server accessible");
        } catch (Exception e) {
            logResult("WARNING: Network connection: OpenAI API server not accessible - " + e.getMessage());
        }
    }
    
    private TestBookData prepareTestData() {
        logResult("\\n--- Test Data Preparation ---");
        
        TestBookData book = new TestBookData();
        book.bookId = "12345";
        book.title = "Wizard's Adventure"; // English title to avoid encoding issues
        book.authorId = "67890";
        book.context = "Once upon a time, there was an ordinary high school student named Kim Min-su who discovered a mysterious book one day. " +
                "The moment he opened the book, he was drawn into a magical world. " +
                "There, he learned that he was the prophesied wizard and began an adventure to save the world. " +
                "Min-su embarked on a dangerous journey with companions he met in the magical world to defeat the dark lord. " +
                "Will he be able to accept his destiny and save the world? " +
                "This novel is a young adult fantasy genre that tells the story of growth, friendship, and courage.";
        
        logResult("Test book data:");
        logResult("- Book ID: " + book.bookId);
        logResult("- Title: " + book.title);
        logResult("- Author ID: " + book.authorId);
        logResult("- Content length: " + book.context.length() + " characters");
        
        return book;
    }
    
    private void testDirectOpenAiApiCall(TestBookData book) {
        logResult("\\n--- OpenAI API 직접 호출 테스트 ---");
        
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logResult("API 키가 없어서 실제 호출을 건너뜁니다. 요청/응답 구조만 시뮬레이션합니다.");
            simulateApiCall(book);
            return;
        }
        
        try {
            // 요약 생성 API 호출
            logResult("\\n1. 요약 생성 API 호출:");
            String summaryResult = callOpenAiForSummary(book, apiKey);
            logResult("요약 결과: " + summaryResult);
            
            // 장르 분류 API 호출
            logResult("\\n2. 장르 분류 API 호출:");
            String genreResult = callOpenAiForGenre(book, apiKey);
            logResult("장르 분류 결과: " + genreResult);
            
        } catch (Exception e) {
            logResult("API 호출 중 오류 발생: " + e.getMessage());
            simulateApiCall(book);
        }
    }
    
    private String callOpenAiForSummary(TestBookData book, String apiKey) {
        logResult("요약 생성 요청 구성:");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4-1106-preview");
        
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "다음 소설 내용을 500자 이내로 요약해주세요: " + book.context);
        messages.add(message);
        
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);
        
        logResult("- URL: " + OPENAI_API_URL);
        logRequestBodySafely(requestBody);
        logResult("- API Key (처음 10자): " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            logResult("OpenAI API 호출 시작...");
            long startTime = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, request, String.class);
            long endTime = System.currentTimeMillis();
            
            logResult("응답 시간: " + (endTime - startTime) + "ms");
            logResult("응답 상태: " + response.getStatusCode());
            logResponseHeadersSafely(response.getHeaders());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                logResult("응답 본문 길이: " + (responseBody != null ? responseBody.length() : 0) + " 문자");
                if (responseBody != null && responseBody.length() > 200) {
                    logResult("응답 본문 미리보기: " + responseBody.substring(0, 200) + "...");
                } else if (responseBody != null) {
                    logResult("응답 본문: " + responseBody);
                }
                return extractContentFromResponse(responseBody);
            } else {
                logResult("API 호출 실패 - 상태 코드: " + response.getStatusCode());
                logResult("응답 본문: " + response.getBody());
                return "API 호출 실패: " + response.getStatusCode();
            }
        } catch (Exception e) {
            logResult("API 호출 예외 발생:");
            logResult("- 예외 타입: " + e.getClass().getSimpleName());
            logResult("- 예외 메시지: " + e.getMessage());
            if (e.getCause() != null) {
                logResult("- 원인: " + e.getCause().getMessage());
            }
            return "API 호출 오류: " + e.getMessage();
        }
    }
    
    private String callOpenAiForGenre(TestBookData book, String apiKey) {
        logResult("장르 분류 요청 구성:");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4-1106-preview");
        
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "다음 소설의 장르를 한 단어로 분류해주세요. 제목: " + book.title + ", 내용: " + book.context);
        messages.add(message);
        
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 50);
        requestBody.put("temperature", 0.1);
        
        logRequestBodySafely(requestBody);
        logResult("- API Key (처음 10자): " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        logResult("- 분석 대상: 제목='" + book.title + "', 내용 길이=" + book.context.length() + "자");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            logResult("OpenAI API 호출 시작...");
            long startTime = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, request, String.class);
            long endTime = System.currentTimeMillis();
            
            logResult("응답 시간: " + (endTime - startTime) + "ms");
            logResult("응답 상태: " + response.getStatusCode());
            logResponseHeadersSafely(response.getHeaders());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                logResult("응답 본문 길이: " + (responseBody != null ? responseBody.length() : 0) + " 문자");
                if (responseBody != null && responseBody.length() > 200) {
                    logResult("응답 본문 미리보기: " + responseBody.substring(0, 200) + "...");
                } else if (responseBody != null) {
                    logResult("응답 본문: " + responseBody);
                }
                return extractContentFromResponse(responseBody);
            } else {
                logResult("API 호출 실패 - 상태 코드: " + response.getStatusCode());
                logResult("응답 본문: " + response.getBody());
                return "API 호출 실패: " + response.getStatusCode();
            }
        } catch (Exception e) {
            logResult("API 호출 예외 발생:");
            logResult("- 예외 타입: " + e.getClass().getSimpleName());
            logResult("- 예외 메시지: " + e.getMessage());
            if (e.getCause() != null) {
                logResult("- 원인: " + e.getCause().getMessage());
            }
            return "API 호출 오류: " + e.getMessage();
        }
    }
    
    @SuppressWarnings("unchecked")
    private String extractContentFromResponse(String responseBody) {
        try {
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                return (String) message.get("content");
            }
            
            return "응답에서 내용을 추출할 수 없음";
        } catch (Exception e) {
            return "응답 파싱 오류: " + e.getMessage();
        }
    }
    
    private void simulateApiCall(TestBookData book) {
        logResult("\\n--- API 호출 시뮬레이션 ---");
        logResult("실제 API 키가 없어서 시뮬레이션 모드로 실행합니다.");
        
        logResult("\\n1. 요약 생성 시뮬레이션:");
        logResult("요청 URL: " + OPENAI_API_URL);
        logResult("요청 본문 예시:");
        logResult("  {");
        logResult("    \\\"model\\\": \\\"gpt-4-1106-preview\\\",");
        logResult("    \\\"messages\\\": [");
        logResult("      {");
        logResult("        \\\"role\\\": \\\"user\\\",");
        logResult("        \\\"content\\\": \\\"다음 소설 내용을 500자 이내로 요약해주세요: " + book.context.substring(0, Math.min(50, book.context.length())) + "...\\\",");
        logResult("      }");
        logResult("    ],");
        logResult("    \\\"max_tokens\\\": 500,");
        logResult("    \\\"temperature\\\": 0.7");
        logResult("  }");
        
        logResult("\\n예상 응답:");
        logResult("  {");
        logResult("    \\\"choices\\\": [");
        logResult("      {");
        logResult("        \\\"message\\\": {");
        logResult("          \\\"content\\\": \\\"평범한 고등학생이 마법의 세계로 빨려들어가 예언된 마법사가 되어 세상을 구하는 모험 이야기\\\"");
        logResult("        }");
        logResult("      }");
        logResult("    ]");
        logResult("  }");
        
        logResult("\\n2. 장르 분류 시뮬레이션:");
        logResult("예상 장르 분류 결과: 판타지");
    }
    
    private void testGptServiceClass(TestBookData book) {
        logResult("\\n--- GptService 클래스 테스트 ---");
        
        try {
            GptService gptService = new GptService();
            logResult("GptService 인스턴스 생성 완료");
            
            // API 키 확인
            String apiKey = System.getenv("OPENAI_API_KEY");
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                logResult("환경변수에서 API 키 확인됨");
                
                // 실제 서비스 메서드 호출 시도
                try {
                    String summary = gptService.generateSummary(book.context, 500, "KO", "일반요약");
                    logResult("요약 생성 결과: " + (summary != null ? summary : "null"));
                    
                    String genre = gptService.classifyGenre(book.title, book.context);
                    logResult("장르 분류 결과: " + (genre != null ? genre : "null"));
                    
                } catch (Exception e) {
                    logResult("GptService 메서드 호출 중 오류: " + e.getMessage());
                }
            } else {
                logResult("API 키가 설정되지 않아 실제 서비스 호출을 건너뜁니다.");
            }
            
        } catch (Exception e) {
            logResult("GptService 테스트 중 오류: " + e.getMessage());
        }
    }
    
    private void testHttpApiSimulation() {
        logResult("\\n--- HTTP REST API 시뮬레이션 ---");
        
        logResult("\\n1. ContentAnalyzer API 엔드포인트:");
        logResult("- GET /contentAnalyzers");
        logResult("- GET /contentAnalyzers/{id}");
        logResult("- POST /contentAnalyzers");
        logResult("- PUT /contentAnalyzers/{id}");
        logResult("- DELETE /contentAnalyzers/{id}");
        
        logResult("\\n2. 샘플 POST 요청 (새 분석 요청):");
        logResult("URL: http://localhost:8080/contentAnalyzers");
        logResult("Method: POST");
        logResult("Content-Type: application/json");
        logResult("Body:");
        logResult("{");
        logResult("  \\\"bookId\\\": \\\"12345\\\",");
        logResult("  \\\"context\\\": \\\"한때 평범한 고등학생이었던 김민수는...\\\",");
        logResult("  \\\"language\\\": \\\"KO\\\",");
        logResult("  \\\"maxLength\\\": 500,");
        logResult("  \\\"classificationType\\\": \\\"임시분류\\\",");
        logResult("  \\\"requestedBy\\\": \\\"AI-SYSTEM\\\"");
        logResult("}");
        
        logResult("\\n3. 샘플 응답:");
        logResult("{");
        logResult("  \\\"authorId\\\": 1,");
        logResult("  \\\"bookId\\\": \\\"12345\\\",");
        logResult("  \\\"context\\\": \\\"한때 평범한 고등학생이었던 김민수는...\\\",");
        logResult("  \\\"summary\\\": \\\"평범한 고등학생이 마법의 세계로 빨려들어가 예언된 마법사가 되어 세상을 구하는 모험 이야기\\\",");
        logResult("  \\\"language\\\": \\\"KO\\\",");
        logResult("  \\\"maxLength\\\": 500,");
        logResult("  \\\"classificationType\\\": \\\"판타지\\\",");
        logResult("  \\\"requestedBy\\\": \\\"AI-SYSTEM\\\",");
        logResult("  \\\"_links\\\": {");
        logResult("    \\\"self\\\": {");
        logResult("      \\\"href\\\": \\\"http://localhost:8080/contentAnalyzers/1\\\"");
        logResult("    }");
        logResult("  }");
        logResult("}");
    }
    
    /**
     * 한글 깨짐 문제를 방지하기 위한 안전한 헤더 출력
     */
    private void logResponseHeadersSafely(HttpHeaders headers) {
        try {
            logResult("응답 헤더 정보:");
            logResult("- Content-Type: " + headers.getContentType());
            logResult("- Content-Length: " + headers.getContentLength());
            logResult("- Status: " + (headers.containsKey("status") ? headers.getFirst("status") : "N/A"));
            logResult("- X-Request-ID: " + (headers.containsKey("x-request-id") ? headers.getFirst("x-request-id") : "N/A"));
        } catch (Exception e) {
            logResult("헤더 정보 출력 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * API 요청 본문을 안전하게 출력 (한글 깨짐 방지)
     */
    private void logRequestBodySafely(Map<String, Object> requestBody) {
        try {
            logResult("API 요청 정보:");
            logResult("- Model: " + requestBody.get("model"));
            logResult("- Max Tokens: " + requestBody.get("max_tokens"));
            logResult("- Temperature: " + requestBody.get("temperature"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> messages = (List<Map<String, Object>>) requestBody.get("messages");
            if (messages != null && !messages.isEmpty()) {
                String content = (String) messages.get(0).get("content");
                if (content != null) {
                    // 한글이 포함된 내용은 길이만 출력
                    logResult("- 요청 내용 길이: " + content.length() + " 문자");
                    if (content.length() > 100) {
                        logResult("- 요청 내용 미리보기: [한글 내용 - " + content.length() + "자]");
                    }
                }
            }
        } catch (Exception e) {
            logResult("요청 본문 출력 중 오류: " + e.getMessage());
        }
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
            System.out.println("\\n테스트 결과가 파일에 저장되었습니다: " + OUTPUT_FILE);
        } catch (IOException e) {
            System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
        }
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    // 테스트용 데이터 클래스
    private static class TestBookData {
        String bookId;
        String title;
        String authorId;
        String context;
    }
}
