package thminiprojthebook.domain;

import lombok.*;
import thminiprojthebook.infra.AbstractEvent;
import java.util.Date;

//<<< DDD / Domain Event
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class AiSummarized extends AbstractEvent {

    private Long authorId;
    private Long bookId;
    private String context;
    private String summary;
    private String language;
    private Integer maxLength;
    private String classificationType;
    private String requestedBy;
    private Date createdAt;

    public AiSummarized(ContentAnalyzer aggregate) {
        super(aggregate);
        // ContentAnalyzer의 속성들을 매핑
        if (aggregate != null) {
            this.authorId = aggregate.getAuthorId();
            this.bookId = aggregate.getBookId();
            this.context = aggregate.getContext();
            this.summary = aggregate.getSummary();
            this.language = aggregate.getLanguage();
            this.maxLength = aggregate.getMaxLength();
            this.classificationType = aggregate.getClassificationType();
            this.requestedBy = aggregate.getRequestedBy();
            this.createdAt = new Date(); // 현재 시간으로 설정
        }
    }

    public AiSummarized() {
        super();
        this.createdAt = new Date();
    }
}
//>>> DDD / Domain Event
