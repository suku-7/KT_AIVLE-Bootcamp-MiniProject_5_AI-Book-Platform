package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
public static void pointDecrease(BuyBookSub event) {
    repository().findByUserId(event.getSubscriberId()).ifPresentOrElse(point -> {
        int bookPrice = 1000; // 현재 정책

        if (point.getPointBalance() >= bookPrice) {
            point.setPointBalance(point.getPointBalance() - bookPrice);
            repository().save(point);

            PointDecreased decreased = new PointDecreased(point);
            decreased.setUserId(point.getUserId());
            decreased.publishAfterCommit();

        } else {
            PointInsufficient insufficient = new PointInsufficient(point);
            insufficient.setUserId(point.getUserId());
            insufficient.setPointBalance(point.getPointBalance());
            insufficient.publishAfterCommit();
        }
    }, () -> {
        System.err.println("포인트 정보 없음: userId = " + event.getSubscriberId());
    });
}
//>>> DDD / Domain Event
