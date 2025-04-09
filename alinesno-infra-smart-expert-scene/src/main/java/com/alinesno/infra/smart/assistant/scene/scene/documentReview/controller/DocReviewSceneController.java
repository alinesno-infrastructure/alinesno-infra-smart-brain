package com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewGenReviewDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewInitDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewRulesDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.AnalysisTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.scene.enums.ContractTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private IDocReviewSceneService service;

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
    @PostMapping("/genReviewList")
    public AjaxResult genReviewList(@RequestBody @Validated DocReviewGenReviewDto dto){

        long sceneId = dto.getSceneId() ;
        List<DocReviewRulesDto> rules = new ArrayList<DocReviewRulesDto>() ;

        DocReviewSceneEntity entity = service.getBySceneId(sceneId) ;

        entity.setContractType(dto.getContractType());
        entity.setReviewPosition(dto.getReviewPosition());
        entity.setReviewListKnowledgeBase(dto.getReviewListKnowledgeBase());
        entity.setReviewListOption(dto.getReviewListOption());

        if(dto.getReviewListOption().equals("aigen")){  // 如果是aigen则直接调用agent生成内容

            MessageTaskInfo taskInfo = new MessageTaskInfo() ;

            taskInfo.setRoleId(entity.getAnalysisAgentEngineer());
            taskInfo.setChannelId(sceneId);
            taskInfo.setSceneId(sceneId) ;
            taskInfo.setText("合同类型的审查清单项");

            String documentInfo = """
                    合同名称：%s
                    合同类型：%s
                    合同概述: %s
                    """ ;

            taskInfo.setText(String.format(documentInfo ,
                    entity.getDocumentId() ,
                    ContractTypeEnum.getByScenarioId(entity.getContractType()) ,
                    entity.getContractOverview()));

            // 优先获取到结果内容
            WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;

            // 获取到格式化的内容
            analysisTool.handleFormatRuleMessage(genContent , taskInfo);

            // 解析得到代码内容
            if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
                String codeContent = genContent.getCodeContent().get(0).getContent() ;
                // 验证是否可以正常解析json
                try{
                    List<DocReviewRulesDto> nodeDtos = JSON.parseArray(codeContent, DocReviewRulesDto.class);
                    log.debug("nodeDtos = {}", JSONUtil.toJsonPrettyStr(nodeDtos));
                    for(DocReviewRulesDto d : nodeDtos){
                        d.setId(IdUtil.getSnowflakeNextId());
                    }
                    entity.setReviewList(JSONUtil.toJsonStr(nodeDtos));
                }catch (Exception e){
                    throw new RpcServiceRuntimeException("生成审核规则格式不正确，请点击重新生成.") ;
                }

            }

        }else if(dto.getReviewListOption().equals("knowledgeBase")){

        }

        service.updateById(entity) ;

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

    @Override
    public IDocReviewSceneService getFeign() {
        return this.service;
    }
}