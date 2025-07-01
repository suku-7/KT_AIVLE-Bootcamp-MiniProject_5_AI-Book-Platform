package thminiprojthebook.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "coverDesigns",
    path = "coverDesigns"
)
public interface CoverDesignRepository
    extends PagingAndSortingRepository<CoverDesign, Long> {
    
    // 특정 저자의 커버 디자인 조회
    List<CoverDesign> findByAuthorId(Long authorId);
    
    // 특정 제목의 커버 디자인 조회
    List<CoverDesign> findByTitle(String title);
    
    // 특정 생성 방식의 커버 디자인 조회 (예: DALL-E-3)
    List<CoverDesign> findByGeneratedBy(String generatedBy);
}
