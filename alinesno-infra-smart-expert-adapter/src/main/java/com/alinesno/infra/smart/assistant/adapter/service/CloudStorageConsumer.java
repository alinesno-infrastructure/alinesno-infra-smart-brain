package com.alinesno.infra.smart.assistant.adapter.service;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.im.dto.FileAttachmentDto;
import com.dtflys.forest.callback.OnProgress;

import java.io.File;
import java.util.List;

/**
 * 云文件存储上传
 */
public interface CloudStorageConsumer {

    /**
     * 上传文件到指定的存储平台并返回访问的url
     */
    R<String> uploadCallbackUrl(File file) ;

    /**
     * 上传文件到指定的存储平台并返回访问的url
     * @param file
     * @param platform
     * @return
     */
    R<String> uploadCallbackUrl(File file, String platform);

    /**
     * 上传文件到存储
     *
     * @param file 待上传的文件，使用multipart/form-data方式提交
     * @return 返回上传结果的AjaxResult对象
     */
    R<String> upload(File file) ;

    /**
     * 从存储下载文件
     *
     * @param storageId 存储文件的唯一标识符，用于定位特定文件
     * @param onProgress 下载进度回调接口，用于实时获取下载进度
     * @return 返回文件的字节数组表示形式
     */
    byte[] download(String storageId, OnProgress onProgress) ;

    /**
     * 获取文件列表
     *
     * @param fileIds 文件ID列表，以逗号分隔
     * @return 返回文件列表的列表形式
     */
    List<FileAttachmentDto> list(List<Long> fileIds) ;

}
