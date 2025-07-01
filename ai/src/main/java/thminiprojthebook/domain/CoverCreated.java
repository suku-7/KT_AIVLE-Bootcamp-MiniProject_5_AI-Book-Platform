package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class CoverCreated extends AbstractEvent {

    private Long bookId;
    private Long authorId;
    private String authorName;
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
