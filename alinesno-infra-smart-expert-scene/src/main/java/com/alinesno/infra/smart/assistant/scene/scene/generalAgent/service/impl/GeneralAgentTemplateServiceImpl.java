package com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.enums.ArticleCategoryEnums;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools.ImageCompressionUtil;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.mapper.IGeneralAgentTemplateMapper;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTemplateGroupService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.service.IGeneralAgentTemplateService;
import com.alinesno.infra.smart.assistant.scene.scene.generalAgent.tools.GeneralRobustExcelParser;
import com.alinesno.infra.smart.assistant.scene.scene.longtext.tools.LongTextExcelData;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTemplateEntity;
import com.alinesno.infra.smart.scene.entity.GeneralAgentTemplateGroupEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class GeneralAgentTemplateServiceImpl extends IBaseServiceImpl<GeneralAgentTemplateEntity, IGeneralAgentTemplateMapper> implements IGeneralAgentTemplateService {

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer;

    @Autowired
    private IGeneralAgentTemplateGroupService generalAgentTemplateGroupService ;

    @Override
    public List<GeneralAgentTemplateEntity> getTemplateByType(PermissionQuery query, String typeCode) {

        Long orgId = query.getOrgId();

        // 过滤出来的数据是
        // - 类型为公共的 PUBLIC_SCENE
        // - 类型为ORG_SCENE，而且等于orgId的
        LambdaQueryWrapper<GeneralAgentTemplateEntity> wrapper = new LambdaQueryWrapper<>();

        // 先构建 OR 条件（PUBLIC_SCENE 或 ORG_SCENE+orgId）
        wrapper.and(w -> w
                .eq(GeneralAgentTemplateEntity::getLongTextTemplatePermission, SceneScopeType.PUBLIC_SCENE.getValue())
                .or()
                .eq(GeneralAgentTemplateEntity::getLongTextTemplatePermission, SceneScopeType.ORG_SCENE.getValue())
                .eq(GeneralAgentTemplateEntity::getOrgId, orgId)
        );

        // 再添加 typeCode 条件（如果有）
        if (typeCode != null && !typeCode.isEmpty()) {
            wrapper.eq(GeneralAgentTemplateEntity::getTemplateGroupId, typeCode);
        } else {
            wrapper.last("LIMIT 100");
        }


        wrapper.orderByDesc(GeneralAgentTemplateEntity::getAddTime);

        // 执行查询并返回结果
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> readExcel(List<GeneralRobustExcelParser.TemplateBean> templates, PermissionQuery query) {
        // 初始化失败列表和成功列表
        List<LongTextExcelData> failedList = new ArrayList<>();
        List<GeneralAgentTemplateEntity> successList = new ArrayList<>();
        // 用于保存分组和模板的映射关系
        Map<Long, List<GeneralAgentTemplateEntity>> groupTemplateMap = new HashMap<>();

        try {
            // 获取分组后的模板数据
            List<GeneralRobustExcelParser.TemplateGroup> fullGroups = GeneralRobustExcelParser.getTemplateGroups(templates);

            // 遍历每个分组
            for (GeneralRobustExcelParser.TemplateGroup group : fullGroups) {
                try {
                    // 处理分组信息并保存到数据库
                    GeneralAgentTemplateGroupEntity templateGroup = processTemplateGroup(group , query);

                    // 处理当前分组下的所有模板
                    List<GeneralAgentTemplateEntity> templateEntities = processTemplates(group, templateGroup, failedList , query);

                    // 保存分组和模板的映射关系
                    groupTemplateMap.put(templateGroup.getId(), templateEntities);
                    // 添加到成功列表
                    successList.addAll(templateEntities);
                } catch (Exception e) {
                    // 如果分组处理失败，将该分组下所有模板标记为失败
                    addGroupToFailedList(group, failedList, "分组处理失败: " + e.getMessage());
                }
            }

            // 批量保存成功的模板记录
            if (!successList.isEmpty()) {
                saveBatch(successList);
            }
        } catch (Exception e) {
            // 处理顶层异常
            // 处理顶层异常
            LongTextExcelData systemError = new LongTextExcelData();
            systemError.setFailReason(Map.of(
                    "error", "系统级错误: " + e.getClass().getSimpleName(),
                    "message", e.getMessage() != null ? e.getMessage() : "无详细错误信息",
                    "stacktrace", getSimplifiedStackTrace(e)
            ));
            failedList.add(systemError);
            log.error("Excel导入系统错误", e);  // 记得记录完整日志
        }

        // 返回处理结果
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successList.size());  // 成功数量
        result.put("failedCount", failedList.size());    // 失败数量
        result.put("failedList", failedList);            // 失败明细
        result.put("groupTemplateMap", groupTemplateMap); // 分组映射(可选)

        return result;
    }

    private String getSimplifiedStackTrace(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .limit(5)  // 只取前5行堆栈
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }

    /**
     * 处理模板分组信息
     *
     * @param group 模板分组数据
     * @param query
     * @return 保存后的分组实体
     */
    private GeneralAgentTemplateGroupEntity processTemplateGroup(GeneralRobustExcelParser.TemplateGroup group, PermissionQuery query) {
        GeneralAgentTemplateGroupEntity templateGroup = new GeneralAgentTemplateGroupEntity();
        BeanUtil.copyProperties(query, templateGroup);
        templateGroup.setSort(Integer.valueOf(group.getSerialNumber()));
        templateGroup.setIcon(group.getTypeIcon());
        templateGroup.setName(group.getType());       // 设置分组名称
        templateGroup.setFieldProp(group.getTypeCode()); // 设置分组编码
        generalAgentTemplateGroupService.save(templateGroup); // 保存分组
        return templateGroup;
    }

    /**
     * 处理分组下的模板数据
     *
     * @param group         模板分组
     * @param templateGroup 分组实体
     * @param failedList    失败列表(用于收集错误信息)
     * @param query
     * @return 处理成功的模板实体列表
     */
    private List<GeneralAgentTemplateEntity> processTemplates(GeneralRobustExcelParser.TemplateGroup group,
                                                          GeneralAgentTemplateGroupEntity templateGroup,
                                                          List<LongTextExcelData> failedList,
                                                          PermissionQuery query) {
        List<GeneralAgentTemplateEntity> templateEntityList = new ArrayList<>();

        // 遍历分组下的每个模板
        for (GeneralRobustExcelParser.TemplateBean template : group.getTemplates()) {
            try {
                // 创建模板实体
                GeneralAgentTemplateEntity templateEntity = createTemplateEntity(template, templateGroup , query);
                templateEntityList.add(templateEntity);
            } catch (Exception e) {
                // 模板处理失败，添加到失败列表
                failedList.add(createFailedRecord(template,
                        "模板处理失败: " + e.getMessage()));
            }
        }

        return templateEntityList;
    }

    /**
     * 创建模板实体对象
     *
     * @param template      模板数据
     * @param templateGroup 所属分组
     * @param query
     * @return 模板实体
     */
    private GeneralAgentTemplateEntity createTemplateEntity(GeneralRobustExcelParser.TemplateBean template,
                                                        GeneralAgentTemplateGroupEntity templateGroup,
                                                        PermissionQuery query) {
        GeneralAgentTemplateEntity templateEntity = new GeneralAgentTemplateEntity();
        BeanUtil.copyProperties(query, templateEntity);
        templateEntity.setTemplateGroupId(String.valueOf(templateGroup.getId())); // 设置分组ID
        templateEntity.setLongTextTemplatePermission(template.getPermission()); // 设置权限

        templateEntity.setIcon(getIconId(template.getIcon()));       // 设置图标

        templateEntity.setName(template.getTemplateName()); // 设置模板名称
        templateEntity.setDescription(template.getTemplateDescription()); // 设置描述
        templateEntity.setPrompt(template.getTemplateContent()); // 设置模板内容
        return templateEntity;
    }

    private String getIconId(String imageUrl) {
        String tempFilePath = null;
        String fileId = null;
        try {
            tempFilePath = ImageCompressionUtil.compressAndSaveToTemp(imageUrl);

            // 上传到云存储中，并返回文件id
            fileId = cloudStorageConsumer.upload(new File(tempFilePath)).getData();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 删除临时文件
            if (tempFilePath != null) {
                FileUtils.deleteQuietly(new File(tempFilePath));
            }
        }
        return fileId;
    }

    /**
     * 将整个分组标记为失败
     * @param group 失败的分组
     * @param failedList 失败列表
     * @param errorMessage 错误信息
     */
    private void addGroupToFailedList(GeneralRobustExcelParser.TemplateGroup group,
                                      List<LongTextExcelData> failedList,
                                      String errorMessage) {
        // 将该分组下所有模板添加到失败列表
        for (GeneralRobustExcelParser.TemplateBean template : group.getTemplates()) {
            failedList.add(createFailedRecord(template, errorMessage));
        }
    }

    /**
     * 创建失败记录
     * @param template 模板数据
     * @param errorMessage 错误信息
     * @return 失败记录对象
     */
    private LongTextExcelData createFailedRecord(GeneralRobustExcelParser.TemplateBean template, String errorMessage) {
        LongTextExcelData failedRecord = new LongTextExcelData();
        failedRecord.setIndex(Integer.valueOf(template.getSerialNumber())); // 设置序号
        failedRecord.setType(template.getType());         // 设置类型
        failedRecord.setTypeName(template.getTemplateName()); // 设置类型名称
        failedRecord.setPrompt(template.getTemplateContent()); // 设置提示内容
        failedRecord.setFailReason(Map.of("error", errorMessage)); // 设置失败原因
        return failedRecord;
    }

    /**
     * 判断是否存在同名的模板
     *
     * @param title
     * @param query
     * @return
     */
    private boolean isExistByName(String title, PermissionQuery query) {
        return this.lambdaQuery()
                .eq(GeneralAgentTemplateEntity::getName, title)
                .eq(GeneralAgentTemplateEntity::getOrgId, query.getOrgId())
                .exists();
    }

    /**
     * 根据类型获取类型ID
     *
     * @param type
     * @return
     */
    private String getTypeIdByTypeCode(String type) {
        ArticleCategoryEnums typeEnum = ArticleCategoryEnums.getByCode(type);
        if (typeEnum == null) {
            log.error("类型错误：{}", type);
            return null;
        }
        return typeEnum.getCode();
    }

}
