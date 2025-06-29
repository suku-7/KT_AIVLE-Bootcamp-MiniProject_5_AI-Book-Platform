package thminiprojthebook.infra;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;

@RepositoryRestResource(
    collectionResourceRel = "subscribeLists",
    path = "subscribeLists"
)
public interface SubscribeListRepository
    extends PagingAndSortingRepository<SubscribeList, Long> {}
