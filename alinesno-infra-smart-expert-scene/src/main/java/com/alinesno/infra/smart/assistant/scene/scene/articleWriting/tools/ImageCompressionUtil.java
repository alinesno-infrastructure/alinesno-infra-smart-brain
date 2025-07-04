package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

/**
 * 图片压缩工具类
 */
public class ImageCompressionUtil {

    /**
     * 压缩图片至指定宽度（默认200px）并保存到临时文件
     * @param imageUrl 图片URL
     * @return 临时文件路径
     * @throws IOException 图片处理异常
     */
    public static String compressAndSaveToTemp(String imageUrl) throws IOException {
        return compressAndSaveToTemp(imageUrl, 200);
    }

    /**
     * 压缩图片至指定宽度并保存到临时文件
     * @param imageUrl 图片URL
     * @param targetWidth 目标宽度（像素）
     * @return 临时文件路径
     * @throws IOException 图片处理异常
     */
    public static String compressAndSaveToTemp(String imageUrl, int targetWidth) throws IOException {
        byte[] compressedData = compressImage(imageUrl, targetWidth);

        // 创建临时文件
        File tempFile = File.createTempFile("compressed_image_", ".jpg");
        tempFile.deleteOnExit(); // JVM退出时自动删除

        // 将压缩后的图片数据写入临时文件
        Files.write(tempFile.toPath(), compressedData);

        return tempFile.getAbsolutePath();
    }

    /**
     * 压缩图片至指定宽度
     * @param imageUrl 图片URL
     * @param targetWidth 目标宽度（像素）
     * @return 压缩后的图片字节数组
     * @throws IOException 图片处理异常
     */
    private static byte[] compressImage(String imageUrl, int targetWidth) throws IOException {
        // 从URL加载图片
        URL url = new URL(imageUrl);
        BufferedImage originalImage = ImageIO.read(url);

        // 计算目标高度，保持原始宽高比
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int targetHeight = (int) ((float) originalHeight / originalWidth * targetWidth);

        // 创建缩放后的图片
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        try {
            // 设置高质量缩放
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制缩放后的图片
            g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        } finally {
            g.dispose();
        }

        // 将处理后的图片转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        return baos.toByteArray();
    }

    // 示例用法
    public static void main(String[] args) {
        try {
            String imageUrl = "https://example.com/image.jpg";
            String tempFilePath = compressAndSaveToTemp(imageUrl);
            System.out.println("图片压缩并保存到: " + tempFilePath);
        } catch (IOException e) {
            System.err.println("图片处理失败: " + e.getMessage());
        }
    }
}    