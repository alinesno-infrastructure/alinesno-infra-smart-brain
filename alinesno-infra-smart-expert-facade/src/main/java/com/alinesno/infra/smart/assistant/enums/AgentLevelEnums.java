package com.alinesno.infra.smart.assistant.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能体等级，根据人工智能评估出来的等级来定场景或者智能体的等级，等级越高越聪明
 * SceneLevelEnums
 */
@Getter
public enum AgentLevelEnums {

    L1("L1", "简单步骤跟随", "代理按照用户或开发人员预定义的确切步骤完成任务。"),
    L2("L2", "确定任务自动化", "根据用户对确定性任务的描述，代理自动完成预定义操作空间中的步骤。"),
    L3("L3", "战略任务自动化", "根据用户指定的任务，代理使用各种资源和工具自主规划执行步骤，并根据中间反馈迭代计划直至完成。"),
    L4("L4", "记忆和情境感知", "代理感知用户上下文，了解用户记忆，并有时主动提供个性化服务。"),
    L5("L5", "数字角色", "智能体代表用户完成事务，代表用户与他人进行交互，保证安全可靠。");

    private final String code;
    private final String name;
    private final String desc;

    AgentLevelEnums(String code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    /**
     * 获取到所有的列表List<Map<Key,Value>>
     */
    public static List<Map<String, Object>> getAllList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AgentLevelEnums value : values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", value.getCode());
            map.put("name", value.getName());
            map.put("desc", value.getDesc());
            list.add(map);
        }
        return list;
    }

}
