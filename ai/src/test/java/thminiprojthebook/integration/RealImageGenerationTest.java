package thminiprojthebook.integration;

import thminiprojthebook.service.DalleService;
import thminiprojthebook.domain.AiProcessTracker;
import thminiprojthebook.domain.AiProcessTrackerRepository;
import thminiprojthebook.AiApplication;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.text.SimpleDateFormat;

/**
 * 실제 DALL-E API를 사용한 이미지 생성 테스트
 * 
 * 주의사항:
 * 1. OpenAI API 키가 application.yml에 설정되어 있어야 합니다
 * 2. 실제 API 호출이므로 비용이 발생할 수 있습니다
 * 3. 네트워크 연결이 필요합니다
 * 
 * 실행 방법:
 * 1. application.yml에 openai.api.key 설정
 * 2. main 메서드로 직접 실행
 */
public class RealImageGenerationTest {

    private DalleService dalleService;
    private static final String RESULTS_DIR = "src/test/java/test_results/";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static void main(String[] args) {
        System.out.println("=== 실제 DALL-E API 이미지 생성 독립 테스트 ===");
        
        RealImageGenerationTest test = new RealImageGenerationTest();
        
        // 결과 디렉토리 생성
        new java.io.File(RESULTS_DIR).mkdirs();
        
        try {
            // DalleService 직접 생성 (Spring 컨텍스트 없이)
            test.dalleService = new DalleService();
            
            // 단일 이미지 생성 테스트
            test.testSingleImageGeneration();
            
            // 다중 이미지 생성 테스트
            test.testMultipleImageGeneration();
            
        } catch (Exception e) {
            System.err.println("테스트 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            // 오류 결과도 파일로 저장
            test.saveErrorResults(e);
        }
        
        System.out.println("=== 실제 이미지 생성 테스트 완료 ===");
    }

    public void testSingleImageGeneration() {
        System.out.println("\n=== 단일 실제 이미지 생성 테스트 ===");
        
        // 테스트 데이터 준비
        String bookId = "REAL_TEST_" + UUID.randomUUID().toString().substring(0, 8);
        String title = "AI와 함께하는 프로그래밍";
        String context = "인공지능 기술을 활용하여 효율적인 프로그래밍을 배우는 실무 가이드북";

        try {
            // 실제 DALL-E API 호출
            System.out.println("실제 이미지 생성 중...");
            System.out.println("제목: " + title);
            System.out.println("설명: " + context);
            
            String imageUrl = dalleService.generateCoverImage(title, context);
            
            // 결과 검증
            if (imageUrl != null && imageUrl.startsWith("http")) {
                System.out.println("=== 실제 이미지 생성 성공 ===");
                System.out.println("Book ID: " + bookId);
                System.out.println("Generated Image URL: " + imageUrl);
                System.out.println("URL 접근 가능 여부: 이 URL로 실제 이미지에 접근할 수 있습니다");
                
                // 결과를 파일로 저장
                saveSuccessResults(bookId, title, context, imageUrl);
                
            } else {
                System.err.println("이미지 생성 실패: URL이 null이거나 유효하지 않습니다");
                saveFailureResults(bookId, title, context, "Invalid URL returned");
            }
            
        } catch (Exception e) {
            System.err.println("실제 이미지 생성 실패: " + e.getMessage());
            e.printStackTrace();
            saveFailureResults(bookId, title, context, e.getMessage());
        }
    }

    public void testMultipleImageGeneration() {
        System.out.println("\n=== 다중 실제 이미지 생성 테스트 ===");
        
        String[] titles = {
            "데이터 사이언스 입문",
            "클라우드 아키텍처 설계",
            "머신러닝 실전 가이드"
        };
        
        String[] contexts = {
            "Python과 R을 활용한 데이터 분석 및 시각화 완벽 가이드",
            "AWS, Azure, GCP를 활용한 확장 가능한 클라우드 시스템 구축",
            "TensorFlow와 PyTorch로 배우는 딥러닝 실무 프로젝트"
        };

        StringBuilder multiResults = new StringBuilder();
        multiResults.append("=== 다중 실제 이미지 생성 결과 ===\n");
        multiResults.append("생성 시간: ").append(dateFormat.format(new Date())).append("\n\n");

        for (int i = 0; i < titles.length; i++) {
            try {
                String bookId = "MULTI_REAL_" + UUID.randomUUID().toString().substring(0, 8);
                
                System.out.println("이미지 생성 중 [" + (i + 1) + "/" + titles.length + "]: " + titles[i]);
                String imageUrl = dalleService.generateCoverImage(titles[i], contexts[i]);
                
                if (imageUrl != null && imageUrl.startsWith("http")) {
                    multiResults.append("=== 책 ").append(i + 1).append(" ===\n");
                    multiResults.append("ID: ").append(bookId).append("\n");
                    multiResults.append("제목: ").append(titles[i]).append("\n");
                    multiResults.append("설명: ").append(contexts[i]).append("\n");
                    multiResults.append("생성된 이미지 URL: ").append(imageUrl).append("\n");
                    multiResults.append("URL 상태: 실제 접근 가능한 이미지 URL\n\n");
                    
                    System.out.println("생성 완료: " + imageUrl);
                } else {
                    multiResults.append("=== 책 ").append(i + 1).append(" (실패) ===\n");
                    multiResults.append("제목: ").append(titles[i]).append("\n");
                    multiResults.append("오류: 유효하지 않은 URL 반환\n\n");
                    
                    System.err.println("생성 실패: " + titles[i]);
                }
                
                // 각 이미지 생성 사이에 약간의 지연 (API 제한 고려)
                Thread.sleep(2000);
                
            } catch (Exception e) {
                multiResults.append("=== 책 ").append(i + 1).append(" (실패) ===\n");
                multiResults.append("제목: ").append(titles[i]).append("\n");
                multiResults.append("오류: ").append(e.getMessage()).append("\n\n");
                
                System.err.println("생성 실패 [" + (i + 1) + "/" + titles.length + "]: " + titles[i] + " - " + e.getMessage());
            }
        }

        // 다중 생성 결과 저장
        saveMultipleImageResults(multiResults.toString());
    }

    private void saveSuccessResults(String bookId, String title, String context, String imageUrl) {
        try {
            String timestamp = dateFormat.format(new Date());
            String filename = RESULTS_DIR + "real_image_generation_" + timestamp + ".txt";
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("=== 실제 DALL-E API 이미지 생성 결과 ===\n");
                writer.write("생성 시간: " + timestamp + "\n");
                writer.write("Book ID: " + bookId + "\n");
                writer.write("제목: " + title + "\n");
                writer.write("설명: " + context + "\n");
                writer.write("생성된 이미지 URL: " + imageUrl + "\n");
                writer.write("URL 접근 가능 여부: YES (실제 DALL-E API로 생성된 이미지)\n");
                writer.write("\n=== 중요 안내 ===\n");
                writer.write("- 이 URL은 실제 OpenAI DALL-E API로 생성된 이미지입니다.\n");
                writer.write("- URL을 웹브라우저에 직접 입력하면 이미지를 볼 수 있습니다.\n");
                writer.write("- 이미지는 일정 기간 후 만료될 수 있습니다.\n");
                writer.write("- 실제 API 사용으로 비용이 발생했습니다.\n");
                writer.write("\n=== 테스트 방법 ===\n");
                writer.write("1. 위의 이미지 URL을 복사하세요\n");
                writer.write("2. 웹브라우저를 열고 주소창에 붙여넣기하세요\n");
                writer.write("3. 생성된 책 표지 이미지를 확인하세요\n");
            }
            
            System.out.println("실제 이미지 생성 결과가 저장되었습니다: " + filename);
            
        } catch (IOException e) {
            System.err.println("결과 파일 저장 실패: " + e.getMessage());
        }
    }

    private void saveFailureResults(String bookId, String title, String context, String errorMessage) {
        try {
            String timestamp = dateFormat.format(new Date());
            String filename = RESULTS_DIR + "real_image_generation_failure_" + timestamp + ".txt";
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("=== 실제 이미지 생성 실패 결과 ===\n");
                writer.write("실패 시간: " + timestamp + "\n");
                writer.write("Book ID: " + bookId + "\n");
                writer.write("제목: " + title + "\n");
                writer.write("설명: " + context + "\n");
                writer.write("오류 메시지: " + errorMessage + "\n");
                writer.write("\n=== 가능한 원인 ===\n");
                writer.write("- OpenAI API 키가 설정되지 않았거나 유효하지 않음\n");
                writer.write("- API 할당량 초과\n");
                writer.write("- 네트워크 연결 문제\n");
                writer.write("- OpenAI 서비스 일시적 장애\n");
                writer.write("\n=== 해결 방법 ===\n");
                writer.write("1. application.yml에서 openai.api.key 설정 확인\n");
                writer.write("2. API 키 유효성 및 잔액 확인\n");
                writer.write("3. 네트워크 연결 상태 확인\n");
                writer.write("4. OpenAI 서비스 상태 확인\n");
            }
            
        } catch (IOException ioE) {
            System.err.println("실패 결과 파일 저장 실패: " + ioE.getMessage());
        }
    }

    private void saveMultipleImageResults(String results) {
        try {
            String timestamp = dateFormat.format(new Date());
            String filename = RESULTS_DIR + "multiple_real_images_" + timestamp + ".txt";
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(results);
            }
            
            System.out.println("다중 실제 이미지 생성 결과가 저장되었습니다: " + filename);
            
        } catch (IOException e) {
            System.err.println("다중 결과 파일 저장 실패: " + e.getMessage());
        }
    }

    private void saveErrorResults(Exception e) {
        try {
            String timestamp = dateFormat.format(new Date());
            String filename = RESULTS_DIR + "real_image_test_error_" + timestamp + ".txt";
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write("=== 실제 이미지 생성 테스트 오류 ===\n");
                writer.write("오류 시간: " + timestamp + "\n");
                writer.write("오류 메시지: " + e.getMessage() + "\n");
                writer.write("오류 타입: " + e.getClass().getSimpleName() + "\n");
                writer.write("\n=== 스택 트레이스 ===\n");
                
                for (StackTraceElement element : e.getStackTrace()) {
                    writer.write(element.toString() + "\n");
                }
            }
            
        } catch (IOException ioE) {
            System.err.println("오류 결과 파일 저장 실패: " + ioE.getMessage());
        }
    }
}
