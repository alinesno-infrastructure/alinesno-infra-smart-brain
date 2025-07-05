//package com.alinesno.infra.smart.assistant.scene.scene.examPaper.tools;
//
//import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class ImageTools {
//
//    @Autowired
//    private CloudStorageConsumer storageConsumer;
//
//    // 本地缓存配置（Guava Cache）
//    private final Cache<String, byte[]> imageCache = CacheBuilder.newBuilder()
//            .maximumSize(1000)                // 最大缓存1000个条目
//            .expireAfterAccess(1, TimeUnit.HOURS)  // 访问后1小时过期
//            .build();
//
//    public byte[] getImage(String imageId) throws IOException {
//        try {
//            // 从缓存获取，不存在则加载
//            return imageCache.get(imageId, () -> {
//                // 缓存未命中，从存储下载
//                return storageConsumer.download(imageId, progress -> {});
//            });
//        } catch (ExecutionException e) {
//            Throwable cause = e.getCause();
//            if (cause instanceof IOException) {
//                throw (IOException) cause;
//            }
//            throw new IOException("获取图片失败", cause);
//        }
//    }
//
//    // 手动清除缓存
//    public void clearCache(String imageId) {
//        imageCache.invalidate(imageId);
//    }
//}