package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BookRegisted extends AbstractEvent {

    private Long bookId;
    private String context;
    private Long authorId;
    private String title;
    private Boolean registration;

    public BookRegisted(Writing aggregate) {
        super(aggregate);
    }

    public BookRegisted() {
        super();
    }
}
//>>> DDD / Domain Event
