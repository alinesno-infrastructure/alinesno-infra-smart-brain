package com.alinesno.infra.smart.assistant.scene.scene.documentReview.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto.TaskGeneratorDTO;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTaskService;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentSceneEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTaskEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
@RequestMapping("/api/infra/smart/assistant/scene/docReviewTaskManager")
public class ReviewTaskManagerController extends BaseController<DocReviewTaskEntity, IDocReviewTaskService> {

    @Autowired
    private IDocReviewTaskService service;

    @Autowired
    private IDocReviewSceneService docReviewSceneService ;

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
     * 更新chapterPromptContent内容
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/addTask")
    public AjaxResult addTask(@RequestBody @Validated TaskGeneratorDTO dto , PermissionQuery query) {

        DocReviewSceneEntity sceneEntity = docReviewSceneService.getBySceneId(dto.getSceneId() , query) ;

        DocReviewTaskEntity taskEntity = new DocReviewTaskEntity() ;
        BeanUtil.copyProperties(query , taskEntity);

        taskEntity.setTaskName(dto.getPromptText());
        taskEntity.setTaskDescription(dto.getPromptText());
        taskEntity.setSceneId(dto.getSceneId());
        taskEntity.setDocumentReviewSceneId(sceneEntity.getId());
        taskEntity.setTaskStartTime(new Date());
        taskEntity.setGenStatus(0);

        if(dto.getAttachments() != null){
            String attachments = dto.getAttachments().stream().map(String::valueOf).collect(Collectors.joining(",")) ;
            taskEntity.setDocumentId(attachments); // 附件ID号，以逗号进行分隔
        }

        service.save(taskEntity);

        return AjaxResult.success("操作成功" , taskEntity.getId()) ;
    }

    /**
     * 分页查询试题(pagerListByPage)
     */
    @DataPermissionQuery
    @PostMapping("/pagerListByPage")
    public AjaxResult pagerListByPage(DatatablesPageBean page, PermissionQuery query , Long sceneId) {
        List<DocReviewTaskEntity> list = service.pagerListByPage(page, query , sceneId);
        return AjaxResult.success("操作成功." ,list);
    }

    /**
     * 通过任务id获取到任务详情
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getReviewTask")
    public AjaxResult getReviewTask(@RequestParam Long taskId , PermissionQuery query) {

        Assert.notNull(taskId, "任务ID不能为空");

        LambdaQueryWrapper<DocReviewTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocReviewTaskEntity::getId , taskId);
        wrapper.eq(DocReviewTaskEntity::getOrgId, query.getOrgId());

        DocReviewTaskEntity entity = service.getOne(wrapper) ;
        return AjaxResult.success("操作成功." ,entity);
    }

    @Override
    public IDocReviewTaskService getFeign() {
        return this.service;
    }
}