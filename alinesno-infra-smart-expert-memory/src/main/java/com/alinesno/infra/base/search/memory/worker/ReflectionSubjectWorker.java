package com.alinesno.infra.base.search.memory.worker;

import com.alinesno.infra.base.search.memory.BaseWorker;
import com.alinesno.infra.base.search.memory.prompt.GetReflectionSubjectWorkerPrompt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReflectionSubjectWorker extends BaseWorker {

    @Autowired
    private GetReflectionSubjectWorkerPrompt reflectionSubjectWorkerPrompt ;

    @SneakyThrows
    public String parseMemory(String userName , String memoryData) {

        String[] lines = memoryData.split("\\R");
        int lineCount = lines.length;

        String systemPrompt = reflectionSubjectWorkerPrompt.getReflectionSubjectPrompt(userName, String.valueOf(lineCount));
        String fewShot = reflectionSubjectWorkerPrompt.getReflectionSubjectFewShot(userName);
        String userQuery = reflectionSubjectWorkerPrompt.getReflectionSubjectUserQuery(userName, memoryData , "");

        return getResultContent(systemPrompt, fewShot, userQuery);
    }

}
