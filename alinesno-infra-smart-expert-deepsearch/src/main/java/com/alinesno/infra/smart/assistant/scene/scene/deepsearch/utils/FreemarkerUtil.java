package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class FreemarkerUtil {
    private static final Configuration cfg;

    static {
        cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");
    }

    /**
     * 使用 FreeMarker 处理模板字符串并返回格式化后的内容
     *
     * @param templateString 模板字符串
     * @param dataModel      数据模型，包含要替换占位符的数据
     * @return 格式化后的内容
     * @throws IOException       如果读取模板或写入输出时发生 I/O 错误
     * @throws TemplateException 如果处理模板时发生错误
     */
    public static String processTemplate(String templateString, Map<String, Object> dataModel) throws IOException, TemplateException {
        // 创建模板对象
        Template template = new Template("dynamicTemplate", templateString, cfg);

        // 合并模板和数据
        StringWriter out = new StringWriter();
        template.process(dataModel, out);

        // 返回格式化后的文本
        return out.toString();
    }
}