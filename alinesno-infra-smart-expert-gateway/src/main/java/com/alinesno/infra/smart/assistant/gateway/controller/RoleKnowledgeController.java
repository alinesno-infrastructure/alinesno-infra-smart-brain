package com.alinesno.infra.smart.assistant.gateway.controller;

import cn.hutool.core.io.FileTypeUtil;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.SuperController;
import com.alinesno.infra.smart.assistant.adapter.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorDto;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Slf4j
@Api(tags = "IndustryRole")
@RestController
@Scope("prototype")
@RequestMapping("/api/infra/smart/assistant/roleKnowledge")
public class RoleKnowledgeController extends SuperController {

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    @Autowired
    private BaseSearchConsumer baseSearchConsumer ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    /**
     * 分页搜索数据结果
     */
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request ,  DatatablesPageBean page) {

        log.debug("page = {}" , ToStringBuilder.reflectionToString(page));

        String knowledgeId = request.getParameter("datasetId") ;
        TableDataInfo tableDataInfo = baseSearchConsumer.datasetSearchPage(knowledgeId, page.getPageNum() , page.getPageSize()) ;

        log.debug("tableDataInfo = {}", tableDataInfo);

        return tableDataInfo ;
    }

    /**
     * 搜索向量库 getSearchKnowledge
     */
    @PostMapping("/getSearchKnowledge")
    public AjaxResult getSearchKnowledge(@RequestBody VectorSearchDto topK){

        log.debug("topK:{}" , topK);
        R<List<DocumentVectorDto>> document = baseSearchConsumer.datasetSearch(topK) ;
        return AjaxResult.success(document.getData()) ;
    }

    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult importData(@RequestPart("file") MultipartFile file, String datasetId) throws Exception {

        // 获取原始文件名
        String fileName = file.getOriginalFilename();
        log.debug("fileName:{}" , fileName);

        // 复制文件
        assert fileName != null;
        File targetFile = new File(localPath, fileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        String fileType = FileTypeUtil.getType(targetFile);
        log.debug("fileType:{}" , fileType);

        R<String> result = baseSearchConsumer.datasetUpload(targetFile.getAbsolutePath() , datasetId , progress -> {
            log.debug("total bytes: " + progress.getTotalBytes());   // 文件大小
            log.debug("current bytes: " + progress.getCurrentBytes());   // 已上传字节数
            log.debug("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已上传百分比
            if (progress.isDone()) {   // 是否上传完成
                log.debug("--------   Upload Completed!   --------");
            }
        }) ;
        log.debug("upload file result = {}" , result);

        return AjaxResult.success();
    }

}
