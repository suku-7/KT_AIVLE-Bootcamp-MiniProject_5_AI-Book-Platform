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
    PointRepository pointRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='BuyBookSub'"
    )
    public void wheneverBuyBookSub_PointDecrease(
        @Payload BuyBookSub buyBookSub
    ) {
        BuyBookSub event = buyBookSub;
        System.out.println(
            "\n\n##### listener PointDecrease : " + buyBookSub + "\n\n"
        );

        // Sample Logic //
        Point.pointDecrease(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='SubscriberCreated'"
    )
    public void wheneverSubscriberCreated_Signup(
        @Payload SubscriberCreated subscriberCreated
    ) {
        SubscriberCreated event = subscriberCreated;
        System.out.println(
            "\n\n##### listener Signup : " + subscriberCreated + "\n\n"
        );

        // Sample Logic //
        Point.signup(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
