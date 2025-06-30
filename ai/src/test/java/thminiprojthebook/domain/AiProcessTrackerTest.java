package thminiprojthebook.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AiProcessTrackerTest {

    @Mock
    private AiProcessTrackerRepository aiProcessTrackerRepository;

    private AiProcessTracker testTracker;
    
    @BeforeEach
    void setUp() {
        testTracker = new AiProcessTracker();
        testTracker.setId(1L);
        testTracker.setBookId("1");
        testTracker.setTitle("Test Book");
        testTracker.setAuthorId(123L);
    }

    @Test
    void testInitializeForBook() {
        // Given
        String bookId = "1";
        String title = "Test Book";
        Long authorId = 123L;
        
        try (MockedStatic<AiProcessTracker> trackerMock = Mockito.mockStatic(AiProcessTracker.class)) {
            trackerMock.when(() -> AiProcessTracker.repository()).thenReturn(aiProcessTrackerRepository);
            trackerMock.when(() -> AiProcessTracker.initializeForBook(bookId, title, authorId))
                .thenCallRealMethod();
            
            when(aiProcessTrackerRepository.save(any(AiProcessTracker.class))).thenReturn(testTracker);
            
            // When
            AiProcessTracker result = AiProcessTracker.initializeForBook(bookId, title, authorId);
            
            // Then
            assertNotNull(result);
            verify(aiProcessTrackerRepository).save(any(AiProcessTracker.class));
        }
    }

    @Test
    void testMarkCoverGenerationCompleted() {
        // Given
        String imageUrl = "https://example.com/cover.jpg";
        String generatedBy = "DALL-E-3";
        
        testTracker.setContentAnalysisCompleted(false);
        testTracker.setCoverGenerationCompleted(false);
        
        try (MockedStatic<AiProcessTracker> trackerMock = Mockito.mockStatic(AiProcessTracker.class)) {
            trackerMock.when(() -> AiProcessTracker.repository()).thenReturn(aiProcessTrackerRepository);
            when(aiProcessTrackerRepository.save(any(AiProcessTracker.class))).thenReturn(testTracker);
            
            // When
            testTracker.markCoverGenerationCompleted(imageUrl, generatedBy);
            
            // Then
            assertTrue(testTracker.getCoverGenerationCompleted());
            assertEquals(imageUrl, testTracker.getImageUrl());
            assertEquals(generatedBy, testTracker.getGeneratedBy());
            verify(aiProcessTrackerRepository).save(testTracker);
        }
    }

    @Test
    void testMarkContentAnalysisCompleted() {
        // Given
        String summary = "This is a test summary";
        String classificationType = "Fiction";
        String language = "Korean";
        Integer maxLength = 1000;
        
        testTracker.setContentAnalysisCompleted(false);
        testTracker.setCoverGenerationCompleted(false);
        
        try (MockedStatic<AiProcessTracker> trackerMock = Mockito.mockStatic(AiProcessTracker.class)) {
            trackerMock.when(() -> AiProcessTracker.repository()).thenReturn(aiProcessTrackerRepository);
            when(aiProcessTrackerRepository.save(any(AiProcessTracker.class))).thenReturn(testTracker);
            
            // When
            testTracker.markContentAnalysisCompleted(summary, classificationType, language, maxLength);
            
            // Then
            assertTrue(testTracker.getContentAnalysisCompleted());
            assertEquals(summary, testTracker.getSummary());
            assertEquals(classificationType, testTracker.getClassificationType());
            assertEquals(language, testTracker.getLanguage());
            assertEquals(maxLength, testTracker.getMaxLength());
            verify(aiProcessTrackerRepository).save(testTracker);
        }
    }

    @Test
    void testBothProcessesCompleted_발행이벤트() {
        // Given
        testTracker.setContentAnalysisCompleted(true);
        testTracker.setCoverGenerationCompleted(false);
        testTracker.setSummary("Test summary");
        testTracker.setClassificationType("Fiction");
        testTracker.setLanguage("Korean");
        testTracker.setMaxLength(1000);
        
        String imageUrl = "https://example.com/cover.jpg";
        String generatedBy = "DALL-E-3";
        
        try (MockedStatic<AiProcessTracker> trackerMock = Mockito.mockStatic(AiProcessTracker.class)) {
            trackerMock.when(() -> AiProcessTracker.repository()).thenReturn(aiProcessTrackerRepository);
            when(aiProcessTrackerRepository.save(any(AiProcessTracker.class))).thenReturn(testTracker);
            
            // When
            testTracker.markCoverGenerationCompleted(imageUrl, generatedBy);
            
            // Then
            assertTrue(testTracker.getContentAnalysisCompleted());
            assertTrue(testTracker.getCoverGenerationCompleted());
            assertNotNull(testTracker.getCompletedAt());
            verify(aiProcessTrackerRepository, times(1)).save(testTracker);
        }
    }

    @Test
    void testFindByBookId() {
        // Given
        String bookId = "1";
        
        try (MockedStatic<AiProcessTracker> trackerMock = Mockito.mockStatic(AiProcessTracker.class)) {
            trackerMock.when(() -> AiProcessTracker.repository()).thenReturn(aiProcessTrackerRepository);
            trackerMock.when(() -> AiProcessTracker.findByBookId(bookId)).thenCallRealMethod();
            when(aiProcessTrackerRepository.findByBookId(bookId)).thenReturn(testTracker);
            
            // When
            AiProcessTracker result = AiProcessTracker.findByBookId(bookId);
            
            // Then
            assertNotNull(result);
            assertEquals(bookId, result.getBookId());
            verify(aiProcessTrackerRepository).findByBookId(bookId);
        }
    }

    @Test
    void testTracker_초기상태() {
        // Given
        AiProcessTracker newTracker = new AiProcessTracker();
        
        // Then
        assertFalse(newTracker.getContentAnalysisCompleted());
        assertFalse(newTracker.getCoverGenerationCompleted());
        assertNotNull(newTracker.getCreatedAt());
        assertNull(newTracker.getCompletedAt());
    }
}
