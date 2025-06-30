package thminiprojthebook.infra;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        @RequestBody ModifyContextCommand command,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        Optional<Writing> optionalWriting = writingRepository.findById(id);
        optionalWriting.orElseThrow(() -> new Exception("No Entity Found"));

        Writing writing = optionalWriting.get();
        writing.modifyContext(command);

        writingRepository.save(writing);
        return writing;
    }
    @RequestMapping(
        value = "/writings/my",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8"
    )
    public List<Writing> getAllMyWritings(String token){
        
        return
        
    }
}
//>>> Clean Arch / Inbound Adaptor
