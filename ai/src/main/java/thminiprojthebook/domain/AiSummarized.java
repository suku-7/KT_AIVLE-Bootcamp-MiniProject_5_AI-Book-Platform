package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class AiSummarized extends AbstractEvent {

    private Long bookId;
    private Long authorId;
    private String authorName;
    private String context;
    private Integer maxLength;
    private String language;
    private String classificationType;
    private String requestedBy;

    public AiSummarized(ContentAnalyzer aggregate) {
        super(aggregate);
    }

    public AiSummarized() {
        super();
    }
}
//>>> DDD / Domain Event
