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
import thminiprojthebook.domain.AiSummarized;

@Entity
@Table(name = "ContentAnalyzer_table")
@Data
//<<< DDD / Aggregate Root
public class ContentAnalyzer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bookId;

    private String context;

    private String language;

    private Integer maxLength;

    private String classificationType;

    private String requestedBy;

    public static ContentAnalyzerRepository repository() {
        ContentAnalyzerRepository contentAnalyzerRepository = AiApplication.applicationContext.getBean(
            ContentAnalyzerRepository.class
        );
        return contentAnalyzerRepository;
    }

    //<<< Clean Arch / Port Method
    public static void aiSummarize(BookRegisted bookRegisted) {
        //implement business logic here:

        /** Example 1:  new item 
        ContentAnalyzer contentAnalyzer = new ContentAnalyzer();
        repository().save(contentAnalyzer);

        AiSummarized aiSummarized = new AiSummarized(contentAnalyzer);
        aiSummarized.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(bookRegisted.get???()).ifPresent(contentAnalyzer->{
            
            contentAnalyzer // do something
            repository().save(contentAnalyzer);

            AiSummarized aiSummarized = new AiSummarized(contentAnalyzer);
            aiSummarized.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
