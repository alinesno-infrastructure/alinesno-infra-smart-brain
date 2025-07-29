package com.alinesno.infra.smart.assistant.adapter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alinesno.infra.common.core.utils.DateUtils;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.FileDetailService;
import com.alinesno.infra.smart.assistant.entity.StorageFileEntity;
import com.alinesno.infra.smart.assistant.service.IStorageFileService;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtflys.forest.callback.OnProgress;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.ProgressListener;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 三方存储管理平台
 */
@Slf4j
@Component
public class CloudStorageServiceImpl implements CloudStorageConsumer {

    @Autowired
    private FileStorageService fileStorageService;//注入实列

    @Autowired
    private IStorageFileService fileService ;

    @Autowired
    private FileDetailService fileDetailService ;

    @Override
    public R<String> uploadCallbackUrl(File file) {
        String date = DateUtils.getDate() + "/" ;

        FileInfo fileInfo = fileStorageService.of(file)
                .setProgressMonitor(new ProgressListener() {
                    @Override
                    public void start() {
                        log.debug("上传开始");
                    }

                    @Override
                    public void progress(long progressSize,long allSize) {
                        log.debug("已上传 " + progressSize + " 总大小" + allSize);
                    }

                    @Override
                    public void finish() {
                        log.debug("上传结束");
                    }
                })
                .setPath(date)
                .upload() ;

        return R.ok(fileInfo.getUrl() , "上传成功.");
    }

    @Override
    public R<String> getPreviewUrl(String storageId) {
        //判断对应的存储平台是否支持预签名 URL
        FileStorage storage = fileStorageService.getFileStorage();
        boolean supportPresignedUrl = fileStorageService.isSupportPresignedUrl(storage);

        Assert.isTrue(supportPresignedUrl , "当前存储平台不支持预签名 URL");

        FileInfo fileInfo = getById(storageId) ;

        //快速生成用于访问或下载的 URL ，有效期为1小时
        String presignedUrl = fileStorageService.generatePresignedUrl(fileInfo, DateUtil.offsetHour(new Date(), 1));
        log.debug("文件授权访问地址：{}" , presignedUrl);

        return R.ok(presignedUrl);
    }

    @Override
    public R<String> uploadCallbackUrl(File file, String platform) {
        String date = DateUtils.getDate() + "/" ;

        FileInfo fileInfo = fileStorageService.of(file)
                .setProgressMonitor(new ProgressListener() {
                    @Override
                    public void start() {
                        log.debug("上传开始");
                    }

                    @Override
                    public void progress(long progressSize,long allSize) {
                        log.debug("已上传 " + progressSize + " 总大小" + allSize);
                    }

                    @Override
                    public void finish() {
                        log.debug("上传结束");
                    }
                })
                .setPlatform(platform)
                .setPath(date)
                .upload() ;

        return R.ok(fileInfo.getUrl() , "上传成功.");
    }

    @Override
    public R<String> upload(File file) {
        FileInfo info = fileStorageService
                .of(file)
                .setProgressMonitor(new ProgressListener() {
                    @Override
                    public void start() {
                        log.debug("上传开始");
                    }

                    @Override
                    public void progress(long progressSize,long allSize) {
                        log.debug("已上传 " + progressSize + " 总大小" + allSize);
                    }

                    @Override
                    public void finish() {
                        log.debug("上传结束");
                    }
                })
                .upload() ;

        return R.ok(info.getId());
    }

    @Override
    public byte[] download(String storageId, OnProgress onProgress) {

        FileInfo fileInfo = getById(storageId) ;

        return fileStorageService.download(fileInfo).setProgressMonitor(new ProgressListener() {
            @Override
            public void start() {
                log.trace("下载开始");
            }

            @Override
            public void progress(long progressSize,long allSize) {
                log.trace("已下载 " + progressSize + " 总大小" + allSize);
            }

            @Override
            public void finish() {
                log.trace("下载结束");
            }
        }).bytes() ;

    }

    @Override
    public List<FileAttachmentDto> list(List<Long> fileIds) {

        if(fileIds != null && !fileIds.isEmpty()){
            List<StorageFileEntity> list = fileService.list(new LambdaQueryWrapper<StorageFileEntity>().in(StorageFileEntity::getId,fileIds));
            log.debug("文件列表：{}",list);

            if(list != null && !list.isEmpty()){
                return list.stream().map(item -> {
                    FileAttachmentDto dto = new FileAttachmentDto();
                    dto.setFileId(item.getId());
                    dto.setFileName(item.getOriginalFilename());
                    dto.setFileType(item.getExt());
                    dto.setLength(item.getSize());
                    dto.setWordCount(100);
                    dto.setOrder(0);
                    return dto;
                }).collect(Collectors.toList());
            }

        }

        return null;
    }

    public FileInfo getById(String id) {
        StorageFileEntity detail = fileService.getOne(new LambdaQueryWrapper<StorageFileEntity>().eq(StorageFileEntity::getId,Long.parseLong(id)));
        if(detail == null){
            throw new RpcServiceRuntimeException("文件不存在.");
        }

        FileInfo info = BeanUtil.copyProperties(detail,FileInfo.class,"metadata","userMetadata","thMetadata","thUserMetadata","attr");

        try {
            //这是手动获取数据库中的 json 字符串 并转成 元数据，方便使用
            info.setMetadata(fileDetailService.jsonToMetadata(detail.getMetadata()));
            info.setUserMetadata(fileDetailService.jsonToMetadata(detail.getUserMetadata()));
            info.setThMetadata(fileDetailService.jsonToMetadata(detail.getThMetadata()));
            info.setThUserMetadata(fileDetailService.jsonToMetadata(detail.getThUserMetadata()));

            //这是手动获取数据库中的 json 字符串 并转成 附加属性字典，方便使用
            info.setAttr(fileDetailService.jsonToDict(detail.getAttr()));
        }catch (Exception e){
            log.error("转换json异常:{}" , e.getMessage());
            throw new RpcServiceRuntimeException("JSON转换异常.");
        }

        return info;
    }
}
