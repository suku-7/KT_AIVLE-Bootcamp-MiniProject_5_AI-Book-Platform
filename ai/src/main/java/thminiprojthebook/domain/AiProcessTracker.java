package thminiprojthebook.domain;

import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.AiApplication;
import java.util.Date;

@Entity
@Table(name = "AiProcessTracker_table")
@Data
//<<< DDD / Aggregate Root
public class AiProcessTracker {

    @Id
    private Long bookId;
    private String title;
    private Long authorId;
    
    // Progress tracking
    private Boolean contentAnalysisCompleted = false;
    private Boolean coverGenerationCompleted = false;
    private Date createdAt;
    private Date completedAt;
    
    // Content Analysis Data
    private String summary;
    private String classificationType;
    private String language;
    private Integer maxLength;
    
    // Cover Design Data
    private String imageUrl;
    private String generatedBy;
    private Date coverCreatedAt; // 커버 생성 완료 시간 추가

    public static AiProcessTrackerRepository repository() {
        AiProcessTrackerRepository aiProcessTrackerRepository = AiApplication.applicationContext.getBean(
            AiProcessTrackerRepository.class
        );
        return aiProcessTrackerRepository;
    }

    public AiProcessTracker() {
        this.createdAt = new Date();
    }

    /**
     * Initialize AI process tracking for a book
     */
    public static AiProcessTracker initializeForBook(Long bookId, String title, Long authorId) {
        AiProcessTracker tracker = new AiProcessTracker();
        tracker.setBookId(bookId);
        tracker.setTitle(title);
        tracker.setAuthorId(authorId);
        repository().save(tracker);
        
        System.out.println("AI Process Tracker initialized for book: " + title + " (ID: " + bookId + ")");
        return tracker;
    }

    /**
     * Mark content analysis as completed and check if both processes are done
     */
    public void markContentAnalysisCompleted(String summary, String classificationType, String language, Integer maxLength) {
        this.contentAnalysisCompleted = true;
        this.summary = summary;
        this.classificationType = classificationType;
        this.language = language;
        this.maxLength = maxLength;
        
        System.out.println("Content analysis completed for book: " + this.title);
        checkAndPublishCompletion();
    }

    /**
     * Mark cover generation as completed and check if both processes are done
     */
    public void markCoverGenerationCompleted(String imageUrl, String generatedBy) {
        this.coverGenerationCompleted = true;
        this.imageUrl = imageUrl;
        this.generatedBy = generatedBy;
        this.coverCreatedAt = new Date(); // 커버 완료 시간 기록
        
        System.out.println("Cover generation completed for book: " + this.title);
        checkAndPublishCompletion();
    }

    /**
     * Check if both processes are completed and publish integrated event
     */
    private void checkAndPublishCompletion() {
        if (contentAnalysisCompleted && coverGenerationCompleted) {
            this.completedAt = new Date();
            repository().save(this);
            
            // Publish integrated completion event
            BookAiProcessCompleted completedEvent = new BookAiProcessCompleted();
            completedEvent.setAuthorId(this.authorId);
            completedEvent.setBookId(this.bookId);
            completedEvent.setTitle(this.title);
            
            // Content Analysis Data
            completedEvent.setSummary(this.summary);
            completedEvent.setClassificationType(this.classificationType);
            completedEvent.setLanguage(this.language);
            completedEvent.setMaxLength(this.maxLength);
            
            // Cover Design Data
            completedEvent.setImageUrl(this.imageUrl);
            completedEvent.setGeneratedBy(this.generatedBy);
            completedEvent.setCoverCreatedAt(this.coverCreatedAt); // 실제 커버 생성 시간 전달
            
            // Status flags
            completedEvent.setContentAnalysisCompleted(true);
            completedEvent.setCoverGenerationCompleted(true);
            completedEvent.setCompletedAt(this.completedAt);
            
            completedEvent.publishAfterCommit();
            
            System.out.println("=== AI Process Fully Completed ===");
            System.out.println("Book: " + this.title + " (ID: " + this.bookId + ")");
            System.out.println("- Content Analysis: ✅ " + this.summary);
            System.out.println("- Genre Classification: ✅ " + this.classificationType);
            System.out.println("- Cover Generation: ✅ " + this.imageUrl);
            System.out.println("BookAiProcessCompleted event published!");
        } else {
            repository().save(this);
            System.out.println("AI Process partially completed for book: " + this.title);
            System.out.println("- Content Analysis: " + (contentAnalysisCompleted ? "✅" : "⏳"));
            System.out.println("- Cover Generation: " + (coverGenerationCompleted ? "✅" : "⏳"));
        }
    }

    /**
     * Find existing tracker for a book
     */
    public static AiProcessTracker findByBookId(Long bookId) {
        return repository().findByBookId(bookId);
    }
}
//>>> DDD / Aggregate Root
