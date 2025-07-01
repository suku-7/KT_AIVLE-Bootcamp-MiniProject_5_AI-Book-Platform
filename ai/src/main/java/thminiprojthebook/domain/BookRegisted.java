package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class BookRegisted extends AbstractEvent {

    private Long bookId;
    private Long authorId;
    private String authorName;
    private String title;
    private String context;
    private Boolean registration;
}
