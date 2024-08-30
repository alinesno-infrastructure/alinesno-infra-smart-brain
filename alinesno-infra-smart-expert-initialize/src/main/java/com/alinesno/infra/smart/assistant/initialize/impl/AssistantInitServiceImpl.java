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
        List<PromptCatalogEntity> rootEntities = new ArrayList<>();

        PromptCatalogEntity rootEntity = new PromptCatalogEntity();
        rootEntity.setName("研发智能体指令");
        rootEntity.setDescription("研发智能体指令");
        rootEntity.setIcon("");
        rootEntity.setOrderNum(1);
        rootEntity.setParentId(0L);
        rootEntity.setChildren(new ArrayList<>());

        // 人才管理指令
        PromptCatalogEntity personalAssistant = createEntity("人才管理指令", "人才管理指令", 0L, rootEntity);
        PromptCatalogEntity contentRecommendation = createEntity("内容推荐指令", "内容推荐指令", 0L, personalAssistant);
        PromptCatalogEntity trainingExpert = createEntity("培训专家指令", "培训专家指令", 0L, personalAssistant);

        rootEntities.add(personalAssistant) ;
        rootEntities.add(contentRecommendation) ;
        rootEntities.add(trainingExpert) ;

        // 解决方案指令
        PromptCatalogEntity solutions = createEntity("解决方案指令", "解决方案指令", 8L, rootEntity);
        PromptCatalogEntity solutionDesign = createEntity("解决方案设计指令", "解决方案设计指令", 0L, solutions);
        PromptCatalogEntity requirementAnalysis = createEntity("需求分析设计指令", "需求分析设计指令", 0L, solutions);
        PromptCatalogEntity projectManagement = createEntity("项目管理指令", "项目管理指令", 0L, solutions);

        rootEntities.add(solutions) ;
        rootEntities.add(solutionDesign) ;
        rootEntities.add(requirementAnalysis) ;
        rootEntities.add(projectManagement) ;

        // 技术工程师指令
        PromptCatalogEntity technicalEngineer = createEntity("技术工程师指令", "技术工程师指令", 0L, rootEntity);
        PromptCatalogEntity technicalArchitecture = createEntity("技术架构指令", "技术架构指令", 0L, technicalEngineer);
        PromptCatalogEntity databaseDesign = createEntity("数据库设计指令", "数据库设计指令", 0L, technicalEngineer);
        PromptCatalogEntity interfaceTesting = createEntity("接口测试指令", "接口测试指令", 0L, technicalEngineer);
        PromptCatalogEntity developerInstructions = createEntity("开发工程师指令", "开发工程师指令", 0L, technicalEngineer);

        rootEntities.add(technicalEngineer) ;
        rootEntities.add(technicalArchitecture) ;
        rootEntities.add(databaseDesign) ;
        rootEntities.add(interfaceTesting) ;
        rootEntities.add(developerInstructions) ;

        // 数据挖掘指令
        PromptCatalogEntity dataMining = createEntity("数据挖掘指令", "数据挖掘指令", 0L, rootEntity);
        PromptCatalogEntity dataPlanning = createEntity("数据规划指令", "数据规划指令", 0L, dataMining);
        PromptCatalogEntity dataCollection = createEntity("数据采集指令", "数据采集指令", 0L, dataMining);
        PromptCatalogEntity dataMiningCalculation = createEntity("数据挖掘(计算)指令", "数据挖掘(计算)指令", 0L, dataMining);
        PromptCatalogEntity dataServices = createEntity("数据服务指令", "数据服务指令", 0L, dataMining);

        rootEntities.add(dataMining) ;
        rootEntities.add(dataPlanning) ;
        rootEntities.add(dataCollection) ;
        rootEntities.add(dataMiningCalculation) ;
        rootEntities.add(dataServices) ;

        // 数据分析指令
        PromptCatalogEntity dataAnalysis = createEntity("数据分析指令", "数据分析指令", 0L, rootEntity);
        PromptCatalogEntity indicatorAnalysis = createEntity("指标分析指令", "指标分析指令", 0L, dataAnalysis);
        PromptCatalogEntity dataReporting = createEntity("数据报表指令", "数据报表指令", 0L, dataAnalysis);

        rootEntities.add(dataAnalysis) ;
        rootEntities.add(indicatorAnalysis) ;
        rootEntities.add(dataReporting) ;

        // 运营管理指令
        PromptCatalogEntity operationManagement = createEntity("运营管理指令", "运营管理指令", 0L, rootEntity);
        PromptCatalogEntity scriptWriting = createEntity("脚本编写指令", "脚本编写指令", 0L, operationManagement);
        PromptCatalogEntity continuousRelease = createEntity("持续发布指令", "持续发布指令", 0L, operationManagement);

        rootEntities.add(operationManagement) ;
        rootEntities.add(scriptWriting) ;
        rootEntities.add(continuousRelease) ;

        // 业务运营指令
        PromptCatalogEntity businessOperation = createEntity("业务运营指令", "业务运营指令", 0L, rootEntity);
        PromptCatalogEntity productMarket = createEntity("产品市场指令", "产品市场指令", 0L, businessOperation);
        PromptCatalogEntity productCustomerService = createEntity("产品客服指令", "产品客服指令", 0L, businessOperation);

        rootEntities.add(businessOperation) ;
        rootEntities.add(productMarket) ;
        rootEntities.add(productCustomerService) ;

        // 其它指令
        PromptCatalogEntity others = createEntity("其它指令", "其它指令", 0L, rootEntity);

        rootEntities.add(others) ;

        AtomicLong id = new AtomicLong(1L);
        rootEntities.forEach(entity -> {
            entity.setId(id.getAndIncrement());
        }) ;

        promptCatalogService.saveOrUpdateBatch(rootEntities) ;
    }

    private PromptCatalogEntity createEntity(String name, String description, long parentId, PromptCatalogEntity parent) {
        PromptCatalogEntity entity = new PromptCatalogEntity();

        entity.setId(IdUtil.getSnowflakeNextId());
        entity.setName(name);
        entity.setDescription(description);
        entity.setIcon("");
        entity.setOrderNum(1);
        entity.setParentId(parent.getId());
        entity.setChildren(new ArrayList<>());

        return entity ;
    }

    @Override
    public void prompt() {

        List<PromptPostsEntity> promptPostsEntityList = new ArrayList<>();

        // 添加所有指令对象
        promptPostsEntityList.add(new PromptPostsEntity("5l1jPKV8", 12734L, "Admin1",  "培训试卷信息指令", "password1", 1L, "Title1", "Type1",  100, "Model1", 25, 50));
        promptPostsEntityList.add(new PromptPostsEntity("Zo5ueZ34", 12734L, "Admin2", "Ansible服务脚本指令", "password2", 1L, "Title2", "Type2",  150, "Model2", 30, 60));
        promptPostsEntityList.add(new PromptPostsEntity("XPGeYb4a", 12734L, "Admin3", "K8S部署脚本生成工程师", "password3", 1L, "Title3", "Type3",  120, "Model3", 28, 55));
        promptPostsEntityList.add(new PromptPostsEntity("RUABZdu2", 12734L, "Admin4", "API接口转成SpringBoot代码指令", "password4", 1L, "Title4", "Type4",  110, "Model4", 26, 52));
        promptPostsEntityList.add(new PromptPostsEntity("Qoh7hgA3", 12734L, "Admin5", "接口用例生成指令", "password5", 1L, "Title5", "Type5",  130, "Model5", 29, 58));

        promptPostsEntityList.add(new PromptPostsEntity("JQxPeLRJ", 12734L, "Admin6", "服务接口生成指令", "password6", 1L, "Title6", "Type6",  140, "Model6", 27, 54));
        promptPostsEntityList.add(new PromptPostsEntity("KTHkQiDg", 12734L, "Admin7", "培训内容推荐指令", "password7", 1L, "Title7", "Type7",  105, "Model7", 24, 48));
        promptPostsEntityList.add(new PromptPostsEntity("OyezG1Hk", 12734L, "Admin8", "产品软文_内容编写指令", "password8", 1L, "Title8", "Type8", 115, "Model8", 22, 44));
        promptPostsEntityList.add(new PromptPostsEntity("35lxXaDe", 12734L, "Admin9", "产品软文_目录大纲指令", "password9", 1L, "Title9", "Type9", 125, "Model9", 23, 46));
        promptPostsEntityList.add(new PromptPostsEntity("gDT9coiQ", 12734L, "Admin10", "服务工程生成指令", "password10", 1L, "Title10", "Type10", 135, "Model10", 21, 42));

        promptPostsEntityList.add(new PromptPostsEntity("0ucP2rvS", 12734L, "Admin11", "数据库表结构设计指令", "password11", 1L, "Title11", "Type11", 155, "Model11", 31, 62));
        promptPostsEntityList.add(new PromptPostsEntity("IR0i5mjt", 12734L, "Admin12", "实时数据采集指令", "password12", 1L, "Title12", "Type12", 145, "Model12", 32, 64));
        promptPostsEntityList.add(new PromptPostsEntity("uy7Qsfti", 12734L, "Admin13", "培训题目设计指令", "password13", 1L, "Title13", "Type13", 160, "Model13", 33, 66));
        promptPostsEntityList.add(new PromptPostsEntity("kGLGZK9j", 12734L, "Admin14", "平台菜单生成指令", "password14", 1L, "Title14", "Type14", 170, "Model14", 34, 68));
        promptPostsEntityList.add(new PromptPostsEntity("hfSZuEkT", 12734L, "Admin15", "数据库实体设计指令", "password15", 1L, "Title15", "Type15", 180, "Model15", 35, 70));

        promptPostsEntityList.add(new PromptPostsEntity("wbqZCMp8", 12734L, "Admin16", "需求分析_项目非功能性需求", "password16", 1L, "Title16", "Type16", 190, "Model16", 36, 72));
        promptPostsEntityList.add(new PromptPostsEntity("3hmyCqhJ", 12734L, "Admin17", "需求分析_项目功能细化分析指令", "password17", 1L, "Title17", "Type17", 200, "Model17", 37, 74));
        promptPostsEntityList.add(new PromptPostsEntity("RgPSt3oS", 12734L, "Admin18", "需求分析_项目功能分析指令", "password18", 1L, "Title18", "Type18", 210, "Model18", 38, 76));
        promptPostsEntityList.add(new PromptPostsEntity("V5yZ1l3H", 12734L, "Admin19", "需求分析_项目介绍分析指令", "password19", 1L, "Title19", "Type19", 220, "Model19", 39, 78));
        promptPostsEntityList.add(new PromptPostsEntity("mOO5Fq5s", 12734L, "Admin20", "需求分析_需求文档分析指令", "password20", 1L, "Title20", "Type20", 230, "Model20", 40, 80));

        AtomicLong id = new AtomicLong(1L);
        promptPostsEntityList.forEach(entity -> {
            entity.setId(id.getAndIncrement());
        }) ;

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
