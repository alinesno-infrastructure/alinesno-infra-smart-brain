package com.alinesno.infra.base.search.utils.parse;

import com.alinesno.infra.base.search.utils.TextParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
public class ExcelParser extends TextParser {

    public static String parse(String filepath) {

        StringBuffer sb = new StringBuffer() ;

        try {
            InputStream inp = new FileInputStream(filepath);
            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0); // 读取第一个工作表
            for (Row row : sheet) {
                for (Cell cell : row) {
                    switch (cell.getCellType()) {
                        case STRING -> sb.append(cell.getRichStringCellValue().getString());
                        case NUMERIC -> {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                sb.append(cell.getDateCellValue());
                            } else {
                                sb.append(cell.getNumericCellValue());
                            }
                        }
                        case BOOLEAN -> sb.append(cell.getBooleanCellValue());
                        case FORMULA -> sb.append(cell.getCellFormula());
                        case BLANK -> {
                        }
                        // 空单元格处理
                        default -> sb.append("\r\n");
                    }
                    sb.append("\t");
                }
                sb.append("\r\n");
            }
        } catch (Exception e) {
            log.error("文件解析异常:{}" , e.getMessage());
        }
        return sb.toString();
    }

}
