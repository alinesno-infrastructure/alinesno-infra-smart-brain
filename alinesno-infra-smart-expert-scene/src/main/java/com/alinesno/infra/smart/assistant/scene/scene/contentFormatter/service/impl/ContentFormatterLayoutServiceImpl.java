package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools.ImageCompressionUtil;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.consumer.SmartDocumentConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.dto.DocumentFormatDTO;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums.GroupTypeEnums;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterLayoutGroupMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterLayoutMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.mapper.ContentFormatterOfficeConfigMapper;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterLayoutService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.service.IContentFormatterOfficeConfigService;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.ContentLayoutExcelData;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.ContentLayoutExcelParser;
import com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.tools.HtmlUtils;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterLayoutGroupEntity;
import com.alinesno.infra.smart.scene.entity.ContentFormatterOfficeConfigEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContentFormatterLayoutServiceImpl extends IBaseServiceImpl<ContentFormatterLayoutEntity, ContentFormatterLayoutMapper> implements IContentFormatterLayoutService {

    @Autowired
    private ContentFormatterLayoutGroupMapper groupMapper;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer;

    @Autowired
    private IContentFormatterOfficeConfigService officeConfigService;

    @Override
    public Map<String, Object> readExcel(List<ContentLayoutExcelParser.LayoutBean> layouts, PermissionQuery query) {
        List<ContentLayoutExcelData> failedList = new ArrayList<>();
        List<ContentFormatterLayoutEntity> successList = new ArrayList<>();
        Map<Long, List<ContentFormatterLayoutEntity>> groupLayoutMap = new HashMap<>();

        try {
            // 1. 检查导入数据中的重复排版名称
            checkDuplicateLayoutNamesInImport(layouts, failedList);
            if (!failedList.isEmpty()) {
                return buildResult(successList, failedList, groupLayoutMap);
            }

            // 2. 按分组处理数据
            List<ContentLayoutExcelParser.LayoutGroup> fullGroups = ContentLayoutExcelParser.getLayoutGroups(layouts);

            for (ContentLayoutExcelParser.LayoutGroup group : fullGroups) {
                try {
                    // 检查分组是否已存在
                    LambdaQueryWrapper<ContentFormatterLayoutGroupEntity> queryGroupWrapper = new LambdaQueryWrapper<>();
                    queryGroupWrapper.eq(ContentFormatterLayoutGroupEntity::getName, group.getGroupName())
                            .eq(ContentFormatterLayoutGroupEntity::getGroupType, GroupTypeEnums.LAYOUT.getValue())
                            .eq(ContentFormatterLayoutGroupEntity::getDataScope, SceneScopeType.ORG_SCENE.getValue())
                            .eq(ContentFormatterLayoutGroupEntity::getOrgId, query.getOrgId());

                    ContentFormatterLayoutGroupEntity existingGroup = groupMapper.selectOne(queryGroupWrapper);

                    ContentFormatterLayoutGroupEntity layoutGroup;
                    if (existingGroup != null) {
                        layoutGroup = existingGroup;
                        log.info("使用已存在的分组: {}", group.getGroupName());
                    } else {
                        layoutGroup = processLayoutGroup(group, query);
                    }

                    // 处理排版配置
                    List<ContentFormatterLayoutEntity> layoutEntities = processLayoutsWithDuplicateCheck(group, layoutGroup, failedList, query);

                    groupLayoutMap.put(layoutGroup.getId(), layoutEntities);
                    successList.addAll(layoutEntities);
                } catch (Exception e) {
                    addGroupToFailedList(group, failedList, "分组处理失败: " + e.getMessage());
                    log.error("分组处理失败: {}", group.getGroupName(), e);
                }
            }

            // 批量保存成功的排版配置
            if (!successList.isEmpty()) {
                saveBatch(successList);
            }
        } catch (Exception e) {
            ContentLayoutExcelData systemError = new ContentLayoutExcelData();
            systemError.setFailReason(Map.of(
                    "error", "系统级错误: " + e.getClass().getSimpleName(),
                    "message", e.getMessage() != null ? e.getMessage() : "无详细错误信息",
                    "stacktrace", getSimplifiedStackTrace(e)
            ));
            failedList.add(systemError);
            log.error("Excel导入系统错误", e);
        }

        return buildResult(successList, failedList, groupLayoutMap);
    }

    @Override
    public List<ContentFormatterLayoutEntity> listByGroupIds(List<Long> groupIds) {
        LambdaQueryWrapper<ContentFormatterLayoutEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ContentFormatterLayoutEntity::getGroupId, groupIds);
        return list(queryWrapper);
    }

    @Override
    public String formatContent(DocumentFormatDTO content, PermissionQuery query) {
        File tempHtmlFile = null;
        File tempDocxFile = null;
        File formattedDocxFile = null;

        try {
            // 1. 获取办公工具配置
            ContentFormatterOfficeConfigEntity officeConfig = officeConfigService.getConfig(query.getOrgId());
            if (officeConfig == null) {
                throw new RuntimeException("未找到机构[" + query.getOrgId() + "]的办公工具配置");
            }

            // 2. 初始化智能文档处理器
            SmartDocumentConsumer smartDocumentConsumer = new SmartDocumentConsumer();
            smartDocumentConsumer.configure(officeConfig.getToolPath(), officeConfig.getRequestToken());

            // 3. 将HTML内容保存为临时文件
            String htmlContent = HtmlUtils.ensureHtmlStructure(content.getContent());
            tempHtmlFile = File.createTempFile("temp_input_", ".html");
            Files.write(tempHtmlFile.toPath(), htmlContent.getBytes(StandardCharsets.UTF_8));

            // 4. 将HTML转换为DOCX
            tempDocxFile = File.createTempFile("temp_converted_", ".docx");
            ResponseEntity<byte[]> convertResponse = smartDocumentConsumer.convertHtmlToDocx(tempHtmlFile);
            if (!convertResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("HTML转DOCX失败，状态码：" + convertResponse.getStatusCode());
            }
            // 假设返回的是文件内容，需要写入临时文件
            Files.write(tempDocxFile.toPath(), Objects.requireNonNull(convertResponse.getBody()));

            // 5. 将DOCX格式化为公文格式
            formattedDocxFile = File.createTempFile("temp_formatted_", ".docx");
            ResponseEntity<byte[]> formatResponse = smartDocumentConsumer.formatOfficialDocument(tempDocxFile);
            if (!formatResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("公文格式化失败，状态码：" + formatResponse.getStatusCode());
            }
            // 假设返回的是文件内容，需要写入临时文件
            Files.write(formattedDocxFile.toPath(), Objects.requireNonNull(formatResponse.getBody())) ;

            // 6. 将格式化后的DOCX转回HTML
            ResponseEntity<String> finalResponse = smartDocumentConsumer.convertToHtml(formattedDocxFile);
            if (!finalResponse.getStatusCode().is2xxSuccessful() || finalResponse.getBody() == null) {
                throw new RuntimeException("最终HTML转换失败，状态码：" + finalResponse.getStatusCode());
            }

            return finalResponse.getBody();

        } catch (IOException e) {
            throw new RuntimeException("处理文档时发生IO异常", e);
        } catch (Exception e) {
            throw new RuntimeException("文档格式化处理失败", e);
        } finally {
            // 7. 清理所有临时文件
//            try {
//                if (tempHtmlFile != null && !tempHtmlFile.delete()) {
//                    log.warn("临时HTML文件删除失败：" + tempHtmlFile.getAbsolutePath());
//                }
//                if (tempDocxFile != null && !tempDocxFile.delete()) {
//                    log.warn("临时DOCX文件删除失败：" + tempDocxFile.getAbsolutePath());
//                }
//                if (formattedDocxFile != null && !formattedDocxFile.delete()) {
//                    log.warn("格式化后DOCX文件删除失败：" + formattedDocxFile.getAbsolutePath());
//                }
//            } catch (Exception e) {
//                log.error("删除临时文件时出错", e);
//            }

           // 打印出所有的问题
           log.info("tempHtmlFile = {}" , tempHtmlFile);
           log.info("tempDocxFile = {}" , tempDocxFile);
           log.info("formattedDocxFile = {}" , formattedDocxFile);

        }
    }

    /**
     * 检查导入数据中的重复排版名称
     */
    private void checkDuplicateLayoutNamesInImport(List<ContentLayoutExcelParser.LayoutBean> layouts, List<ContentLayoutExcelData> failedList) {
        Map<String, Long> nameCount = layouts.stream()
                .collect(Collectors.groupingBy(ContentLayoutExcelParser.LayoutBean::getLayoutName, Collectors.counting()));

        nameCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> {
                    String errorMsg = "导入数据中存在重复排版名称: " + entry.getKey();
                    layouts.stream()
                            .filter(l -> l.getLayoutName().equals(entry.getKey()))
                            .forEach(l -> failedList.add(createFailedRecord(l, errorMsg)));
                });
    }

    /**
     * 处理分组信息
     */
    private ContentFormatterLayoutGroupEntity processLayoutGroup(ContentLayoutExcelParser.LayoutGroup group, PermissionQuery query) {
        ContentFormatterLayoutGroupEntity layoutGroup = ContentFormatterLayoutGroupEntity.toPower(query);

        BeanUtil.copyProperties(query, layoutGroup);
        layoutGroup.setName(group.getGroupName());
        layoutGroup.setGroupDesc(group.getGroupDesc());
        layoutGroup.setGroupType(GroupTypeEnums.LAYOUT.getValue()); // 排版分类
        layoutGroup.setSort(0);
        layoutGroup.setDataScope(SceneScopeType.ORG_SCENE.getValue());

        // 处理分组图标
        if (group.getGroupIcon() != null && !group.getGroupIcon().isEmpty()) {
            try {
                String iconId = getIconId(group.getGroupIcon());
                layoutGroup.setIcon(iconId);
            } catch (Exception e) {
                log.warn("分组图标处理失败: {}", e.getMessage());
            }
        }

        groupMapper.insert(layoutGroup);
        return layoutGroup;
    }

    /**
     * 处理排版配置（带重复检查）
     */
    private List<ContentFormatterLayoutEntity> processLayoutsWithDuplicateCheck(
            ContentLayoutExcelParser.LayoutGroup group,
            ContentFormatterLayoutGroupEntity layoutGroup,
            List<ContentLayoutExcelData> failedList,
            PermissionQuery query) {

        return group.getLayouts().stream()
                .map(layout -> {
                    try {
                        // 检查是否已存在同名排版配置
                        LambdaQueryWrapper<ContentFormatterLayoutEntity> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(ContentFormatterLayoutEntity::getName, layout.getLayoutName())
                                .eq(ContentFormatterLayoutEntity::getGroupId, layoutGroup.getId());
                        ContentFormatterLayoutEntity existingLayout = mapper.selectOne(queryWrapper);
                        if (existingLayout != null) {
                            failedList.add(createFailedRecord(layout, "排版配置已存在: " + layout.getLayoutName()));
                            return null;
                        }

                        return createLayoutEntity(layout, layoutGroup, query);
                    } catch (Exception e) {
                        failedList.add(createFailedRecord(layout, "排版配置处理失败: " + e.getMessage()));
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 创建排版配置实体
     */
    private ContentFormatterLayoutEntity createLayoutEntity(
            ContentLayoutExcelParser.LayoutBean layout,
            ContentFormatterLayoutGroupEntity layoutGroup,
            PermissionQuery query) {

        ContentFormatterLayoutEntity layoutEntity = new ContentFormatterLayoutEntity();
        BeanUtil.copyProperties(query, layoutEntity);

        layoutEntity.setGroupId(layoutGroup.getId());
        layoutEntity.setName(layout.getLayoutName());
        layoutEntity.setLayoutDesc(layout.getLayoutDesc());
        layoutEntity.setLayoutConfig(layout.getLayoutConfig());

        // 处理图标字段
        if (layout.getTemplateIcon() != null && !layout.getTemplateIcon().isEmpty()) {
            try {
                String iconId = getIconId(layout.getTemplateIcon());
                layoutEntity.setIcon(iconId);
            } catch (Exception e) {
                log.warn("排版图标处理失败: {}", e.getMessage());
            }
        }

        // 处理排序字段
        try {
            layoutEntity.setSort(Integer.parseInt(layout.getSort()));
        } catch (NumberFormatException e) {
            layoutEntity.setSort(0);
        }

        return layoutEntity;
    }

    /**
     * 构建返回结果
     */
    private Map<String, Object> buildResult(
            List<ContentFormatterLayoutEntity> successList,
            List<ContentLayoutExcelData> failedList,
            Map<Long, List<ContentFormatterLayoutEntity>> groupLayoutMap) {

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successList.size());
        result.put("failedCount", failedList.size());
        result.put("failedList", failedList);
        result.put("groupLayoutMap", groupLayoutMap);
        return result;
    }

    /**
     * 将失败的分组添加到失败列表
     */
    private void addGroupToFailedList(
            ContentLayoutExcelParser.LayoutGroup group,
            List<ContentLayoutExcelData> failedList,
            String errorMessage) {

        for (ContentLayoutExcelParser.LayoutBean layout : group.getLayouts()) {
            failedList.add(createFailedRecord(layout, errorMessage));
        }
    }

    /**
     * 创建失败记录
     */
    private ContentLayoutExcelData createFailedRecord(ContentLayoutExcelParser.LayoutBean layout, String errorMessage) {
        ContentLayoutExcelData failedRecord = new ContentLayoutExcelData();
        failedRecord.setLayoutName(layout.getLayoutName());
        failedRecord.setFailReason(Map.of(
                "error", errorMessage,
                "layoutName", layout.getLayoutName(),
                "groupName", layout.getGroupName()
        ));
        return failedRecord;
    }

    /**
     * 获取简化的堆栈跟踪
     */
    private String getSimplifiedStackTrace(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * 处理图标文件并返回文件ID
     */
    private String getIconId(String imageUrl) {
        String tempFilePath = null;
        String fileId = null;
        try {
            tempFilePath = ImageCompressionUtil.compressAndSaveToTemp(imageUrl);
            // 上传到云存储中，并返回文件id
            fileId = cloudStorageConsumer.upload(new File(tempFilePath)).getData();
        } catch (IOException e) {
            throw new RuntimeException("图标处理失败", e);
        } finally {
            // 删除临时文件
            if (tempFilePath != null) {
                FileUtils.deleteQuietly(new File(tempFilePath));
            }
        }
        return fileId;
    }
}