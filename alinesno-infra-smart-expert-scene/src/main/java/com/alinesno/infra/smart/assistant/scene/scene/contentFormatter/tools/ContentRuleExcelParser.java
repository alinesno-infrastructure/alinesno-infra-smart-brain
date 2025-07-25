package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ContentRuleExcelParser
 */
public class ContentRuleExcelParser {

    public static List<TemplateBean> parseExcel(String filePath) throws IOException {
        List<TemplateBean> templateList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

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

        template.setGroupName(getCellValueByColumnName(row, columnIndexMap, "所属分组"));
        template.setGroupDesc(getCellValueByColumnName(row, columnIndexMap, "分组描述"));

        template.setRuleName(getCellValueByColumnName(row, columnIndexMap, "审核规则名称"));
        template.setRiskLevel(getCellValueByColumnName(row, columnIndexMap, "风险级别"));
        template.setRuleContent(getCellValueByColumnName(row, columnIndexMap, "规则内容"));
        template.setReviewPosition(getCellValueByColumnName(row, columnIndexMap, "审核立场"));

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
                    // Handle integers and decimals
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
     * 按文档类型分组模板
     * @param templates 模板对象列表
     * @return 分组后的TemplateGroup对象列表（包含组名和对应模板集合）
     */
    public static List<TemplateGroup> getTemplateGroups(List<TemplateBean> templates) {
        // 空值安全检查
        if (templates == null || templates.isEmpty()) {
            return new ArrayList<>();
        }

        // 按groupName字段分组（关键修改点）
        // 使用Lambda表达式明确指定分组字段，避免方法引用导致的歧义
        Map<String, List<TemplateBean>> groupedByDocType = templates.stream()
                .collect(Collectors.groupingBy(TemplateBean::getGroupName));

        // 转换为TemplateGroup对象
        return groupedByDocType.entrySet().stream()
                .map(entry -> new TemplateGroup(
                        entry.getKey(),                     // 组名称
                        entry.getValue().get(0).getGroupDesc(), // 从组内首个元素获取组描述
                        entry.getValue()                   // 该组下的所有模板
                ))
                .collect(Collectors.toList());
    }

    /**
     * Gets just the group information without templates
     * @param templates List of TemplateBean objects
     * @return List of unique docTypes
     */
    public static List<String> getGroupList(List<TemplateBean> templates) {
        if (templates == null || templates.isEmpty()) {
            return new ArrayList<>();
        }

        return templates.stream()
                .map(TemplateBean::getGroupName)
                .distinct()
                .collect(Collectors.toList());
    }

    @Data
    public static class TemplateBean {
        /** 分组名称 */
        private String groupName;
        /** 分组描述 */
        private String groupDesc;
        /** 规则名称 */
        private String ruleName;
        /** 风险等级（high/medium/low） */
        private String riskLevel;
        /** 规则详细内容 */
        private String ruleContent;
        /** 分组ID */
        private Long groupId;
        /** 审核立场（禁止性/强制性/建议性） */
        private String reviewPosition;
        /** 适用范围 */
        private String scope;
        /** 规则描述说明 */
        private String ruleDescription;

        // @Data注解会自动生成以下方法：
        // getGroupName(), setGroupName(), getGroupDesc()...等所有字段的getter/setter
    }

    @Data
    public static class TemplateGroup {
        /** 分组名称 */
        private String groupName;
        /** 分组描述 */
        private String groupDesc;
        /** 该分组下的模板集合 */
        private List<TemplateBean> templates;

        /**
         * 全参数构造方法
         * @param groupName 分组名称
         * @param groupDesc 分组描述
         * @param templates 模板列表
         */
        public TemplateGroup(String groupName, String groupDesc, List<TemplateBean> templates) {
            this.groupName = groupName;
            this.groupDesc = groupDesc;
            this.templates = templates;
        }
    }
}