package thminiprojthebook.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.AiApplication;
import thminiprojthebook.service.DalleService;

@Entity
@Table(name = "CoverDesign_table")
@Data
//<<< DDD / Aggregate Root
public class CoverDesign {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO) // <-- 1. 이 줄을 제거 또는 주석 처리합니다.
    private Long bookId;

    private Long authorId;

    private String authorName;

    private String title;

    private String imageUrl;

    private String generatedBy;

    private Date updatedAt;

    private Date createdAt;

    public static CoverDesignRepository repository() {
        CoverDesignRepository coverDesignRepository = AiApplication.applicationContext.getBean(
            CoverDesignRepository.class
        );
        return coverDesignRepository;
    }

    //<<< Clean Arch / Port Method
    public static void autoCoverGeneratePolicy(BookRegisted bookRegisted) {
        /**
         * '책 등록됨' 이벤트를 받으면, AI 표지 생성을 위한 CoverDesign 데이터를 생성합니다.
         */

        // 1. 새로운 CoverDesign 객체를 생성합니다.
        CoverDesign coverDesign = new CoverDesign();
        
        // 2. Policy에서 직접 bookId를 설정해줍니다. (이 줄 추가)
        coverDesign.setBookId(bookRegisted.getBookId());

        // 2. bookRegisted 이벤트에 있는 값들을 설정합니다.
        coverDesign.setAuthorId(bookRegisted.getAuthorId());
        coverDesign.setAuthorName(bookRegisted.getAuthorName());
        coverDesign.setTitle(bookRegisted.getTitle());

        // 3. 이벤트에 없는 값들은 테스트용 임의의 값으로 설정합니다.
        coverDesign.setImageUrl("https://picsum.photos/seed/" + bookRegisted.getBookId() + "/400/600"); // 테스트용 랜덤 이미지 URL
        coverDesign.setGeneratedBy("AI (DALL-E)");
        coverDesign.setCreatedAt(new java.util.Date());
        coverDesign.setUpdatedAt(new java.util.Date());

        // 4. 생성된 객체를 DB에 저장합니다.
        repository().save(coverDesign);

        // 5. 'AI 표지 생성 완료' 이벤트를 발행합니다.
        CoverCreated coverCreated = new CoverCreated(coverDesign);
        coverCreated.publishAfterCommit();

        System.out.println(
            "autoCoverGeneratePolicy: Cover design task created for bookId: " + bookRegisted.getBookId()
        );
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
