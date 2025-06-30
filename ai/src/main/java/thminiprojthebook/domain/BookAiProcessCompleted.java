package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class BookAiProcessCompleted extends AbstractEvent {

    private Long authorId;
    private String bookId;
    private String title;
    
    // Content Analysis Data
    private String summary;
    private String classificationType;
    private String language;
    private Integer maxLength;
    
    // Cover Design Data
    private String imageUrl;
    private String generatedBy;
    private Date coverCreatedAt;
    
    // Status flags
    private Boolean contentAnalysisCompleted;
    private Boolean coverGenerationCompleted;
    private Date completedAt;

    public BookAiProcessCompleted() {
        super();
        this.completedAt = new Date();
        this.contentAnalysisCompleted = false;
        this.coverGenerationCompleted = false;
    }
}
//>>> DDD / Domain Event
