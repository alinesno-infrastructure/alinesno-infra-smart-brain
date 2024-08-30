package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.response.AjaxResult;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.callback.OnProgress;

/**
 * 基础的搜索管理服务
 */
@BaseRequest(baseURL = "#{alinesno.infra.gateway.host}/base-search" , connectTimeout = 30*1000)
public interface BaseSearchController {

   @Post(url = "/api/base/search/vectorData/upload" , contentType = "multipart/form-data")
   AjaxResult datasetUpload(@DataFile("file") String filePath , @Body("datasetId") String datasetId  , OnProgress onProgress) ;

}
