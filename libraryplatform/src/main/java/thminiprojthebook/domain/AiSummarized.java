package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

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
}
