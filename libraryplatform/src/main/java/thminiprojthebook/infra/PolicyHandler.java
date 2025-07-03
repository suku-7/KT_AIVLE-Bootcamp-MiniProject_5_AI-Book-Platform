package thminiprojthebook.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thminiprojthebook.config.kafka.KafkaProcessor;
import thminiprojthebook.domain.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@Transactional
public class PolicyHandler {

    @Autowired
    LibraryInfoRepository libraryInfoRepository;

    // 임시 저장소: bookId 기준으로 이벤트 매칭
    private Map<Long, AiSummarized> aiSummarizedMap = new ConcurrentHashMap<>();
    private Map<Long, CoverCreated> coverCreatedMap = new ConcurrentHashMap<>();

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='BuyBookSub'"
    )
    public void wheneverBuyBookSub_BuyBookIncrease(@Payload BuyBookSub buyBookSub) {
        System.out.println("\n\n##### listener BuyBookIncrease : " + buyBookSub + "\n\n");
        LibraryInfo.buyBookIncrease(buyBookSub);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='AiSummarized'"
    )
    public void wheneverAiSummarized(@Payload AiSummarized aiSummarized) {
        Long bookId = aiSummarized.getBookId();
        aiSummarizedMap.put(bookId, aiSummarized);
        System.out.println("\n\n##### listener Received AiSummarized : " + aiSummarized + "\n\n");

        
        publishIfReady(bookId);
        
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='CoverCreated'"
    )
    public void wheneverCoverCreated(@Payload CoverCreated coverCreated) {
        Long bookId = coverCreated.getBookId();
        coverCreatedMap.put(bookId, coverCreated);
        System.out.println("\n\n##### listener Received CoverCreated : " + coverCreated + "\n\n");

        
        publishIfReady(bookId);
        
    }

    private void publishIfReady(Long bookId) {
        AiSummarized aiEvent = aiSummarizedMap.get(bookId);
        CoverCreated coverEvent = coverCreatedMap.get(bookId);

        if (aiEvent != null && coverEvent != null) {
            System.out.println("\n\n##### Publishing LibraryInfo for bookId: " + bookId + "\n\n");

            LibraryInfo.publish(aiEvent, coverEvent); // 오버로딩 필요
            aiSummarizedMap.remove(bookId);
            coverCreatedMap.remove(bookId);
        }
    }
}