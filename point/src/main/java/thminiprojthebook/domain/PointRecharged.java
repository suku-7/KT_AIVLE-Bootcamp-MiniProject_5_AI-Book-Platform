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

    private Long id;
    private String userId;
    private Integer pointBalance;
    private Integer amount;
    private Date usedAt;

    public PointRecharged(Point aggregate) {
        super(aggregate);
    }

    public PointRecharged() {
        super();
    }
}
//>>> DDD / Domain Event
