package thminiprojthebook.domain;

import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.AiApplication;
import thminiprojthebook.service.GptService;

@Entity
@Table(name = "ContentAnalyzer_table")
@Data
//<<< DDD / Aggregate Root
public class ContentAnalyzer {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO) // <-- 1. 이 줄을 제거 또는 주석 처리합니다.
    private Long bookId;

    private Long authorId;

    private String context;

    private String summary;

    private String language;

    private Integer maxLength;

    private String language;

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
        try {
            System.out.println("=== AI Summary Generation Started ===");
            System.out.println("Input BookRegisted data:");
            System.out.println("- BookId: " + bookRegisted.getBookId());
            System.out.println("- Title: " + bookRegisted.getTitle());
            System.out.println("- Context: " + bookRegisted.getContext());
            System.out.println("- AuthorId: " + bookRegisted.getAuthorId());
            
            // Initialize or get AI process tracker
            String bookIdStr = bookRegisted.getBookId().toString();
            AiProcessTracker tracker = AiProcessTracker.findByBookId(bookIdStr);
            if (tracker == null) {
                tracker = AiProcessTracker.initializeForBook(bookIdStr, bookRegisted.getTitle(), bookRegisted.getAuthorId());
            }
            
            // Get GptService from application context
            GptService gptService = AiApplication.applicationContext.getBean(GptService.class);
            
            // Create new ContentAnalyzer entity with initial values
            ContentAnalyzer contentAnalyzer = new ContentAnalyzer();
            contentAnalyzer.setBookId(bookRegisted.getBookId().toString());
            contentAnalyzer.setContext(bookRegisted.getContext());
            contentAnalyzer.setLanguage("KO"); // Default to Korean
            contentAnalyzer.setMaxLength(500); // Default max length
            contentAnalyzer.setClassificationType("임시분류"); // Temporary classification
            contentAnalyzer.setRequestedBy("AI-SYSTEM");
            
            // First, generate summary using general approach
            System.out.println("Generating initial summary for genre classification...");
            String initialSummary = gptService.generateSummary(
                bookRegisted.getContext(),
                contentAnalyzer.getMaxLength(),
                contentAnalyzer.getLanguage(),
                "일반요약" // General summary first
            );
            System.out.println("Initial summary generated: " + initialSummary);
            
            if (initialSummary != null && !initialSummary.trim().isEmpty()) {
                contentAnalyzer.setSummary(initialSummary);
                
                // Now classify genre based on title and summary
                System.out.println("Classifying book genre based on title and summary...");
                String classifiedGenre = gptService.classifyGenre(bookRegisted.getTitle(), initialSummary);
                System.out.println("Classified genre: " + classifiedGenre);
                
                // Update the classification type with the classified genre
                contentAnalyzer.setClassificationType(classifiedGenre);
                
                System.out.println("ContentAnalyzer entity updated with final data:");
                System.out.println("- BookId: " + contentAnalyzer.getBookId());
                System.out.println("- Language: " + contentAnalyzer.getLanguage());
                System.out.println("- MaxLength: " + contentAnalyzer.getMaxLength());
                System.out.println("- Final Genre (ClassificationType): " + contentAnalyzer.getClassificationType());
                
                // Save the content analyzer
                repository().save(contentAnalyzer);
                
                // Publish AiSummarized event with proper data mapping
                AiSummarized aiSummarized = new AiSummarized(contentAnalyzer);
                aiSummarized.setAuthorId(bookRegisted.getAuthorId());
                aiSummarized.setBookId(contentAnalyzer.getBookId());
                aiSummarized.setContext(contentAnalyzer.getContext());
                aiSummarized.setSummary(contentAnalyzer.getSummary());
                aiSummarized.setLanguage(contentAnalyzer.getLanguage());
                aiSummarized.setMaxLength(contentAnalyzer.getMaxLength());
                aiSummarized.setClassificationType(contentAnalyzer.getClassificationType());
                aiSummarized.setRequestedBy(contentAnalyzer.getRequestedBy());
                aiSummarized.publishAfterCommit();
                
                // Mark content analysis as completed in tracker
                tracker.markContentAnalysisCompleted(
                    contentAnalyzer.getSummary(),
                    contentAnalyzer.getClassificationType(),
                    contentAnalyzer.getLanguage(),
                    contentAnalyzer.getMaxLength()
                );
                
                System.out.println("Summary generated successfully for book: " + bookRegisted.getTitle());
                System.out.println("Generated summary: " + initialSummary);
                System.out.println("AiSummarized event published with data: " + aiSummarized.toString());
            } else {
                System.err.println("Failed to generate summary for book: " + bookRegisted.getTitle());
            }
            
        } catch (Exception e) {
            System.err.println("Error in aiSummarize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
