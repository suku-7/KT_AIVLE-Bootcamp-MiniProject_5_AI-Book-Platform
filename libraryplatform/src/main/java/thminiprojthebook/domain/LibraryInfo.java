package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.LibraryplatformApplication;
import thminiprojthebook.domain.BestsellerGiven;
import thminiprojthebook.domain.Published;

@Entity
@Table(name = "LibraryInfo_table")
@Data
//<<< DDD / Aggregate Root
public class LibraryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;

    private String title;

    private String imageUrl;

    private String authorId;

    private String authorName;

    private String context;

    private String classificationType;

    private Date publishDate;

    private String summary;

    private String context;

    private Boolean bestseller;

    private String imageUrl;

    private String maxLength;

    private String requestedBy;

    private Integer rank;

    

    public static LibraryInfoRepository repository() {
        LibraryInfoRepository libraryInfoRepository = LibraryplatformApplication.applicationContext.getBean(
            LibraryInfoRepository.class
        );
        return libraryInfoRepository;
    }

    // 카운트를 1 증가시키는 내부 비즈니스 메서드
    public void increaseSelectCount() {
        if (this.selectCount == null) {
            this.selectCount = 0L; // Long 타입이므로 0L로 초기화
        }
        this.selectCount++;
    }

    //<<< Clean Arch / Port Method
    public static void subscribeIncrease(PointDecreased pointDecreased) {
          repository().findById(pointDecreased.getBookId()).ifPresent(libraryInfo -> {
        
        // 구독 수 증가
        Integer currentSelectCount = libraryInfo.getSelectCount();
        if (currentSelectCount == null) {
            currentSelectCount = 0;
        }
        libraryInfo.setSelectCount(currentSelectCount + 1);
        
        // 구독 수가 5회 이상이면 베스트셀러 부여
        boolean wasBestseller = libraryInfo.getBestseller() != null && libraryInfo.getBestseller();
        if (libraryInfo.getSelectCount() >= 5) {
            libraryInfo.setBestseller(true);
        }
        
        // 변경사항 저장
        repository().save(libraryInfo);
        
        // 전체 베스트셀러 순위(rank) 업데이트
        updateAllBestsellerRanks();
        
        // 새로 베스트셀러가 된 경우 이벤트 발행
        if (libraryInfo.getBestseller() && !wasBestseller) {
            BestsellerGiven bestsellerGiven = new BestsellerGiven(libraryInfo);
            bestsellerGiven.publishAfterCommit();
        }
    });
}

// 모든 베스트셀러의 rank(순위)를 업데이트하는 메소드
private static void updateAllBestsellerRanks() {
    // 베스트셀러인 책들을 selectCount 기준 내림차순으로 조회
    List<LibraryInfo> bestsellers = repository()
        .findByBestsellerTrueOrderBySelectCountDesc();
    
    // rank 값 할당 (1위, 2위, 3위...)
    for (int i = 0; i < bestsellers.size(); i++) {
        LibraryInfo book = bestsellers.get(i);
        book.setRank(i + 1); // rank 필드 사용
        repository().save(book);
    }
}

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void publish(AiSummarized aiSummarized) {
       repository().findById(aiSummarized.getBookId()).ifPresent(libraryInfo -> {
        
        // AI 문서 요약 데이터를 도서정보에 저장
        libraryInfo.setSummary(aiSummarized.getContext()); // context를 summary로 저장
        libraryInfo.setClassficationTpe(aiSummarized.getClassificationType());
        
        // 원본 컨텍스트도 저장 (필요시)
        libraryInfo.setContext(aiSummarized.getContext());
        
        // 출간 날짜 설정
        libraryInfo.setPublishDate(new Date());
        
        // 도서정보 저장
        repository().save(libraryInfo);
        
        // 출간됨 이벤트 발행
        Published published = new Published(libraryInfo);
        published.publishAfterCommit();
    });
}

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void publish(CoverCreated coverCreated) {
         repository().findById(coverCreated.getBookId()).ifPresent(libraryInfo -> {
        
        // AI 표지 생성 데이터를 도서정보에 저장
        libraryInfo.setImageUrl(coverCreated.getImageUrl());
        libraryInfo.setTitle(coverCreated.getTitle());
        
        // 출간 날짜 설정
        libraryInfo.setPublishDate(new Date());
        
        // 도서정보 저장
        repository().save(libraryInfo);
        
        // 출간됨 이벤트 발행
        Published published = new Published(libraryInfo);
        published.publishAfterCommit();
    });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
