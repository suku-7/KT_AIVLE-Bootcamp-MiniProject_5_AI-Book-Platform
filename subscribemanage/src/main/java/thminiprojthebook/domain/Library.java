package thminiprojthebook.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import thminiprojthebook.SubscribemanageApplication;

@Entity
@Table(name = "Library_table")
@Data
//<<< DDD / Aggregate Root
public class Library {

    @Id
    // @GeneratedValue는 데이터베이스가 ID를 자동으로 생성하라는 뜻입니다. 하지만 bookInfoPolicy에서는 Published 이벤트로부터 받은 bookId를 그대로 Library의 ID로 직접 설정해야 합니다. 두 방식이 충돌하게 됩니다.
    // 따라서 @GeneratedValue 어노테이션을 반드시 제거하셔야 합니다.
    //@GeneratedValue(strategy = GenerationType.AUTO) - 미사용 처리
    private Long bookId;

    private String title;

    private String authorName;

    private String imageUrl;

    private String summary;

    public static LibraryRepository repository() {
        LibraryRepository libraryRepository = SubscribemanageApplication.applicationContext.getBean(
            LibraryRepository.class
        );
        return libraryRepository;
    }

    public static void bookInfoPolicy(Published published) {
        /**
         * libraryplatform에서 새로운 책이 출간(Published)될 때마다
         * 해당 도서 정보를 구독관리(subscribemanage) 컨텍스트의 Library DB에
         * 동기화(복제)하여 저장합니다.
         *
         * 이렇게하면 '내 서재' 같은 기능을 만들 때 매번 libraryplatform에
         * 책 정보를 물어볼 필요 없이, 자체 DB에서 빠르게 조회할 수 있습니다.
         */

        // 1. Published 이벤트에 bookId가 있는지 먼저 확인합니다.
        if (published.getBookId() != null) {
            
            // 2. 새로운 Library 객체를 생성합니다.
            Library library = new Library();

            // 3. Published 이벤트에 담겨온 정보로 객체의 속성을 설정합니다.
            //    이때 Published 이벤트의 bookId를 Library의 Id로 사용합니다.
            library.setBookId(published.getBookId());
            library.setTitle(published.getTitle());
            library.setAuthorName(published.getAuthorName());
            library.setImageUrl(published.getImageUrl());
            library.setSummary(published.getSummary());

            // 4. 생성된 Library 객체를 DB에 저장합니다.
            repository().save(library);

            System.out.println(
                "bookInfoPolicy: A new book has been synchronized to the library." +
                " bookId: " + published.getBookId() +
                ", title: " + published.getTitle()
            );
        }
    }

}
//>>> DDD / Aggregate Root
