package com.Penske.CodeProducer.Config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class CodeConfig {

    @Bean
    public NewTopic topicBuilder(){
        return TopicBuilder.name(AppConstant.topic_name).build();
    }
}
