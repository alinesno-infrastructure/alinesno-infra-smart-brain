package com.alinesno.infra.smart.assistant.initialize.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.smart.brain.entity.PromptCatalogEntity;
import com.alinesno.infra.smart.brain.entity.PromptPostsEntity;
import lombok.SneakyThrows;

import java.util.List;

public class PromptPostCreator {

    @SneakyThrows
    public static List<PromptPostsEntity> createPromptPostEntity(){
        final String jsonArr = ResourceUtil.readUtf8Str("prompt-post.json");
        return JSONArray.parseArray(jsonArr , PromptPostsEntity.class);

    }

    public static List<PromptCatalogEntity> createPromptCatalog() {
        final String jsonArr = ResourceUtil.readUtf8Str("prompt-type.json");
        return JSONArray.parseArray(jsonArr , PromptCatalogEntity.class);
    }
}
