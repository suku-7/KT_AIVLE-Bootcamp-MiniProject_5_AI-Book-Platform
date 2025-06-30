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

    private Long id;
    private String bookId;
    private String context;
    private String summary;
    private String language;
    private Integer maxLength;
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
