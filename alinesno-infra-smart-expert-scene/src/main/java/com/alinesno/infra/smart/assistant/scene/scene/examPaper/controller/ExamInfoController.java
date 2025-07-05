package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamInfoPage;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamInfoService;
import com.alinesno.infra.smart.scene.entity.ExamInfoEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/infra/smart/assistant/scene/examInfo")
public class ExamInfoController extends BaseController<ExamInfoEntity, IExamInfoService> {

    @Autowired
    private IExamInfoService service;

    /**
     * 添加考试信息
     * @param examInfoDto 考试信息DTO
     * @return 操作结果
     */
    @DataPermissionSave
    @PostMapping("/addExamInfo")
    public AjaxResult addExamInfo(@RequestBody @Valid ExamInfoDto examInfoDto) {

        // 检查时间逻辑
        if (examInfoDto.getStartTime().isAfter(examInfoDto.getEndTime())) {
            return AjaxResult.error("考试开始时间不能晚于结束时间");
        }

        ExamInfoEntity examInfoEntity = new ExamInfoEntity();
        BeanUtils.copyProperties(examInfoDto, examInfoEntity);

        // 设置其他必要字段
        examInfoEntity.setPagerId(examInfoDto.getPaperId());
        examInfoEntity.setExamStatus(0); // 默认未开始

        return service.save(examInfoEntity) ? AjaxResult.success("考试信息添加成功") : AjaxResult.error("考试信息添加失败");
    }

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
     * 分页查询考试信息
     * @return
     */
    @DataPermissionQuery
    @PostMapping("/queryExamList")
    public TableDataInfo queryExamList(@RequestBody @Valid ExamInfoPage examInfoPage , PermissionQuery query) {
        return service.queryExamList(examInfoPage , query);
    }


    @Override
    public IExamInfoService getFeign() {
        return this.service;
    }
}
