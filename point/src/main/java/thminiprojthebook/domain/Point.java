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
//<<< DDD / Aggregate Root
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pointId;

    private String userId;

    private Integer pointBalance;

    private Integer standardSignupPoint;

    private Integer ktSignupPoint;

    private Integer amount;

    private Date usedAt;

    @PostPersist
    public void onPostPersist() {
        PointRecharged pointRecharged = new PointRecharged(this);
        pointRecharged.publishAfterCommit();

        PointDecreased pointDecreased = new PointDecreased(this);
        pointDecreased.publishAfterCommit();

        StandardSignedupPointCharged standardSignedupPointCharged = new StandardSignedupPointCharged(
            this
        );
        standardSignedupPointCharged.publishAfterCommit();

        KtSignedupPointCharged ktSignedupPointCharged = new KtSignedupPointCharged(
            this
        );
        ktSignedupPointCharged.publishAfterCommit();

        PointInsufficient pointInsufficient = new PointInsufficient(this);
        pointInsufficient.publishAfterCommit();
    }

    public static PointRepository repository() {
        PointRepository pointRepository = PointApplication.applicationContext.getBean(
            PointRepository.class
        );
        return pointRepository;
    }

    //<<< Clean Arch / Port Method
    public static void pointDecrease(BuyBookSub buyBookSub) {
        //implement business logic here:

        /** Example 1:  new item 
        Point point = new Point();
        repository().save(point);

        PointInsufficient pointInsufficient = new PointInsufficient(point);
        pointInsufficient.publishAfterCommit();
        PointDecreased pointDecreased = new PointDecreased(point);
        pointDecreased.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        // if buyBookSub.subscriberId exists, use it
        
        // ObjectMapper mapper = new ObjectMapper();
        // Map<Long, Object> subscribedBookMap = mapper.convertValue(buyBookSub.getSubscriberId(), Map.class);

        repository().findById(buyBookSub.get???()).ifPresent(point->{
            
            point // do something
            repository().save(point);

            PointInsufficient pointInsufficient = new PointInsufficient(point);
            pointInsufficient.publishAfterCommit();
            PointDecreased pointDecreased = new PointDecreased(point);
            pointDecreased.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void signup(SubscriberCreated subscriberCreated) {
        //implement business logic here:

        /** Example 1:  new item 
        Point point = new Point();
        repository().save(point);

        StandardSignedupPointCharged standardSignedupPointCharged = new StandardSignedupPointCharged(point);
        standardSignedupPointCharged.publishAfterCommit();
        KtSignedupPointCharged ktSignedupPointCharged = new KtSignedupPointCharged(point);
        ktSignedupPointCharged.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(subscriberCreated.get???()).ifPresent(point->{
            
            point // do something
            repository().save(point);

            StandardSignedupPointCharged standardSignedupPointCharged = new StandardSignedupPointCharged(point);
            standardSignedupPointCharged.publishAfterCommit();
            KtSignedupPointCharged ktSignedupPointCharged = new KtSignedupPointCharged(point);
            ktSignedupPointCharged.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
