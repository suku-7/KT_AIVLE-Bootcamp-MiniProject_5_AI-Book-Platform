package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.PointApplication;
import thminiprojthebook.domain.KtSignedupPointCharged;
import thminiprojthebook.domain.PointDecreased;
import thminiprojthebook.domain.StandardSignedupPointCharged;

@Entity
@Table(name = "Point_table")
@Data
//<<< DDD / Aggregate Root
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private Integer pointBalance;

    private String isKt;

    public static PointRepository repository() {
        PointRepository pointRepository = PointApplication.applicationContext.getBean(
            PointRepository.class
        );
        return pointRepository;
    }

    //=================================================================
    // 1. 애그리거트 내부의 비즈니스 메서드들
    //=================================================================

    public void pointRecharge(PointRechargeCommand command) {
        if(this.pointBalance == null) this.pointBalance = 0;
        this.setPointBalance(this.getPointBalance() + command.getAmount());

        PointRecharged pointRecharged = new PointRecharged(this);
        pointRecharged.publishAfterCommit();
    }

    public void decreaseForSubscription() {
        this.setPointBalance(this.getPointBalance() - 11900);
        PointDecreased pointDecreased = new PointDecreased(this);
        pointDecreased.publishAfterCommit();
    }

    public void decreaseForBookPurchase() {
        this.setPointBalance(this.getPointBalance() - 5000);
        PointDecreased pointDecreased = new PointDecreased(this);
        pointDecreased.publishAfterCommit();
    }

    public void applyKtUpgrade() {
        if (this.getIsKt() == null || !this.getIsKt().equals("true")) {
            this.setIsKt("true");
            if(this.pointBalance == null) this.pointBalance = 0;
            this.setPointBalance(this.getPointBalance() + 5000);
            KtSignedupPointCharged ktCharged = new KtSignedupPointCharged(this);
            ktCharged.publishAfterCommit();
        }
    }

    //=================================================================
    // 2. 외부 이벤트를 수신하는 Policy 핸들러들
    //=================================================================

    public static void pointDecrease(BuyBookSub buyBookSub) {
        repository().findById(buyBookSub.getUserId()).ifPresent(point -> {
            point.decreaseForBookPurchase();
            repository().save(point);
        });
    }

    public static void pointDecrease(BookServiceSubscribed bookServiceSubscribed) {
        repository().findById(bookServiceSubscribed.getUserId()).ifPresent(point -> {
            point.decreaseForSubscription();
            repository().save(point);
        });
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void initialPointPolicy(UserRegistered userRegistered) {
        Point point = new Point();
        point.setUserId(userRegistered.getUserId());
        point.setIsKt(userRegistered.getIsKt());
        point.setPointBalance(1000);

        boolean isKtUser = "true".equals(userRegistered.getIsKt());
        if (isKtUser) {
            point.setPointBalance(point.getPointBalance() + 5000);
        }
        repository().save(point);

        StandardSignedupPointCharged standard = new StandardSignedupPointCharged(point);
        standard.publishAfterCommit();

        if (isKtUser) {
            KtSignedupPointCharged kt = new KtSignedupPointCharged(point);
            kt.publishAfterCommit();
        }
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void ktSignedupPointPolicy(UserUpdated userUpdated) {
        if ("true".equals(userUpdated.getIsKt())) {
            repository().findById(userUpdated.getUserId()).ifPresent(point -> {
                point.applyKtUpgrade();
                repository().save(point);
            });
        }
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
