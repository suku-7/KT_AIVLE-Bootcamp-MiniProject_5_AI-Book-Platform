package thminiprojthebook.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "libraryInfos",
    path = "libraryInfos"
)
public interface LibraryInfoRepository
    extends PagingAndSortingRepository<LibraryInfo, Long> {}
