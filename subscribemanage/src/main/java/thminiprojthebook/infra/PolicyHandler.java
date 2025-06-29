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
    SubscriberRepository subscriberRepository;

    @Autowired
    SubscribedBookRepository subscribedBookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PointInsufficient'"
    )
    public void wheneverPointInsufficient_PurchaseFail(
        @Payload PointInsufficient pointInsufficient
    ) {
        PointInsufficient event = pointInsufficient;
        System.out.println(
            "\n\n##### listener PurchaseFail : " + pointInsufficient + "\n\n"
        );

        // Sample Logic //
        SubscribedBook.purchaseFail(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
