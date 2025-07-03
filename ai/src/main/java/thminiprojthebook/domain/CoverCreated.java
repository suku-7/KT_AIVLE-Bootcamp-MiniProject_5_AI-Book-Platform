package thminiprojthebook.domain;
import java.sql.Date;

import lombok.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class CoverCreated extends AbstractEvent {

    private Long bookId;
    private Long authorId;
    private String title;
    private String imageUrl;
    private String generatedBy;
    private Date updatedAt;
    private Date createdAt;

    public CoverCreated(CoverDesign aggregate) {
        super(aggregate);
    }

    public CoverCreated() {
        super();
    }
}
//>>> DDD / Domain Event
