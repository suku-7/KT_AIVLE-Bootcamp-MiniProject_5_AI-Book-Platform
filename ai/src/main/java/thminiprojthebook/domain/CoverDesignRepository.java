package thminiprojthebook.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "coverDesigns",
    path = "coverDesigns"
)
public interface CoverDesignRepository
    extends PagingAndSortingRepository<CoverDesign, Long> {}
