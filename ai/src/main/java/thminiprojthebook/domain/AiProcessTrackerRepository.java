package thminiprojthebook.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "aiProcessTrackers", path = "aiProcessTrackers")
public interface AiProcessTrackerRepository extends JpaRepository<AiProcessTracker, Long> {
    AiProcessTracker findByBookId(String bookId);
}
//>>> PoEAA / Repository
