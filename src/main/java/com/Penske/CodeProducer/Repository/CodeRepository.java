package com.Penske.CodeProducer.Repository;

import com.Penske.CodeProducer.Model.CodeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeRepository extends MongoRepository<CodeEntity , String> {

    public List<CodeEntity> findByIsActive(String Status);
}
