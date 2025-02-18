package com.alinesno.infra.base.search.memory.prompt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties
@Configuration
public class GetReflectionSubjectWorkerPrompt {

    private String get_reflection_subject_system ;
    private String get_reflection_subject_few_shot ;
    private String get_reflection_subject_user_query ;

    public String getReflectionSubjectPrompt(String userName, String numQuestions) {
        return getGet_reflection_subject_system()
                .replace("{user_name}", userName)
                .replace("{num_questions}", numQuestions);
    }

    public String getReflectionSubjectFewShot(String userName) {
        return getGet_reflection_subject_few_shot()
                .replace("{user_name}", userName);
    }

    public String getReflectionSubjectUserQuery(String userName, String userQuery, String existKeys) {
        return getGet_reflection_subject_user_query()
                .replace("{user_name}", userName)
                .replace("{user_query}", userQuery)
                .replace("{exist_keys}", existKeys);
    }
}
