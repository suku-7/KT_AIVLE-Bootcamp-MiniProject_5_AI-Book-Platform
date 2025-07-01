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
        condition = "headers['type']=='BookServiceSubscribed'"
    )
    public void wheneverBookServiceSubscribed_PointDecrease(
        @Payload BookServiceSubscribed bookServiceSubscribed
    ) {
        BookServiceSubscribed event = bookServiceSubscribed;
        System.out.println(
            "\n\n##### listener PointDecrease : " +
            bookServiceSubscribed +
            "\n\n"
        );

        // Sample Logic //
        Point.pointDecrease(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='UserRegistered'"
    )
    public void wheneverUserRegistered_InitialPointPolicy(
        @Payload UserRegistered userRegistered
    ) {
        UserRegistered event = userRegistered;
        System.out.println(
            "\n\n##### listener InitialPointPolicy : " + userRegistered + "\n\n"
        );

        // Sample Logic //
        Point.initialPointPolicy(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='UserUpdated'"
    )
    public void wheneverUserUpdated_KtSignedupPointPolicy(
        @Payload UserUpdated userUpdated
    ) {
        UserUpdated event = userUpdated;
        System.out.println(
            "\n\n##### listener KtSignedupPointPolicy : " + userUpdated + "\n\n"
        );

        // Sample Logic //
        Point.ktSignedupPointPolicy(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
