package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class AiSummarized extends AbstractEvent {

    private Long authorId;
    private Long bookId;
    private String context;
    private String summary;
    private String language;
    private Integer maxLength;
    private String classificationType;
    private String requestedBy;
}
