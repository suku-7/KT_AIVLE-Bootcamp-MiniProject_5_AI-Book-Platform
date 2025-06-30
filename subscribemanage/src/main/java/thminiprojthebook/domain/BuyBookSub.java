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

    private Long subscribedBookId;
    private Boolean status;
    private Long bookId;
    private SubscriberId subscriberId;

    public BuyBookSub(SubscribedBook aggregate) {
        super(aggregate);
        this.subscribedBookId = aggregate.getSubscribedBookId();
        this.bookId = aggregate.getBookId();
        this.status = aggregate.getStatus();
        this.subscriberId = aggregate.getSubscriberId();
    }

    public BuyBookSub() {
        super();
    }
}
//>>> DDD / Domain Event

