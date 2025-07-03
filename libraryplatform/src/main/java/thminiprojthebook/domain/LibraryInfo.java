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
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;

    private Long authorId;

    private String authorName;

    private String title;

    @Column(length = 1500) // 긴 URL을 저장하기 위해 길이를 늘림
    private String imageUrl;

    private String summary;

    private String context;

    private String classificationType;

    private Date publishDate;

    private Long selectCount;

    private Integer rank;

    private Boolean bestseller;

    public static LibraryInfoRepository repository() {
        LibraryInfoRepository libraryInfoRepository = LibraryplatformApplication.applicationContext.getBean(
            LibraryInfoRepository.class
        );
        return libraryInfoRepository;
    }

    //<<< Clean Arch / Port Method
   public void increaseSelectCount() {
        if (this.selectCount == null) {
            this.selectCount = 0L; // Long 타입이므로 0L로 초기화
        }
        this.selectCount++;

        // 추가: 베스트셀러 조건 체크 (5회 이상 시)
    if (this.selectCount >= 5) {
        this.bestseller = true; // 베스트셀러로 설정
        new BestsellerGiven(this).publishAfterCommit(); // 이벤트 발행
    }
    }

    //<<< Clean Arch / Port Method
    public static void buyBookIncrease(BuyBookSub buyBookSub) {
        /**
         * subscribemanage에서 책 소장(구매) 이벤트가 발생하면,
         * libraryplatform의 해당 책(LibraryInfo)의 소장 횟수(selectCount)를 1 증가시킵니다.
         */
        if (buyBookSub.getBookId() != null) {
            repository().findById(buyBookSub.getBookId()).ifPresent(libraryInfo -> {
                libraryInfo.increaseSelectCount();
                repository().save(libraryInfo);

                System.out.println(
                    "buyBookIncrease Policy: bookId " + libraryInfo.getBookId() +
                    " selectCount is now " + libraryInfo.getSelectCount()
                );
            });
        }
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
   public static void publish(AiSummarized aiSummarized ,CoverCreated coverCreated) {
        /**
         * AI 서비스가 문서 요약을 완료하면, 그 내용을 바탕으로
         * 새로운 LibraryInfo(도서 정보)를 생성하고 Published 이벤트를 발행합니다.
         */

        // 1. 새로운 LibraryInfo 객체를 생성합니다.
        LibraryInfo libraryInfo = new LibraryInfo();
        
        // 2. AiSummarized 이벤트에 담겨온 정보로 객체의 속성을 설정합니다.
        libraryInfo.setBookId(aiSummarized.getBookId());
        libraryInfo.setAuthorId(aiSummarized.getAuthorId());
        libraryInfo.setAuthorName(aiSummarized.getAuthorName()); // 이벤트에 authorName도 있다면 추가
        libraryInfo.setTitle(aiSummarized.getTitle()); // 이벤트에 title도 있다면 추가
        libraryInfo.setSummary(aiSummarized.getSummary()); // AI가 생성한 요약
        libraryInfo.setContext(aiSummarized.getContext()); // AI가 분석한 원문
        libraryInfo.setClassificationType(aiSummarized.getClassificationType());
        libraryInfo.setPublishDate(new Date()); // 현재 시간으로 발행일 설정
        libraryInfo.setSelectCount(0L); // 최초 생성 시 선택 횟수는 0
        libraryInfo.setImageUrl(coverCreated.getImageUrl());
        
        // 3. 생성된 객체를 DB에 저장합니다.
        repository().save(libraryInfo);

        // 4. '출간됨' 이벤트를 발행하여 다른 서비스에 알립니다.
        Published published = new Published(libraryInfo);
        published.publishAfterCommit();

    
        System.out.println(
            "publish(AiSummarized) Policy: A new book has been published." + "publish(CoverCreated) Policy: Cover image updated for bookId: " +
            " bookId: " + libraryInfo.getBookId()
        );
    }
}

