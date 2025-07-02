// =================================================================
// FILENAME: subscribemanage/domain/UserRepository.java (수정)
// 역할: loginId로 사용자를 찾을 수 있는 기능을 추가합니다.
// =================================================================
package thminiprojthebook.domain;

import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import thminiprojthebook.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    
    // 이 메서드 한 줄을 추가합니다.
    Optional<User> findByLoginId(String loginId);

}