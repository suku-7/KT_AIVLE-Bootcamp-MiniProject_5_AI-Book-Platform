package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.SubscribemanageApplication;

@Entity
@Table(name = "SubscribedBook_table")
@Data
//<<< DDD / Aggregate Root
public class SubscribedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long subscribedBookId;

    private Boolean status;

    private Long bookId;

    @Embedded
    private SubscriberId subscriberId;

    public static SubscribedBookRepository repository() {
        SubscribedBookRepository subscribedBookRepository = SubscribemanageApplication.applicationContext.getBean(
            SubscribedBookRepository.class
        );
        return subscribedBookRepository;
    }

    //<<< Clean Arch / Port Method
    public void buyBook() {
        //implement business logic here:

        BuyBookSub buyBookSub = new BuyBookSub(this);
        buyBookSub.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void purchaseFail(PointInsufficient pointInsufficient) {
        //implement business logic here:

        /** Example 1:  new item 
        SubscribedBook subscribedBook = new SubscribedBook();
        repository().save(subscribedBook);

        */

        /** Example 2:  finding and process
        

        repository().findById(pointInsufficient.get???()).ifPresent(subscribedBook->{
            
            subscribedBook // do something
            repository().save(subscribedBook);


         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
