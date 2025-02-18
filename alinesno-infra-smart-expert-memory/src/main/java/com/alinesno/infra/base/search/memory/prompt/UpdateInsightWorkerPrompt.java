package com.alinesno.infra.base.search.memory.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties
@Configuration
public class UpdateInsightWorkerPrompt {

    public String update_insight_system ;
    public String update_insight_few_shot ;
    public String update_insight_user_query ;
    public String insight_string_format ;

}