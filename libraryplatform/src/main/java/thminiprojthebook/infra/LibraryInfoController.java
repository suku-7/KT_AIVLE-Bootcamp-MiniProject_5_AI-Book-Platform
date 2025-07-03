package thminiprojthebook.infra;

import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thminiprojthebook.domain.*;
import org.springframework.transaction.annotation.Transactional; //Transactional 


@RestController
@RequestMapping("/libraryInfos")
@Transactional
public class LibraryInfoController {

    @Autowired
    LibraryInfoRepository libraryInfoRepository;

    // 1. 전체 도서 목록 조회
    @GetMapping
    public ResponseEntity<Iterable<LibraryInfo>> getAllLibraryInfos() {
        Iterable<LibraryInfo> libraryInfos = libraryInfoRepository.findAll();
        return ResponseEntity.ok(libraryInfos);
    }

    // 2. 특정 도서 상세 조회
    @GetMapping("/{bookId}")
    public ResponseEntity<LibraryInfo> getLibraryInfoById(
            @PathVariable("bookId") Long bookId) {
        Optional<LibraryInfo> optionalLibraryInfo = 
            libraryInfoRepository.findById(bookId);
        
        return optionalLibraryInfo
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // 3. AI 요약/표지 생성을 통한 출간 프로세스
    @PostMapping("/publish")
    public ResponseEntity<LibraryInfo> publishBook(
        @RequestBody AiSummarized aiSummarized) {
    
    LibraryInfo savedBook = LibraryInfo.publish(aiSummarized);
    
    return ResponseEntity
            .created(URI.create("/libraryInfos/" + savedBook.getBookId()))
            .body(savedBook);
}


    // 4. 책 소장 증가 처리 (베스트셀러 부여 트리거)
    @PostMapping("/select")
    public ResponseEntity<String> processBookSelection(
            @RequestBody BuyBookSub buyBookSub) {
        
        // 도메인 이벤트 처리 (소장 횟수 증가)
        LibraryInfo.buyBookIncrease(buyBookSub);
        
        // 베스트셀러 부여는 자동으로 수행됨
        return ResponseEntity.ok(
            "Book selection processed."
        );
    }

    // 5. 베스트셀러 목록 조회 (추가 기능)
    @GetMapping("/bestsellers")
    public ResponseEntity<Iterable<LibraryInfo>> getBestsellers() {
        Iterable<LibraryInfo> bestsellers = 
            libraryInfoRepository.findByBestsellerTrue();
        return ResponseEntity.ok(bestsellers);
    }
}
