package com.Penske.CodeProducer.Model;

import lombok.Data;
import lombok.Generated;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document(collection = "Practice-02")
@CompoundIndexes({
        @CompoundIndex(name = "Unique_All_Fields" , def = "{'code':1 , 'version':1}" , unique = true)
})
public class CodeEntity {
        @Id
        private String id;
        private String code;
        private String description;
        private String name;
        private double version;
        private String isActive = "FALSE";
}
// index compound index