package thminiprojthebook.integration;

import thminiprojthebook.service.DalleService;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * API 키 설정 상태 확인 및 간단한 연결 테스트
 */
public class ApiKeyCheckTest {

    private static final String RESULTS_DIR = "src/test/java/test_results/";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static void main(String[] args) {
        System.out.println("=== OpenAI API 키 설정 상태 확인 ===");
        
        ApiKeyCheckTest test = new ApiKeyCheckTest();
        
        // 결과 디렉토리 생성
        new java.io.File(RESULTS_DIR).mkdirs();
        
        try {
            // 환경변수 확인
            test.checkEnvironmentVariables();
            
            // DalleService 생성 및 설정 확인
            test.checkDalleServiceConfiguration();
            
        } catch (Exception e) {
            System.err.println("테스트 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== API 키 확인 완료 ===");
    }

    public void checkEnvironmentVariables() {
        System.out.println("\n=== 환경변수 확인 ===");
        
        String apiKey = System.getenv("OPENAI_API_KEY");
        
        StringBuilder report = new StringBuilder();
        report.append("=== OpenAI API 키 환경변수 확인 결과 ===\n");
        report.append("확인 시간: ").append(dateFormat.format(new Date())).append("\n\n");
        
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            String maskedKey = maskApiKey(apiKey);
            report.append("✅ OPENAI_API_KEY 환경변수 설정됨: ").append(maskedKey).append("\n");
            report.append("상태: 실제 API 호출 가능\n");
            System.out.println("✅ OPENAI_API_KEY 환경변수가 설정되어 있습니다: " + maskedKey);
        } else {
            report.append("❌ OPENAI_API_KEY 환경변수가 설정되지 않음\n");
            report.append("상태: Mock 서비스만 사용 가능\n");
            report.append("\n=== 설정 방법 ===\n");
            report.append("Windows PowerShell: $env:OPENAI_API_KEY=\"your-api-key\"\n");
            report.append("Windows CMD: set OPENAI_API_KEY=your-api-key\n");
            report.append("Linux/Mac: export OPENAI_API_KEY=\"your-api-key\"\n");
            System.out.println("❌ OPENAI_API_KEY 환경변수가 설정되지 않았습니다");
        }
        
        // 결과 저장
        saveReport("api_key_check", report.toString());
    }

    public void checkDalleServiceConfiguration() {
        System.out.println("\n=== DalleService 설정 확인 ===");
        
        try {
            DalleService dalleService = new DalleService();
            
            StringBuilder report = new StringBuilder();
            report.append("=== DalleService 구성 확인 결과 ===\n");
            report.append("확인 시간: ").append(dateFormat.format(new Date())).append("\n\n");
            
            report.append("✅ DalleService 객체 생성 성공\n");
            report.append("클래스: ").append(dalleService.getClass().getName()).append("\n");
            
            // 간단한 API 호출 시뮬레이션 (실제 호출 없이)
            report.append("\n=== 테스트 시나리오 ===\n");
            report.append("제목: 테스트 책\n");
            report.append("설명: API 연결 테스트용 샘플 책\n");
            
            System.out.println("✅ DalleService 객체가 정상적으로 생성되었습니다");
            System.out.println("📋 실제 이미지 생성을 원하시면 RealImageGenerationTest를 실행하세요");
            
            // 결과 저장
            saveReport("dalle_service_check", report.toString());
            
        } catch (Exception e) {
            System.err.println("❌ DalleService 설정 확인 실패: " + e.getMessage());
            
            StringBuilder errorReport = new StringBuilder();
            errorReport.append("=== DalleService 설정 오류 ===\n");
            errorReport.append("오류 시간: ").append(dateFormat.format(new Date())).append("\n");
            errorReport.append("오류 메시지: ").append(e.getMessage()).append("\n");
            errorReport.append("오류 타입: ").append(e.getClass().getSimpleName()).append("\n");
            
            saveReport("dalle_service_error", errorReport.toString());
        }
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        
        String prefix = apiKey.substring(0, 7);
        String suffix = apiKey.substring(apiKey.length() - 4);
        return prefix + "..." + suffix;
    }

    private void saveReport(String type, String content) {
        try {
            String timestamp = dateFormat.format(new Date());
            String filename = RESULTS_DIR + type + "_" + timestamp + ".txt";
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(content);
            }
            
            System.out.println("📄 결과가 저장되었습니다: " + filename);
            
        } catch (IOException e) {
            System.err.println("결과 파일 저장 실패: " + e.getMessage());
        }
    }
}
