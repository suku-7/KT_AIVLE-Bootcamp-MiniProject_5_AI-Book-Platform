package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class UserUpdated extends AbstractEvent {

    private Long userId;
    private String isKt;

    public UserUpdated(User aggregate) {
        super(aggregate);
    }

    public UserUpdated() {
        super();
    }
}
//>>> DDD / Domain Event
