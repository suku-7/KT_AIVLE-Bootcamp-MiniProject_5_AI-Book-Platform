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
    public ResponseEntity<?> registBook(
        @PathVariable(value = "id") Long id,
        @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("토큰이 필요합니다.");
        }

        String token = authHeader.substring(7); // "Bearer " 제거
        Long authorId = jwtUtil.validateAndGetAuthorId(token);

        // 승인 여부 확인
        Optional<ApprovalAuthor> optional = approvalAuthorRepository.findById(authorId);
        if (optional.isEmpty() || !optional.get().getIsApproved()) {
            return ResponseEntity.status(403).body("작가 승인되지 않았습니다.");
        }

        Writing writing = writingRepository.findById(id)
            .orElseThrow(() -> new Exception("해당 책이 존재하지 않습니다."));

        // 작성자 본인인지 확인
        if (!writing.getAuthorId().equals(authorId)) {
            return ResponseEntity.status(403).body("본인의 책만 등록할 수 있습니다.");
        }

        writing.registBook();
        writingRepository.save(writing);
        return ResponseEntity.ok(writing);
    }

    @Autowired
    ApprovalAuthorRepository approvalAuthorRepository; 

    @PostMapping("/writings")
    public ResponseEntity<?> createWriting(
        @RequestBody Writing writing,
        @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("토큰이 필요합니다.");
        }

        String token = authHeader.substring(7);
        Long authorId = jwtUtil.validateAndGetAuthorId(token);

        // 승인 여부 확인
        Optional<ApprovalAuthor> optional = approvalAuthorRepository.findById(authorId);
        if (optional.isEmpty() || !optional.get().getIsApproved()) {
            return ResponseEntity.status(403).body("작가 승인되지 않았습니다.");
        }

        // 필요한 필드 설정
        writing.setAuthorId(authorId);
        writing.setRegistration(false); // 처음엔 미등록 상태
        // authorName은 클라이언트에서 보내주거나 추후 별도 로직 필요
        writingRepository.save(writing);

        return ResponseEntity.ok(writing);
    }

    @DeleteMapping("/writings/{id}")
    public ResponseEntity<?> deleteWriting(
        @PathVariable Long id,
        @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("토큰이 필요합니다.");
        }

        String token = authHeader.substring(7);
        Long authorId = jwtUtil.validateAndGetAuthorId(token);

        Optional<ApprovalAuthor> optional = approvalAuthorRepository.findById(authorId);
        if (optional.isEmpty() || !optional.get().getIsApproved()) {
            return ResponseEntity.status(403).body("작가 승인되지 않았습니다.");
        }

        Writing writing = writingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("책이 존재하지 않습니다."));

        if (!writing.getAuthorId().equals(authorId)) {
            return ResponseEntity.status(403).body("본인의 책만 삭제할 수 있습니다.");
        }

        writingRepository.delete(writing);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    @RequestMapping(
    value = "/writings/{id}/modifycontext",
    method = RequestMethod.PUT,
    produces = "application/json;charset=UTF-8"
    )
    public ResponseEntity<?> modifyContext(
        @PathVariable(value = "id") Long id,
        @RequestBody ModifyContextCommand command,
        @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("토큰이 필요합니다.");
        }

        String token = authHeader.substring(7);
        Long authorId = jwtUtil.validateAndGetAuthorId(token);

        Optional<ApprovalAuthor> optional = approvalAuthorRepository.findById(authorId);
        if (optional.isEmpty() || !optional.get().getIsApproved()) {
            return ResponseEntity.status(403).body("작가 승인되지 않았습니다.");
        }

        Writing writing = writingRepository.findById(id)
            .orElseThrow(() -> new Exception("해당 책이 존재하지 않습니다."));

        if (!writing.getAuthorId().equals(authorId)) {
            return ResponseEntity.status(403).body("본인의 책만 수정할 수 있습니다.");
        }

        writing.modifyContext(command);
        writingRepository.save(writing);
        return ResponseEntity.ok(writing);
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
