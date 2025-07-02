package thminiprojthebook.infra;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thminiprojthebook.domain.*;
import thminiprojthebook.service.GptService;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/contentAnalyzers")
@Transactional
public class ContentAnalyzerController {

    @Autowired
    ContentAnalyzerRepository contentAnalyzerRepository;

    /**
     * AI 요약 생성 요청
     */
    @RequestMapping(
        value = "/customs/contentAnalyzers/{id}/generateSummary",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public ContentAnalyzer generateSummary(
        @PathVariable(value = "id") Long id,
        @RequestParam(value = "summaryType", defaultValue = "일반요약") String summaryType,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /contentAnalyzers/{id}/generateSummary called #####");
        
        Optional<ContentAnalyzer> optionalAnalyzer = contentAnalyzerRepository.findById(id);
        optionalAnalyzer.orElseThrow(() -> new Exception("ContentAnalyzer not found with ID: " + id));
        
        ContentAnalyzer analyzer = optionalAnalyzer.get();
        
        try {
            GptService gptService = new GptService();
            String summary = gptService.generateSummary(
                analyzer.getContext(),
                analyzer.getMaxLength() != null ? analyzer.getMaxLength() : 500,
                analyzer.getLanguage() != null ? analyzer.getLanguage() : "KO",
                summaryType
            );
            
            if (summary != null && !summary.trim().isEmpty()) {
                analyzer.setSummary(summary);
                contentAnalyzerRepository.save(analyzer);
                
                // AI 요약 완료 이벤트 발행
                AiSummarized aiSummarized = new AiSummarized(analyzer);
                aiSummarized.publishAfterCommit();
                
                System.out.println("Summary generated successfully for ContentAnalyzer ID: " + id);
                System.out.println("Summary preview: " + summary.substring(0, Math.min(50, summary.length())) + "...");
            } else {
                throw new Exception("Failed to generate summary - empty response from AI service");
            }
            
        } catch (Exception e) {
            System.err.println("Error generating summary for ContentAnalyzer ID " + id + ": " + e.getMessage());
            throw new Exception("AI summary generation failed: " + e.getMessage());
        }
        
        return analyzer;
    }

    /**
     * AI 장르 분류 요청
     */
    @RequestMapping(
        value = "/customs/contentAnalyzers/{id}/classifyGenre",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public ContentAnalyzer classifyGenre(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /contentAnalyzers/{id}/classifyGenre called #####");
        
        Optional<ContentAnalyzer> optionalAnalyzer = contentAnalyzerRepository.findById(id);
        optionalAnalyzer.orElseThrow(() -> new Exception("ContentAnalyzer not found with ID: " + id));
        
        ContentAnalyzer analyzer = optionalAnalyzer.get();
        
        try {
            GptService gptService = new GptService();
            
            // 제목과 요약(또는 원본 내용)을 사용하여 장르 분류
            String contentForClassification = analyzer.getSummary() != null ? 
                analyzer.getSummary() : analyzer.getContext();
                
            String genre = gptService.classifyGenre(
                analyzer.getBookId().toString(), // 제목 역할
                contentForClassification
            );
            
            if (genre != null && !genre.trim().isEmpty()) {
                analyzer.setClassificationType(genre);
                contentAnalyzerRepository.save(analyzer);
                
                System.out.println("Genre classified successfully for ContentAnalyzer ID: " + id + " -> " + genre);
            } else {
                throw new Exception("Failed to classify genre - empty response from AI service");
            }
            
        } catch (Exception e) {
            System.err.println("Error classifying genre for ContentAnalyzer ID " + id + ": " + e.getMessage());
            throw new Exception("AI genre classification failed: " + e.getMessage());
        }
        
        return analyzer;
    }

    /**
     * AI 프로세스 실행 (요약 + 장르분류)
     */
    @RequestMapping(
        value = "/customs/contentAnalyzers/{id}/processAnalysis",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public ResponseEntity<?> processAnalysis(
        @PathVariable(value = "id") Long id,
        @RequestParam(value = "summaryType", defaultValue = "일반요약") String summaryType,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /contentAnalyzers/{id}/processAnalysis called #####");
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            // ContentAnalyzer 존재 확인
            Optional<ContentAnalyzer> optionalAnalyzer = contentAnalyzerRepository.findById(id);
            if (!optionalAnalyzer.isPresent()) {
                result.put("status", "ERROR");
                result.put("message", "ContentAnalyzer not found with ID: " + id);
                return ResponseEntity.badRequest().body(result);
            }
            
            System.out.println("Starting AI analysis for ContentAnalyzer ID: " + id);
            
            // 1. 요약 생성
            ContentAnalyzer analyzer = generateSummary(id, summaryType, request, response);
            System.out.println("Step 1/2 completed: Summary generation");
            
            // 2. 장르 분류
            analyzer = classifyGenre(id, request, response);
            System.out.println("Step 2/2 completed: Genre classification");
            
            // 결과 반환
            result.put("contentAnalyzer", analyzer);
            result.put("status", "SUCCESS");
            result.put("message", "AI analysis completed successfully");
            result.put("summary", analyzer.getSummary());
            result.put("genre", analyzer.getClassificationType());
            
            System.out.println("AI analysis completed successfully for ContentAnalyzer ID: " + id);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("Error in AI analysis for ContentAnalyzer ID " + id + ": " + e.getMessage());
            
            result.put("status", "ERROR");
            result.put("message", "AI analysis failed: " + e.getMessage());
            result.put("contentAnalyzerId", id);
            
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 미처리 AI 요청 목록 조회 (관리자용)
     */
    @GetMapping("/customs/contentAnalyzers/pending")
    public List<ContentAnalyzer> getPendingAnalyzers() {
        // AI 처리가 필요한 콘텐츠 조회 (요약이 없거나 분류가 없는 것들)
        return contentAnalyzerRepository.findPendingForAIProcessing();
    }

    /**
     * 특정 저자의 AI 처리 상태 조회 (관리자용)
     */
    @GetMapping("/customs/contentAnalyzers/author/{authorId}")
    public List<ContentAnalyzer> getAnalyzersByAuthor(@PathVariable(value = "authorId") Long authorId) {
        return contentAnalyzerRepository.findByAuthorId(authorId);
    }

    /**
     * AI 처리 완료된 콘텐츠 조회 (요약과 분류가 모두 완료된 것들)
     */
    @GetMapping("/customs/contentAnalyzers/completed")
    public List<ContentAnalyzer> getCompletedAnalyzers() {
        List<ContentAnalyzer> allAnalyzers = contentAnalyzerRepository.findAllAsList();
        return allAnalyzers.stream()
            .filter(analyzer -> analyzer.getSummary() != null && analyzer.getClassificationType() != null)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * AI 요약만 완료된 콘텐츠 조회
     */
    @GetMapping("/customs/contentAnalyzers/summary-only")
    public List<ContentAnalyzer> getSummaryOnlyAnalyzers() {
        List<ContentAnalyzer> allAnalyzers = contentAnalyzerRepository.findAllAsList();
        return allAnalyzers.stream()
            .filter(analyzer -> analyzer.getSummary() != null && analyzer.getClassificationType() == null)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * ContentAnalyzer 생성/등록 (AuthorController의 패턴을 따라)
     */
    @RequestMapping(
        value = "/customs/contentAnalyzers",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public ContentAnalyzer createContentAnalyzer(
        @RequestBody ContentAnalyzer contentAnalyzer,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /contentAnalyzers POST called #####");
        
        try {
            // 기본값 설정
            if (contentAnalyzer.getLanguage() == null) {
                contentAnalyzer.setLanguage("KO");
            }
            if (contentAnalyzer.getMaxLength() == null) {
                contentAnalyzer.setMaxLength(500);
            }
            if (contentAnalyzer.getRequestedBy() == null) {
                contentAnalyzer.setRequestedBy("SYSTEM");
            }
            
            ContentAnalyzer savedAnalyzer = contentAnalyzerRepository.save(contentAnalyzer);
            System.out.println("ContentAnalyzer created successfully with ID: " + savedAnalyzer.getAuthorId());
            
            return savedAnalyzer;
            
        } catch (Exception e) {
            System.err.println("Error creating ContentAnalyzer: " + e.getMessage());
            throw new Exception("Failed to create ContentAnalyzer: " + e.getMessage());
        }
    }

    /**
     * AI 처리 상태 조회
     */
    @GetMapping("/customs/contentAnalyzers/{id}/status")
    public ResponseEntity<?> getProcessingStatus(@PathVariable(value = "id") Long id) {
        Optional<ContentAnalyzer> optionalAnalyzer = contentAnalyzerRepository.findById(id);
        
        if (!optionalAnalyzer.isPresent()) {
            java.util.Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "ContentAnalyzer not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
        
        ContentAnalyzer analyzer = optionalAnalyzer.get();
        
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        status.put("id", analyzer.getAuthorId());
        status.put("bookId", analyzer.getBookId());
        status.put("hasSummary", analyzer.getSummary() != null);
        status.put("hasClassification", analyzer.getClassificationType() != null);
        status.put("requestedBy", analyzer.getRequestedBy());
        status.put("language", analyzer.getLanguage());
        status.put("maxLength", analyzer.getMaxLength());
        
        // 처리 완료율 계산 (요약과 분류 기준)
        int completionCount = 0;
        if (analyzer.getSummary() != null) completionCount++;
        if (analyzer.getClassificationType() != null) completionCount++;
        status.put("completionRate", (completionCount / 2.0) * 100);
        
        return ResponseEntity.ok(status);
    }

    /**
     * 모든 ContentAnalyzer 조회 (장르 분류 결과 포함)
     */
    @GetMapping("/contentAnalyzers")
    public List<ContentAnalyzer> getAllContentAnalyzers() {
        System.out.println("##### GET /contentAnalyzers called #####");
        List<ContentAnalyzer> analyzers = contentAnalyzerRepository.findAllAsList();
        
        // 각 analyzer의 상태를 로그로 출력
        for (ContentAnalyzer analyzer : analyzers) {
            System.out.println("ContentAnalyzer ID: " + analyzer.getAuthorId() + 
                             ", BookID: " + analyzer.getBookId() +
                             ", Genre: " + analyzer.getClassificationType() +
                             ", HasSummary: " + (analyzer.getSummary() != null));
        }
        
        return analyzers;
    }
}
//>>> Clean Arch / Inbound Adaptor
