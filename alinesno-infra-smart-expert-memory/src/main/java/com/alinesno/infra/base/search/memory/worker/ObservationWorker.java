package com.alinesno.infra.base.search.memory.worker;

import com.alinesno.infra.base.search.memory.BaseWorker;
import com.alinesno.infra.base.search.memory.prompt.GetObservationWorkerPrompt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ObservationWorker extends BaseWorker {

    @Autowired
    private GetObservationWorkerPrompt observeWorkerPrompt;

    @SneakyThrows
    public String parseMemory(String userName , String memoryData) {

        String[] lines = memoryData.split("\\R");
        int lineCount = lines.length;

        String systemPrompt = observeWorkerPrompt.getObservationPrompt(userName, String.valueOf(lineCount));
        String fewShot = observeWorkerPrompt.getObservationFewShot(userName);
        String userQuery = observeWorkerPrompt.getObservationUserQuery(userName, memoryData);

        return getResultContent(systemPrompt, fewShot, userQuery);
    }

}