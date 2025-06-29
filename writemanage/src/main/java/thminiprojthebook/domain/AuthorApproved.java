package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import thminiprojthebook.infra.AbstractEvent;

@Data
public class AuthorApproved extends AbstractEvent {

    private Long authorId;
    private Boolean isApproved;
}
