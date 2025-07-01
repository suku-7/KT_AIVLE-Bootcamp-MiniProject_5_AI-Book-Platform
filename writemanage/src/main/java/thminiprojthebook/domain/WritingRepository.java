package thminiprojthebook.domain;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "writings", path = "writings")
public interface WritingRepository
    extends PagingAndSortingRepository<Writing, Long> {
        List<Writing> findByAuthorName(String authorName);
        List<Writing> findByAuthorId(Long authorId);
    }
