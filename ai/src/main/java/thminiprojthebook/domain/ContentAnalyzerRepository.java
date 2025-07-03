package thminiprojthebook.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "contentAnalyzers",
    path = "contentAnalyzers"
) //CRUD
public interface ContentAnalyzerRepository
    extends PagingAndSortingRepository<ContentAnalyzer, Long> {
    
    // AI 처리가 필요한 콘텐츠 조회 (요약이 없거나 분류가 없는 것들)
    @Query("SELECT ca FROM ContentAnalyzer ca WHERE ca.summary IS NULL OR ca.classificationType IS NULL")
    List<ContentAnalyzer> findPendingForAIProcessing();
    
    // 특정 저자의 콘텐츠 분석 조회
    List<ContentAnalyzer> findByAuthorId(Long authorId);
    
    // 특정 북 ID의 콘텐츠 분석 조회
    List<ContentAnalyzer> findByBookId(Long bookId);
    
    // 모든 ContentAnalyzer를 List로 반환
    @Query("SELECT ca FROM ContentAnalyzer ca")
    List<ContentAnalyzer> findAllAsList();
}
