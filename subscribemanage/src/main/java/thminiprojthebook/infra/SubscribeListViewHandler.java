package thminiprojthebook.infra;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import thminiprojthebook.config.kafka.KafkaProcessor;
import thminiprojthebook.domain.*;

@Service
public class SubscribeListViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private SubscribeListRepository subscribeListRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBookRegisted_then_CREATE_1(
        @Payload BookRegisted bookRegisted
    ) {
        try {
            if (!bookRegisted.validate()) return;

            // view 객체 생성
            SubscribeList subscribeList = new SubscribeList();
            // view 객체에 이벤트의 Value 를 set 함
            // view 레파지 토리에 save
            subscribeListRepository.save(subscribeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //>>> DDD / CQRS
}
