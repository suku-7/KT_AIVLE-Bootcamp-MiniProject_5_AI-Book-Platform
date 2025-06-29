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

    private Long id;
    private String bookTitle;
    private Integer rank;
    private Boolean bestseller;
    private String author;
    private Integer selectCount;
    private Date publishDate;

    public BestsellerGiven(LibraryInfo aggregate) {
        super(aggregate);
    }

    public BestsellerGiven() {
        super();
    }
}
//>>> DDD / Domain Event
