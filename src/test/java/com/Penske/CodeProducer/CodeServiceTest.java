package com.Penske.CodeProducer;

import com.Penske.CodeProducer.Config.AppConstant;
import com.Penske.CodeProducer.Exception.DuplicateCodeException;
import com.Penske.CodeProducer.Model.CodeEntity;
import com.Penske.CodeProducer.Repository.CodeRepository;
import com.Penske.CodeProducer.Service.CodeService;
import com.mongodb.MongoWriteException;
import dto.CodeVersionDto;
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
import org.springframework.data.mongodb.core.query.Criteria;
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
    private CodeVersionDto codeVersionDto;
//hi nigga
    @BeforeEach
    void setUp() {
        // Initialize a CodeEntity object with default values
        codeEntity = new CodeEntity();
        codeEntity.setId(new ObjectId().toString()); // Ensure ID is a String
        codeEntity.setCode("CODE001");
        codeEntity.setDescription("Description for code 001");
        codeEntity.setName("Code Name 1");
        codeEntity.setVersion(1.0);
        codeEntity.setIsActive("FALSE");

        // Initialize a CodeVersionDto object with default values
        codeVersionDto = new CodeVersionDto();
        codeVersionDto.setCode(codeEntity.getCode());
        codeVersionDto.setVersion(codeEntity.getVersion());

        // Mock the repository and KafkaTemplate behavior
        when(codeRepository.save(any(CodeEntity.class))).thenReturn(codeEntity);
        doNothing().when(kafkaTemplate).send(eq(AppConstant.topic_name), any(CodeVersionDto.class));
    }

    @Test
    void testSendCode_Success() {
        String result = codeService.sendCode(codeEntity);
        assertEquals("Code has been sent...", result);
        verify(codeRepository, times(1)).save(codeEntity);
        verify(kafkaTemplate, times(1)).send(eq(AppConstant.topic_name), any(CodeVersionDto.class));
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
        verify(codeRepository, times(1)).findAll();
    }

    @Test
    void testGetFilterData_WithStatus() {
        List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

        when(codeRepository.findByIsActive("TRUE")).thenReturn(codeEntities);

        List<CodeEntity> result = codeService.getFilterData("TRUE");

        assertEquals(codeEntities, result);
        verify(codeRepository, times(1)).findByIsActive("TRUE");
    }

    @Test
    void testGetLatestCode_EmptyStatus() {
        List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

        AggregationResults<CodeEntity> results = new AggregationResults<>(codeEntities, null);

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("Practice-02"), eq(CodeEntity.class)))
                .thenReturn(results);

        List<CodeEntity> result = codeService.getLatestCode("");

        assertEquals(codeEntities, result);
        verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("Practice-02"), eq(CodeEntity.class));
    }

    @Test
    void testGetLatestCode_WithStatus() {
        List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

        AggregationResults<CodeEntity> results = new AggregationResults<>(codeEntities, null);

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("Practice-02"), eq(CodeEntity.class)))
                .thenReturn(results);

        List<CodeEntity> result = codeService.getLatestCode("TRUE");

        assertEquals(codeEntities, result);
        verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("Practice-02"), eq(CodeEntity.class));
    }

    @Test
    void testGetLatestVersionOfCode() {
        List<CodeEntity> codeEntities = Collections.singletonList(codeEntity);

        AggregationResults<CodeEntity> results = new AggregationResults<>(codeEntities, null);

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("Practice-02"), eq(CodeEntity.class)))
                .thenReturn(results);

        CodeEntity result = codeService.getLatestVersionOfCode("CODE001");

        assertNotNull(result);
        assertEquals(1.0, result.getVersion());
    }
}
