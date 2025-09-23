package com.alinesno.infra.smart.assistant.point.listener;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.dto.ConsumePointsDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.point.event.AgentPointConsumeEvent;
import com.alinesno.infra.smart.assistant.point.listener.base.BaseListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 积分消费事件监听器
 * 处理积分消费后的后续操作，与核心业务解耦
 */
@Slf4j
@Component
public class AgentPointConsumeListener extends BaseListener {

    /**
     * 异步处理积分消费事件
     * @param event 积分消费事件
     */
    public void handlePointConsumeEvent(AgentPointConsumeEvent event) {
        // 记录积分消费日志
        log.info("用户[{}]在组织[{}]，角色:{}",
                event.getUserId(),
                event.getOrgId(),
                event.getRoleId());
        
        // 可以在这里添加其他业务逻辑，如：
        // 1. 保存积分消费记录到数据库
        // 2. 当用户积分过低时发送提醒
        // 3. 统计积分消费数据用于分析
        // 4. 积分消费达到一定阈值时触发其他业务流程

        IndustryRoleEntity role = industryRoleService.getById(event.getRoleId()) ;

        ConsumePointsDto consumePointsDto = new ConsumePointsDto();
        consumePointsDto.setUserId(event.getUserId());
        consumePointsDto.setOrgId(event.getOrgId());
        consumePointsDto.setTaskType("agent");
        consumePointsDto.setLevel(role.getRoleLevel() == null ? "L3" : role.getRoleLevel());
        consumePointsDto.setRoleId(role.getId());
        consumePointsDto.setRoleName(role.getRoleName());
        consumePointsDto.setTimestamp(System.currentTimeMillis());

        R<String> result = pointConsumer.consumePoints(consumePointsDto) ;

        if(!R.isSuccess(result)){
            throw new RuntimeException(result.getMsg()) ;
        }
        log.debug("用户[{}]在组织[{}]积分消耗结果: {}", event.getUserId(), event.getOrgId(), result);
    }

}
    