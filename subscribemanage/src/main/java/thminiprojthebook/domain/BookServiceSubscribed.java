package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BookServiceSubscribed extends AbstractEvent {

    private Long userId;
    private String isSubscribe;

    public BookServiceSubscribed(User aggregate) {
        super(aggregate);
    }

    public BookServiceSubscribed() {
        super();
    }
}
//>>> DDD / Domain Event
