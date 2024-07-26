package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnProgress;

import java.io.File;

/**
 * 云文件存储上传
 */
@BaseRequest(baseURL = "#{alinesno.infra.gateway.host}/base-storage" , connectTimeout = 30*1000)
public interface CloudStorageConsumer {

    @Post(url = "/api/base/storage/upload" , contentType = "multipart/form-data")
    AjaxResult upload(@DataFile("file") File file, @Body("platform") String platform, OnProgress onProgress) ;

    @Get(url = "/api/base/storage/download")
    byte[] download(@Query("storageId") String storageId, OnProgress onProgress) ;
}
