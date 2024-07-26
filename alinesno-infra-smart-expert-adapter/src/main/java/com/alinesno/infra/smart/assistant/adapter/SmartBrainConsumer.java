package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.smart.assistant.api.adapter.BrainTaskDto;
import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Query;

/**
 * 调用接口
 */
@BaseRequest(baseURL = "#{alinesno.infra.gateway.host}/smart-brain" , connectTimeout = 30*1000)
public interface SmartBrainConsumer {

    /**
     * 发送离线任务
     * @param dto
     * @return
     */
    @Post(url="/api/llm/chatTask")
    AjaxResult chatTask(@JSONBody BrainTaskDto dto) ;

    /**
     * 查询离线任务
     * @param businessId
     * @return
     */
    @Post(url="/api/llm/chatContent")
    AjaxResult chatContent(@Query("businessId") String businessId) ;

    /**
     * 更新任务生成内容
     * @param dto
     * @return
     */
    @Post(url="/api/llm/modifyContent")
    AjaxResult modifyContent(@JSONBody TaskContentDto dto);
}
