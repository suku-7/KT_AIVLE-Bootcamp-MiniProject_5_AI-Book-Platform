package thminiprojthebook.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "contentAnalyzers",
    path = "contentAnalyzers"
)
public interface ContentAnalyzerRepository
    extends PagingAndSortingRepository<ContentAnalyzer, Long> {}
