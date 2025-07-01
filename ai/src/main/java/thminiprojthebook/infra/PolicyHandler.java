package thminiprojthebook.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
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
            // Process AutoCoverGeneratePolicy
            System.out.println("=== Starting AutoCoverGeneratePolicy ===");
            CoverDesign.autoCoverGeneratePolicy(event);
            System.out.println("=== AutoCoverGeneratePolicy completed ===");

            // Process AiSummarize
            System.out.println("=== Starting AiSummarize ===");
            ContentAnalyzer.aiSummarize(event);
            System.out.println("=== AiSummarize completed ===");
        } catch (Exception e) {
            System.err.println("Error in sequential processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
