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



//<<< Clean Arch / Port Method
    public void join(){
        
        //implement business logic here:

    }
//>>> Clean Arch / Port Method
//<<< Clean Arch / Port Method
    public void approve(){
        
        //implement business logic here:
        AuthorApproved authorApproved = new AuthorApproved(this);
        authorApproved.publishAfterCommit();
    }
//>>> Clean Arch / Port Method
//<<< Clean Arch / Port Method
    public void disApprove(){
        
        //implement business logic here:
        

        thminiprojthebook.external.AuthorQuery authorQuery = new thminiprojthebook.external.AuthorQuery();
        // authorQuery.set??()        
          = AuthorApplication.applicationContext
            .getBean(thminiprojthebook.external.Service.class)
            .author(authorQuery);

    }
//>>> Clean Arch / Port Method



}
//>>> DDD / Aggregate Root
