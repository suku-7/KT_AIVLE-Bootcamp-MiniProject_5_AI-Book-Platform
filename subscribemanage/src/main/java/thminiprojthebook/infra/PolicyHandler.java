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
    PointCommandService pointCommandService;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBuyBookSub_포인트차감(@Payload BuyBookSub event) {
        if (!event.validate()) return;

        System.out.println("##### listener 포인트차감 : " + event.toJson());

        PointChargeCommand command = new PointChargeCommand();
        command.setUserId(event.getSubscriberId().getValue());
        command.setPrice(500); // 예시로 책 가격 500포인트
        command.setSubscribedBookId(event.getSubscribedBookId());

        pointCommandService.차감요청(command); // 외부 마이크로서비스 호출 또는 메시지 발행
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPointInsufficient_구매실패(@Payload PointInsufficient event) {
        if (!event.validate()) return;

        System.out.println("##### listener 구매실패 : " + event.toJson());
        SubscribedBook.purchaseFail(event);
    }
}
//>>> Clean Arch / Inbound Adaptor

