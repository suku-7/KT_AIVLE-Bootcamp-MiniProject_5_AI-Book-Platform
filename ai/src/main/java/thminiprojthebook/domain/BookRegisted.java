package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class BookRegisted extends AbstractEvent {

    private Long bookId;
    private String context;
    private Long authorId;
    private String title;
    private Boolean registration;
    private String authorName;
}
