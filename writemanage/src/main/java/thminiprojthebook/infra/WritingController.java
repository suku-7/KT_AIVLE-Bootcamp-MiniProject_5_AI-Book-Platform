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
// @RequestMapping(value="/writings")
@Transactional
public class WritingController {

    @Autowired
    WritingRepository writingRepository;

    @RequestMapping(
        value = "/writings/{id}/registbook",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Writing registBook(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /writing/registBook  called #####");
        Optional<Writing> optionalWriting = writingRepository.findById(id);

        optionalWriting.orElseThrow(() -> new Exception("No Entity Found"));
        Writing writing = optionalWriting.get();
        writing.registBook();

        writingRepository.save(writing);
        return writing;
    }

    @RequestMapping(
        value = "/writings/{id}/modifycontext",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public Writing modifyContext(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /writing/modifyContext  called #####");
        Optional<Writing> optionalWriting = writingRepository.findById(id);

        optionalWriting.orElseThrow(() -> new Exception("No Entity Found"));
        Writing writing = optionalWriting.get();
        writing.modifyContext();

        writingRepository.save(writing);
        return writing;
    }
}
//>>> Clean Arch / Inbound Adaptor
