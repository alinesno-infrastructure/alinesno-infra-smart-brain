package com.alinesno.infra.smart.assistant.screen.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 领导者场景执行结果实体类
 */
@EqualsAndHashCode(callSuper = true)
@TableName("role_execute_result")
@Data
public class RoleExecuteEntity extends InfraBaseEntity {

    @Column(name = "role_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "角色ID")
    private Long leaderRoleId ; // 领导角色ID

    @Column(name = "worker_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "工作者ID")
    private Long workerRoleId; // 工作者角色ID

    @Column(name = "execute_result", type = MySqlTypeConstant.LONGTEXT, comment = "执行结果")
    private String executeResult ; // 执行结果

    @Column(name = "screen_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "所属场景ID")
    private Long screenId ; // 所属场景

    @Column(name = "error_message", type = MySqlTypeConstant.LONGTEXT, comment = "错误信息")
    private String errorMessage ;

    @Column(name = "is_finished", type = MySqlTypeConstant.TINYINT, length = 1, comment = "是否执行结束(1执行中|0未开始|2结束|9失败)")
    private Integer isFinished ; // 是否执行结束，由LeaderRole判断

}
