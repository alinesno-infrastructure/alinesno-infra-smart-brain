package com.alinesno.infra.smart.assistant.initialize.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleCatalogEntity;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.PluginEntity;
import com.alinesno.infra.smart.assistant.initialize.IAssistantInitService;
import com.alinesno.infra.smart.assistant.initialize.utils.ExpertListCreator;
import com.alinesno.infra.smart.assistant.initialize.utils.PromptPostCreator;
import com.alinesno.infra.smart.assistant.plugin.AssistantPluginProperties;
import com.alinesno.infra.smart.assistant.plugin.loader.PluginLoader;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IPluginService;
import com.alinesno.infra.smart.brain.entity.PromptCatalogEntity;
import com.alinesno.infra.smart.brain.entity.PromptPostsEntity;
import com.alinesno.infra.smart.brain.service.IPromptCatalogService;
import com.alinesno.infra.smart.brain.service.IPromptPostsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 智能助手初始化服务实现类
 */
@Slf4j
@Component
public class AssistantInitServiceImpl implements IAssistantInitService {

    @Autowired
    private AssistantPluginProperties assistantPluginProperties;

    @Autowired
    private IPromptCatalogService promptCatalogService ;

    @Autowired
    private IIndustryRoleCatalogService industryRoleCatalogService ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Autowired
    private IPromptPostsService promptPostsService ;

    @Override
    public void promptCatalog() {
        List<PromptCatalogEntity> rootEntities = PromptPostCreator.createPromptCatalog() ;

        promptCatalogService.saveOrUpdateBatch(rootEntities) ;
    }

    @Override
    public void prompt() {

        List<PromptPostsEntity> promptPostsEntityList = PromptPostCreator.createPromptPostEntity() ;

        promptPostsService.saveOrUpdateBatch(promptPostsEntityList) ;

    }

    @Override
    public void expertCatalog() {

        List<IndustryRoleCatalogEntity> allEntities = new ArrayList<>();

        // 创建根节点
        IndustryRoleCatalogEntity root = new IndustryRoleCatalogEntity();
        root.setName("所有角色");
        root.setParentId(0L);

        // 创建技术研发部
        IndustryRoleCatalogEntity techResearchDept = createExpertEntity("技术研发部", "技术研发部", root.getId(), root);
        allEntities.add(techResearchDept);

        // 创建需求分析团队
        IndustryRoleCatalogEntity demandAnalysisTeam = createExpertEntity("需求分析团队", "需求分析团队", techResearchDept.getId(), techResearchDept);
        allEntities.add(demandAnalysisTeam);

        // 创建技术工程师团队
        IndustryRoleCatalogEntity technicalEngineerTeam = createExpertEntity("技术工程师团队", "工程师团队", techResearchDept.getId(), techResearchDept);
        allEntities.add(technicalEngineerTeam);

        // 创建数据工程师团队
        IndustryRoleCatalogEntity dataEngineerTeam = createExpertEntity("数据工程师团队", "数据工程师团队", techResearchDept.getId(), techResearchDept);
        allEntities.add(dataEngineerTeam);

        // 创建数据分析团队
        IndustryRoleCatalogEntity dataAnalysisTeam = createExpertEntity("数据分析团队", "数据分析团队", techResearchDept.getId(), techResearchDept);
        allEntities.add(dataAnalysisTeam);

        // 创建自动化运维团队
        IndustryRoleCatalogEntity automationOpsTeam = createExpertEntity("自动化运维团队", "自动化运维团队", techResearchDept.getId(), techResearchDept);
        allEntities.add(automationOpsTeam);

        // 创建技术架构师团队
        IndustryRoleCatalogEntity techArchitectsTeam = createExpertEntity("技术架构师团队", "用于架构师相关事项管理", techResearchDept.getId(), techResearchDept);
        allEntities.add(techArchitectsTeam);

        // 创建产品研发部
        IndustryRoleCatalogEntity productResearchDept = createExpertEntity("产品研发部", "产品研发部", root.getId(), root);
        allEntities.add(productResearchDept);

        // 创建人才培养团队
        IndustryRoleCatalogEntity talentCultivationTeam = createExpertEntity("人才培养团队", "人才培养团队", productResearchDept.getId(), productResearchDept);
        allEntities.add(talentCultivationTeam);

        // 创建方案架构师团队
        IndustryRoleCatalogEntity solutionArchitectsTeam = createExpertEntity("方案架构师团队", "方案架构师团队", productResearchDept.getId(), productResearchDept);
        allEntities.add(solutionArchitectsTeam);

        // 创建产品运营团队
        IndustryRoleCatalogEntity productOpsTeam = createExpertEntity("产品运营团队", "产品运营团队", productResearchDept.getId(), productResearchDept);
        allEntities.add(productOpsTeam);

        // 创建市场营销团队
        IndustryRoleCatalogEntity marketingTeam = createExpertEntity("市场营销团队", "市场营销团队", productResearchDept.getId(), productResearchDept);
        allEntities.add(marketingTeam);

        // 创建产品服务团队
        IndustryRoleCatalogEntity productServiceTeam = createExpertEntity("产品服务团队", "产品服务团队", productResearchDept.getId(), productResearchDept);
        allEntities.add(productServiceTeam);

        // 创建技术运营团队
        IndustryRoleCatalogEntity techOpsTeam = createExpertEntity("技术运营团队", "技术运营团队", productResearchDept.getId(), productResearchDept);
        allEntities.add(techOpsTeam);

        AtomicLong id = new AtomicLong(1L);
        allEntities.forEach(entity -> {
            entity.setId(id.getAndIncrement());
        });

        industryRoleCatalogService.saveOrUpdateBatch(allEntities) ;
    }

    public static IndustryRoleCatalogEntity createExpertEntity(String name, String desc, Long parentId, IndustryRoleCatalogEntity parent) {
        IndustryRoleCatalogEntity entity = new IndustryRoleCatalogEntity();

        entity.setId(IdUtil.getSnowflakeNextId());
        entity.setName(name);
        entity.setDescription(desc);
        entity.setParentId(parentId);

        if (parent != null) {
            parent.getChildren().add(entity);
        }

        return entity;
    }

    @Override
    public void expert() {
        List<IndustryRoleEntity> allEntities = ExpertListCreator.createExpertList() ;
        industryRoleService.saveBatch(allEntities) ;
    }

    @Override
    public void initPlugin() {

        log.debug("assistantPluginProperties = {}", assistantPluginProperties) ;

        List<PluginLoader.PluginInfo> list = PluginLoader.loadPlugin(assistantPluginProperties.getCentral() , assistantPluginProperties.getPath());
        savePluginToDb(list);
    }

    // 保存插件信息到数据库
    private void savePluginToDb(List<PluginLoader.PluginInfo> pluginInfoList) {

        IPluginService service = SpringUtils.getBean(IPluginService.class) ;

        ClassLoader classLoader = getClass().getClassLoader() ;

        List<PluginEntity> pluginEntities = new ArrayList<>() ;

        AtomicLong id = new AtomicLong(1L);
        pluginInfoList.forEach(pluginInfo -> {

            File directory = new File(assistantPluginProperties.getPath());
            File[] jarFiles = directory.listFiles((dir, name) -> name.endsWith(".jar"));

            if (jarFiles != null) {
                for (File jarFile : jarFiles) {
                    try (JarFile jf = new JarFile(jarFile)) {
                        Enumeration<JarEntry> entries = jf.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry jarEntry = entries.nextElement();

                            if (jarEntry.getName().endsWith(".class")) {
                                String className = jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 6);
                                log.debug("--->>>> className = {}" , className);

//                                Class<?> classes = classLoader.loadClass(className) ;
//                                for (Annotation annotation : classes.getAnnotations()) {
//                                    log.debug("--->>>> annotation = {}" , annotation);
//                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            PluginEntity entity = new PluginEntity() ;

            entity.setId(id.getAndIncrement());
            entity.setAuthor(pluginInfo.getAuthor());
            entity.setDescription(pluginInfo.getDesc());
            entity.setJarName(pluginInfo.getJarName());
            entity.setLocalPath(pluginInfo.getLocalPath());
            entity.setName(pluginInfo.getName());

            pluginEntities.add(entity) ;
        });

        service.saveOrUpdateBatch(pluginEntities);
        log.debug("保存插件信息到数据库:{}" , pluginEntities.size());
    }


}
