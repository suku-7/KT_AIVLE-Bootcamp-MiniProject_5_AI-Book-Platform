package thminiprojthebook.domain;

import lombok.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class AiSummarized extends AbstractEvent {

    private Long authorId;
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
