package com.alinesno.infra.smart.im.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏的实体类对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CollectItemObjectDto extends CollectItemDto {

    private Long id ; // ID
    private String title;  // 名称
    private String avatar ; // 头像
    private String description ; // 描述
    private String stats ; // 状态

    private String editAvatar ; // 编辑者头像
    private String editInfo ; // 编辑者信息
    private String editTime ; // 编辑时间

    private String sceneIcon ; // 场景font图标
    private String sceneType ; // 场景类型
    private String sceneTypeName ; // 场景名称
    private String orgName ; // 组织名称

}
