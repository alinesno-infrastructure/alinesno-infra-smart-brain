package com.alinesno.infra.base.search.memory.worker;

import com.alinesno.infra.base.search.memory.BaseWorker;
import com.alinesno.infra.base.search.memory.prompt.GetObservationWithTimeWorkerPrompt;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ObservationWithTimeWorker extends BaseWorker {

    @Autowired
    private GetObservationWithTimeWorkerPrompt observeWorkerPrompt;

    @SneakyThrows
    public String parseMemory(String userName , String memoryData) {

        String[] lines = memoryData.split("\\R");
        int lineCount = lines.length;

        String systemPrompt = observeWorkerPrompt.getObservationWithTimePrompt(userName, String.valueOf(lineCount));
        String fewShot = observeWorkerPrompt.getObservationWithTimeFewShot(userName);
        String userQuery = observeWorkerPrompt.getObservationWithTimeUserQuery(userName, memoryData);

        return getResultContent(systemPrompt, fewShot, userQuery);
    }

}
