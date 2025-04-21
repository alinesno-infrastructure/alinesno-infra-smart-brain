package com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * ExcelParserToMapPrinter 解析Excel文件为Map的工具类
 */
public class ExcelParserToMapPrinter {

    public static Map<String, List<Map<String, Object>>> parseExcelToMap(String filePath) throws IOException {
        Map<String, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                List<Map<String, Object>> sheetData = new ArrayList<>();

                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    continue;
                }
                int columnCount = headerRow.getLastCellNum();
                List<String> headers = new ArrayList<>();
                for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                    Cell cell = headerRow.getCell(colIndex);
                    if (cell != null) {
                        headers.add(cell.getStringCellValue());
                    }
                }

                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                        Cell cell = row.getCell(colIndex);
                        if (cell != null) {
                            CellType cellType = cell.getCellType();
                            switch (cellType) {
                                case STRING:
                                    rowData.put(headers.get(colIndex), cell.getStringCellValue());
                                    break;
                                case NUMERIC:
                                    if (DateUtil.isCellDateFormatted(cell)) {
                                        rowData.put(headers.get(colIndex), cell.getDateCellValue());
                                    } else {
                                        rowData.put(headers.get(colIndex), cell.getNumericCellValue());
                                    }
                                    break;
                                case BOOLEAN:
                                    rowData.put(headers.get(colIndex), cell.getBooleanCellValue());
                                    break;
                                case FORMULA:
                                    rowData.put(headers.get(colIndex), cell.getCellFormula());
                                    break;
                                default:
                                    rowData.put(headers.get(colIndex), null);
                            }
                        } else {
                            rowData.put(headers.get(colIndex), null);
                        }
                    }
                    sheetData.add(rowData);
                }
                resultMap.put(sheetName, sheetData);
            }
        }
        return resultMap;
    }

    public static void printMap(Map<String, List<Map<String, Object>>> map) {
        for (Map.Entry<String, List<Map<String, Object>>> entry : map.entrySet()) {
            System.out.println("工作表名称: " + entry.getKey());
            for (Map<String, Object> row : entry.getValue()) {
                System.out.println(row);
            }
            System.out.println();
        }
    }
}    