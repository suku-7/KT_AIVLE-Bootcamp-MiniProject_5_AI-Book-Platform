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
        this.id = aggregate.getPointId();
        this.userId = aggregate.getUserId();
        this.pointBalance = aggregate.getPointBalance();
        this.amount = aggregate.getAmount();    // 적립된 금액
        this.usedAt = aggregate.getUsedAt();    // 적립 시점
    }

    public PointRecharged() {
        super();
    }
}
//>>> DDD / Domain Event
