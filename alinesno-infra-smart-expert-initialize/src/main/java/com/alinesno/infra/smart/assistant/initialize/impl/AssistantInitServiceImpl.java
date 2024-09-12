package com.alinesno.infra.smart.assistant.initialize.impl;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleCatalogEntity;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.PluginEntity;
import com.alinesno.infra.smart.assistant.initialize.IAssistantInitService;
import com.alinesno.infra.smart.assistant.initialize.utils.ExpertListCreator;
import com.alinesno.infra.smart.assistant.plugin.AssistantPluginProperties;
import com.alinesno.infra.smart.assistant.plugin.loader.PluginLoader;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleCatalogService;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.IPluginService;
import com.alinesno.infra.smart.im.constants.ImConstants;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.enums.ChannelType;
import com.alinesno.infra.smart.im.service.IChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private IChannelService agentChannelService ;

//    @Autowired
//    private IPromptCatalogService promptCatalogService ;

    @Autowired
    private IIndustryRoleCatalogService industryRoleCatalogService ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

//    @Autowired
//    private IPromptPostsService promptPostsService ;

//    @Override
//    public void promptCatalog() {
//        List<PromptCatalogEntity> rootEntities = PromptPostCreator.createPromptCatalog() ;
//
//        promptCatalogService.saveOrUpdateBatch(rootEntities) ;
//    }
//
//    @Override
//    public void prompt() {
//
//        List<PromptPostsEntity> promptPostsEntityList = PromptPostCreator.createPromptPostEntity() ;
//
//        promptPostsService.saveOrUpdateBatch(promptPostsEntityList) ;
//
//    }

    @Override
    public void expertCatalog() {

        List<IndustryRoleCatalogEntity> allEntities = new ArrayList<>();

        // 创建根节点
        IndustryRoleCatalogEntity root = new IndustryRoleCatalogEntity();
        root.setName("所有角色");
        root.setParentId(0L);

        // 创建技术研发部
        IndustryRoleCatalogEntity techResearchDept = createExpertEntity("技术研发部", "技术研发部", root.getId(), root);
        techResearchDept.setId(9527L);
        industryRoleCatalogService.saveOrUpdate(techResearchDept);
//        allEntities.add(techResearchDept);

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
        techResearchDept.setId(9528L);
        industryRoleCatalogService.saveOrUpdate(productResearchDept);
//        allEntities.add(productResearchDept);

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

    @Override
    public void initChannel() {
        // 生成推荐频道列表
        List<ChannelEntity> recommendChannels = Arrays.asList(
                createChannel("代码审查与优化频道", "在这里，开发者可以提交自己的代码进行同行评审，也可以参与他人的代码审查，共同寻找优化点，提高代码质量。", ChannelType.RECOMMEND_CHANNEL, 16L, "code_reviewer", "https://example.com/images/code_review.jpg"),
                createChannel("DevOps 实践与CI/CD 频道", "分享和学习持续集成（CI）和持续部署（CD）的最佳实践，讨论自动化构建和部署流程，提升团队的DevOps能力。", ChannelType.RECOMMEND_CHANNEL, 17L, "devops_engineer", "https://example.com/images/devops_ci_cd.jpg"),
                createChannel("前端框架与库频道", "专注于Vue3和Element Plus等前端框架和技术栈的最新动态，提供教程和案例研究，促进前端开发者之间的知识共享。", ChannelType.RECOMMEND_CHANNEL, 18L, "frontend_developer", "https://example.com/images/frontend_frameworks.jpg"),
                createChannel("微服务架构设计频道", "探讨微服务架构的设计原则和实践经验，包括服务划分、通信协议选择和服务治理策略等，帮助团队构建可扩展的应用程序。", ChannelType.RECOMMEND_CHANNEL, 19L, "architect", "https://example.com/images/microservices_architecture.jpg"),
                createChannel("安全与合规性频道", "讨论软件开发生命周期中的安全措施和合规要求，如代码审计、数据加密及隐私保护等，确保应用的安全性和合法性。", ChannelType.RECOMMEND_CHANNEL, 20L, "security_specialist", "https://example.com/images/security_compliance.jpg")
        );

        // 假设 createChannel 方法已经被定义
        List<ChannelEntity> exampleChannels = Arrays.asList(
                createChannel("解决方案编写频道", "分享和讨论各种技术难题的解决方案，帮助团队提高开发效率。", ChannelType.PUBLIC_CHANNEL, 1L, "solution_writer", "https://example.com/images/solution_writing.jpg"),
                createChannel("数据库设计频道", "专注于数据库模式设计的最佳实践，探讨高效的数据结构。", ChannelType.PUBLIC_CHANNEL, 2L, "db_designer", "https://example.com/images/db_design.jpg"),
                createChannel("需求方案编写分享频道", "提供一个平台给产品经理分享需求文档，并获取反馈。", ChannelType.PUBLIC_CHANNEL, 3L, "product_manager", "https://example.com/images/requirement_sharing.jpg"),
                createChannel("测试策略讨论频道", "讨论如何制定有效的测试计划以及执行策略。", ChannelType.PUBLIC_CHANNEL, 4L, "qa_engineer", "https://example.com/images/testing_strategy.jpg"),
                createChannel("UI/UX 设计分享频道", "展示最新的UI/UX设计趋势，提供设计师交流空间。", ChannelType.PUBLIC_CHANNEL, 5L, "ui_ux_designer", "https://example.com/images/ui_ux_design.jpg"),
                createChannel("敏捷开发实践频道", "探索敏捷开发方法论的应用实例，分享敏捷团队的日常经验和挑战。", ChannelType.PUBLIC_CHANNEL, 6L, "agile_practitioner", "https://example.com/images/agile_practices.jpg"),
                createChannel("后端架构设计频道", "探讨后端系统的架构设计，包括服务化、模块化等方面的知识。", ChannelType.PUBLIC_CHANNEL, 7L, "backend_architect", "https://example.com/images/backend_architecture.jpg"),
                createChannel("前端性能优化频道", "针对前端应用的加载速度、响应时间和用户体验等方面提出优化建议。", ChannelType.PUBLIC_CHANNEL, 8L, "frontend_optimizer", "https://example.com/images/frontend_performance.jpg"),
                createChannel("移动应用开发频道", "分享iOS和Android平台上的应用开发经验，涵盖从设计到发布的全过程。", ChannelType.PUBLIC_CHANNEL, 9L, "mobile_developer", "https://example.com/images/mobile_app_development.jpg"),
                createChannel("云原生技术频道", "介绍云原生技术栈及其在现代云计算环境下的应用案例。", ChannelType.PUBLIC_CHANNEL, 10L, "cloud_native_specialist", "https://example.com/images/cloud_native_technology.jpg")
        );

        log.debug("保存推荐频道信息到数据库:{}" , recommendChannels.size());
        exampleChannels.forEach(item -> {
            item.setChannelId(IdUtil.nanoId(8));
            item.setIcon(ImConstants.DEFAULT_AVATAR);
        });
        recommendChannels.forEach(item -> {
            item.setChannelId(IdUtil.nanoId(8));
            item.setIcon(ImConstants.DEFAULT_AVATAR);
        }) ;

        agentChannelService.saveOrUpdateBatch(exampleChannels) ;
        agentChannelService.saveOrUpdateBatch(recommendChannels) ;
    }

    /**
     * 创建频道实体
     *
     * @param name        频道名称
     * @param description 描述
     * @param type        类型
     * @param id          ID
     * @param creator     创建者
     * @param imageUrl    图片链接
     * @return 创建的频道实体
     */
    private static ChannelEntity createChannel(String name, String description, ChannelType type, Long id, String creator, String imageUrl) {
        ChannelEntity channel = new ChannelEntity();
        channel.setChannelName(name);
        channel.setChannelDesc(description);
        channel.setChannelType(type.getValue());
        channel.setId(id);
        channel.setOperatorId(id);
        channel.setIcon(imageUrl);
        return channel;
    }

}
