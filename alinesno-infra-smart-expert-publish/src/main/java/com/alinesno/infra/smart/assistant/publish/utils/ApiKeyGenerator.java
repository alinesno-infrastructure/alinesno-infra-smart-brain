package com.alinesno.infra.smart.assistant.publish.utils;

import cn.hutool.core.util.IdUtil;

public class ApiKeyGenerator {

    /**
     * 生成 API Key 的方法
     * @return 生成的 API Key
     */
    public static String generateApiKey() {
        // 定义 API Key 的前缀
        String prefix = "aip-";
        return prefix + IdUtil.nanoId(28);
    }

}