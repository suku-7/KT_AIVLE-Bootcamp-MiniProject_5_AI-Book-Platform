package thminiprojthebook.domain;

import thminiprojthebook.AuthormanageApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;
import java.time.LocalDate;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;


@Entity
@Table(name="Author_table")
@Data

//<<< DDD / Aggregate Root
public class Author  {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long authorId;    
    
    private String name;    
    
    private String loginId;    
    
    private String password;    
    
    private Boolean isApproved;    
    
    private String portfolioUrl;


    public static AuthorRepository repository(){
        AuthorRepository authorRepository = AuthormanageApplication.applicationContext.getBean(AuthorRepository.class);
        return authorRepository;
    }


    @PrePersist
    public void prePersist() {
        if (isApproved == null) {
            this.isApproved = false;
        }
    }

//>>> Clean Arch / Port Method
//<<< Clean Arch / Port Method
    public void approve(){
        this.setIsApproved(true);
        AuthorApproved authorApproved = new AuthorApproved(this);
        authorApproved.publishAfterCommit();
    }
//>>> Clean Arch / Port Method
//<<< Clean Arch / Port Method
    public void disApprove(){
        this.setIsApproved(false);
    }
}
