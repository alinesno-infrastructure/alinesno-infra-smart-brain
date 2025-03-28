package com.alinesno.infra.smart.scene.enums;

import lombok.Getter;

/**
 * ScreenTypeEnums 枚举类
 */
@Deprecated
@Getter
public enum SceneTypeEnums {

    LARGE_TEXT("large_text","大文本"),
    EXAM("exam","培训考试"),
    VIDEO_CLIP("video_clip","视频剪辑"),
    LEADER_MODEL("leader_model","管理者");

    private final String key;
    private final String name;

    private SceneTypeEnums(String key, String name) {
        this.key = key;
        this.name = name;
    }

    /**
     * 通过key获取到类型
     */
    public static SceneTypeEnums getByKey(String key) {
        for (SceneTypeEnums type : values()) {
            if (type.getKey().equals(key)) {
                return type;
            }
        }
        return null;
    }

}
