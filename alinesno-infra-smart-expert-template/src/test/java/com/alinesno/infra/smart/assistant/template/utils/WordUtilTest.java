package com.alinesno.infra.smart.assistant.template.utils;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具类单元测试
 */
public class WordUtilTest {

    @Test
    public void testCreateDoc() throws Exception {

    	String templatePath = "工作日报_模板.xml";
    	String outputPath = "/Users/luodong/Desktop/aa/工作日报_模板"+ IdUtil.getSnowflakeNextIdStr() +".docx";

        Map<String, Object> dataMap = new HashMap<>() ;
        dataMap.put("templateKey", "张三") ;

        Map<String, Object> dataJson = new HashMap<>();
        dataJson.put("key1", "hello world") ;
        dataJson.put("key2", "hello world") ;

        dataMap.put("dataJson", dataJson);

//   	    WordUtil.createDoc(dataMap, templatePath, outputPath);
    }

}
