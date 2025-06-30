package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.PointApplication;
import thminiprojthebook.domain.KtSignedupPointCharged;
import thminiprojthebook.domain.PointDecreased;
import thminiprojthebook.domain.PointInsufficient;
import thminiprojthebook.domain.PointRecharged;
import thminiprojthebook.domain.StandardSignedupPointCharged;

@Entity
@Table(name = "Point_table")
@Data
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointId;
    private String userId;
    private Integer pointBalance;

    public static PointRepository repository() {
        return SubscribemanageApplication.applicationContext.getBean(PointRepository.class);
    }

    public static void signup(SubscriberCreated event) {
        Point point = new Point();
        point.setUserId(event.getId().toString());

        if ("Y".equalsIgnoreCase(event.getIsKt())) {
            point.setPointBalance(5000);
            KtSignedupPointCharged charged = new KtSignedupPointCharged(point);
            charged.setUserId(point.getUserId());
            charged.publishAfterCommit();

        } else {
            point.setPointBalance(1000);
            StandardSignedupPointCharged charged = new StandardSignedupPointCharged(point);
            charged.setUserId(point.getUserId());
            charged.publishAfterCommit();
        }

        repository().save(point);
    }

}
//>>> DDD / Aggregate Root
