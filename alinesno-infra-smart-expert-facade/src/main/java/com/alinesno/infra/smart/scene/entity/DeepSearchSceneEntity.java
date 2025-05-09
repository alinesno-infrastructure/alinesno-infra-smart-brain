package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 深度搜索场景实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("deep_search_scene")
public class DeepSearchSceneEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private long sceneId;

    @TableField
    @Column(name = "search_planner_engineer", type = MySqlTypeConstant.BIGINT, length = 32, comment = "搜索规划者ID")
    private Long searchPlannerEngineer ;

//    @TableField
//    @Column(name = "search_executor_engineer", type = MySqlTypeConstant.BIGINT, length = 32, comment = "执行规划者ID")
//    private Long searchExecutorEngineer; ;

    @TableField("gen_status")
    @Column(type = MySqlTypeConstant.INT, length = 1, comment = "生成状态(1已生成|0未生成)")
    private Integer genStatus = 0 ;

    @TableField
    @Column(name = "prompt_content", type = MySqlTypeConstant.TEXT, comment = "用于存储与数据分析相关的提示内容，可作为引导数据分析流程或操作的文本信息")
    private String promptContent;
}
