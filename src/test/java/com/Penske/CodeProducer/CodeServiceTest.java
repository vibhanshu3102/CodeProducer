package com.Penske.CodeProducer;

import com.Penske.CodeProducer.Exception.DuplicateCodeException;
import com.Penske.CodeProducer.Model.CodeEntity;
import com.Penske.CodeProducer.Repository.CodeRepository;
import com.Penske.CodeProducer.Service.CodeService;
import com.mongodb.MongoWriteException;
import dto.CodeVersionDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CodeServiceTest {

    @Mock
    private CodeRepository codeRepository;

    @Mock
    private KafkaTemplate<String, CodeVersionDto> kafkaTemplate;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private CodeService codeService;

    private CodeEntity codeEntity;

    @BeforeEach
    void setUp() {
        // Initialize a CodeEntity object with default values
        codeEntity = new CodeEntity();
        codeEntity.setId(new ObjectId().toString());
        codeEntity.setCode("CODE001");
        codeEntity.setDescription("Description for code 001");
        codeEntity.setName("Code Name 1");
        codeEntity.setVersion(1.0);
        codeEntity.setIsActive("FALSE");
    }

    @Test
    void testSendCode_Success() {
        String result = codeService.sendCode(codeEntity);
        assertEquals("Code has been sent...", result);
    }

    @Test
    void testSendCode_DuplicateException() {
        // Mock MongoWriteException with appropriate error code
        MongoWriteException mongoWriteException = mock(MongoWriteException.class);
        when(mongoWriteException.getCode()).thenReturn(11000);

        // Set up the mock repository to throw the exception
        when(codeRepository.save(any(CodeEntity.class))).thenThrow(mongoWriteException);

        // Perform the test
        DuplicateCodeException thrownException = assertThrows(DuplicateCodeException.class, () -> {
            codeService.sendCode(codeEntity);
        });

        // Assert that the exception message contains the expected text
        assertTrue(thrownException.getMessage().contains("Duplicate code value"));
    }
    @Test
    void testGetFilterData_EmptyStatus() {
        List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

        when(codeRepository.findAll()).thenReturn(codeEntities);

        List<CodeEntity> result = codeService.getFilterData("");

        assertEquals(codeEntities, result);

    }

    @Test
    void testGetFilterData_WithStatus() {
        List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

        when(codeRepository.findByIsActive("TRUE")).thenReturn(codeEntities);

        List<CodeEntity> result = codeService.getFilterData("TRUE");

        assertEquals(codeEntities, result);
    }

@Test
void testGetLatestCode_EmptyStatus() {
    // Prepare test data
    List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

    // Create AggregationResults with non-null results list and a compatible metadata type
    AggregationResults<CodeEntity> results = new AggregationResults<>(codeEntities, new Document()); // Replace SomeMetadataClass with actual type

    // Mock the aggregation result
    when(mongoTemplate.aggregate(any(Aggregation.class), eq("Practice-02"), eq(CodeEntity.class)))
            .thenReturn(results);

    // Call the method under test
    List<CodeEntity> result = codeService.getLatestCode("");

    // Verify the results
    assertEquals(codeEntities, result);

}


    @Test
    void testGetLatestCode_WithStatus() {
        // Prepare test data
        List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

        // Create AggregationResults with non-null results list and metadata
        AggregationResults<CodeEntity> results = new AggregationResults<CodeEntity>(codeEntities,new Document());

        // Mock the aggregation result
        when(mongoTemplate.aggregate(any(Aggregation.class), eq("Practice-02"), eq(CodeEntity.class)))
                .thenReturn(results);

        // Call the method under test with a specific status
        List<CodeEntity> result = codeService.getLatestCode("TRUE");

        // Verify the results
        assertEquals(codeEntities, result);

    }


    @Test
    void testGetLatestVersionOfCode() {
        List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

        AggregationResults<CodeEntity> results = new AggregationResults<>(codeEntities, new Document());

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("Practice-02"), eq(CodeEntity.class)))
                .thenReturn(results);

        CodeEntity result = codeService.getLatestVersionOfCode("CODE001");

        assertNotNull(result);
        assertEquals(1.0, result.getVersion());
    }
}
