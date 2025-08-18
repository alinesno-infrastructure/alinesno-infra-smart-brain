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
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewGenReviewDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.DocReviewTaskDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.ModifyDocumentDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.dto.UpdateTaskNameDto;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.enums.ReviewRuleGenStatusEnums;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewSceneService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.service.IDocReviewTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReview.tools.DocReviewListGeneratorService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.dto.TaskGeneratorDTO;
import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import com.alinesno.infra.smart.scene.entity.DocReviewSceneEntity;
import com.alinesno.infra.smart.scene.entity.DocReviewTaskEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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

    @Autowired
    private DocReviewListGeneratorService docReviewListGeneratorService ;

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
     * 生成审核清单
     */
    @ScenePointAnnotation(sceneCode = SceneEnum.DOCUMENT_REVIEW)
    @DataPermissionQuery
    @PostMapping("/genReviewList")
    public AjaxResult genReviewList(@RequestBody @Validated DocReviewGenReviewDto dto, PermissionQuery query) {

        // 如果任务的状态是在生成中，则返回正在生成中
        DocReviewTaskEntity entity = service.getById(dto.getTaskId());

        if (ReviewRuleGenStatusEnums.GENERATING.getCode().equals(entity.getReviewGenStatus())) {
            return AjaxResult.error("任务正在生成中，请稍后再试");
        }

        if (ReviewRuleGenStatusEnums.SUCCESS.getCode().equals(entity.getReviewGenStatus())) {
            return AjaxResult.success("任务已生成，请勿重复生成");
        }

        docReviewListGeneratorService.startGenerateReviewList(dto, query) ;

        return AjaxResult.success();
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

        return AjaxResult.success("操作成功." , DocReviewTaskDto.fromEntity(entity));
    }

    /**
     * 保存修改后的文档
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/saveDocumentContent")
    public AjaxResult saveDocument(@Validated @RequestBody ModifyDocumentDto dto) {

        DocReviewTaskEntity entity = service.getById(dto.getTaskId());
        entity.setHtmlContent(dto.getHtmlContent());
        service.update(entity);

        return AjaxResult.success("保存成功");
    }

    /**
     * 下载文档
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/downloadDocument")
    public void downloadDocument(@RequestParam Long taskId , HttpServletResponse response , PermissionQuery query) {
        try {

            DocReviewTaskEntity entity = service.getById(taskId);
            String content = entity.getHtmlContent();

            // 2. 调用服务进行转换
            byte[] result = content.getBytes(StandardCharsets.UTF_8) ;

            // 3. 设置响应头
            // 创建日期时间格式化器
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timeStamp = dateFormat.format(new Date());

            // 构建最终文件名
            String realFileName = String.format("%s_%s.docx", taskId, timeStamp);

            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(realFileName, StandardCharsets.UTF_8));
            response.setContentLength(result.length);

            // 4. 写入响应流
            try (OutputStream out = response.getOutputStream()) {
                out.write(result);
                out.flush();
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            try {
                response.getWriter().write("文件下载失败: " + e.getMessage());
            } catch (IOException ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }

    /**
     * 更新文档名称
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/updateDocumentName")
    public AjaxResult updateDocumentName(@RequestBody @Validated UpdateTaskNameDto dto , PermissionQuery query) {

        DocReviewTaskEntity entity = service.getById(dto.getTaskId());
        entity.setTaskName(dto.getTaskName());
        service.update(entity);

        return AjaxResult.success("更新成功");
    }

    @Override
    public IDocReviewTaskService getFeign() {
        return this.service;
    }
}