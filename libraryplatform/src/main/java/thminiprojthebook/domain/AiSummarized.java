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
    private String title;   // LibraryInfo에서 필요로 하는 title 필드
    private String summary; // LibraryInfo에서 필요로 하는 summary 필드
}
