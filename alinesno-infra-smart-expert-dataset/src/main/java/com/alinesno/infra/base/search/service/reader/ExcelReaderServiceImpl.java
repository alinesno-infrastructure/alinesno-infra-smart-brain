package com.alinesno.infra.base.search.service.reader;

import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ExcelReaderServiceImpl 类是 Excel 附件读取服务的具体实现，
 * 它实现了 IAttachmentReaderService 接口，主要负责处理 Excel 附件的读取操作。
 * 该类提供了一个方法用于读取指定 Excel 附件的内容。
 */
@Scope("prototype")
@Slf4j
@Service
public class ExcelReaderServiceImpl extends BaseReaderServiceImpl {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    /**
     * 此方法用于读取指定 ID 的 Excel 附件的内容。
     *
     * @param attachmentDto 要读取的 Excel 附件的唯一标识符。
     * @param uploadData
     * @return 返回读取到的 Excel 附件内容，如果读取失败或者没有内容则返回 null。
     */
    @SneakyThrows
    @Override
    public String readAttachment(FileAttachmentDto attachmentDto, UploadData uploadData) {

        File file ;
        if(attachmentDto.getFile() != null){
            file = attachmentDto.getFile() ;
        }else{
            file = getFileById(attachmentDto.getFileId(), attachmentDto.getFileType());
        }

        if (file == null || !file.exists()) {
            return null;
        }
        String content = parseExcelFile(file);
        log.debug("ExcelReaderServiceImpl.readAttachment: {}" , content);

        return content ;
    }

    /**
     * 解析 Excel 文件内容。
     *
     * @param file Excel 文件
     * @return Excel 文件的文本内容，包含格式化的sheet和内容
     * @throws IOException 读取文件时可能出现的异常
     */
    private static String parseExcelFile(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            Workbook workbook;
            String fileName = file.getName();
            if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                throw new IOException("Unsupported Excel file format");
            }

            StringBuilder content = new StringBuilder();
            content.append("=== Excel File: ").append(fileName).append(" ===\n\n");

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                content.append("--- Sheet ").append(i + 1).append(": ").append("名称:").append(sheet.getSheetName()).append(" ---\n");

                // Add column headers if they exist (first row)
                if (sheet.getPhysicalNumberOfRows() > 0) {
                    Row headerRow = sheet.getRow(0);
                    if (headerRow != null) {
                        content.append("[Headers]: ");
                        for (Cell cell : headerRow) {
                            content.append(cell.toString()).append(" | ");
                        }
                        content.append("\n");
                    }
                }

                // Add data rows
                content.append("[Data]:\n");
                for (Row row : sheet) {
                    // Skip header row if we already printed it
                    if (row.getRowNum() == 0 && sheet.getPhysicalNumberOfRows() > 1) {
                        continue;
                    }

                    for (Cell cell : row) {
                        content.append(cell.toString()).append(" | ");
                    }
                    content.append("\n");
                }
                content.append("\n"); // Add space between sheets
            }
            return content.toString();
        }
    }

}