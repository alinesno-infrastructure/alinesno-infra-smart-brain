package com.alinesno.infra.base.search.gateway.controller;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.base.search.api.CrawlerDto;
import com.alinesno.infra.base.search.api.DataProcessingDto;
import com.alinesno.infra.base.search.api.SegmentUpdateDto;
import com.alinesno.infra.base.search.entity.DatasetKnowledgeEntity;
import com.alinesno.infra.base.search.enums.FileTypeEnums;
import com.alinesno.infra.base.search.service.ICrawlerService;
import com.alinesno.infra.base.search.service.IDatasetKnowledgeService;
import com.alinesno.infra.base.search.service.IDocumentParserService;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.ConditionDto;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 处理与BusinessLogEntity相关的请求的Controller。
 * 继承自BaseController类并实现IBusinessLogService接口。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@RestController
@Scope(SpringInstanceScope.PROTOTYPE)
@RequestMapping("/api/infra/base/search/datasetKnowledge")
public class DatasetKnowledgeController extends BaseController<DatasetKnowledgeEntity, IDatasetKnowledgeService> {

    @Autowired
    private IDatasetKnowledgeService service;

    @Autowired
    private IDocumentParserService documentParserService ;

    @Autowired
    private ICrawlerService crawlerService ;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model Model对象。
     * @param page DatatablesPageBean对象。
     * @return 包含DataTables数据的TableDataInfo对象。
     */
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));

        List<ConditionDto> conditionList = page.getConditionList() ; // new ArrayList<>() ;

        ConditionDto orderConditionDto = new ConditionDto() ;
        orderConditionDto.setType("orderBy");
        orderConditionDto.setColumn("updateTime");
        orderConditionDto.setValue("false");

        conditionList.add(orderConditionDto) ;

        page.setConditionList(conditionList);

        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 查询上传未导入的文件
     */
    @GetMapping("/queryTmpFileByDatasetId")
    public AjaxResult queryTmpFileByDatasetId(@RequestParam Long datasetId) {
        List<DatasetKnowledgeEntity> list = service.queryTmpFileByDatasetId(datasetId) ;
        return AjaxResult.success(list) ;
    }

    /**
     * 上传解析文件
     */
    @PostMapping("/uploadTmpFileByDatasetId")
    public AjaxResult uploadTmpFileByDatasetId(@Valid @RequestBody DataProcessingDto dto) throws Exception {
        log.debug("dto = {}" , dto);

        service.dataUploadToVectorDataset(dto) ;

        return AjaxResult.success("解析成功") ;
    }

    /**
     * 上传文件
     */
    @DataPermissionQuery
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult upload(@RequestPart("file") MultipartFile file, Long datasetId , PermissionQuery query) throws Exception {
        String fileName = file.getOriginalFilename();

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        String fileType = FileTypeUtil.getType(targetFile);

        service.saveDatasetTmpFile(datasetId, fileName, targetFile , fileType, fileSuffix , query) ;

        // 处理完成之后删除文件
        // FileUtils.forceDeleteOnExit(targetFile);

        return AjaxResult.success("上传成功") ;
    }

    /**
     * 导入数据
     *
     * @param file 导入文件
     */
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult importData(@RequestPart("file") MultipartFile file, Long datasetId) throws Exception {

        // 获取原始文件名
        String fileName = file.getOriginalFilename();
        List<String> sentenceList = new ArrayList<>();

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        String fileType = FileTypeUtil.getType(targetFile);

        FileTypeEnums constants = FileTypeEnums.getByValue(fileSuffix.toLowerCase()) ;
        assert constants != null;

        sentenceList = switch (constants) {
            case PDF ->  documentParserService.parsePDF(targetFile);
            case MD -> documentParserService.parseMD(targetFile);
            case EXCEL, EXCEL_OLD -> documentParserService.parseExcel(targetFile);
            case DOCX -> documentParserService.getContentDocx(targetFile);
            case DOC -> documentParserService.getContentDoc(targetFile);
            default -> sentenceList;
        };

        log.debug("sentenceList = {}" , new Gson().toJson(sentenceList));

        // 处理完成之后删除文件
        FileUtils.forceDeleteOnExit(targetFile);

        if(sentenceList.isEmpty()){
            return AjaxResult.error(fileName + " 文档解析失败.") ;
        }

        service.parserDocument(datasetId, sentenceList, fileName, fileType);

        return AjaxResult.success(fileName) ;
    }

    /**
     * 爬取数据
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/crawler")
    public AjaxResult crawler(@RequestBody @Validated CrawlerDto dto , PermissionQuery query) {
        crawlerService.parseWebsite(dto , query);
        return ok() ;
    }

    /**
     * 查询数据文件详情
     * @return
     */
    @GetMapping("/knowledgeDetail")
    public AjaxResult knowledgeDetail(@RequestParam Long id) {
        DatasetKnowledgeEntity entity = service.getById(id) ;
        return AjaxResult.success(entity) ;
    }

    /**
     * 分页查询指定的文档ID
     * @return
     */
    @GetMapping("/queryDocumentPage")
    public TableDataInfo queryDocumentPage(@RequestParam Long id,
                                        @RequestParam int pageNum ,
                                        @RequestParam int pageSize) {
        DatasetKnowledgeEntity entity = service.getById(id) ;
        return service.queryDocumentPage(entity, pageNum , pageSize);
    }

    /**
     * 更新分段内容
     * @return
     */
    @PostMapping("/updateSegmentContent")
    public AjaxResult updateSegmentContent(@RequestBody @Valid SegmentUpdateDto dto) {
        service.updateSegmentContent(dto);
        return ok() ;
    }


    @Override
    public IDatasetKnowledgeService getFeign() {
        return this.service;
    }
}

