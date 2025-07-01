package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class AiSummarized extends AbstractEvent {

    private Long id;
    private String bookId;
    private Object content;
    private String language;
    private Integer maxLength;
    private String classificationType;
    private String requestedBy;
}
