package com.alinesno.infra.smart.assistant.initialize.impl;

import com.alinesno.infra.smart.assistant.initialize.IAssistantInitService;
import com.alinesno.infra.smart.brain.entity.PromptCatalogEntity;
import com.alinesno.infra.smart.brain.service.IPromptCatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 智能助手初始化服务实现类
 */
@Slf4j
@Component
public class AssistantInitServiceImpl implements IAssistantInitService {

    @Autowired
    private IPromptCatalogService promptCatalogService ;

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

        entity.setName("人才管理指令");
        entity.setDescription("人才管理指令");
        entity.setIcon("");
        entity.setOrderNum(1);
        entity.setParentId(parent.getParentId());
        entity.setChildren(new ArrayList<>());

        return entity ;
    }

    @Override
    public void prompt() {

    }

    @Override
    public void expertCatalog() {

    }

    @Override
    public void expert() {

    }

    @Override
    public void initPlugin() {

    }
}
