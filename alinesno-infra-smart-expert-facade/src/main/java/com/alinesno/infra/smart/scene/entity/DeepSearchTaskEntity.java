package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 深度搜索任务实体类
 * @author luoxiaodong
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("deep_search_task")
public class DeepSearchTaskEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private long sceneId;

    @TableField
    @Column(name = "deepsearch_scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "深度检索场景ID")
    private long deepsearchSceneId;

    @TableField("gen_status")
    @Column(type = MySqlTypeConstant.INT, length = 1, comment = "生成状态(1已生成|0未生成|2生成中|3生成失败)")
    private Integer genStatus = 0 ;

    @TableField
    @Column(name = "search_planner_engineer", type = MySqlTypeConstant.BIGINT, length = 32, comment = "搜索规划者ID")
    private Long searchPlannerEngineer ;

    @TableField
    @Column(name = "prompt_content", type = MySqlTypeConstant.TEXT, comment = "用于存储与数据分析相关的提示内容，可作为引导数据分析流程或操作的文本信息")
    private String promptContent;

    @TableField
    @Column(name = "task_name", type = MySqlTypeConstant.VARCHAR, length = 255, comment = "任务名称")
    private String taskName ;

}
