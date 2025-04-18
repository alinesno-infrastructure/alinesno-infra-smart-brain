package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.scene.common.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.dto.DataAnalysisSceneDto;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.dto.DataPromptContentRequestDto;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.IDataAnalysisPlanService;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.IDataAnalysisSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.tools.DataAnalysisFormatMessageTool;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.scene.dto.ChatRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.DataAnalysisSceneEntity;
import com.alinesno.infra.smart.scene.entity.LongTextSceneEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import io.swagger.annotations.Api;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.List;

/**
 * 数据分析场景控制器
 */
@Slf4j
@Api(tags = "数据分析场景")
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/dataAnalysisScene/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class DataAnalysisSceneController extends BaseController<DataAnalysisSceneEntity, IDataAnalysisSceneService> {

    @Autowired
    private IDataAnalysisSceneService service;

    @Autowired
    private IDataAnalysisPlanService dataAnalysisPlanService;

    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private DataAnalysisFormatMessageTool formatMessageTool;

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
     * 通过Id获取到场景
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getScene")
    public AjaxResult getScene(@RequestParam("id") long id , PermissionQuery query) {

        Assert.isTrue(id > 0, "参数不能为空");

        SceneEntity entity = sceneService.getById(id);
        if (entity == null) {
            return AjaxResult.error("未找到对应的场景实体");
        }

        DataAnalysisSceneDto dto = new DataAnalysisSceneDto();
        BeanUtils.copyProperties(entity, dto);

        SceneInfoDto sceneInfoDto = SceneEnum.getSceneInfoByCode(entity.getSceneType());

        dto.setAgents(sceneInfoDto.getAgents());
        dto.setSceneId(sceneInfoDto.getId());

        DataAnalysisSceneEntity dataAnalysisSceneEntity = service.getBySceneId(id, query);
        if(dataAnalysisSceneEntity != null){

            dto.setTextParserEngineer(dataAnalysisSceneEntity.getTextParserEngineer());
            dto.setDataMinerEngineer(dataAnalysisSceneEntity.getDataMinerEngineer());

            dto.setGenStatus(dataAnalysisSceneEntity.getGenStatus());
            dto.setAnalysisPromptContent(dataAnalysisSceneEntity.getAnalysisPromptContent());

            dto.setTextParserEngineers(RoleUtils.getEditors(roleService , dataAnalysisSceneEntity.getTextParserEngineer())); // 查询出当前的数据分析编辑人员
            dto.setDataMinerEngineers(RoleUtils.getEditors(roleService, dataAnalysisSceneEntity.getDataMinerEngineer())); // 查询出当前的内容编辑人员

            dto.setChapterTree(dataAnalysisPlanService.getPlanTree(entity.getId() , dataAnalysisSceneEntity.getId())); // 数据分析树信息
        }

        return AjaxResult.success("操作成功.", dto);
    }


    /**
     * 与角色交互，获取到内容结果
     */
    @DataPermissionSave
    @SneakyThrows
    @PostMapping("/chatRole")
    public AjaxResult chatRole(@RequestBody @Validated ChatRoleDto chatRole) {

        MessageTaskInfo taskInfo = chatRole.toPowerMessageTaskInfo() ; // new MessageTaskInfo() ;

        taskInfo.setRoleId(chatRole.getRoleId());
        taskInfo.setChannelStreamId(chatRole.getChannelStreamId());
        taskInfo.setChannelId(chatRole.getSceneId());
        taskInfo.setSceneId(chatRole.getSceneId());
        taskInfo.setText(chatRole.getMessage());

        // 优先获取到结果内容
        WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;

        // 获取到格式化的内容
        formatMessageTool.handleChapterMessage(genContent , taskInfo);

        // 解析得到代码内容
        if(genContent.getCodeContent() !=null && !genContent.getCodeContent().isEmpty()){
            String codeContent = genContent.getCodeContent().get(0).getContent() ;
            JSONArray dataObject = JSONArray.parseArray(codeContent) ;

            // 验证是否可以正常解析json
            try{
                List<TreeNodeDto> nodeDtos = JSON.parseArray(codeContent, TreeNodeDto.class);
                log.debug("nodeDtos = {}", JSONUtil.toJsonPrettyStr(nodeDtos));
            }catch (Exception e){
                throw new RpcServiceRuntimeException("生成大纲格式不正确，请点击重新生成.") ;
            }

            return AjaxResult.success("操作成功" , dataObject) ;
        }

        return AjaxResult.success("操作成功" , genContent) ;
    }

    /**
     * 更新chapterPromptContent内容
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/updateChapterPromptContent")
    public AjaxResult updateChapterPromptContent(@RequestBody @Validated DataPromptContentRequestDto dto , PermissionQuery query) {

        DataAnalysisSceneEntity sceneEntity = service.getBySceneId(dto.getSceneId() , query) ;
        sceneEntity.setAnalysisPromptContent(dto.getPromptContent());

        service.updateById(sceneEntity);
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 更新生成状态
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/updateSceneGenStatus")
    public AjaxResult updateSceneGenStatus(@RequestParam("sceneId") long sceneId ,
                                           @RequestParam("genStatus") int genStatus,
                                           PermissionQuery query) {

        DataAnalysisSceneEntity sceneEntity = service.getBySceneId(sceneId , query) ;
        sceneEntity.setGenStatus(genStatus);

        service.updateById(sceneEntity);
        return AjaxResult.success("操作成功") ;
    }

    /**
     * 接收JSON数据并保存到数据库
     * @param nodes JSON格式的章节列表
     * @return 操作结果
     */
    @DataPermissionQuery
    @PostMapping("/saveDataPlan")
    public AjaxResult saveChapters(@RequestBody List<TreeNodeDto> nodes, @RequestParam("sceneId") long sceneId , PermissionQuery query) {

        if(nodes == null || nodes.isEmpty()){
            return error("参数错误") ;
        }

        DataAnalysisSceneEntity dataAnalysisSceneEntity = service.getBySceneId(sceneId , query) ;
        dataAnalysisPlanService.saveChaptersWithHierarchy(nodes, null, 1 , sceneId , dataAnalysisSceneEntity.getId());

        return ok() ;
    }

    @Override
    public IDataAnalysisSceneService getFeign() {
        return this.service;
    }
}