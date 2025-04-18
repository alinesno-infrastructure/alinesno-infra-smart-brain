package com.alinesno.infra.smart.assistant.template.utils;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TemplateProcessor {


    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    public String processTemplateFromUrl(String templateUrl, JSONObject jsonObject) {
        try {
            // 下载模板到临时目录
            Path tempTemplatePath = downloadTemplateToTemp(templateUrl);

            // 初始化 FreeMarker 配置
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);

            cfg.setDirectoryForTemplateLoading(tempTemplatePath.getParent().toFile());
            cfg.setDefaultEncoding("UTF-8");

            // 使用 fastjson 解析 JSON 参数
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("document_letter", jsonObject.getJSONObject("document_letter"));
            dataModel.put("plan_document", jsonObject.getJSONObject("plan_document"));
            dataModel.put("attachments", jsonObject.getJSONObject("attachments"));

            // 获取模板
            Template template = cfg.getTemplate(tempTemplatePath.getFileName().toString());

            // 处理模板
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            String processedText = writer.toString();

            // 保存处理后的文本到临时文件
            File tempFile = new File(localPath , IdUtil.getSnowflakeNextIdStr() + ".docx") ;
            Path tempDocxPath = Path.of(tempFile.getAbsolutePath()) ; // Files.createTempFile("output_", ".docx");
            try (BufferedWriter out = Files.newBufferedWriter(tempDocxPath)) {
                out.write(processedText);
            }
            System.out.println("模板处理完成，输出文件为: " + tempDocxPath.toAbsolutePath());

            // 删除临时模板文件
            Files.delete(tempTemplatePath);

            return tempDocxPath.toAbsolutePath().toFile().getAbsolutePath() ;
        } catch (IOException | TemplateException e) {
            log.error("模板处理失败", e);
            throw new RuntimeException(e);
        }
    }

    private static Path downloadTemplateToTemp(String templateUrl) throws IOException {
        URL url = new URL(templateUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Path tempPath = Files.createTempFile("template", ".xml");
            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, tempPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return tempPath;
        } else {
            throw new IOException("Failed to download template. Response code: " + responseCode);
        }
    }


}