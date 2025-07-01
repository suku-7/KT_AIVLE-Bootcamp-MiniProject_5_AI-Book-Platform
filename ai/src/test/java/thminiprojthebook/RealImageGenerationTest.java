package thminiprojthebook;

import thminiprojthebook.service.DalleService;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 실제 DALL-E API를 사용한 이미지 생성 테스트
 * API 키가 설정되어 있을 때만 실제 이미지 생성
 */
public class RealImageGenerationTest {
    
    private static final String RESULTS_DIR = "src/test/results/";
    
    public static void main(String[] args) {
        System.out.println("=== 실제 DALL-E API 이미지 생성 테스트 ===\n");
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String resultFileName = RESULTS_DIR + "real_image_generation_" + timestamp + ".txt";
        
        try {
            // API 키 확인
            String apiKey = System.getenv("OPENAI_API_KEY");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.out.println("❌ OPENAI_API_KEY 환경변수가 설정되지 않았습니다.");
                System.out.println("실제 이미지 생성을 위해 API 키를 설정하세요:");
                System.out.println("1. ./setup_api_key.sh 실행");
                System.out.println("2. 또는 export OPENAI_API_KEY=\"your-api-key\"");
                return;
            }
            
            System.out.println("✅ API 키 확인됨: " + apiKey.substring(0, 10) + "...");
            System.out.println("🚀 실제 DALL-E API로 이미지 생성을 시작합니다...\n");
            
            DalleService dalleService = new DalleService();
            StringBuilder resultLog = new StringBuilder();
            
            // 테스트 케이스들
            String[][] testCases = {
                {"마법사의 모험", "한때 평범한 고등학생이었던 김민수는 어느 날 신비로운 책을 발견하게 된다."},
                {"디지털 혁신과 AI의 미래", "4차 산업혁명 시대에 인공지능과 디지털 기술이 어떻게 우리의 삶을 변화시키는지 다룬다."},
                {"코딩으로 배우는 알고리즘", "프로그래밍 초보자를 위한 실전 알고리즘 학습서"}
            };
            
            resultLog.append("=== 실제 DALL-E API 이미지 생성 테스트 결과 ===\n");
            resultLog.append("테스트 시간: ").append(LocalDateTime.now()).append("\n");
            resultLog.append("API 키: ").append(apiKey.substring(0, 10)).append("...\n\n");
            
            int successCount = 0;
            for (int i = 0; i < testCases.length; i++) {
                String title = testCases[i][0];
                String context = testCases[i][1];
                
                System.out.println("📚 테스트 " + (i + 1) + ": " + title);
                System.out.println("   내용: " + context.substring(0, Math.min(50, context.length())) + "...");
                System.out.println("   🎨 이미지 생성 중... (약 5-10초 소요)");
                
                resultLog.append("--- 테스트 ").append(i + 1).append(" ---\n");
                resultLog.append("제목: ").append(title).append("\n");
                resultLog.append("내용: ").append(context).append("\n");
                
                try {
                    String imageUrl = dalleService.generateCoverImage(title, context);
                    
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        if (imageUrl.contains("oaidalleapiprodscus.blob.core.windows.net")) {
                            System.out.println("   ✅ 실제 이미지 생성 성공!");
                            System.out.println("   🌐 URL: " + imageUrl);
                            System.out.println("   📌 브라우저에서 위 URL을 열어 이미지를 확인하세요.\n");
                            
                            resultLog.append("결과: 성공 (실제 DALL-E API)\n");
                            resultLog.append("이미지 URL: ").append(imageUrl).append("\n");
                            resultLog.append("URL 접근 가능 여부: YES (실제 DALL-E API로 생성된 이미지)\n");
                            successCount++;
                        } else {
                            System.out.println("   ⚠️  Mock URL 반환: " + imageUrl);
                            resultLog.append("결과: Mock URL 반환\n");
                            resultLog.append("URL: ").append(imageUrl).append("\n");
                        }
                    } else {
                        System.out.println("   ❌ 이미지 생성 실패");
                        resultLog.append("결과: 실패\n");
                    }
                } catch (Exception e) {
                    System.out.println("   ❌ 오류 발생: " + e.getMessage());
                    resultLog.append("결과: 오류 - ").append(e.getMessage()).append("\n");
                }
                
                resultLog.append("\n");
                
                // API 호출 간격 (Rate Limit 방지)
                if (i < testCases.length - 1) {
                    Thread.sleep(2000);
                }
            }
            
            // 결과 요약
            System.out.println("=== 테스트 결과 요약 ===");
            System.out.println("🏆 성공한 이미지: " + successCount + "/" + testCases.length);
            System.out.println("💰 예상 비용: $" + String.format("%.3f", successCount * 0.04));
            
            resultLog.append("=== 결과 요약 ===\n");
            resultLog.append("성공한 이미지 수: ").append(successCount).append("/").append(testCases.length).append("\n");
            resultLog.append("예상 비용: $").append(String.format("%.3f", successCount * 0.04)).append("\n");
            
            if (successCount > 0) {
                System.out.println("🎉 실제 이미지가 생성되었습니다!");
                System.out.println("📂 결과 파일: " + resultFileName);
            } else {
                System.out.println("⚠️  실제 이미지 생성에 실패했습니다.");
                resultFileName = RESULTS_DIR + "real_image_generation_failure_" + timestamp + ".txt";
            }
            
            // 결과를 파일에 저장
            saveResult(resultFileName, resultLog.toString());
            
        } catch (Exception e) {
            System.err.println("❌ 테스트 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void saveResult(String fileName, String content) {
        try {
            // 디렉토리가 없으면 생성
            java.io.File directory = new java.io.File(RESULTS_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(content);
            }
            System.out.println("📁 결과가 저장되었습니다: " + fileName);
        } catch (IOException e) {
            System.err.println("❌ 결과 파일 저장 실패: " + e.getMessage());
        }
    }
}
