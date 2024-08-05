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
import com.alinesno.infra.smart.brain.service.IPromptCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

        promptCatalogService.saveBatch(rootEntities) ;
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

        industryRoleCatalogService.saveBatch(allEntities) ;
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

        List<PluginEntity> pluginEntities = new ArrayList<>() ;

        AtomicLong id = new AtomicLong(1L);
        pluginInfoList.forEach(pluginInfo -> {

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
