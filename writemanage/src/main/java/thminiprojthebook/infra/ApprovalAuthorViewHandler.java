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
public class ApprovalAuthorViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private ApprovalAuthorRepository approvalAuthorRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenAuthorApproved_then_CREATE_1(
        @Payload AuthorApproved authorApproved
    ) {
        try {
            if (!authorApproved.validate()) return;

            // view 객체 생성
            ApprovalAuthor approvalAuthor = new ApprovalAuthor();
            // view 객체에 이벤트의 Value 를 set 함
            approvalAuthor.setId(Long.valueOf(authorApproved.getAuthorId()));
            approvalAuthor.setIsApproved(authorApproved.getIsApproved());
            // view 레파지 토리에 save
            approvalAuthorRepository.save(approvalAuthor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //>>> DDD / CQRS
}
