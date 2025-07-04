package com.alinesno.infra.smart.assistant.scene.scene.longtext.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTemplateGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTemplateService;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.tools.RobustExcelParser;
import com.alinesno.infra.smart.scene.entity.LongTextTemplateEntity;
import com.alinesno.infra.smart.scene.entity.LongTextTemplateGroupEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/infra/smart/assistant/scene/longTextTemplate")
public class LongTextTemplateController extends BaseController<LongTextTemplateEntity, ILongTextTemplateService> {

    @Autowired
    private ILongTextTemplateService service;

    @Autowired
    private ILongTextTemplateGroupService longTextTemplateGroupService;

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath  ;

    /**
     * 获取BusinessLogEntity的DataTables数据。
     *
     * @param request HttpServletRequest对象。
     * @param model   Model对象。
     * @param page    DatatablesPageBean对象。
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
     * 获取所有文章分类
     *
     * @return
     */
    @DataPermissionQuery
    @GetMapping("/getAllLongTextTemplate")
    public AjaxResult getAllLongTextTemplate(PermissionQuery query) {
        LambdaQueryWrapper<LongTextTemplateGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(LongTextTemplateGroupEntity.class);
        query.toWrapper(queryWrapper);
        queryWrapper.eq(LongTextTemplateGroupEntity::getHasDelete, 0);

        List<LongTextTemplateGroupEntity> allLongTextTemplateGroup = longTextTemplateGroupService.list(queryWrapper);
        return AjaxResult.success(allLongTextTemplateGroup) ;
    }

    /**
     * 获取模板详情
     */
    @GetMapping("/getTemplateDetail")
    public AjaxResult getTemplateDetail(@RequestParam(value = "templateId") Long templateId) {
        return AjaxResult.success(service.getById(templateId));
    }

    /**
     * 获取所有的模板
     *
     * @param query
     * @return
     * @throws Exception
     */
    @DataPermissionQuery
    @GetMapping("/getTemplateByType")
    public AjaxResult getTemplateByType(PermissionQuery query , String typeCode) throws Exception {
        return AjaxResult.success(service.getTemplateByType(query , typeCode));
    }

    @DataPermissionSave
    @ResponseBody
    @PostMapping("/saveLongTextTemplate")
    public AjaxResult saveLongTextTemplate(@RequestBody LongTextTemplateEntity entity) throws Exception {
        service.save(entity);
        return this.ok();
    }

    /**
     * 文件上传
     * @return
     */
    @SneakyThrows
    @DataPermissionQuery
    @PostMapping("/importData")
    public AjaxResult importData(@RequestPart("file") MultipartFile file, String type , String updateSupport , PermissionQuery query){

        // 新生成的文件名称
        String fileSuffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String newFileName = IdUtil.getSnowflakeNextId() + "." + fileSuffix;

        // 复制文件
        File targetFile = new File(localPath , newFileName);
        FileUtils.writeByteArrayToFile(targetFile, file.getBytes());

        log.debug("newFileName = {} , targetFile = {}" , newFileName , targetFile.getAbsoluteFile());

        List<RobustExcelParser.TemplateBean> templates = RobustExcelParser.parseExcel(targetFile.getAbsolutePath()) ;
        templates.forEach(System.out::println);

        Map<String, Object> failResult = service.readExcel(templates , query) ;

        FileUtils.forceDeleteOnExit(targetFile);

        return AjaxResult.success("操作成功." , failResult);
    }

    @Override
    public ILongTextTemplateService getFeign() {
        return this.service;
    }
}