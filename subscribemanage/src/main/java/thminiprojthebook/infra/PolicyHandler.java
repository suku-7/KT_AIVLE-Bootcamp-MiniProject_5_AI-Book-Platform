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
    UserRepository userRepository;

    @Autowired
    LibraryRepository libraryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PointDecreased'"
    )
    public void wheneverPointDecreased_PointSyncPolicy(
        @Payload PointDecreased pointDecreased
    ) {
        PointDecreased event = pointDecreased;
        System.out.println(
            "\n\n##### listener PointSyncPolicy : " + pointDecreased + "\n\n"
        );

        // Sample Logic //
        User.pointSyncPolicy(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='PointRecharged'"
    )
    public void wheneverPointRecharged_PointSyncPolicy(
        @Payload PointRecharged pointRecharged
    ) {
        PointRecharged event = pointRecharged;
        System.out.println(
            "\n\n##### listener PointSyncPolicy : " + pointRecharged + "\n\n"
        );

        // Sample Logic //
        User.pointSyncPolicy(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='KtSignedupPointCharged'"
    )
    public void wheneverKtSignedupPointCharged_PointSyncPolicy(
        @Payload KtSignedupPointCharged ktSignedupPointCharged
    ) {
        KtSignedupPointCharged event = ktSignedupPointCharged;
        System.out.println(
            "\n\n##### listener PointSyncPolicy : " +
            ktSignedupPointCharged +
            "\n\n"
        );

        // Sample Logic //
        User.pointSyncPolicy(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='StandardSignedupPointCharged'"
    )
    public void wheneverStandardSignedupPointCharged_PointSyncPolicy(
        @Payload StandardSignedupPointCharged standardSignedupPointCharged
    ) {
        StandardSignedupPointCharged event = standardSignedupPointCharged;
        System.out.println(
            "\n\n##### listener PointSyncPolicy : " +
            standardSignedupPointCharged +
            "\n\n"
        );

        // Sample Logic //
        User.pointSyncPolicy(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='Published'"
    )
    public void wheneverPublished_BookInfoPolicy(@Payload Published published) {
        Published event = published;
        System.out.println(
            "\n\n##### listener BookInfoPolicy : " + published + "\n\n"
        );

        // Sample Logic //
        Library.bookInfoPolicy(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
