package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BuyBookSub extends AbstractEvent {

    private Long userId;
    private Long bookId; // <-- List<Long> 에서 Long 으로 수정

    public BuyBookSub(User aggregate) {
        super(aggregate);
    }

    public BuyBookSub() {
        super();
    }
}
//>>> DDD / Domain Event
