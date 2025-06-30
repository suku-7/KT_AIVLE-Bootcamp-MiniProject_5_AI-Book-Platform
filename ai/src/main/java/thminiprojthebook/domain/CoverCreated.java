package thminiprojthebook.domain;

import lombok.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class CoverCreated extends AbstractEvent {

    private Long id;
    private Long authorId;
    private String bookId;
    private String title;
    private String imageUrl;
    private String generatedBy;
    private String createdAt;

    public CoverCreated(CoverDesign aggregate) {
        super(aggregate);
    }

    public CoverCreated() {
        super();
    }
}
//>>> DDD / Domain Event
