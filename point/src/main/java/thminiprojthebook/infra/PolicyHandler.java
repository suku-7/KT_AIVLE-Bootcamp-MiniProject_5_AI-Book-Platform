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

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='BuyBookSub'")
    public void onBuyBookSub(@Payload BuyBookSub event) {
        System.out.println("[Event] 소장 요청 감지됨 → 포인트 차감 시도");
        Point.pointDecrease(event);
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='SubscriberCreated'")
    public void onSubscriberCreated(@Payload SubscriberCreated event) {
        System.out.println("[Event] 신규 가입 감지됨 → 가입 포인트 지급");
        Point.signup(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
