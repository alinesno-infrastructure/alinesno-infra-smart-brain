package com.alinesno.infra.smart.brain.gateway.provider;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.api.dto.ChatRequestDto;
import com.alinesno.infra.smart.brain.service.IGenerateTaskService;
import com.alinesno.infra.smart.brain.service.ILLMApiService;
import com.alinesno.infra.smart.brain.utils.ChatUtils;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/llm/")
public class BrainTaskController {

    @Autowired
    private ILLMApiService llmApiService ;

    @Autowired
    private IGenerateTaskService generateTaskService ;

    /**
     * 获取实时内容响应接口
     * @return
     */
    @SneakyThrows
    @PostMapping("/chatCompletions")
    public AjaxResult chatCompletions(@RequestBody ChatRequestDto chatRequestDto ){

        ChatCompletion chatCompletion = ChatUtils.convert(chatRequestDto) ;

        try (Response response = llmApiService.chatCompletions(chatCompletion)) {
            if (response.isSuccessful()) {

                assert response.body() != null;
                String responseBody = response.body().string();

                log.debug("Response: " + responseBody);
                return AjaxResult.success("操作成功", JSONObject.parseObject(responseBody));
            } else {
                log.error("Request failed: " + response.code() + " " + response.message());
            }

            return AjaxResult.error(response.message());
        }
    }

    /**
     * 提交任务
     * @param dto 任务数据传输对象
     * @return 返回提交结果
     */
    @PostMapping("/chatTask")
    public AjaxResult chatTask(@Validated @RequestBody BrainTaskDto dto) {

        log.debug("dto = {}" , dto);
        // 处理提交任务的逻辑
        Long businessId = generateTaskService.commitTask(dto) ;
        return AjaxResult.success("任务提交成功." , businessId); // 返回成功结果
    }

}
