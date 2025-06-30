package thminiprojthebook.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import thminiprojthebook.domain.*;
import thminiprojthebook.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class Published extends AbstractEvent {

   private Long bookId;

    private String title;

    private Boolean bestseller;

    private String authorId;

    private String authorName;

    private Integer selectCount;

    private Date publishDate;

    private String summary;

    private String context;

    private String classficationTpe;

    private String imageUrl;

    private String maxLength;

    private String requestedBy;


    public Published(LibraryInfo aggregate) {
        super(aggregate);
    }

    public Published() {
        super();
    }
}
//>>> DDD / Domain Event
