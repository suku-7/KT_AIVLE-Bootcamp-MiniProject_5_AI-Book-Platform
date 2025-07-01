package thminiprojthebook.infra;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.service.DalleService;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/coverDesigns")
@Transactional
public class CoverDesignController {

    @Autowired
    CoverDesignRepository coverDesignRepository;
    
    @Autowired
    ContentAnalyzerRepository contentAnalyzerRepository;

    /**
     * AI 커버 이미지 생성 요청
     */
    @RequestMapping(
        value = "/coverDesigns/generate",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public CoverDesign generateCoverImage(
        @RequestParam(value = "title") String title,
        @RequestParam(value = "context") String context,
        @RequestParam(value = "authorId") Long authorId,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /coverDesigns/generate called #####");
        
        try {
            DalleService dalleService = new DalleService();
            
            String imageUrl = dalleService.generateCoverImage(title, context);
            
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                // CoverDesign 엔티티 생성 및 저장
                CoverDesign coverDesign = new CoverDesign();
                coverDesign.setTitle(title);
                coverDesign.setImageUrl(imageUrl);
                coverDesign.setAuthorId(authorId);
                coverDesign.setGeneratedBy("DALL-E-3");
                coverDesign.setCreatedAt(new java.util.Date());
                coverDesign.setUpdatedAt(new java.util.Date());
                
                coverDesignRepository.save(coverDesign);
                
                // 커버 생성 완료 이벤트 발행
                CoverCreated coverCreated = new CoverCreated(coverDesign);
                coverCreated.publishAfterCommit();
                
                System.out.println("Cover image generated successfully: " + imageUrl);
                return coverDesign;
                
            } else {
                throw new Exception("Failed to generate cover image - empty response from AI service");
            }
            
        } catch (Exception e) {
            System.err.println("Error generating cover image: " + e.getMessage());
            throw new Exception("AI cover generation failed: " + e.getMessage());
        }
    }

    /**
     * ContentAnalyzer 기반 커버 생성
     */
    @RequestMapping(
        value = "/coverDesigns/generateFromAnalyzer/{analyzerId}",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public CoverDesign generateFromAnalyzer(
        @PathVariable(value = "analyzerId") Long analyzerId,
        @RequestParam(value = "title") String title,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /coverDesigns/generateFromAnalyzer called #####");
        
        Optional<ContentAnalyzer> optionalAnalyzer = contentAnalyzerRepository.findById(analyzerId);
        optionalAnalyzer.orElseThrow(() -> new Exception("ContentAnalyzer not found with ID: " + analyzerId));
        
        ContentAnalyzer analyzer = optionalAnalyzer.get();
        
        // 요약이나 원본 내용을 사용하여 이미지 생성
        String contextForImage = analyzer.getSummary() != null ? 
            analyzer.getSummary() : analyzer.getContext();
            
        return generateCoverImage(title, contextForImage, analyzer.getAuthorId(), request, response);
    }

    /**
     * 커버 디자인 승인
     */
    @RequestMapping(
        value = "/coverDesigns/{id}/approve",
        method = RequestMethod.POST,
        produces = "application/json;charset=UTF-8"
    )
    public CoverDesign approveCover(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /coverDesigns/{id}/approve called #####");
        
        Optional<CoverDesign> optionalCover = coverDesignRepository.findById(id);
        optionalCover.orElseThrow(() -> new Exception("CoverDesign not found with ID: " + id));
        
        CoverDesign coverDesign = optionalCover.get();
        coverDesign.setUpdatedAt(new java.util.Date());
        
        coverDesignRepository.save(coverDesign);
        
        System.out.println("Cover approved successfully: " + coverDesign.getTitle());
        return coverDesign;
    }

    /**
     * 저자별 커버 디자인 조회
     */
    @GetMapping("/coverDesigns/author/{authorId}")
    public List<CoverDesign> getCoversByAuthor(@PathVariable(value = "authorId") Long authorId) {
        return coverDesignRepository.findByAuthorId(authorId);
    }

    /**
     * 제목별 커버 디자인 조회
     */
    @GetMapping("/coverDesigns/title/{title}")
    public List<CoverDesign> getCoversByTitle(@PathVariable(value = "title") String title) {
        return coverDesignRepository.findByTitle(title);
    }

    /**
     * AI 생성 방식별 커버 디자인 조회
     */
    @GetMapping("/coverDesigns/generatedBy/{method}")
    public List<CoverDesign> getCoversByMethod(@PathVariable(value = "method") String method) {
        return coverDesignRepository.findByGeneratedBy(method);
    }

    /**
     * 커버 디자인 상태 조회
     */
    @GetMapping("/coverDesigns/{id}/status")
    public ResponseEntity<?> getCoverStatus(@PathVariable(value = "id") Long id) {
        Optional<CoverDesign> optionalCover = coverDesignRepository.findById(id);
        
        if (!optionalCover.isPresent()) {
            java.util.Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "CoverDesign not found with ID: " + id);
            return ResponseEntity.notFound().build();
        }
        
        CoverDesign coverDesign = optionalCover.get();
        
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        status.put("id", coverDesign.getId());
        status.put("title", coverDesign.getTitle());
        status.put("authorId", coverDesign.getAuthorId());
        status.put("bookId", coverDesign.getBookId());
        status.put("imageUrl", coverDesign.getImageUrl());
        status.put("generatedBy", coverDesign.getGeneratedBy());
        status.put("createdAt", coverDesign.getCreatedAt());
        status.put("updatedAt", coverDesign.getUpdatedAt());
        
        return ResponseEntity.ok(status);
    }
}
//>>> Clean Arch / Inbound Adaptor
