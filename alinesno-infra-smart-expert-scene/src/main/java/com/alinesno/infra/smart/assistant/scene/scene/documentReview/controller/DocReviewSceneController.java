package com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.common.utils.GenPdfTool;
import com.alinesno.infra.smart.assistant.scene.common.utils.MarkdownToWord;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.*;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewAuditService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.AnalysisTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.DocReviewRulesEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.alinesno.infra.smart.scene.enums.ContractTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
@RequestMapping("/api/infra/smart/assistant/scene/documentReview/sceneInfo")
public class DocReviewSceneController extends BaseController<DocReviewSceneEntity, IDocReviewSceneService> {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    @Autowired
    private IDocReviewSceneService service;

    @Autowired
    private IDocReviewAuditService reviewAuditService ;

    @Autowired
    private IDocReviewTaskService docReviewTaskService ;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private AnalysisTool analysisTool;

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

        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 生成审核清单
     */
    @DataPermissionQuery
    @PostMapping("/genReviewList")
    public AjaxResult genReviewList(@RequestBody @Validated DocReviewGenReviewDto dto , PermissionQuery query){

        long sceneId = dto.getSceneId() ;

        DocReviewSceneEntity sceneEntity = service.getBySceneId(sceneId, query) ;
        DocReviewTaskEntity entity = docReviewTaskService.getById(dto.getTaskId());
        entity.setGenStatus(1) ;
        docReviewTaskService.update(entity);

        entity.setContractType(dto.getContractType());
        entity.setReviewPosition(dto.getReviewPosition());
        entity.setReviewListKnowledgeBase(dto.getReviewListKnowledgeBase());
        entity.setReviewListOption(dto.getReviewListOption());

        if(dto.getReviewListOption().equals("aigen")){  // 如果是aigen则直接调用agent生成内容

            MessageTaskInfo taskInfo = new MessageTaskInfo() ;

            taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
            taskInfo.setRoleId(sceneEntity.getAnalysisAgentEngineer());
            taskInfo.setChannelId(sceneId);
            taskInfo.setSceneId(sceneId) ;
            taskInfo.setText("合同类型的审查清单项");

            String documentInfo = """
                    合同名称：%s
                    合同类型：%s
                    合同概述: %s
                    """ ;

            ContractTypeEnum contractType = ContractTypeEnum.getByScenarioId(entity.getContractType()) ;
            if(contractType == null){
                throw new RpcServiceRuntimeException("合同类型不正确，请点击重新生成.") ;
            }

            taskInfo.setText(String.format(documentInfo ,
                    entity.getDocumentName() ,
                    contractType.getTitle() + ":" + contractType.getDesc() ,
                    entity.getContractOverview() == null ?"":entity.getContractOverview()));

            // 优先获取到结果内容
            CompletableFuture<WorkflowExecutionDto> genContent  = roleService.runRoleAgent(taskInfo) ;

//            // 获取到格式化的内容
//            analysisTool.handleFormatRuleMessage(genContent , taskInfo);
//
//            // 解析得到代码内容
//            if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
//                String codeContent = genContent.getCodeContent().get(0).getContent() ;
//                // 验证是否可以正常解析json
//                try{
//                    List<DocReviewRulesDto> nodeDtos = JSON.parseArray(codeContent, DocReviewRulesDto.class);
//                    log.debug("nodeDtos = {}", JSONUtil.toJsonPrettyStr(nodeDtos));
//                    for(DocReviewRulesDto d : nodeDtos){
//                        d.setId(IdUtil.getSnowflakeNextId());
//                    }
//                    entity.setReviewList(JSONUtil.toJsonStr(nodeDtos));
//                }catch (Exception e){
//                    throw new RpcServiceRuntimeException("生成审核规则格式不正确，请点击重新生成.") ;
//                }
//
//            }
        }

        docReviewTaskService.updateById(entity) ;
        return AjaxResult.success();
    }

    /**
     * 初始化智能助手
     * @return
     */
    @PostMapping("/initAgents")
    public AjaxResult initAgents(@RequestBody DocReviewInitDto dto){
        service.initAgents(dto) ;
        return AjaxResult.success();
    }

    /**
     * 生成审核清单
     */
    @DataPermissionQuery
    @PostMapping("/genReviewListByDataset")
    public AjaxResult genReviewListByDataset(@RequestBody @Validated DocReviewGenReviewByDatasetDto dto , PermissionQuery query){

        log.debug("dto = {}", ToStringBuilder.reflectionToString(dto));

        DocReviewTaskEntity entity = docReviewTaskService.getById(dto.getTaskId());

//        entity.setOperatorId(dto.getOperatorId());
//        entity.setOrgId(dto.getOrgId()) ;
//        entity.setDepartmentId(dto.getDepartmentId()) ;

        List<DocReviewRulesDto> nodeDtos = new ArrayList<>() ;

        List<DocReviewRulesEntity> rules = reviewAuditService.getRulesByAuditId(dto.getAuditId()) ;

        // 转换成nodeDtos
        for(DocReviewRulesEntity rule : rules){
            DocReviewRulesDto dto1 = new DocReviewRulesDto() ;

            dto1.setId(rule.getId());
            dto1.setRuleName(rule.getRuleName());
            dto1.setRuleContent(rule.getRuleContent());
            dto1.setRiskLevel(rule.getRiskLevel());
            dto1.setReviewPosition(rule.getReviewPosition());
            dto1.setGroupId(rule.getGroupId());

            nodeDtos.add(dto1);
        }

        entity.setGenStatus(1) ;
        entity.setAuditId(dto.getAuditId()) ;
        entity.setSceneId(dto.getSceneId());
        entity.setReviewList(JSONUtil.toJsonStr(nodeDtos));
        entity.setContractType(dto.getContractType());
        entity.setReviewPosition(dto.getReviewPosition());
        entity.setReviewListOption(dto.getReviewListOption());

        docReviewTaskService.updateById(entity) ;

        return AjaxResult.success();
    }

    /**
     * 获取审核清单列表
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getAuditList")
    public AjaxResult getAuditList(PermissionQuery query){
        List<DocReviewAuditDto> list = reviewAuditService.getAuditList(query) ;
        return AjaxResult.success("操作成功", list);
    }

    /**
     * 生成审核报告信息
     * @param sceneId
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/genDocxReport")
    public AjaxResult genDocxReport(@RequestParam("sceneId") long sceneId ,
                                    @RequestParam("taskId") long taskId ,
                                    PermissionQuery query) {

        DocReviewSceneEntity longTextSceneEntity = service.getBySceneId(sceneId , query) ;
        String markdownContent = service.genMarkdownReport(sceneId , query , longTextSceneEntity.getId() , taskId) ;
        String filename = IdUtil.fastSimpleUUID() ;

        log.debug("markdownContent = {}", markdownContent);
        Assert.notNull(markdownContent, "markdownContent为空") ;

        String filePath = MarkdownToWord.convertMdToDocx(markdownContent, filename) ;
        Assert.notNull(filePath, "文件路径为空") ;

        R<String> r = storageConsumer.upload(new File(filePath)) ;
        String storageId = r.getData() ;

        // 删除文件
        FileUtil.del(filePath);

        return AjaxResult.success("操作成功" , storageId);
    }

    /**
     * 获取预览地址
     * @param storageId
     * @return
     */
    @GetMapping("/getPreviewUrl")
    public AjaxResult getPreviewUrl(@RequestParam String storageId) {
        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
        return AjaxResult.success("操作成功" , previewUrl);
    }

    /**
     * 获取预览文件
     * @param storageId
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getPreviewReportDocx")
    public ResponseEntity<Resource> getPreviewReportDocx(@RequestParam String storageId , Long taskId) {
        String previewUrl = storageConsumer.getPreviewUrl(storageId).getData();
        return GenPdfTool.getResourceResponseEntity(storageId , previewUrl , taskId);
    }

    @Override
    public IDocReviewSceneService getFeign() {
        return this.service;
    }
}