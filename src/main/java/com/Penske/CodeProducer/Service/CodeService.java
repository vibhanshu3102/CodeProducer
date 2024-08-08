package com.Penske.CodeProducer.Service;

import com.Penske.CodeProducer.Config.AppConstant;
import com.Penske.CodeProducer.Exception.DuplicateCodeException;
import com.Penske.CodeProducer.Exception.GlobalExceptionHandler;
import com.Penske.CodeProducer.Model.CodeEntity;
import com.Penske.CodeProducer.Repository.CodeRepository;
import com.mongodb.MongoWriteException;
import dto.CodeVersionDto;
import org.bson.types.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeService {
    @Autowired
    CodeRepository codeRepository;

    @Autowired
    KafkaTemplate<String , CodeVersionDto> kafkaTemplate;

    @Autowired
    MongoTemplate mongoTemplate;

    public String sendCode(CodeEntity codeEntity) {
        try {
            codeRepository.save(codeEntity);
            String message = codeEntity.getCode();
            CodeVersionDto codeVersionDto = new CodeVersionDto();
            codeVersionDto.setCode(codeEntity.getCode());
            codeVersionDto.setVersion(codeEntity.getVersion());
            kafkaTemplate.send(AppConstant.topic_name, codeVersionDto);
            return "Code has been sent...";
        } catch (MongoWriteException e) {

            if (e.getCode() == 11000) {
                throw new DuplicateCodeException("Duplicate code value: " + codeEntity.getCode());
            }
            throw new RuntimeException("Error saving code entity", e);
        }
    }

    public List<CodeEntity> getFilterData(String status) {
        if(status.isEmpty()){
            return codeRepository.findAll();
        }
        return codeRepository.findByIsActive(status);
    }

    //Function to get Latest Version of Code may it be TRue or False......

    public List<CodeEntity> getLatestCode(String status) {
        Aggregation aggregation;

        if (status.equals("")) {
            // Case 1: When status is an empty string
            aggregation = Aggregation.newAggregation(
                    // Stage 1: Sort documents by version in descending order
                    Aggregation.sort(Sort.by(Sort.Direction.DESC, "version")),

                    // Stage 2: Group by 'code' and get the latest version for each code
                    Aggregation.group("code")
                            .first("$_id").as("id")
                            .first("code").as("code")
                            .first("description").as("description")
                            .first("name").as("name")
                            .first("version").as("version")
                            .first("isActive").as("isActive"),

                    // Stage 3: Project fields to return
                    Aggregation.project("id", "code", "description", "name", "version", "isActive")
            );
        } else {
            // Case 2: When status is not an empty string
            aggregation = Aggregation.newAggregation(
                    // Stage 1: Match documents by 'isActive' status
                    Aggregation.match(Criteria.where("isActive").is(status)),

                    // Stage 2: Sort documents by version in descending order
                    Aggregation.sort(Sort.by(Sort.Direction.DESC, "version")),

                    // Stage 3: Group by 'code' and get the latest version for each code
                    Aggregation.group("code")
                            .first("$_id").as("id")
                            .first("code").as("code")
                            .first("description").as("description")
                            .first("name").as("name")
                            .first("version").as("version")
                            .first("isActive").as("isActive"),

                    // Stage 4: Project fields to return
                    Aggregation.project("id", "code", "description", "name", "version", "isActive")
            );
        }

        AggregationResults<CodeEntity> results = mongoTemplate.aggregate(aggregation, "Practice-02", CodeEntity.class);
        return results.getMappedResults();
    }



    //Get the Details of code with max version

    public CodeEntity getLatestVersionOfCode(String code) {
        List<CodeEntity> codeEntity = new ArrayList<>();
        Aggregation aggregation = Aggregation.newAggregation(

                // Stage 1: Match documents by 'code' status
                Aggregation.match(Criteria.where("code").is(code)),

                // Stage 2: Sort documents by version in descending order
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "version")),

                // Stage 3: Group by 'code' and get the latest version for each code
                Aggregation.group("code")
                        .first("$_id").as("id")
                        .first("code").as("code")
                        .first("description").as("description")
                        .first("name").as("name")
                        .first("version").as("version")
                        .first("isActive").as("isActive"),

                // Stage 4: Project fields to return
                Aggregation.project("id", "code", "description", "name", "version", "isActive")
        );
        AggregationResults<CodeEntity> results = mongoTemplate.aggregate(aggregation, "Practice-02", CodeEntity.class);
       List<CodeEntity> codeEntityList = results.getMappedResults();
        return codeEntityList.get(0);
    }
}
