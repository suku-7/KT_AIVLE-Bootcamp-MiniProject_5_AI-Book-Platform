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
// @RequestMapping(value="/subscribedBooks")
@Transactional
public class SubscribedBookController {

    @Autowired
    SubscribedBookRepository subscribedBookRepository;

    @RequestMapping(
        value = "/subscribedBooks/{id}/buybook",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public SubscribedBook buyBook(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /subscribedBook/buyBook  called #####");
        Optional<SubscribedBook> optionalSubscribedBook = subscribedBookRepository.findById(
            id
        );

        optionalSubscribedBook.orElseThrow(() ->
            new Exception("No Entity Found")
        );
        SubscribedBook subscribedBook = optionalSubscribedBook.get();
        subscribedBook.buyBook();

        subscribedBookRepository.save(subscribedBook);
        return subscribedBook;
    }
}
//>>> Clean Arch / Inbound Adaptor

