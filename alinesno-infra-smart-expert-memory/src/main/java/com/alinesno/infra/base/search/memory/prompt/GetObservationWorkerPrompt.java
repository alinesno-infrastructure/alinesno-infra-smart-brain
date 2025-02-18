package com.alinesno.infra.base.search.memory.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties
@Configuration
public class GetObservationWorkerPrompt {

    private String get_observation_system ;
    private String get_observation_few_shot ;
    private String get_observation_user_query ;

    public String getObservationPrompt(String userName, String numObs) {
        return getGet_observation_system()
                .replace("{user_name}", userName)
                .replace("{num_obs}", numObs);
    }

    public String getObservationFewShot(String userName) {
        return getGet_observation_few_shot()
                .replace("{user_name}", userName);
    }

    public String getObservationUserQuery(String userName, String userQuery) {
        return getGet_observation_user_query()
                .replace("{user_name}", userName)
                .replace("{user_query}", userQuery);
    }

}
