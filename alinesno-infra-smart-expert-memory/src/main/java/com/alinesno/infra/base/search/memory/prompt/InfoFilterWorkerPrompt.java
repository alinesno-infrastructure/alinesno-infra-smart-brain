package com.alinesno.infra.base.search.memory.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties
@Configuration
public class InfoFilterWorkerPrompt {

    private String info_filter_system ;
    private String info_filter_few_shot ;
    private String info_filter_user_query ;
}
