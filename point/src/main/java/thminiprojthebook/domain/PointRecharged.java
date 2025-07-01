package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class PointRecharged extends AbstractEvent {

    private Long userId;
    private Integer pointBalance;

    public PointRecharged(Point aggregate) {
        super(aggregate);
    }

    public PointRecharged() {
        super();
    }
}
//>>> DDD / Domain Event
