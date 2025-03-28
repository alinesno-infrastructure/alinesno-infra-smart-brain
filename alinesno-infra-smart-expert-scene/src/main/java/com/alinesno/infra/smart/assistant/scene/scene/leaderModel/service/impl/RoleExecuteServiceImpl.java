package com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.scene.core.entity.RoleExecuteEntity;
import com.alinesno.infra.smart.scene.enums.TaskStatus;
import com.alinesno.infra.smart.assistant.scene.scene.leaderModel.mapper.RoleExecuteMapper;
import com.alinesno.infra.smart.assistant.scene.scene.leaderModel.service.IRoleExecuteService;
import com.alinesno.infra.smart.scene.dto.RoleTaskDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class RoleExecuteServiceImpl extends IBaseServiceImpl<RoleExecuteEntity, RoleExecuteMapper> implements IRoleExecuteService {

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Override
    public void saveNewTasks(long sceneId, List<RoleTaskDto> tasks) {

        // 更新所有任务的状态为已结束
        LambdaUpdateWrapper<RoleExecuteEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(RoleExecuteEntity::getSceneId, sceneId);
        lambdaUpdateWrapper.set(RoleExecuteEntity::getIsFinished, TaskStatus.COMPLETED.getCode());
        this.update(lambdaUpdateWrapper);

        List<RoleExecuteEntity> records = getRoleExecuteEntities(sceneId, tasks);
        saveBatch(records);
    }

    @Override
    public boolean isAllTaskCompleted(Long sceneId) {

        LambdaQueryWrapper<RoleExecuteEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleExecuteEntity::getSceneId, sceneId);
        lambdaQueryWrapper.ne(RoleExecuteEntity::getIsFinished, TaskStatus.COMPLETED.getCode());

        return count(lambdaQueryWrapper) == 0 ;
    }

    @Override
    public void finishTask(Long id) {
        LambdaUpdateWrapper<RoleExecuteEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(RoleExecuteEntity::getId, id);
        lambdaUpdateWrapper.set(RoleExecuteEntity::getIsFinished, TaskStatus.COMPLETED.getCode());
        this.update(lambdaUpdateWrapper);
    }

    @Override
    public String getPreContent(String preRoleId , long sceneId) {

        String[] split = preRoleId.split(",");
        List<Long> ids = Arrays.stream(split).map(s -> {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new RuntimeException("无效的编辑人员ID: " + s, e);
            }
        }).toList();

        LambdaQueryWrapper<RoleExecuteEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(RoleExecuteEntity::getWorkerRoleId, ids)
                .eq(RoleExecuteEntity::getSceneId, sceneId)
                .eq(RoleExecuteEntity::getIsFinished, TaskStatus.COMPLETED.getCode()) ;

        List<RoleExecuteEntity> list = this.list(lambdaQueryWrapper);

        return list.stream().map(RoleExecuteEntity::getExecuteResult).reduce((a, b) -> a + "\n\n" + b).orElse(null);
    }

    @NotNull
    private List<RoleExecuteEntity> getRoleExecuteEntities(long sceneId, List<RoleTaskDto> tasks) {
        List<RoleExecuteEntity> records = new ArrayList<>() ;

        for (RoleTaskDto task : tasks) {

            // 判断roleId是否正确
            IndustryRoleEntity e =  industryRoleService.getById(task.getWorkerRoleId()) ;
            if(e != null){

                RoleExecuteEntity record = new RoleExecuteEntity();

                long id = IdUtil.getSnowflakeNextId() ;

                task.setId(id);

                record.setId(id);
                record.setSceneId(sceneId);
                record.setTaskDesc(task.getTaskDesc());
                record.setLeaderRoleId(task.getLeaderRoleId());
                record.setWorkerRoleId(task.getWorkerRoleId());
                record.setPreRoleId(task.getPreRoleId());
                record.setIsFinished(TaskStatus.NOT_STARTED.getCode());

                records.add(record);
            }else{
                log.warn("未找到对应的角色信息:{}", task.getWorkerRoleId());
            }

        }
        return records;
    }
}
