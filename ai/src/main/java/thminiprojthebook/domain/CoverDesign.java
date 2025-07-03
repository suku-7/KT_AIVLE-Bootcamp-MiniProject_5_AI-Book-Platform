package thminiprojthebook.domain;

import java.util.Date;
import java.util.Optional;
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
    private Long bookId;
    private Long authorId;
    private Date updatedAt;
    private String title;
    @Column(length = 1000)
    private String imageUrl;
    private String generatedBy;
    private Date createdAt;

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
            
            // Check if cover already exists for this book
            Optional<CoverDesign> existingCover = repository().findById(bookRegisted.getBookId());
            if (existingCover.isPresent() && existingCover.get().getImageUrl() != null && !existingCover.get().getImageUrl().trim().isEmpty()) {
                System.out.println("Cover already exists for BookId: " + bookRegisted.getBookId());
                System.out.println("Skipping duplicate cover generation to prevent repeated API calls");
                return;
            }
            
            // Initialize or get AI process tracker
            AiProcessTracker tracker = AiProcessTracker.findByBookId(bookRegisted.getBookId());
            if (tracker == null) {
                tracker = AiProcessTracker.initializeForBook(bookRegisted.getBookId(), bookRegisted.getTitle(), bookRegisted.getAuthorId());
            }
            
            // Get DalleService from application context
            DalleService dalleService = AiApplication.applicationContext.getBean(DalleService.class);
            
            // Try to get existing ContentAnalyzer with summary for better context
            ContentAnalyzerRepository analyzerRepo = AiApplication.applicationContext.getBean(ContentAnalyzerRepository.class);
            ContentAnalyzer existingAnalyzer = analyzerRepo.findByBookId(bookRegisted.getBookId())
                .stream()
                .findFirst()
                .orElse(null);
            
            // Determine the best content to use for cover generation
            String contentForCover;
            if (existingAnalyzer != null && existingAnalyzer.getSummary() != null && !existingAnalyzer.getSummary().trim().isEmpty()) {
                // Use AI-generated summary for more focused cover design
                contentForCover = existingAnalyzer.getSummary();
                System.out.println("Using AI-generated summary for cover: " + contentForCover.substring(0, Math.min(100, contentForCover.length())) + "...");
            } else {
                // Fallback to original context if no summary available yet
                contentForCover = bookRegisted.getContext();
                System.out.println("Using original context for cover (summary not available yet)");
            }
            
            // Create new CoverDesign entity
            CoverDesign coverDesign = new CoverDesign();
            coverDesign.setAuthorId(bookRegisted.getAuthorId());
            coverDesign.setBookId(bookRegisted.getBookId());
            coverDesign.setTitle(bookRegisted.getTitle());
            coverDesign.setCreatedAt(new Date());
            coverDesign.setUpdatedAt(new Date());
            coverDesign.setGeneratedBy("DALL-E-3");
            
            System.out.println("CoverDesign entity created with initial data:");
            System.out.println("- AuthorId: " + coverDesign.getAuthorId());
            System.out.println("- BookId: " + coverDesign.getBookId());
            System.out.println("- Title: " + coverDesign.getTitle());
            System.out.println("- GeneratedBy: " + coverDesign.getGeneratedBy());
            
            // Generate cover image using DALL-E with optimal content
            System.out.println("Calling DALL-E service...");
            String imageUrl = dalleService.generateCoverImage(
                bookRegisted.getTitle(), 
                contentForCover
            );
            System.out.println("DALL-E service returned: " + imageUrl);
            
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                coverDesign.setImageUrl(imageUrl);
                
                // Save the cover design
                repository().save(coverDesign);
                
                // Publish CoverCreated event
                CoverCreated coverCreated = new CoverCreated(coverDesign);
                coverCreated.publishAfterCommit();
                
                // Mark cover generation as completed in tracker
                tracker.markCoverGenerationCompleted(
                    coverDesign.getImageUrl(),
                    coverDesign.getGeneratedBy()
                );
                
                System.out.println("Cover generated successfully for book: " + bookRegisted.getTitle());
                System.out.println("Generated image URL: " + imageUrl);
                System.out.println("CoverCreated event published");
            } else {
                System.err.println("Failed to generate cover for book: " + bookRegisted.getTitle());
            }
            
        } catch (Exception e) {
            System.err.println("Error in autoCoverGeneratePolicy: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method

    /**
     * Generate or update cover design using AI-generated summary
     * This method is called when AI summary is completed to create better cover
     */
    public static void generateCoverWithSummary(AiSummarized aiSummarized) {
        try {
            System.out.println("=== Cover Generation with AI Summary Started ===");
            System.out.println("AI Summary data:");
            System.out.println("- BookId: " + aiSummarized.getBookId());
            System.out.println("- Summary: " + aiSummarized.getSummary());
            System.out.println("- Genre: " + aiSummarized.getClassificationType());
            
            // Get DalleService from application context
            DalleService dalleService = AiApplication.applicationContext.getBean(DalleService.class);
            
            // Check if cover already exists for this book
            Optional<CoverDesign> existingCover = repository().findById(aiSummarized.getBookId());
            CoverDesign coverDesign;
            
            if (existingCover.isPresent()) {
                coverDesign = existingCover.get();
                // Check if cover already has an image URL - if so, skip regeneration
                if (coverDesign.getImageUrl() != null && !coverDesign.getImageUrl().trim().isEmpty()) {
                    System.out.println("Cover already exists with image URL for BookId: " + aiSummarized.getBookId());
                    System.out.println("Skipping cover regeneration to avoid duplicate API calls");
                    return; // Exit early to prevent duplicate generation
                }
                System.out.println("Updating existing cover with AI summary");
            } else {
                // Create new cover design
                coverDesign = new CoverDesign();
                coverDesign.setBookId(aiSummarized.getBookId());
                coverDesign.setAuthorId(aiSummarized.getAuthorId());
                coverDesign.setTitle(aiSummarized.getTitle()); // Set title from AiSummarized event
                coverDesign.setCreatedAt(new Date());
                coverDesign.setGeneratedBy("DALL-E-3");
                System.out.println("Creating new cover with AI summary");
            }
            
            // Use the AI summary for enhanced cover generation
            String enhancedContext = aiSummarized.getSummary();
            if (aiSummarized.getClassificationType() != null) {
                enhancedContext += " [Genre: " + aiSummarized.getClassificationType() + "]";
            }
            
            System.out.println("Calling DALL-E service with AI summary...");
            String imageUrl = dalleService.generateCoverImage(
                aiSummarized.getTitle() != null ? aiSummarized.getTitle() : "Book Title", 
                enhancedContext
            );
            System.out.println("DALL-E service returned: " + imageUrl);
            
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                coverDesign.setImageUrl(imageUrl);
                coverDesign.setUpdatedAt(new Date());
                
                // Save the cover design
                repository().save(coverDesign);
                
                // Publish CoverCreated event
                CoverCreated coverCreated = new CoverCreated(coverDesign);
                coverCreated.publishAfterCommit();
                
                // Update AI process tracker if exists
                AiProcessTracker tracker = AiProcessTracker.findByBookId(aiSummarized.getBookId());
                if (tracker != null) {
                    tracker.markCoverGenerationCompleted(
                        coverDesign.getImageUrl(),
                        coverDesign.getGeneratedBy()
                    );
                }
                
                System.out.println("Cover generated successfully with AI summary");
                System.out.println("Generated image URL: " + imageUrl);
                System.out.println("CoverCreated event published");
            } else {
                System.err.println("Failed to generate cover with AI summary");
            }
            
        } catch (Exception e) {
            System.err.println("Error in generateCoverWithSummary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //>>> Clean Arch / Port Method
}
//>>> DDD / Aggregate Root
