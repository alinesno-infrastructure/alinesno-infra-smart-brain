package com.alinesno.infra.smart.assistant.role.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板解析的工具类对象
 */
@Slf4j
public class TemplateParser {

    // 创建 Configuration 对象
    private static final Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
    private static final String UTF_8 = "UTF-8" ;

    static {
        configuration.setDefaultEncoding(UTF_8);
    }

    /**
     * 解析模板
     * @param templates
     * @param paramMap
     * @return
     */
    public static String parserTemplate(String templates , Map<?, ?> paramMap){
        try {
            // 创建模板对象并设置模板内容
            Template template = new Template("template", templates , configuration);

            Map<String, Object> params = new HashMap<>();
            params.put("params" , paramMap) ;

            // 执行模板填充
            StringWriter stringWriter = new StringWriter();
            template.process(params, stringWriter);

            return stringWriter.toString() ;

        } catch (IOException | TemplateException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage()) ;
        }

    }

}
