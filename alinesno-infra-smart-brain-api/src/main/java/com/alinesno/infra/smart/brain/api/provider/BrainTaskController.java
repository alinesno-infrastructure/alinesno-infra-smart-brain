package com.alinesno.infra.smart.brain.api.provider;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import com.alinesno.infra.smart.brain.service.IAiGenTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BrainTaskController {

    @Autowired
    private IAiGenTaskService aiGenTaskService ;

    /**
     * 提交任务
     * @param dto 任务数据传输对象
     * @return 返回提交结果
     */
    @PostMapping("/commitTask")
    public AjaxResult commitTask(@RequestBody BrainTaskDto dto) {
        // 处理提交任务的逻辑

        log.debug("dto = {}" , dto);

        aiGenTaskService.commitTask(dto) ;

        return AjaxResult.success(); // 返回成功结果
    }

    /**
     * 提交任务并将结果保存到CMS中
     * @param dto 任务数据传输对象
     * @return 返回提交结果
     */
    @PostMapping("/commitTaskToCms")
    public AjaxResult commitTaskToCms(@RequestBody BrainTaskDto dto) {
        // 处理提交任务并保存到CMS的逻辑

        log.debug("dto = {}" , dto);

        aiGenTaskService.commitTaskToCms(dto) ;

        return AjaxResult.success(); // 返回成功结果
    }
}
