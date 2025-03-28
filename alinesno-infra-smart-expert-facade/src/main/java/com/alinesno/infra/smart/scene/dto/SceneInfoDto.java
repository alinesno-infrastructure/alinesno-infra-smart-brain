package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.smart.scene.enums.ScreenAgent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 抽取的新类
 * 用于展示场景信息的数据结构
 * 包含了场景的基本信息及其关联的屏幕代理类型列表
 */
@AllArgsConstructor
@Data
public class SceneInfoDto {
    // 场景的唯一标识符
    private final int id;
    // 场景的名称，用于显示在界面上
    private final String sceneName;
    // 场景的代码标识，用于内部引用和识别
    private final String code;
    // 场景的描述，提供场景的详细说明
    private final String sceneDescription;
    // 场景的集成进度，表示场景集成的完成程度
    private final String integrationProgress;
    // 场景是否成熟，标记场景是否已经过充分测试和验证
    private final String isMature;
    // 场景的横幅链接，用于展示场景相关的横幅图片或广告
    private final String bannerLink;
    // 场景的图标，用于界面中标识场景的图像
    private final String icon;
    // 场景关联的屏幕代理列表，表示该场景下支持的屏幕代理类型
    private final List<ScreenAgent> agents;
}
