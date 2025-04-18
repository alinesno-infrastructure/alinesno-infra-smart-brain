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
@TableName("content_formatter_scene")
public class ContentFormatterSceneEntity extends InfraBaseEntity {

    /**
     * 场景ID
     */
    @TableField
    @Column(name = "scene_id", type = MySqlTypeConstant.BIGINT, length = 32, comment = "场景ID")
    private long sceneId;

    /**
     * 用于存储与数据分析相关的提示内容，可作为引导数据分析流程或操作的文本信息
     */
    @TableField
    @Column(name = "content_prompt_content", type = MySqlTypeConstant.TEXT, comment = "用于存储与数据分析相关的提示内容，可作为引导数据分析流程或操作的文本信息")
    private String contentPromptContent;

    /**
     * 记录负责模板提取的工程师姓名或标识，方便追溯责任人
     */
    @TableField
    @Column(name = "template_extractor_engineer", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "记录负责模板提取的工程师姓名或标识，方便追溯责任人")
    private String templateExtractorEngineer;

    /**
     * 记录负责内容审核的工程师姓名或标识，方便追溯责任人
     */
    @TableField
    @Column(name = "content_reviewer_engineer", type = MySqlTypeConstant.VARCHAR, length = 128, comment = "记录负责内容审核的工程师姓名或标识，方便追溯责任人")
    private String contentReviewerEngineer;

    /**
     * 生成状态(1生成菜单|0未生成)，默认未生成
     */
    @TableField
    @Column(name = "gen_status", type = MySqlTypeConstant.VARCHAR, length = 32, comment = "生成状态(1生成菜单|0未生成)")
    private Integer genStatus = 0;
}