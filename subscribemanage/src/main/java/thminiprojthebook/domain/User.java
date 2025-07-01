// User.java

package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.SubscribemanageApplication;
import thminiprojthebook.domain.UserRegistered;

@Entity
@Table(name = "User_table")
@Data
//<<< DDD / Aggregate Root
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String loginId;

    private String loginPassword;

    private String name;

    private String isKt;

    private String isSubscribe;

    private Integer pointBalance;

    private Date subscriptionDate;

    @ElementCollection
    private List<Long> bookId;

    @PostPersist
    public void onPostPersist() {
        UserRegistered userRegistered = new UserRegistered(this);
        userRegistered.publishAfterCommit();
    }

    public static UserRepository repository() {
        UserRepository userRepository = SubscribemanageApplication.applicationContext.getBean(
            UserRepository.class
        );
        return userRepository;
    }

    //<<< Clean Arch / Port Method
    public void subscribeToBookService() {
        // 1. 포인트 잔액이 11900 이상인지 확인하는 로직
        if (this.getPointBalance() == null || this.getPointBalance() < 11900) {
            throw new RuntimeException(
                "포인트가 부족하여 구독할 수 없습니다. (현재 잔액: " + this.getPointBalance() + "P)"
            );
        }

        // 2. 포인트 차감 로직 삭제!
        // this.setPointBalance(this.getPointBalance() - 11900);  // <-- 이 줄을 삭제합니다.
        
        // 3. 구독 상태 변경
        String subscriptionDate = java.time.format.DateTimeFormatter
                                    .ofPattern("yyyy-MM-dd")
                                    .format(java.time.LocalDate.now());
        this.setIsSubscribe("월간 구독 중 (시작일: " + subscriptionDate + ")");

        // 4. 구독 완료 이벤트 발행 (point 서비스가 이 이벤트를 듣고 실제 차감을 진행)
        BookServiceSubscribed bookServiceSubscribed = new BookServiceSubscribed(
            this
        );
        bookServiceSubscribed.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void buyBook(BuyBookCommand buyBookCommand) {
        // 1. 포인트 잔액이 5000 이상인지 확인
        if (this.getPointBalance() == null || this.getPointBalance() < 5000) {
            throw new RuntimeException(
                "포인트가 부족하여 책을 구매할 수 없습니다. (현재 잔액: " + this.getPointBalance() + "P)"
            );
        }

        // 2. 포인트 차감 로직 삭제!
        // this.setPointBalance(this.getPointBalance() - 5000); // <-- 이 줄을 삭제합니다.

        // 3. 소장 목록에 새로운 bookId 추가
        if (this.getBookId() == null) {
            this.bookId = new java.util.ArrayList<>();
        }
        this.getBookId().add(buyBookCommand.getBookId());

        // 4. 소장 신청 이벤트 발행 (point 서비스가 이 이벤트를 듣고 실제 차감을 진행)
        BuyBookSub buyBookSub = new BuyBookSub(); // 빈 생성자로 생성
        buyBookSub.setUserId(this.getUserId());
        buyBookSub.setBookId(buyBookCommand.getBookId()); // 방금 구매한 bookId 하나만 담기
        
        buyBookSub.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void updateUser(UpdateUserCommand updateUserCommand) {
        // Command 객체로부터 받은 데이터로 현재 User 객체의 상태를 업데이트합니다.
        this.setLoginId(updateUserCommand.getLoginId());
        this.setLoginPassword(updateUserCommand.getLoginPassword());
        this.setName(updateUserCommand.getName());
        this.setIsKt(updateUserCommand.getIsKt());

        // 회원 정보가 수정되었다는 이벤트를 발행합니다.
        // UserUpdated 이벤트 클래스가 사전에 정의되어 있어야 합니다.
        UserUpdated userUpdated = new UserUpdated(this);
        userUpdated.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void cancelSubscribeToBookService() {
        // 1. 구독 상태와 관련된 필드들을 초기화/변경합니다.
        this.setIsSubscribe("구독 중이 아님");
        this.setSubscriptionDate(null); // 구독 날짜를 null로 초기화합니다.

        // 2. (선택사항이지만 권장) 구독 취소 이벤트를 발행합니다.
        // 이 이벤트를 통해 다른 서비스(예: 통계)가 구독 취소 사실을 알 수 있습니다.
        // BookServiceCancelled 이벤트 클래스가 사전에 정의되어 있어야 합니다.
        // BookServiceCancelled bookServiceCancelled = new BookServiceCancelled(this);
        // bookServiceCancelled.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method

    // [수정됨] String -> Long 타입 변환 코드 추가
    public static void pointSyncPolicy(PointDecreased pointDecreased) {
        Long userIdAsLong = Long.parseLong(pointDecreased.getUserId());
        repository()
            .findById(userIdAsLong)
            .ifPresent(user -> {
                user.setPointBalance(pointDecreased.getPointBalance());
                repository().save(user);
            });
    }

    // [수정됨] String -> Long 타입 변환 코드 추가
    public static void pointSyncPolicy(PointRecharged pointRecharged) {
        Long userIdAsLong = Long.parseLong(pointRecharged.getUserId());
        repository()
            .findById(userIdAsLong)
            .ifPresent(user -> {
                user.setPointBalance(pointRecharged.getPointBalance());
                repository().save(user);
            });
    }

    // [수정됨] String -> Long 타입 변환 코드 추가
    public static void pointSyncPolicy(
        KtSignedupPointCharged ktSignedupPointCharged
    ) {
        Long userIdAsLong = Long.parseLong(ktSignedupPointCharged.getUserId());
        repository()
            .findById(userIdAsLong)
            .ifPresent(user -> {
                user.setPointBalance(ktSignedupPointCharged.getPointBalance());
                repository().save(user);
            });
    }

    // [수정됨] String -> Long 타입 변환 코드 추가
    public static void pointSyncPolicy(
        StandardSignedupPointCharged standardSignedupPointCharged
    ) {
        Long userIdAsLong = Long.parseLong(
            standardSignedupPointCharged.getUserId()
        );
        repository()
            .findById(userIdAsLong)
            .ifPresent(user -> {
                user.setPointBalance(
                    standardSignedupPointCharged.getPointBalance()
                );
                repository().save(user);
            });
    }

}
//>>> DDD / Aggregate Root
