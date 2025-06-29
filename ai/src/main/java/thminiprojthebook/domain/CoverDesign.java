package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.AiApplication;
import thminiprojthebook.domain.CoverCreated;

@Entity
@Table(name = "CoverDesign_table")
@Data
//<<< DDD / Aggregate Root
public class CoverDesign {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date updatedAt;

    private String title;

    private String imageUrl;

    private String generatedBy;

    private Date createdAt;

    private String bookId;

    public static CoverDesignRepository repository() {
        CoverDesignRepository coverDesignRepository = AiApplication.applicationContext.getBean(
            CoverDesignRepository.class
        );
        return coverDesignRepository;
    }

    //<<< Clean Arch / Port Method
    public static void autoCoverGeneratePolicy(BookRegisted bookRegisted) {
        //implement business logic here:

        /** Example 1:  new item 
        CoverDesign coverDesign = new CoverDesign();
        repository().save(coverDesign);

        CoverCreated coverCreated = new CoverCreated(coverDesign);
        coverCreated.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(bookRegisted.get???()).ifPresent(coverDesign->{
            
            coverDesign // do something
            repository().save(coverDesign);

            CoverCreated coverCreated = new CoverCreated(coverDesign);
            coverCreated.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
