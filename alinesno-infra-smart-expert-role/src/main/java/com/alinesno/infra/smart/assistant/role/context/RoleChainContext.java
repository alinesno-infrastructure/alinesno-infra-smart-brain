package com.alinesno.infra.smart.assistant.role.context;

import com.alinesno.infra.smart.assistant.api.adapter.TaskContentDto;
import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;
import com.yomahub.liteflow.slot.DefaultContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 自定义上下文
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleChainContext extends DefaultContext {

    private String businessId ;  // 业务ID
    private String channel ;  // 频道ID

    private String promptId ;  // 指令标识
    private String userContent ; // 用户要求
    private Map<String , Object> params ; // 请求参数

    private String assistantYamlContent ; // assistant的回复内容
    private TaskContentDto assistantContent ; // Brain服务回复
    private NoticeDto noticeDto ; // 机器人请求

    // 执行周期
    private long startTime ;  // 开始时间
    private long endTime ; // 结束时间

    // 针对聚合场景
    private List<String> businessIds ; // 所有业务ID列表
    private List<TaskContentDto> assistantContents ; // 所有返回列表结构

}
