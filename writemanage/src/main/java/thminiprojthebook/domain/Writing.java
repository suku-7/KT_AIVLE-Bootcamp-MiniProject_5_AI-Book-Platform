package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;

import org.springframework.http.ResponseEntity;

import lombok.Data;
import thminiprojthebook.WritemanageApplication;
import thminiprojthebook.auth.JwtUtil;

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

    private String authorName;

    public static WritingRepository repository() {
        WritingRepository writingRepository = WritemanageApplication.applicationContext.getBean(
            WritingRepository.class
        );
        return writingRepository;
    }

    //<<< Clean Arch / Port Method
    public void registBook() {
        //implement business logic here:
        this.registration = true;
        
        BookRegisted bookRegisted = new BookRegisted(this);
        bookRegisted.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public void modifyContext(ModifyContextCommand command) {
        if(command.getNewTitle() != null) {
            this.title = command.getNewTitle();
        }
        if(command.getNewContext() != null) {
            this.context = command.getNewContext();
        }
    }
}
//>>> DDD / Aggregate Root
