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

    private Long authorId;

    private String authorName;

    private String title;

    private String imageUrl;

    private String summary;

    private String context;

    private String classificationType;

    private Date publishDate;

    private Integer rank;

    private Boolean bestseller;

    private Long selectCount;

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
    public static void publish(AiSummarized aiSummarized) {
        /**
         * 'AI 요약 완료' 이벤트를 받으면,
         * 해당 bookId의 LibraryInfo가 있으면 정보를 업데이트하고, 없으면 새로 생성합니다.
         * 그리고 최종적으로 '출간됨(Published)' 이벤트를 발행합니다.
         */
        
        // 1. 이벤트의 bookId로 기존 LibraryInfo를 찾거나, 없으면 새로 만듭니다.
        LibraryInfo libraryInfo = repository().findById(aiSummarized.getBookId())
            .orElseGet(() -> {
                LibraryInfo newLibraryInfo = new LibraryInfo();
                newLibraryInfo.setBookId(aiSummarized.getBookId());
                newLibraryInfo.setAuthorId(aiSummarized.getAuthorId());
                newLibraryInfo.setSelectCount(0L); // 신규 생성이므로 0으로 초기화
                return newLibraryInfo;
            });
            
        // 2. AiSummarized 이벤트에 있는 정보로 필드를 업데이트합니다.
        libraryInfo.setContext(aiSummarized.getContext());
        libraryInfo.setSummary(aiSummarized.getContext()); // summary 정보가 따로 없으므로 context로 대체합니다.
        libraryInfo.setClassificationType(aiSummarized.getClassificationType());
        libraryInfo.setPublishDate(new Date()); // 이 시점을 출간일로 설정합니다.

        // 3. 최종본을 저장합니다.
        repository().save(libraryInfo);

        // 4. '출간됨' 이벤트를 발행하여 다른 서비스에 알립니다.
        Published published = new Published(libraryInfo);
        published.publishAfterCommit();

        System.out.println(
            "publish(AiSummarized): bookId " + libraryInfo.getBookId() + " info updated with summary and PUBLISHED."
        );
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void publish(CoverCreated coverCreated) {
        /**
         * '표지 생성 완료' 이벤트를 받으면,
         * 해당 bookId의 LibraryInfo가 있으면 정보를 업데이트하고, 없으면 새로 생성합니다.
         */
        
        // 1. 이벤트의 bookId로 기존 LibraryInfo를 찾거나, 없으면 새로 만듭니다.
        LibraryInfo libraryInfo = repository().findById(coverCreated.getBookId())
            .orElseGet(() -> {
                LibraryInfo newLibraryInfo = new LibraryInfo();
                newLibraryInfo.setBookId(coverCreated.getBookId());
                newLibraryInfo.setAuthorId(coverCreated.getAuthorId());
                newLibraryInfo.setSelectCount(0L); // 신규 생성이므로 0으로 초기화
                return newLibraryInfo;
            });

        // 2. CoverCreated 이벤트에 있는 정보로 필드를 업데이트합니다.
        libraryInfo.setTitle(coverCreated.getTitle());
        libraryInfo.setImageUrl(coverCreated.getImageUrl());
        // 참고: generatedBy, updatedAt 정보는 LibraryInfo에 없으므로 저장하지 않습니다.
        
        // 3. 최종본을 저장합니다.
        repository().save(libraryInfo);

        System.out.println(
            "publish(CoverCreated): bookId " + libraryInfo.getBookId() + " info updated with cover."
        );
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
