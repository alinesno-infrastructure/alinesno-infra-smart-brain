package com.alinesno.infra.smart.brain.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class DictTypeService {

    public Object selectDictTypeById(String dictId) {
        String str = "[\n" +
                "        {\n" +
                "            \"createBy\": \"admin\",\n" +
                "            \"createTime\": \"2023-04-23 16:13:26\",\n" +
                "            \"updateBy\": null,\n" +
                "            \"updateTime\": null,\n" +
                "            \"remark\": \"正常状态\",\n" +
                "            \"dictCode\": 6,\n" +
                "            \"dictSort\": 1,\n" +
                "            \"dictLabel\": \"正常\",\n" +
                "            \"dictValue\": \"0\",\n" +
                "            \"dictType\": \"sys_normal_disable\",\n" +
                "            \"cssClass\": \"\",\n" +
                "            \"listClass\": \"primary\",\n" +
                "            \"isDefault\": \"Y\",\n" +
                "            \"status\": \"0\",\n" +
                "            \"default\": true\n" +
                "        },\n" +
                "        {\n" +
                "            \"createBy\": \"admin\",\n" +
                "            \"createTime\": \"2023-04-23 16:13:26\",\n" +
                "            \"updateBy\": null,\n" +
                "            \"updateTime\": null,\n" +
                "            \"remark\": \"停用状态\",\n" +
                "            \"dictCode\": 7,\n" +
                "            \"dictSort\": 2,\n" +
                "            \"dictLabel\": \"停用\",\n" +
                "            \"dictValue\": \"1\",\n" +
                "            \"dictType\": \"sys_normal_disable\",\n" +
                "            \"cssClass\": \"\",\n" +
                "            \"listClass\": \"danger\",\n" +
                "            \"isDefault\": \"N\",\n" +
                "            \"status\": \"0\",\n" +
                "            \"default\": false\n" +
                "        }\n" +
                "    ]" ;

        return JSONArray.parseArray(str) ;
    }

    public void deleteDictTypeByIds(Long[] dictIds) {
    }

    public void resetDictCache() {
    }
}
