package thminiprojthebook.infra;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.*;

import thminiprojthebook.auth.JwtUtil;
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
    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/writings/my")
    public ResponseEntity<?> getAllMyWritings(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("토큰이 필요합니다.");
            }

            String token = authHeader.substring(7); // "Bearer " 제거
            Long authorId = jwtUtil.validateAndGetAuthorId(token); // subject에서 userId 꺼냄

            List<Writing> writings = writingRepository.findByAuthorId(authorId);
            return ResponseEntity.ok(writings);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");
        }
    }
}
//>>> Clean Arch / Inbound Adaptor
