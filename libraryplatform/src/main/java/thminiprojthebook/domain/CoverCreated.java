package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class CoverCreated extends AbstractEvent {

    private Long authorId;
    private Long bookId;
    private String title;
    private String imageUrl;
    private String generatedBy;
    private Date updatedAt;
    private Date createdAt;
}
