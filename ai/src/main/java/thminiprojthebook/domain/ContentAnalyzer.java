package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.AiApplication;
import thminiprojthebook.domain.AiSummarized;

@Entity
@Table(name = "ContentAnalyzer_table")
@Data
//<<< DDD / Aggregate Root
public class ContentAnalyzer {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO) // <-- 1. 이 줄을 제거 또는 주석 처리합니다.
    private Long bookId;

    private Long authorId;

    private String context;

    private Integer maxLength;

    private String language;

    private String classificationType;

    private String requestedBy;

    public static ContentAnalyzerRepository repository() {
        ContentAnalyzerRepository contentAnalyzerRepository = AiApplication.applicationContext.getBean(
            ContentAnalyzerRepository.class
        );
        return contentAnalyzerRepository;
    }

    //<<< Clean Arch / Port Method
    public static void aiSummarize(BookRegisted bookRegisted) {
        /**
         * '책 등록됨' 이벤트를 받으면, AI 요약 분석을 위한 ContentAnalyzer 데이터를 생성합니다.
         */

        // 1. 새로운 ContentAnalyzer 객체를 생성합니다.
        ContentAnalyzer contentAnalyzer = new ContentAnalyzer();

        // 2. Policy에서 직접 bookId를 설정해줍니다. (이 줄 추가)
        contentAnalyzer.setBookId(bookRegisted.getBookId());
        
        // 2. bookRegisted 이벤트에 있는 값들을 설정합니다.
        //    (주의: ContentAnalyzer의 Id는 @GeneratedValue 이므로 bookId를 직접 set하지 않습니다.)
        contentAnalyzer.setAuthorId(bookRegisted.getAuthorId());
        contentAnalyzer.setContext(bookRegisted.getContext());
        // contentAnalyzer.setAuthorName(bookRegisted.getAuthorName()); // ContentAnalyzer에 authorName 필드가 있다면 추가

        // 3. 이벤트에 없는 값들은 테스트용 임의의 값으로 설정합니다.
        contentAnalyzer.setMaxLength(500); // 최대 요약 길이는 500자로 임의 설정
        contentAnalyzer.setLanguage("Korean"); // 언어는 Korean으로 임의 설정
        contentAnalyzer.setClassificationType("Fiction"); // 장르는 Fiction으로 임의 설정
        contentAnalyzer.setRequestedBy("System"); // 요청자는 시스템으로 설정

        // 4. 생성된 객체를 DB에 저장합니다.
        repository().save(contentAnalyzer);

        // 5. 'AI 요약 완료' 이벤트를 발행합니다.
        AiSummarized aiSummarized = new AiSummarized(contentAnalyzer);
        aiSummarized.publishAfterCommit();

        System.out.println(
            "aiSummarize Policy: Content analysis task created for bookId: " + bookRegisted.getBookId()
        );
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
