package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.response.R;
import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnProgress;

import java.io.File;

/**
 * 云文件存储上传
 */
@BaseRequest(baseURL = "#{alinesno.infra.gateway.host}/base-storage" , connectTimeout = 30*1000)
public interface CloudStorageConsumer {

    /**
     * 上传文件到存储
     *
     * @param file 待上传的文件，使用multipart/form-data方式提交
     * @param platform 平台信息，用于区分上传文件的来源或用途
     * @param onProgress 上传进度回调接口，用于实时获取上传进度
     * @return 返回上传结果的AjaxResult对象
     */
    @Post(url = "/api/base/storage/upload" , contentType = "multipart/form-data")
    R<String> upload(@DataFile("file") File file, @Body("platform") String platform, OnProgress onProgress) ;

    /**
     * 从存储下载文件
     *
     * @param storageId 存储文件的唯一标识符，用于定位特定文件
     * @param onProgress 下载进度回调接口，用于实时获取下载进度
     * @return 返回文件的字节数组表示形式
     */
    @Get(url = "/api/base/storage/download")
    byte[] download(@Query("storageId") String storageId, OnProgress onProgress) ;
}
