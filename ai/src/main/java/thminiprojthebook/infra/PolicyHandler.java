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
    public void wheneverBookRegisted_ProcessAiServices(
        @Payload BookRegisted bookRegisted
    ) {
        BookRegisted event = bookRegisted;
        System.out.println(
            "\n\n##### listener ProcessAiServices : " +
            bookRegisted +
            "\n\n"
        );

        try {
            // 1. AI 콘텐츠 분석 및 요약 실행
            System.out.println("=== Starting ContentAnalyzer.aiSummarize ===");
            ContentAnalyzer.aiSummarize(event);
            System.out.println("=== ContentAnalyzer.aiSummarize completed ===");
            
            // 2. AI 커버 이미지 생성 실행
            System.out.println("=== Starting CoverDesign.autoCoverGeneratePolicy ===");
            CoverDesign.autoCoverGeneratePolicy(event);
            System.out.println("=== CoverDesign.autoCoverGeneratePolicy completed ===");
            
        } catch (Exception e) {
            System.err.println("Error in AI services processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
