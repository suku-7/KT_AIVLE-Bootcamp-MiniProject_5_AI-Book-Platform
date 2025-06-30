package thminiprojthebook.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.ApplicationContext;
import thminiprojthebook.AiApplication;
import thminiprojthebook.service.DalleService;

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CoverDesignTest {

    @Mock
    private CoverDesignRepository coverDesignRepository;
    
    @Mock
    private DalleService dalleService;
    
    @Mock
    private ApplicationContext applicationContext;
    
    @Mock
    private AiProcessTrackerRepository aiProcessTrackerRepository;

    private BookRegisted testBookRegistedEvent;
    
    @BeforeEach
    void setUp() {
        // Test event 데이터 준비
        testBookRegistedEvent = new BookRegisted();
        testBookRegistedEvent.setBookId(1L);
        testBookRegistedEvent.setTitle("Test Book Title");
        testBookRegistedEvent.setContext("This is a test book about artificial intelligence and machine learning.");
        testBookRegistedEvent.setAuthorId(123L);
        testBookRegistedEvent.setRegistration(true);
    }

    @Test
    void testAutoCoverGeneratePolicy_성공적인_커버_생성() {
        // Given
        String expectedImageUrl = "https://example.com/generated-cover.jpg";
        AiProcessTracker mockTracker = new AiProcessTracker();
        
        // Mock 설정
        when(dalleService.generateCoverImage(anyString(), anyString()))
            .thenReturn(expectedImageUrl);
        
        // MockedStatic을 사용하여 정적 메서드들 모킹
        try (MockedStatic<AiApplication> aiAppMock = Mockito.mockStatic(AiApplication.class);
             MockedStatic<AiProcessTracker> trackerMock = Mockito.mockStatic(AiProcessTracker.class);
             MockedStatic<CoverDesign> coverDesignMock = Mockito.mockStatic(CoverDesign.class)) {
            
            // AiApplication context 모킹
            aiAppMock.when(() -> AiApplication.applicationContext).thenReturn(applicationContext);
            when(applicationContext.getBean(CoverDesignRepository.class)).thenReturn(coverDesignRepository);
            when(applicationContext.getBean(DalleService.class)).thenReturn(dalleService);
            
            // AiProcessTracker 모킹
            trackerMock.when(() -> AiProcessTracker.findByBookId(anyString())).thenReturn(null);
            trackerMock.when(() -> AiProcessTracker.initializeForBook(anyString(), anyString(), anyLong()))
                .thenReturn(mockTracker);
            
            // CoverDesign.repository() 모킹
            coverDesignMock.when(() -> CoverDesign.repository()).thenReturn(coverDesignRepository);
            
            // When - 실제 테스트 대상 메서드를 직접 호출할 수 없으므로 실제 구현 로직을 테스트
            coverDesignMock.when(() -> CoverDesign.autoCoverGeneratePolicy(testBookRegistedEvent))
                .thenCallRealMethod();
            
            // 실제 메서드 호출
            CoverDesign.autoCoverGeneratePolicy(testBookRegistedEvent);
            
            // Then - 검증
            // DalleService가 올바른 파라미터로 호출되었는지 확인
            verify(dalleService).generateCoverImage(
                eq("Test Book Title"), 
                eq("This is a test book about artificial intelligence and machine learning.")
            );
        }
    }

    @Test
    void testAutoCoverGeneratePolicy_DalleService_실패시_처리() {
        // Given
        AiProcessTracker mockTracker = new AiProcessTracker();
        
        // DalleService가 null 반환하도록 설정
        when(dalleService.generateCoverImage(anyString(), anyString()))
            .thenReturn(null);
        
        try (MockedStatic<AiApplication> aiAppMock = Mockito.mockStatic(AiApplication.class);
             MockedStatic<AiProcessTracker> trackerMock = Mockito.mockStatic(AiProcessTracker.class);
             MockedStatic<CoverDesign> coverDesignMock = Mockito.mockStatic(CoverDesign.class)) {
            
            // Mock 설정
            aiAppMock.when(() -> AiApplication.applicationContext).thenReturn(applicationContext);
            when(applicationContext.getBean(CoverDesignRepository.class)).thenReturn(coverDesignRepository);
            when(applicationContext.getBean(DalleService.class)).thenReturn(dalleService);
            
            trackerMock.when(() -> AiProcessTracker.findByBookId(anyString())).thenReturn(null);
            trackerMock.when(() -> AiProcessTracker.initializeForBook(anyString(), anyString(), anyLong()))
                .thenReturn(mockTracker);
            
            coverDesignMock.when(() -> CoverDesign.repository()).thenReturn(coverDesignRepository);
            coverDesignMock.when(() -> CoverDesign.autoCoverGeneratePolicy(testBookRegistedEvent))
                .thenCallRealMethod();
            
            // When
            assertDoesNotThrow(() -> CoverDesign.autoCoverGeneratePolicy(testBookRegistedEvent));
            
            // Then
            // DalleService는 호출되어야 함
            verify(dalleService).generateCoverImage(anyString(), anyString());
            // 하지만 CoverDesign은 저장되지 않아야 함 (imageUrl이 null이므로)
            verify(coverDesignRepository, never()).save(any(CoverDesign.class));
        }
    }

    @Test
    void testCoverDesign_엔티티_생성_및_저장() {
        // Given
        CoverDesign coverDesign = new CoverDesign();
        coverDesign.setId(1L);
        coverDesign.setAuthorId(123L);
        coverDesign.setBookId("1");
        coverDesign.setTitle("Test Book");
        coverDesign.setImageUrl("https://example.com/cover.jpg");
        coverDesign.setGeneratedBy("DALL-E-3");
        coverDesign.setCreatedAt(new Date());
        coverDesign.setUpdatedAt(new Date());
        
        when(coverDesignRepository.save(any(CoverDesign.class))).thenReturn(coverDesign);
        
        // When
        CoverDesign savedCoverDesign = coverDesignRepository.save(coverDesign);
        
        // Then
        assertNotNull(savedCoverDesign);
        assertEquals("Test Book", savedCoverDesign.getTitle());
        assertEquals("https://example.com/cover.jpg", savedCoverDesign.getImageUrl());
        assertEquals("DALL-E-3", savedCoverDesign.getGeneratedBy());
        assertEquals(Long.valueOf(123L), savedCoverDesign.getAuthorId());
        
        verify(coverDesignRepository).save(coverDesign);
    }

    @Test
    void testCoverCreated_이벤트_발행() {
        // Given
        CoverDesign coverDesign = new CoverDesign();
        coverDesign.setId(1L);
        coverDesign.setAuthorId(123L);
        coverDesign.setBookId("1");
        coverDesign.setTitle("Test Book");
        coverDesign.setImageUrl("https://example.com/cover.jpg");
        coverDesign.setGeneratedBy("DALL-E-3");
        coverDesign.setCreatedAt(new Date());
        
        // When
        CoverCreated coverCreated = new CoverCreated(coverDesign);
        coverCreated.setId(coverDesign.getId());
        coverCreated.setAuthorId(coverDesign.getAuthorId());
        coverCreated.setBookId(coverDesign.getBookId());
        coverCreated.setTitle(coverDesign.getTitle());
        coverCreated.setImageUrl(coverDesign.getImageUrl());
        coverCreated.setGeneratedBy(coverDesign.getGeneratedBy());
        coverCreated.setCreatedAt(coverDesign.getCreatedAt().toString());
        
        // Then
        assertNotNull(coverCreated);
        assertEquals(coverDesign.getId(), coverCreated.getId());
        assertEquals(coverDesign.getAuthorId(), coverCreated.getAuthorId());
        assertEquals(coverDesign.getBookId(), coverCreated.getBookId());
        assertEquals(coverDesign.getTitle(), coverCreated.getTitle());
        assertEquals(coverDesign.getImageUrl(), coverCreated.getImageUrl());
        assertEquals(coverDesign.getGeneratedBy(), coverCreated.getGeneratedBy());
    }

    @Test
    void testBookRegisted_이벤트_데이터_검증() {
        // Given & When
        BookRegisted event = testBookRegistedEvent;
        
        // Then
        assertNotNull(event);
        assertEquals(Long.valueOf(1L), event.getBookId());
        assertEquals("Test Book Title", event.getTitle());
        assertEquals("This is a test book about artificial intelligence and machine learning.", event.getContext());
        assertEquals(Long.valueOf(123L), event.getAuthorId());
        assertTrue(event.getRegistration());
    }
}
