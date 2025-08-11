package com.alinesno.infra.smart.assistant.scene.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * MarkdownToWord 类
 */
@Slf4j
public class MarkdownToWord {

    /**
     * 将 Markdown 内容转换为 Word 文档
     * @param mdContent
     * @param baseName
     * @return
     */
    public static String convertMdToDocx(String mdContent, String baseName) {
        try {
            // 创建临时MD文件
            File tempMdFile = File.createTempFile(baseName, ".md");
            try (FileWriter writer = new FileWriter(tempMdFile)) {
                writer.write(mdContent);
            }

            // 定义输出的 DOCX 文件路径
            File outputDocxFile = new File(tempMdFile.getParent(), baseName + ".docx");

            // 构建 Pandoc 命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "pandoc",
                    tempMdFile.getAbsolutePath(),
                    "-o",
                    outputDocxFile.getAbsolutePath() ,
                    "--from" ,
                    "markdown-yaml_metadata_block");

            // 设置工作目录
            processBuilder.directory(tempMdFile.getParentFile());

            // 启动进程
            Process process = processBuilder.start();

            // 等待命令完成
            int exitCode = process.waitFor();

            // 检查命令是否成功执行
            if (exitCode == 0) {
                // 获取生成的 DOCX 文件的绝对路径
                return outputDocxFile.getAbsolutePath();
            } else {
                // 如果命令失败，读取错误流并打印错误信息
                String error = new java.util.Scanner(process.getErrorStream()).useDelimiter("\\A").next();
                log.debug("Failed to execute command: pandoc " + tempMdFile.getAbsolutePath() + " -o " + outputDocxFile.getAbsolutePath());
                log.debug("Error: " + error);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error converting Markdown to Word" , e);
            return null;
        }
    }

}