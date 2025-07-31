package com.alinesno.infra.smart.assistant.scene.common.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Word文档转PDF转换工具类
 * 使用docx4j库实现，支持中文字体映射
 */
@Slf4j
public class WordToPdfConverter {

    /**
     * 将Word文档转换为PDF格式
     * @param inputWord  输入的Word文件对象
     * @param outputFile 输出的PDF文件对象
     * @throws Exception 转换过程中可能抛出的异常
     */
    @SneakyThrows
    public static void convertToPdf(File inputWord, File outputFile) {
        // 初始化Word处理包和输出流
        WordprocessingMLPackage wordMLPackage = null;
        OutputStream outputStream = null;

        try {
            // 1. 加载Word文档
            wordMLPackage = Docx4J.load(inputWord);

            // 2. 创建并配置字体映射器（解决中文乱码问题）
            Mapper fontMapper = createChineseFontMapper();
            wordMLPackage.setFontMapper(fontMapper);

            // 3. 创建输出流并执行PDF转换
            outputStream = new FileOutputStream(outputFile);
            Docx4J.toPDF(wordMLPackage, outputStream);

            // 4. 刷新输出流确保数据写入
            outputStream.flush();

        } finally {
            // 5. 确保资源被正确释放
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    System.err.println("关闭输出流时出错: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 创建中文字体映射器，当找不到指定字体时使用系统默认字体
     * @return 配置好的字体映射器对象
     */
    private static Mapper createChineseFontMapper() {
        Mapper fontMapper = new IdentityPlusMapper();
        Map<String, String> fontMappings = getFontMapper();

        // 尝试加载每种字体，失败则使用默认字体
        PhysicalFont defaultFont = PhysicalFonts.get("SimSun"); // 默认使用宋体

        for (Map.Entry<String, String> entry : fontMappings.entrySet()) {
            try {
                PhysicalFont font = PhysicalFonts.get(entry.getValue());
                if (font != null) {
                    fontMapper.put(entry.getKey(), font);
                    log.debug("成功加载字体: {} -> {}", entry.getKey(), entry.getValue());
                } else {
                    fontMapper.put(entry.getKey(), defaultFont);
                    log.warn("字体 {} ({}) 未找到，使用默认字体", entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                fontMapper.put(entry.getKey(), defaultFont);
                log.warn("加载字体 {} ({}) 失败: {}, 使用默认字体",
                        entry.getKey(), entry.getValue(), e.getMessage());
            }
        }

        return fontMapper;
    }

    @NotNull
    private static Map<String, String> getFontMapper() {
        Map<String, String> fontMappings = new HashMap<>();

        // 中文字体映射表
        fontMappings.put("隶书", "LiSu");
        fontMappings.put("宋体", "SimSun");
        fontMappings.put("微软雅黑", "Microsoft Yahei");
        fontMappings.put("黑体", "SimHei");
        fontMappings.put("楷体", "KaiTi");
        fontMappings.put("新宋体", "NSimSun");
        fontMappings.put("华文行楷", "STXingkai");
        fontMappings.put("华文仿宋", "STFangsong");
        fontMappings.put("仿宋", "FangSong");
        fontMappings.put("幼圆", "YouYuan");
        fontMappings.put("华文宋体", "STSong");
        fontMappings.put("华文中宋", "STZhongsong");
        fontMappings.put("等线", "SimSun");
        fontMappings.put("等线 Light", "SimSun");
        fontMappings.put("华文琥珀", "STHupo");
        fontMappings.put("华文隶书", "STLiti");
        fontMappings.put("华文新魏", "STXinwei");
        fontMappings.put("华文彩云", "STCaiyun");
        fontMappings.put("方正姚体", "FZYaoti");
        fontMappings.put("方正舒体", "FZShuTi");
        fontMappings.put("华文细黑", "STXihei");
        fontMappings.put("宋体扩展", "simsun-extB");
        fontMappings.put("仿宋_GB2312", "FangSong_GB2312");
        fontMappings.put("新細明體", "SimSun");
        fontMappings.put("PMingLiU", "SimSun");
        fontMappings.put("SimSun", "SimSun");
        return fontMappings;
    }

}