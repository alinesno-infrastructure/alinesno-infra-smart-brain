package com.alinesno.infra.base.search.utils;

import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.*;
import org.apache.poi.xslf.usermodel.*;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;

/**
 * PPT内容解析工具类(结合OCR工具)
 */
@Slf4j
public class PPTContentParser {

    private static PPTContentParser instance = null;

    @Setter
    private ImageRecognitionService imageRecognitionService;

    public static PPTContentParser getInstance(ImageRecognitionService service) {
        if (instance == null) {
            instance = new PPTContentParser();
            instance.setImageRecognitionService(service);
        }
        return instance;
    }

    /**
     * 解析PPT内容（按幻灯片组织内容）
     * @param pptFile PPT文件
     * @param recognitionType 识别类型：ocr或llm
     * @param model 模型配置
     * @return 解析结果，按幻灯片组织的文本、表格和图片
     */
    public List<SlideContent> parsePPTContent(File pptFile,
                                              String recognitionType,
                                              LlmModelEntity model) {
        List<SlideContent> slidesContent = new ArrayList<>();

        try (InputStream is = new FileInputStream(pptFile)) {
            // 1. 创建SlideShow对象
            SlideShow<?, ?> slideShow = pptFile.getName().toLowerCase().endsWith(".pptx")
                    ? new XMLSlideShow(is)
                    : new HSLFSlideShow(is);

            // 2. 处理每张幻灯片
            for (Slide<?, ?> slide : slideShow.getSlides()) {
                SlideContent slideContent = new SlideContent();
                slideContent.setSlideNumber(slide.getSlideNumber());

                // 提取文本内容
                slideContent.setTextContent(extractTextFromSlide(slide));

                // 提取表格内容
                slideContent.setTables(extractTablesFromSlide(slide));

                // 提取并处理图片内容
                if (model != null && imageRecognitionService != null) {
                    slideContent.setImages(processImagesFromSlide(slide, pptFile, recognitionType, model));
                }

                slidesContent.add(slideContent);
            }

        } catch (Exception e) {
            throw new RuntimeException("解析PPT内容失败", e);
        }

        return slidesContent;
    }

    /**
     * 提取单张幻灯片中的文本内容
     */
    private String extractTextFromSlide(Slide<?, ?> slide) {
        StringBuilder textContent = new StringBuilder();

        for (Shape<?, ?> shape : slide.getShapes()) {
            if (shape instanceof TextShape && !(shape instanceof TableShape)) {
                textContent.append(((TextShape<?, ?>) shape).getText()).append("\n");
            }
        }

        return textContent.toString();
    }

    /**
     * 提取单张幻灯片中的表格内容
     */
    private List<TableContent> extractTablesFromSlide(Slide<?, ?> slide) {
        List<TableContent> tables = new ArrayList<>();

        if (slide instanceof XSLFSlide) {
            for (XSLFShape shape : ((XSLFSlide) slide).getShapes()) {
                if (shape instanceof XSLFTable) {
                    tables.add(processXSLFTable((XSLFTable) shape));
                }
            }
        } else if (slide instanceof HSLFSlide) {
            for (HSLFShape shape : ((HSLFSlide) slide).getShapes()) {
                if (shape instanceof HSLFTable) {
                    tables.add(processHSLFTable((HSLFTable) shape));
                }
            }
        }

        return tables;
    }

    private TableContent processXSLFTable(XSLFTable table) {
        TableContent content = new TableContent();
        content.setTableIndex(table.getShapeId());

        List<List<String>> rows = new ArrayList<>();
        for (XSLFTableRow row : table.getRows()) {
            List<String> cells = new ArrayList<>();
            for (XSLFTableCell cell : row) {
                cells.add(cell.getText());
            }
            rows.add(cells);
        }
        content.setRows(rows);

        return content;
    }

    private TableContent processHSLFTable(HSLFTable table) {
        TableContent content = new TableContent();
        content.setTableIndex(table.getShapeId());

        List<List<String>> rows = new ArrayList<>();
        List<HSLFTextShape> textShapes = new ArrayList<>();

        for (HSLFShape shape : table.getShapes()) {
            if (shape instanceof HSLFTextShape) {
                textShapes.add((HSLFTextShape) shape);
            }
        }

        Map<String, List<String>> cellTextMap = new HashMap<>();
        for (HSLFTextShape textShape : textShapes) {
            String positionKey = textShape.getAnchor().getX() + "," + textShape.getAnchor().getY();
            String text = textShape.getText();
            cellTextMap.computeIfAbsent(positionKey, k -> new ArrayList<>()).add(text);
        }

        cellTextMap.values().forEach(texts -> {
            List<String> row = new ArrayList<>(texts);
            rows.add(row);
        });

        content.setRows(rows);
        return content;
    }

    /**
     * 处理单张幻灯片中的图片（增加有意义图片的过滤）
     */
    private List<ImageContent> processImagesFromSlide(Slide<?, ?> slide, File pptFile,
                                                      String recognitionType, LlmModelEntity model)
            throws Exception {
        List<ImageContent> imageContents = new ArrayList<>();
        List<? extends PictureData> pictures = getPicturesFromSlide(slide);

//        // 创建按幻灯片编号分类的目录
//        String baseDir = "/Users/luodong/Desktop/tmp";
//        String slideDir = baseDir + "/slide_" + slide.getSlideNumber();
//        new File(slideDir).mkdirs();

        int imageIndex = 1;
        for (PictureData picture : pictures) {
            File tempFile = createTempImageFile(picture, imageIndex++);
            File processedImage = null;

            try {
                // 读取图片尺寸信息
                BufferedImage image = ImageIO.read(tempFile);
                if (image == null) {
                    continue; // 跳过无法读取的图片
                }

                long originalSizeKB = tempFile.length() / 1024;
                int width = image.getWidth();
                int height = image.getHeight();

                // 过滤条件：大小<10KB或尺寸<100x100的图片视为无意义
                if (originalSizeKB < 10 || width < 100 || height < 100) {
                    log.debug("跳过小图片[{}] - 大小: {}KB, 尺寸: {}x{}\n", imageIndex, originalSizeKB, width, height);
                    continue;
                }

                // 保存原始图片
//                String imageName = "img_" + imageIndex + "." + picture.getType().extension;
//                File savedFile = new File(slideDir, imageName);
//                FileUtils.copyFile(tempFile, savedFile);

                log.debug("处理图片[{}] - 大小: {} KB, 尺寸: {}x{}",  imageIndex, originalSizeKB, width, height);

                // 保持原有的压缩和处理逻辑不变
                if (tempFile.length() > 512 * 1024) {
                    log.debug("图片超过512KB，开始无损压缩...");
                    processedImage = losslessCompressImage(tempFile, 512);
                    long compressedSizeKB = processedImage.length() / 1024;
                    log.debug("压缩后大小: {} KB",  compressedSizeKB) ;
                } else {
                    processedImage = preprocessImage(tempFile);
                }

                // 后续识别处理流程
                String fileId = pptFile.getName() + "_img_" + imageIndex;
                String recognitionResult = imageRecognitionService.recognizeImage(
                        processedImage != null ? processedImage : tempFile,
                        model,
                        recognitionType,
                        fileId
                );

                // 构建返回对象（新增尺寸信息）
                ImageContent content = new ImageContent();
                content.setImageIndex(imageIndex);
                content.setImageFormat(picture.getType().extension);
                content.setRecognitionResults(recognitionResult);
                content.setImageSize(originalSizeKB + "KB");
                content.setDimensions(width + "x" + height);
                imageContents.add(content);

            } finally {
                FileUtils.deleteQuietly(tempFile);
                if (processedImage != null && !processedImage.equals(tempFile)) {
                    FileUtils.deleteQuietly(processedImage);
                }
            }
        }
        return imageContents;
    }

    /**
     * 无损图片压缩方法（保持透明通道）
     * @param sourceFile 源图片文件
     * @param targetMaxKB 目标最大值(KB)
     * @return 压缩后的临时文件
     */
    private File losslessCompressImage(File sourceFile, int targetMaxKB) throws IOException {
        // 读取图片信息
        BufferedImage image = ImageIO.read(sourceFile);
        String formatName = getImageFormat(sourceFile); // 根据文件扩展名获取格式

        // 创建临时文件
        File compressedFile = File.createTempFile("compressed_", "." + formatName.toLowerCase());

        // PNG格式处理（支持透明通道）
        if ("png".equalsIgnoreCase(formatName)) {
            return compressPngWithJava(sourceFile, targetMaxKB);
        }
        // JPEG格式处理
        else if ("jpg".equalsIgnoreCase(formatName) || "jpeg".equalsIgnoreCase(formatName)) {
            // 使用渐进式JPEG压缩
            try (OutputStream os = new FileOutputStream(compressedFile)) {
                ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
                try (ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {
                    writer.setOutput(ios);
                    ImageWriteParam param = writer.getDefaultWriteParam();
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.9f); // 初始质量
                    param.setProgressiveMode(ImageWriteParam.MODE_DEFAULT); // 渐进式
                    writer.write(null, new IIOImage(image, null, null), param);
                }
                writer.dispose();
            }
        }

        return compressedFile;
    }

    /**
     * 获取图片格式
     */
    private String getImageFormat(File file) {
        return FilenameUtils.getExtension(file.getName());
    }

    /**
     * 使用 Java 原生方法压缩 PNG 图片
     * @param sourceFile  源文件
     * @param targetMaxKB 目标最大值（KB）
     * @return 压缩后的临时文件
     */
    private File compressPngWithJava(File sourceFile, int targetMaxKB) throws IOException {
        BufferedImage image = ImageIO.read(sourceFile);
        File compressedFile = File.createTempFile("compressed_", ".png");

        // 尝试不同的压缩策略
        float quality = 1.0f; // 初始质量（PNG是无损格式，这里实际控制的是过滤器和压缩级别）
        int attempts = 0;

        do {
            ImageIO.write(image, "PNG", compressedFile);
            long currentSizeKB = compressedFile.length() / 1024;

            if (currentSizeKB <= targetMaxKB || attempts >= 3) {
                break; // 达到目标或尝试次数用完
            }

            // 调整压缩策略（模拟"质量"降低）
            quality = Math.max(0.5f, quality - 0.1f); // 最低不低于0.5
            attempts++;

        } while (true);

        return compressedFile;
    }

    /**
     * 获取当前幻灯片中的图片数据（修正版）
     */
    private List<? extends PictureData> getPicturesFromSlide(Slide<?, ?> slide) {
        List<PictureData> pictures = new ArrayList<>();

        if (slide instanceof XSLFSlide xslfSlide) {
            // 获取幻灯片中的所有形状
            for (XSLFShape shape : xslfSlide.getShapes()) {
                if (shape instanceof XSLFPictureShape) {
                    pictures.add(((XSLFPictureShape) shape).getPictureData());
                }
            }
        } else if (slide instanceof HSLFSlide hslfSlide) {
            // 获取幻灯片中的所有形状
            for (HSLFShape shape : hslfSlide.getShapes()) {
                if (shape instanceof HSLFPictureShape) {
                    pictures.add(((HSLFPictureShape) shape).getPictureData());
                }
            }
        }

        return pictures;
    }

    /**
     * 图片预处理（不对小图片进行二值化处理）
     */
    private File preprocessImage(File originalImage) throws IOException {
        BufferedImage image = ImageIO.read(originalImage);
        if (image == null) return originalImage;

        // 只对大于300x300的图片进行二值化处理
        if (image.getWidth() > 300 && image.getHeight() > 300) {
            BufferedImage processedImage = new BufferedImage(
                    image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g = processedImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();

            File processedFile = File.createTempFile("processed_", ".png");
            ImageIO.write(processedImage, "png", processedFile);
            return processedFile;
        }

        return originalImage;
    }

    /**
     * 创建临时图片文件
     */
    private File createTempImageFile(PictureData picture, int index) throws IOException {
        String format = picture.getType().extension;
        File tempFile = File.createTempFile("ppt_img_" + index, "." + format);

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            out.write(picture.getData());
        }

        return tempFile;
    }

    /**
     * 幻灯片内容类
     */
    @Data
    public static class SlideContent {
        private int slideNumber;
        private String textContent;
        private List<TableContent> tables;
        private List<ImageContent> images;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== 幻灯片 ").append(slideNumber).append(" ===\n");

            sb.append("--- 文本内容 ---\n").append(textContent).append("\n");

            if (tables != null && !tables.isEmpty()) {
                sb.append("--- 表格内容 ---\n");
                for (TableContent table : tables) {
                    sb.append("表格 #").append(table.getTableIndex()).append(":\n");
                    for (List<String> row : table.getRows()) {
                        for (String cell : row) {
                            sb.append(cell).append("\t");
                        }
                        sb.append("\n");
                    }
                    sb.append("\n");
                }
            }

            if (images != null && !images.isEmpty()) {
                sb.append("--- 图片识别结果 ---\n");
                for (ImageContent content : images) {
                    sb.append("图片 ").append(content.getImageIndex())
                            .append(" (").append(content.getImageFormat()).append("):\n");
                    sb.append(content.getRecognitionResults()).append("\n\n");
                }
            }

            return sb.toString();
        }
    }

    /**
     * 表格内容类
     */
    @Data
    public static class TableContent {
        private int tableIndex;
        private List<List<String>> rows;
    }

    /**
     * 图片内容类
     */
    @Data
    public static class ImageContent {
        private int imageIndex;
        private String imageFormat;
        private String recognitionResults;
        private String imageSize;  // 新增：图片大小(KB)
        private String dimensions; // 新增：图片尺寸(宽x高)
    }
}