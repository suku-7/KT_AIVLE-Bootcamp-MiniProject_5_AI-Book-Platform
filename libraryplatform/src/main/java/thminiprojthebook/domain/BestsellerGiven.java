package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class BestsellerGiven extends AbstractEvent {

    private Long bookId;
    private Long authorId;
    private String authorName;
    private String title;
    private String imageUrl;
    private String summary;
    private String context;
    private String classificationType;
    private Date publishDate;
    private Long selectCount;
    private Integer rank;
    private Boolean bestseller;

    public BestsellerGiven(LibraryInfo aggregate) {
        super(aggregate);
    }

    public BestsellerGiven() {
        super();
    }
}
//>>> DDD / Domain Event
