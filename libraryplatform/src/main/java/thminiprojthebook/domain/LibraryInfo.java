package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.LibraryplatformApplication;
import thminiprojthebook.domain.BestsellerGiven;
import thminiprojthebook.domain.Published;

@Entity
@Table(name = "LibraryInfo_table")
@Data
//<<< DDD / Aggregate Root
public class LibraryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;

    private String bookTitle;

    private Boolean bestseller;

    private String author;

    private Integer selectCount;

    private Date publishDate;

    private String summary;

    private String classficationTpe;

    private String bookimage;

    public static LibraryInfoRepository repository() {
        LibraryInfoRepository libraryInfoRepository = LibraryplatformApplication.applicationContext.getBean(
            LibraryInfoRepository.class
        );
        return libraryInfoRepository;
    }

    //<<< Clean Arch / Port Method
    public static void subscribeIncrease(PointDecreased pointDecreased) {
        //implement business logic here:

        /** Example 1:  new item 
        LibraryInfo libraryInfo = new LibraryInfo();
        repository().save(libraryInfo);

        BestsellerGiven bestsellerGiven = new BestsellerGiven(libraryInfo);
        bestsellerGiven.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(pointDecreased.get???()).ifPresent(libraryInfo->{
            
            libraryInfo // do something
            repository().save(libraryInfo);

            BestsellerGiven bestsellerGiven = new BestsellerGiven(libraryInfo);
            bestsellerGiven.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void publish(AiSummarized aiSummarized) {
        //implement business logic here:

        /** Example 1:  new item 
        LibraryInfo libraryInfo = new LibraryInfo();
        repository().save(libraryInfo);

        Published published = new Published(libraryInfo);
        published.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        // if aiSummarized.aiGpt4_1MiniId exists, use it
        
        // ObjectMapper mapper = new ObjectMapper();
        // Map<, Object> contentAnalyzerMap = mapper.convertValue(aiSummarized.getAiGpt4_1MiniId(), Map.class);

        repository().findById(aiSummarized.get???()).ifPresent(libraryInfo->{
            
            libraryInfo // do something
            repository().save(libraryInfo);

            Published published = new Published(libraryInfo);
            published.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void publish(CoverCreated coverCreated) {
        //implement business logic here:

        /** Example 1:  new item 
        LibraryInfo libraryInfo = new LibraryInfo();
        repository().save(libraryInfo);

        Published published = new Published(libraryInfo);
        published.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        // if coverCreated.aiDallEId exists, use it
        
        // ObjectMapper mapper = new ObjectMapper();
        // Map<, Object> coverDesignMap = mapper.convertValue(coverCreated.getAiDallEId(), Map.class);

        repository().findById(coverCreated.get???()).ifPresent(libraryInfo->{
            
            libraryInfo // do something
            repository().save(libraryInfo);

            Published published = new Published(libraryInfo);
            published.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void publishCompleted(BookAiProcessCompleted bookAiProcessCompleted) {
        try {
            System.out.println("=== Publishing AI Completed Book to Library ===");
            System.out.println("Book: " + bookAiProcessCompleted.getTitle());
            System.out.println("BookId: " + bookAiProcessCompleted.getBookId());
            System.out.println("Summary: " + bookAiProcessCompleted.getSummary());
            System.out.println("Genre: " + bookAiProcessCompleted.getClassificationType());
            System.out.println("Cover Image: " + bookAiProcessCompleted.getImageUrl());
            
            // Create new LibraryInfo entry with complete AI data
            LibraryInfo libraryInfo = new LibraryInfo();
            libraryInfo.setBookTitle(bookAiProcessCompleted.getTitle());
            libraryInfo.setAuthor("AI-Enhanced"); // You might want to get actual author data
            libraryInfo.setPublishDate(bookAiProcessCompleted.getCompletedAt());
            libraryInfo.setSelectCount(0);
            libraryInfo.setRank(0);
            libraryInfo.setBestseller(false);
            
            repository().save(libraryInfo);
            
            // Publish the book to library with all AI enhancements
            Published published = new Published(libraryInfo);
            published.publishAfterCommit();
            
            System.out.println("Book published to library successfully!");
            System.out.println("LibraryInfo ID: " + libraryInfo.getId());
            
        } catch (Exception e) {
            System.err.println("Error publishing AI completed book: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
