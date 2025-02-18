package com.alinesno.infra.base.search.memory.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties
@Configuration
public class ExtractTimeWorkerPrompt {

    private String time_string_format ;
    private String extract_time_system ;
    private String extract_time_few_shot;
    private String extract_time_user_query ;
}
