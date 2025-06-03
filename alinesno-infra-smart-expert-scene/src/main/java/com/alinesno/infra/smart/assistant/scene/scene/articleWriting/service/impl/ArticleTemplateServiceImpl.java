package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.mapper.ArticleTemplateMapper;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.service.IArticleTemplateService;
import com.alinesno.infra.smart.scene.entity.ArticleTemplateEntity;
import com.alinesno.infra.smart.scene.enums.SceneScopeType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷场景服务实现类
 */
@Slf4j
@Service
public class ArticleTemplateServiceImpl extends IBaseServiceImpl<ArticleTemplateEntity, ArticleTemplateMapper> implements IArticleTemplateService {

    @Override
    public List<ArticleTemplateEntity> getTemplateByType(PermissionQuery query, String typeCode) {

        Long orgId = query.getOrgId();

        // 创建查询条件构造器
        LambdaQueryWrapper<ArticleTemplateEntity> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件：articleTemplatePermission为public或者类型为org且orgId为查询条件中的orgId
        wrapper.and(w -> w.eq(ArticleTemplateEntity::getArticleTemplatePermission , SceneScopeType.PUBLIC_SCENE.getValue())
                .or()
                .eq( ArticleTemplateEntity::getArticleTemplatePermission, SceneScopeType.ORG_SCENE.getValue())
                .eq(ArticleTemplateEntity::getOrgId, orgId));

        // 可选的类型过滤
        if (typeCode != null && !typeCode.isEmpty()) {
            wrapper.eq(ArticleTemplateEntity::getArticleTemplateType, typeCode);
        }
        wrapper.orderByDesc(ArticleTemplateEntity::getAddTime) ;

        // 执行查询并返回结果
        return this.baseMapper.selectList(wrapper);
    }
}
