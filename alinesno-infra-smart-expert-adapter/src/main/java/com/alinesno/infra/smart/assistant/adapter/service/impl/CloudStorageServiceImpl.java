package com.alinesno.infra.smart.assistant.adapter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.core.utils.DateUtils;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.FileDetailService;
import com.alinesno.infra.smart.assistant.entity.StorageFileEntity;
import com.alinesno.infra.smart.assistant.service.IStorageFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtflys.forest.callback.OnProgress;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.ProgressListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.File;

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

        return R.ok(fileInfo.getUrl() , "上传成功.");    }

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
                log.debug("下载开始");
            }

            @Override
            public void progress(long progressSize,long allSize) {
                log.debug("已下载 " + progressSize + " 总大小" + allSize);
            }

            @Override
            public void finish() {
                log.debug("下载结束");
            }
        }).bytes() ;

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
