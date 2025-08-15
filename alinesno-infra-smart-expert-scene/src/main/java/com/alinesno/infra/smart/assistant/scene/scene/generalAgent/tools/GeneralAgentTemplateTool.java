package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto.GeneralTemplateDto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 通用智能助手模板工具类
 */
public class GeneralAgentTemplateTool {

    private GeneralAgentTemplateTool() {
        // 私有构造器防止实例化
    }

    /**
     * 从配置数据中获取模板列表
     * @param configData JSON格式的配置数据
     * @return 模板列表，如果解析失败或数据为空返回空列表
     */
    public static List<GeneralTemplateDto> getTemplates(String configData) {
        if (configData == null || configData.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            JSONObject jsonObject = JSONObject.parseObject(configData);
            JSONArray templateArray = jsonObject.getJSONArray("selectedTemplateIds");

            if (templateArray == null || templateArray.isEmpty()) {
                return Collections.emptyList();
            }

            return templateArray.toJavaList(GeneralTemplateDto.class);
        } catch (JSONException e) {
            // 根据需求决定是记录日志还是抛出异常
            return Collections.emptyList();
        }
    }
}