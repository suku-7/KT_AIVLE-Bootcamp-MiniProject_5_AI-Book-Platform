package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class KtSignedupPointCharged extends AbstractEvent {

    private Long userId;
    private Integer pointBalance;

    public KtSignedupPointCharged(Point aggregate) {
        super(aggregate);
    }

    public KtSignedupPointCharged() {
        super();
    }
}
//>>> DDD / Domain Event
