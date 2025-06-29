package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class MonthlyBuy extends AbstractEvent {

    private Long id;

    public MonthlyBuy(Subscriber aggregate) {
        super(aggregate);
    }

    public MonthlyBuy() {
        super();
    }
}
//>>> DDD / Domain Event
