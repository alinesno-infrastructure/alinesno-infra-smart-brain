package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.enums.ScreenAgent;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.aspectj.weaver.loadtime.Agent;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景实体类,针对于具体的业务场景配置管理
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneDto extends BaseDto {

    /**
     * 场景名称，用于标识特定的场景或页面
     */
    @NotBlank(message = "场景名称不能为空")
    private String sceneName;

    /**
     * 场景横幅，通常是显示在场景顶部的图像或文字
     */
    private String sceneBanner;

    /**
     * 场景描述，对场景功能或内容的简要说明
     */
    @NotBlank(message = "场景描述不能为空")
    private String sceneDesc;

    /**
     * 场景数量，表示与此场景相关的项目或元素的数量
     */
    private Integer sceneCount;

    /**
     * 文档类型，定义与场景相关的文档的类型
     */
    private String docType;

    /**
     * 知识ID，唯一标识与场景相关联的知识库条目
     */
    private String knowledgeId;

    /**
     * 排序顺序，决定场景在列表或菜单中的显示顺序
     */
    private Integer sortOrder;

    /**
     * 是否推荐，指示场景是否被推荐给用户
     */
    private Boolean isRecommend;

    /**
     * 场景类型，用于定义场景的功能或类型
     */
    private String sceneType;

    /**
     * 智能助手，包含多个智能助手，用于处理场景的智能操作
     */
   private List<ScreenAgent>  agents = new ArrayList<>();

//    /**
//     * 管理人员
//     */
//    private IndustryRoleDto leader = new IndustryRoleDto();
//
//    /**
//     * 工作人员
//     */
//    private List<IndustryRoleDto> workers = new ArrayList<>();

}

