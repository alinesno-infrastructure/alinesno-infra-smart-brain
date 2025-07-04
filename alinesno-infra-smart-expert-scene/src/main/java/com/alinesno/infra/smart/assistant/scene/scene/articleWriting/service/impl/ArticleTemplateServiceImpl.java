package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.enums.ArticleCategoryEnums;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.mapper.ArticleTemplateMapper;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleTemplateService;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools.ExcelData;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools.ImageCompressionUtil;
import com.alinesno.infra.smart.scene.entity.ArticleTemplateEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ArticleTemplateServiceImpl extends IBaseServiceImpl<ArticleTemplateEntity, ArticleTemplateMapper> implements IArticleTemplateService {

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer;

    @Override
    public List<ArticleTemplateEntity> getTemplateByType(PermissionQuery query, String typeCode) {

        Long orgId = query.getOrgId();

//         过滤出来的数据是
//        - 类型为公共的 PUBLIC_SCENE
//        - 类型为ORG_SCENE，而且等于orgId的
        LambdaQueryWrapper<ArticleTemplateEntity> wrapper = new LambdaQueryWrapper<>();

// 先构建 OR 条件（PUBLIC_SCENE 或 ORG_SCENE+orgId）
        wrapper.and(w -> w
                .eq(ArticleTemplateEntity::getArticleTemplatePermission, SceneScopeType.PUBLIC_SCENE.getValue())
                .or()
                .eq(ArticleTemplateEntity::getArticleTemplatePermission, SceneScopeType.ORG_SCENE.getValue())
                .eq(ArticleTemplateEntity::getOrgId, orgId)
        );

// 再添加 typeCode 条件（如果有）
        if (typeCode != null && !typeCode.isEmpty()) {
            wrapper.eq(ArticleTemplateEntity::getArticleTemplateType, typeCode);
        } else {
            wrapper.last("LIMIT 100");
        }


        wrapper.orderByDesc(ArticleTemplateEntity::getAddTime);

        // 执行查询并返回结果
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> readExcel(List<ExcelData> dataList, PermissionQuery query) {
        List<ExcelData> failedList = new ArrayList<>();
        List<ArticleTemplateEntity> templateList = new ArrayList<>();

        for (ExcelData data : dataList) {
            ArticleTemplateEntity template = ArticleTemplateEntity.toPower(query);
            Map<String, String> failReason = new HashMap<>();

            // 检查模板名称是否重复
            if (isExistByName(data.getTitle(), query)) {
                failReason.put("reason", "模板名称重复");
                failReason.put("detail", data.getTitle());
                data.setFailReason(failReason);
                failedList.add(data);
                continue;
            }

            // 检查类型是否有效
            String typeId = getTypeIdByTypeCode(data.getType());
            if (typeId == null) {
                failReason.put("reason", "类型错误");
                failReason.put("detail", data.getType());
                data.setFailReason(failReason);
                failedList.add(data);
                continue;
            }

            try {
                template.setConfig(JSONObject.parseArray(data.getConfig()).toJSONString());
                template.setResultConfig(JSONObject.parseObject(data.getResultConfig()).toJSONString());
            } catch (Exception e) {
                failReason.put("reason", "配置解析失败");
                failReason.put("detail", e.getMessage());
                data.setFailReason(failReason);
                failedList.add(data);
                continue;
            }

            // 处理图标
            String imageUrl = data.getIcon();
            String fileId = null;
            if (imageUrl != null) {
                try {
                    fileId = getIconId(imageUrl);
                } catch (Exception e) {
                    failReason.put("reason", "图标处理失败");
                    failReason.put("detail", e.getMessage());
                    data.setFailReason(failReason);
                    failedList.add(data);
                    continue;
                }
            }

            // 设置模板属性
            template.setName(data.getTitle());
            template.setArticleTemplateType(typeId);
            template.setIcon(fileId);
            template.setDescription(data.getDesc());
            template.setSystemPrompt(data.getSystemPrompt());
            template.setPrompt(data.getPrompt());
            template.setArticleTemplatePermission(data.getPower());


            templateList.add(template);
        }

        // 批量保存成功数据
        if (!templateList.isEmpty()) {
            saveBatch(templateList);
        }

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", templateList.size());
        result.put("failedCount", failedList.size());
        result.put("failedList", failedList);
        return result;
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
                .eq(ArticleTemplateEntity::getName, title)
                .eq(ArticleTemplateEntity::getOrgId, query.getOrgId())
                .exists();
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
