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

    private Long id;
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
