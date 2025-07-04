package com.alinesno.infra.smart.assistant.scene.scene.longtext.tools;

import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RobustExcelParser {

    public static List<TemplateBean> parseExcel(String filePath) throws IOException {
        List<TemplateBean> templateList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 首先读取标题行，建立列名到索引的映射
            if (!rowIterator.hasNext()) {
                return templateList;
            }

            Row headerRow = rowIterator.next();
            Map<String, Integer> columnIndexMap = buildColumnIndexMap(headerRow);

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                TemplateBean template = parseRowToTemplate(row, columnIndexMap);
                if (template != null) {
                    templateList.add(template);
                }
            }
        }

        return templateList;
    }

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

    private static TemplateBean parseRowToTemplate(Row row, Map<String, Integer> columnIndexMap) {
        if (row == null) {
            return null;
        }

        TemplateBean template = new TemplateBean();

        // 通过列名获取单元格值，不再依赖固定索引
        template.setSerialNumber(getCellValueByColumnName(row, columnIndexMap, "序号"));
        template.setTypeIcon(getCellValueByColumnName(row, columnIndexMap, "类型图标"));
        template.setType(getCellValueByColumnName(row, columnIndexMap, "类型"));
        template.setTypeCode(getCellValueByColumnName(row, columnIndexMap, "类型代码"));
        template.setTemplateName(getCellValueByColumnName(row, columnIndexMap, "模板名称"));
        template.setTemplateDescription(getCellValueByColumnName(row, columnIndexMap, "模板描述"));
        template.setTemplateContent(getCellValueByColumnName(row, columnIndexMap, "模板内容"));
        template.setPermission(getCellValueByColumnName(row, columnIndexMap, "权限"));
        template.setIcon(getCellValueByColumnName(row, columnIndexMap, "图标"));

        return template;
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
                    // 处理整数和小数
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
     * 将模板按类型/类型代码分组，并构建包含模板列表的分组对象
     * @param templates 模板Bean列表
     * @return 分组后的模板组列表（包含类型信息和对应模板集合）
     */
    public static List<TemplateGroup> getTemplateGroups(List<TemplateBean> templates) {
        if (templates == null || templates.isEmpty()) {
            return new ArrayList<>();
        }

        // 使用Stream API按typeCode去重
        List<TemplateGroup> templateGroups = templates.stream()
                .map(template -> new TemplateGroup(
                        template.getSerialNumber(),
                        template.getType(),
                        template.getTypeIcon(),
                        template.getTypeCode(),
                        null))
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(
                                () -> new TreeSet<>(Comparator.comparing(TemplateGroup::getTypeCode))),
                        ArrayList::new));

        for (TemplateGroup templateGroup : templateGroups) {

            List<TemplateBean> templatesInGroup = templates.stream()
                    .filter(template -> template.getTypeCode().equals(templateGroup.getTypeCode()))
                    .toList();

            templateGroup.setTemplates(templatesInGroup);
        }

        return templateGroups;

    }

    /**
     * Gets just the group information without templates
     * @param templates List of TemplateBean objects
     * @return List of TemplateGroup objects with only type and typeCode set
     */
    public static List<TemplateGroup> getGroupList(List<TemplateBean> templates) {
        if (templates == null || templates.isEmpty()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(templates.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        template -> Arrays.asList(template.getType(), template.getTypeCode()),
                        template -> new TemplateGroup(template.getSerialNumber() ,  template.getType(), template.getTypeIcon() ,  template.getTypeCode(), null),
                        (existing, replacement) -> existing
                ))
                .values());
    }

    @Data
    public static class TemplateBean {
        private String serialNumber;
        private String typeIcon;
        private String type;
        private String typeCode;
        private String templateName;
        private String templateDescription;
        private String templateContent;
        private String permission;
        private String icon;
    }

    @Data
    public static class TemplateGroup {
        private String serialNumber;
        private String type;
        private String typeIcon;
        private String typeCode;
        private List<TemplateBean> templates;

        public TemplateGroup(String serialNumber ,  String type, String typeIcon ,  String typeCode, List<TemplateBean> templates) {
            this.serialNumber = serialNumber;
            this.type = type;
            this.typeIcon = typeIcon;
            this.typeCode = typeCode;
            this.templates = templates;
        }
    }
}