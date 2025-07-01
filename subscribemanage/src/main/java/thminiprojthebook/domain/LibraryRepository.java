package thminiprojthebook.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "libraries", path = "libraries")
public interface LibraryRepository
    extends PagingAndSortingRepository<Library, Long> {}
