package com.alinesno.infra.base.search.service.reader;

public class TestExcelReaderServiceImpl {

//    /**
//     * 解析 Excel 文件内容。
//     *
//     * @param file Excel 文件
//     * @return Excel 文件的文本内容，包含格式化的sheet和内容
//     * @throws IOException 读取文件时可能出现的异常
//     */
//    private String parseExcelFile(File file) throws IOException {
//        try (InputStream inputStream = new FileInputStream(file)) {
//            Workbook workbook;
//            String fileName = file.getName();
//            if (fileName.endsWith(".xlsx")) {
//                workbook = new XSSFWorkbook(inputStream);
//            } else if (fileName.endsWith(".xls")) {
//                workbook = new HSSFWorkbook(inputStream);
//            } else {
//                throw new IOException("Unsupported Excel file format");
//            }
//
//            StringBuilder content = new StringBuilder();
//            content.append("=== Excel File: ").append(fileName).append(" ===\n\n");
//
//            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
//                Sheet sheet = workbook.getSheetAt(i);
//                content.append("--- Sheet ").append(i + 1).append(": ")
//                        .append(sheet.getSheetName()).append(" ---\n");
//
//                // Add column headers if they exist (first row)
//                if (sheet.getPhysicalNumberOfRows() > 0) {
//                    Row headerRow = sheet.getRow(0);
//                    if (headerRow != null) {
//                        content.append("[Headers]: ");
//                        for (Cell cell : headerRow) {
//                            content.append(cell.toString()).append(" | ");
//                        }
//                        content.append("\n");
//                    }
//                }
//
//                // Add data rows
//                content.append("[Data]:\n");
//                for (Row row : sheet) {
//                    // Skip header row if we already printed it
//                    if (row.getRowNum() == 0 && sheet.getPhysicalNumberOfRows() > 1) {
//                        continue;
//                    }
//
//                    for (Cell cell : row) {
//                        content.append(cell.toString()).append(" | ");
//                    }
//                    content.append("\n");
//                }
//                content.append("\n"); // Add space between sheets
//            }
//            return content.toString();
//        }
//    }

}