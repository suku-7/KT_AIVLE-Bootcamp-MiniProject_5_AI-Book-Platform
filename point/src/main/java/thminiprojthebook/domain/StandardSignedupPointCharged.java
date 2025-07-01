package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class StandardSignedupPointCharged extends AbstractEvent {

    private Long userId;
    private Integer pointBalance;

    public StandardSignedupPointCharged(Point aggregate) {
        super(aggregate);
    }

    public StandardSignedupPointCharged() {
        super();
    }
}
//>>> DDD / Domain Event
