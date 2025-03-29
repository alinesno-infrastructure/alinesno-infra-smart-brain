package com.alinesno.infra.smart.im.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AgentTypeDto类用于表示代理类型的信息
 * 它是一个数据传输对象(DTO)，通常用于在不同的系统组件之间传递数据
 * 该类实现了Serializable接口，允许它的实例被序列化，便于网络传输或存储
 */
@Data
public class AgentTypeDto implements Serializable {

    // 代理类型的唯一标识符
    private final Long id;
    // 代理类型的代码标识，用于系统内部引用
    private final String code;
    // 代理类型的名称，用于用户界面显示
    private final String name;
    // 代理类型的图标路径或名称，用于用户界面的视觉识别
    private final String icon;

    /**
     * 构造一个新的AgentTypeDto实例
     *
     * @param id    代理类型的唯一标识符
     * @param code  代理类型的代码标识
     * @param name  代理类型的名称
     * @param icon  代理类型的图标路径或名称
     */
    public AgentTypeDto(Long id, String code, String name, String icon) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.icon = icon;
    }
}
