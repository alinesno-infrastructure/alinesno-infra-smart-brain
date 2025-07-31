package com.alinesno.infra.smart.assistant.scene.scene.examPaper.controller;

import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamineeAddDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamineeImportDTO;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ExamineeImportParam;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto.ImportResult;
import com.alinesno.infra.smart.assistant.scene.scene.examPaper.service.IExamineeService;
import com.alinesno.infra.smart.scene.entity.ExamineeEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考生管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/examinee")
public class ExamineeController extends BaseController<ExamineeEntity, IExamineeService> {

    @Autowired
    private IExamineeService service;

    // DataTables 接口保持不变
    @DataPermissionScope
    @ResponseBody
    @PostMapping("/datatables")
    public TableDataInfo datatables(HttpServletRequest request, Model model, DatatablesPageBean page) {
        log.debug("page = {}", ToStringBuilder.reflectionToString(page));
        return this.toPage(model, this.getFeign(), page);
    }

    /**
     * 添加考生
     */
    @DataPermissionSave
    @PostMapping("/addExaminee")
    public AjaxResult addExaminee(@Validated @RequestBody ExamineeAddDTO dto) {
        ExamineeEntity entity = new ExamineeEntity();

        BeanUtils.copyProperties(dto, entity);

        service.save(entity);
        return AjaxResult.success("考生添加成功");
    }

    /**
     * 导入考生
     */
    @DataPermissionSave
    @PostMapping("/importExaminee")
    public AjaxResult importExaminee(@RequestBody @Valid ExamineeImportParam importParam) {
        try {
            // 解析文本数据
            List<ExamineeImportDTO> importList = parseImportData(importParam.getData(), importParam.getGroupId());

            // 调用Service层导入
            ImportResult result = service.importExaminees(importList, importParam.getSceneId() , importParam);

            if (result.hasDuplicates()) {
                return AjaxResult.success("存在重复考生编号", result);
            }

            return AjaxResult.success("成功导入 " + result.getSuccessCount() + " 条考生数据");
        } catch (Exception e) {
            log.error("导入考生失败", e);
            return AjaxResult.error("导入失败: " + e.getMessage());
        }
    }

    private List<ExamineeImportDTO> parseImportData(String textData, Long groupId) {
        return Arrays.stream(textData.split("\n"))
                .filter(StringUtils::isNotBlank)
                .map(line -> {
                    String[] parts = line.split("\\|");
                    if (parts.length < 2) {
                        throw new RpcServiceRuntimeException("数据格式错误，应为: 学号|姓名|手机号");
                    }

                    ExamineeImportDTO dto = new ExamineeImportDTO();
                    dto.setExamineeId(parts[0].trim());
                    dto.setName(parts[1].trim());
                    dto.setPhone(parts.length > 2 ? parts[2].trim() : null);
                    dto.setGroupId(groupId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 删除考生
     */
    @DeleteMapping("/deleteExaminee")
    public AjaxResult deleteExaminee(@RequestParam List<Long> ids) {
        service.removeBatchByIds(ids);
        return AjaxResult.success("考生删除成功");
    }

    /**
     * 更新考生信息
     */
    @DataPermissionSave
    @PutMapping("/updateExaminee")
    public AjaxResult updateExaminee(@Validated @RequestBody ExamineeAddDTO dto) {
        ExamineeEntity entity = service.getById(dto.getId()) ;
        BeanUtils.copyProperties(dto, entity);
        service.update(entity);
        return AjaxResult.success("考生信息更新成功");
    }

    @DeleteMapping("/deleteExamineeBatch")
    public AjaxResult deleteExamineeBatch(@RequestParam String ids) {

        List<Long> idsList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();

        service.removeBatchByIds(idsList);

        return AjaxResult.success("批量删除任务已提交");
    }

    /**
     * 配置考生分组
     */
    @PostMapping("/configExamineeGroup")
    public AjaxResult configExamineeGroup(@RequestParam("examineeId") Long examineeId,
                                          @RequestParam("groupId") Long groupId) {
        return AjaxResult.success("考生分组配置成功");
    }

    @Override
    public IExamineeService getFeign() {
        return this.service;
    }
}