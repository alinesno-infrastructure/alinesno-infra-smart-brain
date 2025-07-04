package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.smart.assistant.enums.ModelTypeEnums;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 场景代理类，用于描述屏幕代理的信息。
 */
@Data
@NoArgsConstructor
public class SceneAgent {

    // 唯一标识符，改为 long 类型且不少于 8 位
    private long id;
    private String name;
    private String code;
    private String description;

    private boolean needDataModel = false; // 是否需要数据处理模型
    private boolean needImgModel;  // 是否需要图片处理模型

    public SceneAgent(long id, String name, String code, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public SceneAgent(long id, String name, String code, String description, boolean needDataModel) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.needDataModel = needDataModel;
    }

    public SceneAgent(long id, String name, String code, String description, boolean needDataModel , boolean needImgModel) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.needDataModel = needDataModel;
        this.needImgModel = needImgModel ;
    }
}