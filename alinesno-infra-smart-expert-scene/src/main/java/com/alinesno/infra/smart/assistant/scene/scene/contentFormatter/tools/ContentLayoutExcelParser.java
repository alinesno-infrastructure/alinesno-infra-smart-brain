package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 界面排版配置Excel解析器
 * 用于解析排版配置的Excel文件并转换为实体对象
 */
public class ContentLayoutExcelParser {

    // Excel表头列名常量
    private static final String COLUMN_INDEX = "序号";
    private static final String GROUP_ICON = "分组图片";
    private static final String GROUP_NAME = "所属分组";
    private static final String GROUP_DESC = "分组描述";
    private static final String TEMPLATE_ICON = "模板图片";
    private static final String TEMPLATE_NAME = "模板名称";
    private static final String TEMPLATE_DESC = "模板描述";
    private static final String TEMPLATE_FORMAT = "模板格式";

    /**
     * 验证必要列是否存在
     */
    private static void validateRequiredColumns(Map<String, Integer> columnIndexMap) {
        List<String> requiredColumns = Arrays.asList(
                GROUP_NAME, TEMPLATE_NAME, TEMPLATE_FORMAT
        );

        for (String column : requiredColumns) {
            if (!columnIndexMap.containsKey(column)) {
                throw new IllegalArgumentException("Excel缺少必要列: " + column);
            }
        }
    }

    /**
     * 检查排版配置是否有效
     */
    private static boolean isValidLayout(LayoutBean layout) {
        return layout.getGroupName() != null && !layout.getGroupName().isEmpty() &&
                layout.getLayoutName() != null && !layout.getLayoutName().isEmpty() &&
                layout.getLayoutConfig() != null && !layout.getLayoutConfig().isEmpty();
    }

    /**
     * 构建列名与索引的映射关系
     */
    private static Map<String, Integer> buildColumnIndexMap(Row headerRow) {
        Map<String, Integer> columnIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            if (cell.getCellType() == CellType.STRING) {
                String columnName = cell.getStringCellValue().trim();
                columnIndexMap.put(columnName, cell.getColumnIndex());
            }
        }
        return columnIndexMap;
    }

    /**
     * 将单行数据解析为排版配置对象
     */
    private static LayoutBean parseRowToLayout(Row row, Map<String, Integer> columnIndexMap) {
        LayoutBean layout = new LayoutBean();

        // 分组信息
        layout.setGroupName(getCellValueByColumnName(row, columnIndexMap, GROUP_NAME));
        layout.setGroupDesc(getCellValueByColumnName(row, columnIndexMap, GROUP_DESC));
        layout.setGroupIcon(getCellValueByColumnName(row, columnIndexMap, GROUP_ICON));

        // 模板信息
        layout.setLayoutName(getCellValueByColumnName(row, columnIndexMap, TEMPLATE_NAME));
        layout.setLayoutDesc(getCellValueByColumnName(row, columnIndexMap, TEMPLATE_DESC));
        layout.setLayoutConfig(getCellValueByColumnName(row, columnIndexMap, TEMPLATE_FORMAT));
        layout.setTemplateIcon(getCellValueByColumnName(row, columnIndexMap, TEMPLATE_ICON));
        layout.setSort(getCellValueByColumnName(row, columnIndexMap, COLUMN_INDEX));

        return layout;
    }

    private static String getCellValueByColumnName(Row row, Map<String, Integer> columnIndexMap, String columnName) {
        Integer columnIndex = columnIndexMap.get(columnName);
        if (columnIndex == null) {
            return "";
        }
        return getCellValue(row, columnIndex);
    }

    private static String getCellValue(Row row, int cellIndex) {
        if (row == null) {
            return "";
        }
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return getCellValueAsString(cell);
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING -> {
                return cell.getStringCellValue().trim();
            }
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == (int) numValue) {
                        return String.valueOf((int) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            }
            case BOOLEAN -> {
                return String.valueOf(cell.getBooleanCellValue());
            }
            case FORMULA -> {
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> {
                return "";
            }
        }
    }

    /**
     * 按分组名称分组排版配置
     */
    public static List<LayoutGroup> getLayoutGroups(List<LayoutBean> layouts) {
        if (layouts == null || layouts.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<LayoutBean>> groupedByDocType = layouts.stream()
                .collect(Collectors.groupingBy(LayoutBean::getGroupName));

        return groupedByDocType.entrySet().stream()
                .map(entry -> new LayoutGroup(
                        entry.getKey(),
                        entry.getValue().get(0).getGroupDesc(),
                        entry.getValue().get(0).getGroupIcon(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    public static List<LayoutBean> parseExcel(String filePath) throws IOException {
        List<LayoutBean> layoutList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) {
                return layoutList;
            }

            Row headerRow = rowIterator.next();
            Map<String, Integer> columnIndexMap = buildColumnIndexMap(headerRow);
            validateRequiredColumns(columnIndexMap);

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                LayoutBean layout = parseRowToLayout(row, columnIndexMap);
                if (isValidLayout(layout)) {
                    layoutList.add(layout);
                }
            }
        }
        return layoutList;
    }

    /**
     * 排版配置Bean
     */
    @Data
    public static class LayoutBean {
        /** 分组名称 */
        private String groupName;
        /** 分组描述 */
        private String groupDesc;
        /** 分组图片(Base64编码) */
        private String groupIcon;
        /** 模板名称 */
        private String layoutName;
        /** 模板描述 */
        private String layoutDesc;
        /** 模板格式(配置) */
        private String layoutConfig;
        /** 模板图片(Base64编码) */
        private String templateIcon;
        /** 排序序号 */
        private String sort;
    }

    /**
     * 排版分组对象
     */
    @Data
    public static class LayoutGroup {
        private String groupName;
        private String groupDesc;
        private String groupIcon;
        private List<LayoutBean> layouts;

        public LayoutGroup(String groupName, String groupDesc, String groupIcon, List<LayoutBean> layouts) {
            this.groupName = groupName;
            this.groupDesc = groupDesc;
            this.groupIcon = groupIcon;
            this.layouts = layouts;
        }
    }
}