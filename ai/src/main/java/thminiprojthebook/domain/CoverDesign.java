package thminiprojthebook.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.AiApplication;
import thminiprojthebook.service.DalleService;

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
        try {
            System.out.println("=== Cover Generation Started ===");
            System.out.println("Input BookRegisted data:");
            System.out.println("- BookId: " + bookRegisted.getBookId());
            System.out.println("- Title: " + bookRegisted.getTitle());
            System.out.println("- Context: " + bookRegisted.getContext());
            System.out.println("- AuthorId: " + bookRegisted.getAuthorId());
            
            // Get DalleService from application context
            DalleService dalleService = AiApplication.applicationContext.getBean(DalleService.class);
            
            // Create new CoverDesign entity
            CoverDesign coverDesign = new CoverDesign();
            coverDesign.setBookId(bookRegisted.getBookId().toString());
            coverDesign.setTitle(bookRegisted.getTitle());
            coverDesign.setCreatedAt(new Date());
            coverDesign.setUpdatedAt(new Date());
            coverDesign.setGeneratedBy("DALL-E-3");
            
            System.out.println("CoverDesign entity created with initial data:");
            System.out.println("- BookId: " + coverDesign.getBookId());
            System.out.println("- Title: " + coverDesign.getTitle());
            System.out.println("- GeneratedBy: " + coverDesign.getGeneratedBy());
            
            // Generate cover image using DALL-E
            System.out.println("Calling DALL-E service...");
            String imageUrl = dalleService.generateCoverImage(
                bookRegisted.getTitle(), 
                bookRegisted.getContext()
            );
            System.out.println("DALL-E service returned: " + imageUrl);
            
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                coverDesign.setImageUrl(imageUrl);
                
                // Save the cover design
                repository().save(coverDesign);
                
                // Publish CoverCreated event with proper data mapping
                CoverCreated coverCreated = new CoverCreated(coverDesign);
                coverCreated.setId(coverDesign.getId());
                coverCreated.setBookId(coverDesign.getBookId());
                coverCreated.setTitle(coverDesign.getTitle());
                coverCreated.setImageUrl(coverDesign.getImageUrl());
                coverCreated.setGeneratedBy(coverDesign.getGeneratedBy());
                coverCreated.setCreatedAt(coverDesign.getCreatedAt().toString());
                coverCreated.publishAfterCommit();
                
                System.out.println("Cover generated successfully for book: " + bookRegisted.getTitle());
                System.out.println("Generated image URL: " + imageUrl);
                System.out.println("CoverCreated event published with data: " + coverCreated.toString());
            } else {
                System.err.println("Failed to generate cover for book: " + bookRegisted.getTitle());
            }
            
        } catch (Exception e) {
            System.err.println("Error in autoCoverGeneratePolicy: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
