package com.alinesno.infra.smart.assistant.role.utils;

import com.alinesno.infra.smart.im.dto.ParserDataBean;
import com.alinesno.infra.smart.im.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析数据工具类
 * 用于生成格式化后的解析内容文本
 */
public class ParserDataUtils {

    /**
     * 获取文档类型对应的Font Awesome图标
     * @param type 文档类型
     * @param fileName 文件名(用于类型推断)
     * @return 对应的图标HTML代码
     */
    private static String getIconByType(String type, String fileName) {
        try {
            FileType fileType;

            // 安全处理类型判断
            if (type == null || type.trim().isEmpty()) {
                fileType = FileType.inferFromFileName(fileName);
            } else {
                try {
                    fileType = FileType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    fileType = FileType.FILE; // 类型不存在时回退到默认
                }
            }

            // 安全返回图标
            return switch (fileType) {
                case PDF -> "<i class='fa-solid fa-file-pdf'></i>";
                case MESSAGE -> "<i class='fa-solid fa-message'></i>";
                case DOCX -> "<i class='fa-solid fa-file-word'></i>";
                case LINK, URL -> "<i class='fa-solid fa-link'></i>";
                case IMAGE -> "<i class='fa-solid fa-image'></i>";
                case TXT -> "<i class='fa-solid fa-file-lines'></i>";
                case XLSX -> "<i class='fa-solid fa-file-excel'></i>";
                case PPT -> "<i class='fa-solid fa-file-powerpoint'></i>";
                case ZIP -> "<i class='fa-solid fa-file-zipper'></i>";
                case CSV -> "<i class='fa-solid fa-file-csv'></i>";
                case HTML -> "<i class='fa-solid fa-file-code'></i>";
                default -> "<i class='fa-solid fa-file'></i>";
            };
        } catch (Exception e) {
            // 捕获所有可能的异常，确保永不抛出
            return "<i class='fa-solid fa-file'></i>";
        }
    }

    /**
     * 生成HTML格式的解析内容文本
     * @param dataset 解析数据集合，包含ParserDataBean对象列表
     * @return HTML格式的字符串
     */
    public static String generateParsedItemsHTML(List<ParserDataBean> dataset) {
        if (dataset == null || dataset.isEmpty()) {
            return "<p>未找到解析内容</p>";
        }

        List<ParserDataBean> sortedList = dataset.stream()
                .sorted(Comparator.comparingInt(ParserDataBean::getSort))
                .toList();

        return sortedList.stream()
                .map(item -> {
                    String icon = getIconByType(item.getType(), item.getName());
                    FileType displayType = (item.getType() == null || item.getType().trim().isEmpty())
                            ? FileType.inferFromFileName(item.getName())
                            : FileType.valueOf(item.getType().toUpperCase());
                    return String.format("<p class='chat-step-reference'>%s 解析[%s](%s)</p>",
                            icon, item.getName(), displayType.getDesc());
                })
                .collect(Collectors.joining());
    }
}