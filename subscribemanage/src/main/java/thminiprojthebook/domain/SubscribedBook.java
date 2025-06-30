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
   public void buyBook(BuyBookCommand command) {
    this.bookId = command.getBookId();
    this.subscriberId = new SubscriberId(command.getSubscriberId()); // 생성자 맞게 조정
    this.status = true;

    BuyBookSub event = new BuyBookSub(this);
    event.publishAfterCommit();
    }

    public static void purchaseFail(PointInsufficient event) {
        repository().findById(event.getSubscribedBookId()).ifPresent(subscribedBook -> {
            subscribedBook.setStatus(false);
            repository().save(subscribedBook);
        });
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root

