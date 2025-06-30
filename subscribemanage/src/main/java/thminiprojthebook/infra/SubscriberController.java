package thminiprojthebook.infra;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thminiprojthebook.domain.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/subscribers")
@Transactional
public class SubscriberController {

    @Autowired
    SubscriberRepository subscriberRepository;

    @RequestMapping(
        value = "/subscribers/{id}/createsubscriber",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Subscriber createSubscriber(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /subscriber/createSubscriber  called #####");
        Optional<Subscriber> optionalSubscriber = subscriberRepository.findById(
            id
        );

        optionalSubscriber.orElseThrow(() -> new Exception("No Entity Found"));
        Subscriber subscriber = optionalSubscriber.get();
        subscriber.createSubscriber();

        subscriberRepository.save(subscriber);
        return subscriber;
    }

    @RequestMapping(
        value = "/subscribers/{id}/monthlypaid",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Subscriber monthlyPaid(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /subscriber/monthlyPaid  called #####");
        Optional<Subscriber> optionalSubscriber = subscriberRepository.findById(
            id
        );

        optionalSubscriber.orElseThrow(() -> new Exception("No Entity Found"));
        Subscriber subscriber = optionalSubscriber.get();
        subscriber.monthlyPaid();

        subscriberRepository.save(subscriber);
        return subscriber;
    }
}
//>>> Clean Arch / Inbound Adaptor

