package thminiprojthebook.infra;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;

@RepositoryRestResource(
    collectionResourceRel = "approvalAuthors",
    path = "approvalAuthors"
)
public interface ApprovalAuthorRepository
    extends PagingAndSortingRepository<ApprovalAuthor, Long> {}
