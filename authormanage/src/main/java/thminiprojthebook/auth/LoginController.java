package thminiprojthebook.auth;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import thminiprojthebook.domain.Author;
import thminiprojthebook.domain.AuthorRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final AuthorRepository authorRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthorLoginRequest request) {
        Author author = authorRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!author.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body("비밀번호 불일치");
        }
        
        if(!author.getIsApproved()){
            return ResponseEntity.status(401).body("작가 승인이 되지 않았습니다.");
        }
        String token = jwtUtil.generateToken(author.getName());
        return ResponseEntity.ok(Map.of("token", token));
    }
}