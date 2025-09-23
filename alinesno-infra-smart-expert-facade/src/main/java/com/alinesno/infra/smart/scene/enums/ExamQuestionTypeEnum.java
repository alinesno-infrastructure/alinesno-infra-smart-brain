package com.alinesno.infra.smart.scene.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
@AllArgsConstructor
public enum ExamQuestionTypeEnum {
    // 选择题型
    RADIO("radio", "单选", "单选题，从多个选项中选择一个正确答案", 20, 2, "fa-solid fa-dot-circle", "select"),
    CHECKBOX("checkbox", "多选", "多选题，从多个选项中选择一个或多个正确答案", 10, 3, "fa-solid fa-check-square", "select"),
    IMAGE_RADIO("image-radio", "图片单选", "图片单选题，基于图片内容从多个选项中选择一个正确答案", 5, 2, "fa-regular fa-image", "select"),
    IMAGE_CHECKBOX("image-checkbox", "图片多选", "图片多选题，基于图片内容从多个选项中选择一个或多个正确答案", 5, 3, "fa-regular fa-images", "select"),
    
    // 文本输入
    SINGLE_LINE("single-line", "单行文本", "单行文本输入题，要求用简短文字回答（限制单行输入）", 10, 3, "fa-solid fa-font", "text-input"),
    MULTI_FILL("multi-fill", "多项填空", "多项填空题，题目中包含多个需要填写的空白处", 10, 2, "fa-solid fa-list-alt", "text-input"),
    MULTI_LINE("multi-line", "多行文本", "多行文本输入题，要求用较长的文字回答（允许多行输入）", 8, 5, "fa-solid fa-align-left", "text-input"),
    
    // 高级题型
    IMAGE_FILE("image-file", "图片上传", "图片上传题，要求上传符合题目要求的图片文件", 3, 5, "fa-solid fa-file-image", "advanced"),
    DATETIME("datetime", "日期时间", "日期时间选择题，要求选择或输入特定的日期/时间", 5, 2, "fa-solid fa-calendar", "advanced"),
    LOCATION("location", "位置", "地理位置选择题，要求选择或标记特定地理位置", 3, 4, "fa-solid fa-map-marker-alt", "advanced"),
    DYNAMIC_TABLE("dynamic-table", "动态表单", "动态表单题，可动态添加/删除行列的表格型题目", 2, 10, "fa-solid fa-table-plus", "advanced"),

    // 备注说明
    DESCRIPTION("description", "内容分析", "内容分析题，根据提供的材料进行综合分析回答", 2, 15, "fa-solid fa-file-lines", "note");

    private final String type;          // 类型标识
    private final String typeName;      // 类型名称(简短)
    private final String typeDesc;      // 类型描述(详细)
    private final int defaultTotal;     // 默认题数
    private final int defaultScore;    // 默认分值
    private final String icon;         // 图标类名
    private final String category;     // 题型分类

    // 根据type查找枚举
    public static ExamQuestionTypeEnum getByType(String type) {
        return Arrays.stream(values())
                .filter(questionType -> questionType.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取分类结构的题型列表
     * @return 分类后的题型结构
     */
    public static List<Map<String, Object>> getCategoryList() {
        // 定义分类信息
        Map<String, Map<String, Object>> categories = new LinkedHashMap<>();
        categories.put("select", buildCategory("选择", "fa-solid fa-check-square"));
        categories.put("text-input", buildCategory("文本输入", "fa-solid fa-font"));
        categories.put("advanced", buildCategory("高级题型", "fa-solid fa-star"));
        categories.put("note", buildCategory("备注说明", "fa-solid fa-info-circle"));

        // 填充题型数据
        for (ExamQuestionTypeEnum type : values()) {
            Map<String, Object> typeMap = new LinkedHashMap<>();
            typeMap.put("code", type.getType());
            typeMap.put("label", type.getTypeName());
            typeMap.put("icon", type.getIcon());
            typeMap.put("defaultTotal", type.getDefaultTotal());
            typeMap.put("defaultScore", type.getDefaultScore());
            typeMap.put("desc", type.getTypeDesc());

            // 修正后的代码 - 添加类型转换
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> typesList =
                    (List<Map<String, Object>>) categories.get(type.getCategory()).get("types");
            typesList.add(typeMap);
        }

        return new ArrayList<>(categories.values());
    }

    private static Map<String, Object> buildCategory(String label, String icon) {
        Map<String, Object> category = new LinkedHashMap<>();
        category.put("code", "");
        category.put("label", label);
        category.put("icon", icon);
        category.put("types", new ArrayList<Map<String, Object>>());
        return category;
    }

    /**
     * 获取所有题型的平铺列表
     * @return 所有题型列表
     */
    public static List<Map<String, Object>> getAllTypesList() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ExamQuestionTypeEnum type : values()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type.getType());
            map.put("typeName", type.getTypeName());
            map.put("typeDesc", type.getTypeDesc());
            map.put("defaultTotal", type.getDefaultTotal());
            map.put("defaultScore", type.getDefaultScore());
            map.put("icon", type.getIcon());
            map.put("category", type.getCategory());
            result.add(map);
        }
        return result;
    }
}