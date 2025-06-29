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

    private Long id;

    public BuyBookSub(SubscribedBook aggregate) {
        super(aggregate);
    }

    public BuyBookSub() {
        super();
    }
}
//>>> DDD / Domain Event
