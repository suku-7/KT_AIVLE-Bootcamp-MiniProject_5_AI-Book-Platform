package thminiprojthebook.domain;

import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

@Data
@ToString
public class Published extends AbstractEvent {

    private Long bookId;
    private Long authorId;
    private String authorName;
    private String title;
    private String imageUrl;
    private String summary;
    private String context;
    private String classficationType;
    private Date publishDate;
    private Integer selectCount;
    private Integer rank;
    private Boolean bestseller;
}
