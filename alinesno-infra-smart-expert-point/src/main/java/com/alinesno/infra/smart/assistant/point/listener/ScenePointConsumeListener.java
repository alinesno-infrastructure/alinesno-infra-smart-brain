package com.alinesno.infra.smart.assistant.point.listener;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.dto.ConsumePointsDto;
import com.alinesno.infra.smart.assistant.point.event.ScenePointConsumeEvent;
import com.alinesno.infra.smart.assistant.point.listener.base.BaseListener;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 积分消费事件监听器
 * 处理积分消费后的后续操作，与核心业务解耦
 */
@Slf4j
@Component
public class ScenePointConsumeListener extends BaseListener {

    /**
     * 异步处理积分消费事件
     * @param event 积分消费事件
     */
    public void handlePointConsumeEvent(ScenePointConsumeEvent event) {
        // 记录积分消费日志
        log.info("场景[{}]在组织[{}]，场景:{} , 任务:{}",
                event.getUserId(),
                event.getOrgId(),
                event.getSceneId() ,
                event.getTaskId());
        
        // 可以在这里添加其他业务逻辑，如：
        // 1. 保存积分消费记录到数据库
        // 2. 当用户积分过低时发送提醒
        // 3. 统计积分消费数据用于分析
        // 4. 积分消费达到一定阈值时触发其他业务流程

        SceneEntity scene = sceneService.getById(event.getSceneId()) ;

        ConsumePointsDto consumePointsDto = new ConsumePointsDto();
        consumePointsDto.setUserId(event.getUserId());
        consumePointsDto.setOrgId(event.getOrgId());
        consumePointsDto.setTaskType("scene");
        consumePointsDto.setLevel(scene.getSceneLevel() == null ? "L3" : scene.getSceneLevel());
        consumePointsDto.setTimestamp(System.currentTimeMillis());

        consumePointsDto.setSceneId(scene.getId());
        consumePointsDto.setSceneName(scene.getSceneName());

        R<String> result = pointConsumer.consumePoints(consumePointsDto) ;

        if(!R.isSuccess(result)){
           throw new RuntimeException(result.getMsg()) ;
        }

        log.debug("场景[{}]在组织[{}]积分消耗结果: {}", event.getSceneId(), event.getOrgId(), result);

    }
}
    