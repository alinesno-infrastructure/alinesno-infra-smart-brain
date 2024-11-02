package com.alinesno.infra.smart.assistant.template.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static freemarker.template.Configuration.VERSION_2_3_31;

public class WordUtil {

    //配置信息,代码本身写的还是很可读的,就不过多注解了
    private static Configuration configuration = null;

    // 这里注意的是利用WordUtils的类加载器动态获得模板文件的位置

    static {
        String tempDir = System.getProperty("java.io.tmpdir");
        String templateFolder = tempDir + File.separator +  "templates" ;
        configuration = new Configuration(VERSION_2_3_31);
        configuration.setDefaultEncoding("utf-8");
        try {
            System.out.println(templateFolder);
            configuration.setDirectoryForTemplateLoading(new File(templateFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private WordUtil() {
        throw new AssertionError();
    }

    @SneakyThrows
    public static File createDoc(Map<?, ?> dataMap, String wordName, String name) {

        Template freemarkerTemplate = configuration.getTemplate(wordName);

        File f = new File(name);
        try {
            // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
            Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
            freemarkerTemplate.process(dataMap, w);
            w.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return f;
    }
}
