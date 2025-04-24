package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 描述：通用智能体场景实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("general_agent_scene")
public class GeneralAgentSceneEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private Long sceneId;

    @TableField
    @Column(name = "prompt_content", type = MySqlTypeConstant.TEXT, comment = "用于存储与数据分析相关的提示内容，可作为引导数据分析流程或操作的文本信息")
    private String promptContent;

    @TableField
    @Column(name = "business_processor_engineer" , type = MySqlTypeConstant.BIGINT, length = 32, comment = "业务处理助手负责处理通用业务流程中的各类任务")
    private String businessProcessorEngineer;

    @TableField
    @Column(name = "business_execute_engineer"  , type = MySqlTypeConstant.BIGINT, length = 32, comment = "针对用于处理业务并执行业务指定的内容场景，包括分析业务内容。")
    private String businessExecuteEngineer;

    @TableField
    @Column(name = "data_viewer_engineer"  , type = MySqlTypeConstant.BIGINT, length = 32, comment = "业务展示助手专注于将业务处理助手处理后的数据和结果")
    private String dataViewerEngineer;

    @TableField
    @Column(name = "gen_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成状态(1生成菜单|0未生成)")
    private Integer genStatus = 0;

}
