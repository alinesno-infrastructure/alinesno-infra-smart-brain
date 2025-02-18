package com.alinesno.infra.base.search.memory.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties
@Configuration
public class LongContraRepeatWorkerPrompt {

    private String long_contra_repeat_system ;
    private String long_contra_repeat_few_shot ;
    private String long_contra_repeat_user_query ;
}
