package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用智能体场景 DTO
 * 继承自 SceneDto，用于描述通用智能体场景的相关信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GeneralAgentTaskDto extends SceneDto {

    /**
     * 场景ID
     * 用于唯一标识一个场景
     */
    private long sceneId;

    /**
     * 提示内容
     * 描述场景的提示或说明信息
     */
    private String promptContent;

    /**
     * 业务处理工程师
     * 负责业务处理的工程师名称
     */
    private String businessProcessorEngineer;
    /**
     * 业务处理工程师列表
     * 包含多个业务处理工程师的详细信息
     */
    private List<IndustryRoleDto> businessProcessorEngineers;

    /**
     * 数据查看工程师
     * 负责数据查看的工程师名称
     */
    private String dataViewerEngineer;
    /**
     * 数据查看工程师列表
     * 包含多个数据查看工程师的详细信息
     */
    private List<IndustryRoleDto> dataViewerEngineers;

    /**
     * 业务执行工程师
     * 负责业务执行的工程师名称
     */
    private String businessExecuteEngineer;
    /**
     * 业务执行工程师列表
     * 包含多个业务执行工程师的详细信息
     */
    private List<IndustryRoleDto> businessExecuteEngineers;

    /**
     * 章节树节点信息
     * 存储章节的树状结构信息，每个节点信息是一个TreeNodeDto对象
     */
    private List<TreeNodeDto> chapterTree = new ArrayList<>();

    /**
     * 内容生成状态
     */
    private String gentContentStatus ;

    /**
     * 计划生成状态
     */
    private String genPlanStatus;

    /**
     * 结果生成状态
     */
    private String genResultStatus ;

}
