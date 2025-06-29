package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.WritemanageApplication;

@Entity
@Table(name = "Writing_table")
@Data
//<<< DDD / Aggregate Root
public class Writing {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;

    private String title;

    private String context;

    private Long authorId;

    private Boolean registration;

    public static WritingRepository repository() {
        WritingRepository writingRepository = WritemanageApplication.applicationContext.getBean(
            WritingRepository.class
        );
        return writingRepository;
    }

    //<<< Clean Arch / Port Method
    public void registBook() {
        //implement business logic here:

        BookRegisted bookRegisted = new BookRegisted(this);
        bookRegisted.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void modifyContext() {
        //implement business logic here:

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
