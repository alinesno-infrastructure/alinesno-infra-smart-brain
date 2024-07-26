package com.alinesno.infra.smart.assistant.controller;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;

@Component
public class DictTypeService {

    public Object selectDictTypeById(String dictId) {

        // 0排队中/1运行中/2已完成/9失败

        String str = "[\n" +
                "        {\n" +
                "            \"dictCode\": 6,\n" +
                "            \"dictSort\": 1,\n" +
                "            \"dictLabel\": \"已失败\",\n" +
                "            \"dictValue\": \"9\",\n" +
                "            \"dictType\": \"sys_normal_disable\",\n" +
                "        },\n" +
                "        {\n" +
                "            \"dictCode\": 6,\n" +
                "            \"dictSort\": 1,\n" +
                "            \"dictLabel\": \"已完成\",\n" +
                "            \"dictValue\": \"2\",\n" +
                "            \"dictType\": \"sys_normal_disable\",\n" +
                "        },\n" +
                "        {\n" +
                "            \"dictCode\": 6,\n" +
                "            \"dictSort\": 1,\n" +
                "            \"dictLabel\": \"排队中\",\n" +
                "            \"dictValue\": \"1\",\n" +
                "            \"dictType\": \"sys_normal_disable\",\n" +
                "        },\n" +
                "        {\n" +
                "            \"dictCode\": 7,\n" +
                "            \"dictSort\": 2,\n" +
                "            \"dictLabel\": \"运行中\",\n" +
                "            \"dictValue\": \"0\",\n" +
                "            \"dictType\": \"sys_normal_disable\",\n" +
                "        }\n" +
                "    ]" ;

        return JSONArray.parseArray(str) ;
    }

    public void deleteDictTypeByIds(Long[] dictIds) {
    }

    public void resetDictCache() {
    }
}
