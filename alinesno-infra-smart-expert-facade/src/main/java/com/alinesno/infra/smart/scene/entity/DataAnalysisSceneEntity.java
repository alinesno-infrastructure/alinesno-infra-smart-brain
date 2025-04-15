package com.alinesno.infra.smart.scene.entity;

import com.alinesno.infra.common.facade.mapper.entity.InfraBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据分析场景实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("data_analysis_scene")
public class DataAnalysisSceneEntity extends InfraBaseEntity {

    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private long sceneId;

    @TableField
    @Column(name = "analysis_prompt_content", type = MySqlTypeConstant.TEXT, comment = "用于存储与数据分析相关的提示内容，可作为引导数据分析流程或操作的文本信息")
    private String analysisPromptContent;

    @TableField
    @Column(name = "text_parser_engineer", type = MySqlTypeConstant.BIGINT, length = 32, comment = "记录负责文本解析（textParser）相关工作的工程师姓名或标识，方便追溯责任人")
    private String textParserEngineer;

    @TableField
    @Column(name = "data_miner_engineer", type = MySqlTypeConstant.BIGINT, length = 32, comment = "记录负责数据挖掘（dataMiner）相关工作的工程师姓名或标识，用于明确该环节的责任人")
    private String dataMinerEngineer;

    @TableField
    @Column(name = "gen_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成状态(1生成菜单|0未生成)")
    private Integer genStatus = 0;
}