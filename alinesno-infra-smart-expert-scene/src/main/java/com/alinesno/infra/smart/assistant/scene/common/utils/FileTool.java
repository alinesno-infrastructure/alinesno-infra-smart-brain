package com.alinesno.infra.smart.assistant.scene.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileTool {

    /**
     * 创建临时HTML文件并写入内容
     */
    public static File createTempHtmlFile(String content) throws IOException {
        // 创建临时文件，后缀为.html
        Path tempPath = Files.createTempFile("export-", ".html");
        File tempFile = tempPath.toFile();

        // 设置临时文件在JVM退出时自动删除
        tempFile.deleteOnExit();

        // 将内容写入临时文件
        Files.writeString(tempPath, content,
                StandardOpenOption.TRUNCATE_EXISTING);

        return tempFile;
    }
}
