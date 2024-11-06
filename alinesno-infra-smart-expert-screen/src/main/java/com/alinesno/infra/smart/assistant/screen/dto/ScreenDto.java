package com.alinesno.infra.smart.assistant.screen.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景实体类,针对于具体的业务场景配置管理
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScreenDto extends BaseDto {

    /**
     * 场景名称，用于标识特定的场景或页面
     */
    private String screenName;

    /**
     * 场景横幅，通常是显示在场景顶部的图像或文字
     */
    private String screenBanner;

    /**
     * 场景描述，对场景功能或内容的简要说明
     */
    private String screenDesc;

    /**
     * 场景数量，表示与此场景相关的项目或元素的数量
     */
    private Integer screenCount;

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
}

