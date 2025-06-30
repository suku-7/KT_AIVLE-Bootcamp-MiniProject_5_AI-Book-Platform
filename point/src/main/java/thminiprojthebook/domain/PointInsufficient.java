package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class PointInsufficient extends AbstractEvent {

    private Long id;
    private String userId;
    private Integer pointBalance;
    private Integer standardSignupPoint;
    private Integer ktSignupPoint;
    private Integer amount;
    private Date usedAt;

    public PointInsufficient(Point aggregate) {
        super(aggregate);
        this.id = aggregate.getPointId();
        this.userId = aggregate.getUserId();
        this.pointBalance = aggregate.getPointBalance();
        this.standardSignupPoint = aggregate.getStandardSignupPoint();
        this.ktSignupPoint = aggregate.getKtSignupPoint();
        this.amount = aggregate.getAmount();
        this.usedAt = aggregate.getUsedAt();
    }

    public PointInsufficient() {
        super();
    }
}
//>>> DDD / Domain Event
