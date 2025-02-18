package com.alinesno.infra.base.search.memory.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties
@Configuration
public class GetObservationWithTimeWorkerPrompt {

    private String time_string_format ;
    private String get_observation_with_time_system ;
    private String get_observation_with_time_few_shot ;
    private String get_observation_with_time_user_query ;

    public String getObservationWithTimePrompt(String userName, String numObs) {
        return getGet_observation_with_time_system()
                .replace("{user_name}", userName)
                .replace("{num_obs}", numObs);
    }


    public String getObservationWithTimeFewShot(String userName) {
        return getGet_observation_with_time_few_shot()
                .replace("{user_name}", userName);
    }


    public String getObservationWithTimeUserQuery(String userName, String userQuery) {
        return getGet_observation_with_time_user_query()
                .replace("{user_name}", userName)
                .replace("{user_query}", userQuery);
    }
}
