package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import thminiprojthebook.infra.AbstractEvent;

@Data
public class BookRegisted extends AbstractEvent {

    private Long bookId;
    private String context;
    private Long authorId;
    private String title;
    private Boolean registration;
}
