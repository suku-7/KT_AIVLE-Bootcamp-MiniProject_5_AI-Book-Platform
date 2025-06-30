package thminiprojthebook.domain;

import lombok.*;
import thminiprojthebook.infra.AbstractEvent;

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
}
