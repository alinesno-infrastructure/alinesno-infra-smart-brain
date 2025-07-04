package com.alinesno.infra.smart.assistant.role.utils;

import com.alinesno.infra.smart.assistant.role.context.ParserDataBean;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析数据工具类
 * 用于生成格式化后的解析内容文本
 */
public class ParserDataUtils {

    /**
     * 根据文件名推断文件类型
     * @param fileName 文件名
     * @return 推断的文件类型
     */
    private static String inferFileType(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "file"; // 默认文件类型
        }

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> "pdf";
            case "docx", "doc" -> "docx";
            case "xlsx", "xls" -> "xlsx";
            case "ppt", "pptx" -> "ppt";
            case "png", "jpg", "jpeg", "gif", "bmp", "svg", "webp", "tiff" -> "image";
            case "txt", "text", "log" -> "txt";
            case "zip", "rar", "7z", "tar", "gz" -> "zip";
            case "csv" -> "csv";
            case "html", "htm" -> "html";
            default -> "file";
        };
    }

    /**
     * 获取文档类型对应的Font Awesome图标
     * @param type 文档类型
     * @param fileName 文件名(用于类型推断)
     * @return 对应的图标HTML代码
     */
    private static String getIconByType(String type, String fileName) {
        // 如果type为空，则根据文件名推断类型
        String actualType = (type == null || type.trim().isEmpty())
                ? inferFileType(fileName)
                : type.toLowerCase();

        return switch (actualType) {
            case "pdf" -> "<i class='fa-solid fa-file-pdf'></i>";
            case "docx" -> "<i class='fa-solid fa-file-word'></i>";
            case "link", "url" -> "<i class='fa-solid fa-link'></i>";
            case "image" -> "<i class='fa-solid fa-image'></i>";
            case "txt" -> "<i class='fa-solid fa-file-lines'></i>";
            case "xlsx" -> "<i class='fa-solid fa-file-excel'></i>";
            case "ppt" -> "<i class='fa-solid fa-file-powerpoint'></i>";
            case "zip" -> "<i class='fa-solid fa-file-zipper'></i>";
            case "csv" -> "<i class='fa-solid fa-file-csv'></i>";
            case "html" -> "<i class='fa-solid fa-file-code'></i>";
            default -> "<i class='fa-solid fa-file'></i>";
        };
    }

    /**
     * 生成HTML格式的解析内容文本
     * @param dataset 解析数据集合，包含ParserDataBean对象列表
     * @return HTML格式的字符串
     */
    public static String generateParsedItemsHTML(List<ParserDataBean> dataset) {
        // 检查数据集是否为空
        if (dataset == null || dataset.isEmpty()) {
            return "<p>未找到解析内容</p>";
        }

        // 按照sort字段进行排序
        List<ParserDataBean> sortedList = dataset.stream()
                .sorted((a, b) -> Integer.compare(a.getSort(), b.getSort()))
                .toList();

        // 生成带图标的HTML格式输出文本
        return sortedList.stream()
                .map(item -> {
                    String icon = getIconByType(item.getType(), item.getName());
                    String displayType = (item.getType() == null || item.getType().trim().isEmpty())
                            ? inferFileType(item.getName())
                            : item.getType();
                    return String.format("<p style='border-radius: 5px;margin-left:-10px;padding: 2px 7px;background: #f5f5f5;margin-bottom: 5px;'>%s 解析[%s](%s)</p>",
                            icon, item.getName(), displayType);
                })
                .collect(Collectors.joining());
    }
}