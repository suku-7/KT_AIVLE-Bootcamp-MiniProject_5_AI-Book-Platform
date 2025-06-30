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

    private Long id;
    private String userId;
    private Integer ktSignupPoint;
    private Date signupDate;
    private String isKt;

    public KtSignedupPointCharged(Point aggregate) {
        super(aggregate);
        this.id = aggregate.getPointId(); 
        this.userId = aggregate.getUserId();
        this.ktSignupPoint = aggregate.getKtSignupPoint();
        this.signupDate = aggregate.getSignupDate();
        this.isKt = aggregate.getIsKt();    
    }

    public KtSignedupPointCharged() {
        super();
    }
}
//>>> DDD / Domain Event
