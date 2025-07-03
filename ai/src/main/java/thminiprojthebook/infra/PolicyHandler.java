package thminiprojthebook.infra;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thminiprojthebook.config.kafka.KafkaProcessor;
import thminiprojthebook.domain.*;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    CoverDesignRepository coverDesignRepository;

    @Autowired
    ContentAnalyzerRepository contentAnalyzerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='BookRegisted'"
    )
    public void wheneverBookRegisted_ProcessSequentially(
        @Payload BookRegisted bookRegisted
    ) {
        BookRegisted event = bookRegisted;
        System.out.println(
            "\n\n##### listener ProcessSequentially : " + bookRegisted + "\n\n"
        );

        try {
            // Check if processing is already completed for this book
            List<ContentAnalyzer> existingAnalyzers = contentAnalyzerRepository.findByBookId(event.getBookId());
            boolean contentAnalysisExists = !existingAnalyzers.isEmpty() && 
                existingAnalyzers.get(0).getSummary() != null && 
                !existingAnalyzers.get(0).getSummary().trim().isEmpty();
                
            Optional<CoverDesign> existingCover = coverDesignRepository.findById(event.getBookId());
            boolean coverExists = existingCover.isPresent() && 
                existingCover.get().getImageUrl() != null && 
                !existingCover.get().getImageUrl().trim().isEmpty();
            
            if (contentAnalysisExists && coverExists) {
                System.out.println("Both content analysis and cover generation already completed for BookId: " + event.getBookId());
                System.out.println("Skipping duplicate processing");
                return;
            }
            
            // Process AiSummarize first to generate summary (with internal duplicate check)
            System.out.println("=== Starting AiSummarize ===");
            ContentAnalyzer.aiSummarize(event);
            System.out.println("=== AiSummarize completed ===");

            // Process AutoCoverGeneratePolicy after summary is available (with internal duplicate check)
            System.out.println("=== Starting AutoCoverGeneratePolicy ===");
            CoverDesign.autoCoverGeneratePolicy(event);
            System.out.println("=== AutoCoverGeneratePolicy completed ===");
        } catch (Exception e) {
            System.err.println("Error in sequential processing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='AiSummarized'"
    )
    public void wheneverAiSummarized_GenerateCoverWithSummary(
        @Payload AiSummarized aiSummarized
    ) {
        AiSummarized event = aiSummarized;
        System.out.println(
            "\n\n##### listener GenerateCoverWithSummary : " + aiSummarized + "\n\n"
        );

        try {
            // Generate or update cover using AI summary for better results
            System.out.println("=== Starting Cover Generation with AI Summary ===");
            CoverDesign.generateCoverWithSummary(event);
            System.out.println("=== Cover Generation with AI Summary completed ===");
        } catch (Exception e) {
            System.err.println("Error in cover generation with summary: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
