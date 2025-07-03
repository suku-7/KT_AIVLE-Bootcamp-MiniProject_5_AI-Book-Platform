package thminiprojthebook.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;
import java.util.List; // 베셀 조회 추가시 리스트

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "libraryInfos",
    path = "libraryInfos"
)
public interface LibraryInfoRepository
    extends PagingAndSortingRepository<LibraryInfo, Long> {
        // 베스트셀러 조회 메서드 추가
    List<LibraryInfo> findByBestsellerTrue();
    }
