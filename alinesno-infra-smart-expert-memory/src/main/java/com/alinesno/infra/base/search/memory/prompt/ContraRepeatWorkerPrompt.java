package com.alinesno.infra.base.search.memory.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties
@Configuration
public class ContraRepeatWorkerPrompt {

    public String contra_repeat_system ;
    public String contra_repeat_few_shot;
    public String contra_repeat_user_query;

}