package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.controller;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.constants.SpringInstanceScope;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionQuery;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionSave;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.response.AjaxResult;
import com.alinesno.infra.common.facade.response.ResultCodeEnum;
import com.alinesno.infra.common.extend.datasource.annotation.DataPermissionScope;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.common.facade.pageable.TableDataInfo;
import com.alinesno.infra.common.web.adapter.rest.BaseController;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.ContentFormatterLayoutDto;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.DocumentFormatDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.DocumentTemplateDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.DocumentTemplateInfoDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterLayoutGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterLayoutService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.ContentLayoutExcelParser;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutGroupEntity;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 数据分析规划控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/infra/smart/assistant/scene/contentFormatterLayout/")
@Scope(SpringInstanceScope.PROTOTYPE)
public class ContentFormatterLayoutController extends BaseController<ContentFormatterLayoutEntity, IContentFormatterLayoutService> {

    @Autowired
    private IContentFormatterLayoutService service;

    @Autowired
    private IContentFormatterLayoutGroupService layoutGroupService ;

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
        return this.toPage(model, this.getFeign(), page);
    }

    @ApiOperation("查询排版模板列表")
    @GetMapping("/listLayoutTemplates")
    public AjaxResult listLayoutTemplates(ContentFormatterLayoutDto query) {
        log.debug("查询排版模板列表，参数：{}" , query);
        List<ContentFormatterLayoutEntity> list = service.list();
        return AjaxResult.success(list);
    }

    @ApiOperation("查询排版模板详情")
    @DataPermissionQuery
    @GetMapping("/getLayoutTemplate")
    public AjaxResult getLayoutTemplate(@RequestParam Long id) {
        ContentFormatterLayoutEntity entity = service.findById(id);
        ContentFormatterLayoutDto dto = new ContentFormatterLayoutDto() ;
        BeanUtils.copyProperties(entity, dto);

        DocumentTemplateDTO templateDTO = DocumentTemplateDTO.fromContentFormatterLayoutDto(dto);

        return  AjaxResult.success(templateDTO) ;
    }

    @ApiOperation("新增排版模板")
    @DataPermissionSave
    @PostMapping("/addLayoutTemplate")
    public AjaxResult addLayoutTemplate(@RequestBody @Valid DocumentTemplateDTO dto) {

        ContentFormatterLayoutDto layoutDto = dto.toContentFormatterLayoutDto() ;
        ContentFormatterLayoutEntity entity = new ContentFormatterLayoutEntity();
        BeanUtils.copyProperties(layoutDto, entity);

        service.save(entity);
        dto.setId(entity.getId());

        return AjaxResult.success(dto);
    }

    @ApiOperation("修改排版模板")
    @DataPermissionSave
    @PutMapping("/updateLayoutTemplate")
    public AjaxResult updateLayoutTemplate(@RequestBody @Valid DocumentTemplateDTO dto) {

        ContentFormatterLayoutDto layoutDto = dto.toContentFormatterLayoutDto() ;
        ContentFormatterLayoutEntity entity = service.getById(dto.getId()) ;
        BeanUtils.copyProperties(layoutDto, entity);

        service.update(entity);

        return AjaxResult.success();
    }

    @ApiOperation("修改排版模板")
    @DataPermissionSave
    @PutMapping("/updateLayoutInfoTemplate")
    public AjaxResult updateLayoutInfoTemplate(@RequestBody @Valid DocumentTemplateInfoDTO dto) {

        ContentFormatterLayoutEntity entity = service.getById(dto.getId()) ;

        entity.setName(dto.getName());
        entity.setLayoutDesc(dto.getDescription());
        entity.setGroupId(dto.getGroupId());

        service.update(entity);

        return AjaxResult.success();
    }

    @ApiOperation("删除排版模板")
    @DeleteMapping("/delLayoutTemplate")
    public AjaxResult delLayoutTemplate(@RequestParam Long id) {
        service.removeById(id);
        return AjaxResult.success();
    }

    @ApiOperation("预览排版模板")
    @GetMapping("/previewLayoutTemplate")
    public AjaxResult previewLayoutTemplate(@RequestParam Long id) {
        ContentFormatterLayoutEntity entity = service.findById(id);
        if (entity == null) {
            return AjaxResult.error("无法找到资源" , ResultCodeEnum.NOT_FOUND);
        }
        // Here you would typically add preview logic
        return AjaxResult.success(entity.getLayoutConfig());
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

        List<ContentLayoutExcelParser.LayoutBean> templates = ContentLayoutExcelParser.parseExcel(targetFile.getAbsolutePath()) ;
        templates.forEach(System.out::println);

        Map<String, Object> failResult = service.readExcel(templates , query) ;

        FileUtils.forceDeleteOnExit(targetFile);

        return AjaxResult.success("操作成功." , failResult);
    }

    @DataPermissionQuery
    @ApiOperation("查询组织排版模板")
    @GetMapping("/selectOrgLayout")
    public AjaxResult selectOrgLayout(PermissionQuery query) {
        // 1. 查询所有分组
        List<ContentFormatterLayoutGroupEntity> groups = layoutGroupService.listOrgGroup(query);

        // 2. 查询所有模板
        List<Long> groupIds = groups.stream().map(ContentFormatterLayoutGroupEntity::getId).toList();
        List<ContentFormatterLayoutEntity> layouts = service.listByGroupIds(groupIds);

        // 3. 构建分类数据结构
        Map<String, Object> result = new HashMap<>();

        // 构建主分类
        List<Map<String, Object>> categories = groups.stream()
                .map(group -> {
                    Map<String, Object> category = new HashMap<>();
                    category.put("id", group.getId());
                    category.put("name", group.getName());
                    category.put("desc", group.getGroupDesc());
                    category.put("hasChildren", true);
                    category.put("image", group.getIcon());
                    return category;
                })
                .collect(Collectors.toList());

        // 构建子分类映射
        Map<String, List<Map<String, Object>>> subcategories = new HashMap<>();

        for (ContentFormatterLayoutGroupEntity group : groups) {
            List<ContentFormatterLayoutEntity> groupLayouts = layouts.stream()
                    .filter(layout -> group.getId().equals(layout.getGroupId()))
                    .toList();

            List<Map<String, Object>> subList = groupLayouts.stream()
                    .map(layout -> {
                        Map<String, Object> sub = new HashMap<>();
                        sub.put("id", layout.getId());
                        sub.put("name", layout.getName());
                        sub.put("image", layout.getIcon());
                        return sub;
                    })
                    .collect(Collectors.toList());

            subcategories.put(group.getId().toString(), subList);
        }

        result.put("categories", categories);
        result.put("subcategories", subcategories);

        return AjaxResult.success(result);
    }

    /**
     * 文档内容格式化formatContent
     * @return
     */
    @ApiOperation("文档内容格式化")
    @DataPermissionQuery
    @PostMapping("/formatContent")
    public AjaxResult formatContent(@RequestBody @Valid DocumentFormatDTO dto , PermissionQuery query) {
        String newContent = service.formatContent(dto , query) ;
        return AjaxResult.success("格式化成功" , newContent);
    }

    /**
     * 导出文档为docx格式（异步处理，异常直接抛出）
     */
    @DataPermissionQuery
    @SneakyThrows
    @PostMapping("exportDocx")
    public void exportDocx(@RequestBody DocumentFormatDTO dto ,
                           HttpServletResponse response,
                           PermissionQuery query) {
        // 设置响应编码和格式
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        // 处理文件名
        String originalFileName =  "导出文档";
        String fileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".docx\"");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        String content = dto.getContent();

        // 参数校验
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("导出内容不能为空");
        }

        // 调用服务层方法生成docx字节数组
        byte[] docxBytes = service.exportToDocx(content, query);

        if (docxBytes == null || docxBytes.length == 0) {
            throw new RuntimeException("文档生成失败，内容为空");
        }

        // 写入响应流
        try (OutputStream os = response.getOutputStream()) {
            response.setContentLength(docxBytes.length);
            os.write(docxBytes);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException("响应流写入失败", e);
        }
    }


    @Override
    public IContentFormatterLayoutService getFeign() {
        return this.service;
    }
}