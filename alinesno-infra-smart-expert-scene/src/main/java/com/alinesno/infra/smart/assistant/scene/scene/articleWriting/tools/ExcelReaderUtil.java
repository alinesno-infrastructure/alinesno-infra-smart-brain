package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelReaderUtil {

    public static List<ExcelData> readExcel(String filePath) {
        List<ExcelData> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0); // 获取第一个工作表

            boolean isHeader = true;
            List<String> headers = new ArrayList<>();
            for (Row row : sheet) {
                if (isHeader) {
                    for (Cell cell : row) {
                        headers.add(cell.getStringCellValue());
                    }
                    isHeader = false;
                } else {
                    ExcelData data = new ExcelData();
                    for (int i = 0; i < headers.size(); i++) {
                        Cell cell = row.getCell(i);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case STRING -> {
                                    if ("index".equals(headers.get(i))) {
                                        data.setIndex(Integer.parseInt(cell.getStringCellValue()));
                                    } else if ("icon".equals(headers.get(i))) {
                                        data.setIcon(cell.getStringCellValue());
                                    } else if ("type".equals(headers.get(i))) {
                                        data.setType(cell.getStringCellValue());
                                    } else if ("typeName".equals(headers.get(i))) {
                                        data.setTypeName(cell.getStringCellValue());
                                    }else if("systemPrompt".equals(headers.get(i))) {
                                       data.setSystemPrompt(cell.getStringCellValue());
                                    } else if ("prompt".equals(headers.get(i))) {
                                        data.setPrompt(cell.getStringCellValue());
                                    } else if ("demo".equals(headers.get(i))) {
                                        data.setDemo(cell.getStringCellValue());
                                    } else if ("title".equals(headers.get(i))) {
                                        data.setTitle(cell.getStringCellValue());
                                    } else if ("desc".equals(headers.get(i))) {
                                        data.setDesc(cell.getStringCellValue());
                                    } else if ("resultConfig".equals(headers.get(i))) {
                                        data.setResultConfig(cell.getStringCellValue());
                                    } else if ("config".equals(headers.get(i))) {
                                        data.setConfig(cell.getStringCellValue());
                                    } else if ("power".equals(headers.get(i))) {
                                        data.setPower(cell.getStringCellValue());
                                    }
                                }
                                case NUMERIC -> {
                                    if ("index".equals(headers.get(i))) {
                                        data.setIndex((int) cell.getNumericCellValue());
                                    }
                                }
                                // 处理其他可能的单元格类型，如日期等
                                default -> {
                                }
                            }
                        }
                    }
                    dataList.add(data);
                }
            }
        } catch (IOException e) {
            log.error("文件解析异常:{}" , e.getMessage());
        }

        return dataList;
    }
}