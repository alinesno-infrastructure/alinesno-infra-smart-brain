package com.alinesno.infra.smart.assistant.scene.scene.pptCreation.controller;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.dto.AIPPTRequestDto;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.prompt.AIPPTPromptHandle;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service.IPPTGeneratorSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.pptCreation.service.IPPTManagerService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.ISSEService;
import com.alinesno.infra.smart.scene.entity.PPTGenerateSceneEntity;
import com.alinesno.infra.smart.scene.entity.PPTManagerEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/pptGenerate/tools")
public class AiPPTController {

    @Autowired
    private IPPTManagerService pptManagerService;

    @Autowired
    private ISSEService sseService;

    @Autowired
    private IPPTGeneratorSceneService pptGeneratorEngineer;

    @Autowired
    private IIndustryRoleService roleService ;

    /**
     * 生成PPT
     *
     * @param playload
     * @return
     */
    @PostMapping(value = "/aippt", produces = {MediaType.APPLICATION_JSON_VALUE, "text/event-stream"})
    public AjaxResult generatePPT(@RequestBody AIPPTRequestDto playload) {

        String channelStreamId = playload.getChannelStreamId() ;

        Long pptId = playload.getPptId();
        PPTManagerEntity pptManager = pptManagerService.getById(pptId);
        long sceneId = pptManager.getSceneId();

        PPTGenerateSceneEntity pptGenerateSceneEntity = pptGeneratorEngineer.getBySceneId(sceneId , null) ;

        String promptText = AIPPTPromptHandle.generatorPromptForGenerator(playload , pptManager);

        MessageTaskInfo taskInfo = new MessageTaskInfo();

        taskInfo.setRoleId(pptGenerateSceneEntity.getPptGeneratorEngineer());
        taskInfo.setChannelStreamId(channelStreamId);
        taskInfo.setChannelId(sceneId);
        taskInfo.setSceneId(sceneId);
        taskInfo.setText(promptText);

        // 优先获取到结果内容
        new Thread(new Runnable(){

            @Override
            public void run() {
                roleService.runRoleAgent(taskInfo);
                log.debug("taskInfo.getFullContent = \r\n{}", taskInfo.getFullContent());
                try {
                    sseService.sendDone(channelStreamId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        return AjaxResult.success();
    }

    /**
     * 建立SSE连接
     * @param clientId
     * @return
     */
    @GetMapping(value = "openConn/{clientId}", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter connect(@PathVariable("clientId") String clientId) {
        return sseService.getConn(clientId);
    }

}