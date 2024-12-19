package com.alinesno.infra.smart.assistant.template.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.constants.FieldConstants;
import com.alinesno.infra.smart.assistant.template.entity.RoleTemplateEntity;
import com.alinesno.infra.smart.assistant.template.mapper.RoleTemplateMapper;
import com.alinesno.infra.smart.assistant.template.service.IRoleTemplateService;
import com.alinesno.infra.smart.assistant.template.utils.GitUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.esotericsoftware.yamlbeans.YamlReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * 项目模块 服务实现类
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class RoleTemplateServiceImpl extends IBaseServiceImpl<RoleTemplateEntity, RoleTemplateMapper> implements IRoleTemplateService {

    @Override
    public void syncRoleTemplate(Long userId, String gitUrl) throws IOException {

        String path = "/tmp/gitPath/" + UUID.randomUUID() + "/";
        File files = new File(path);

        // 清理插件
//        this.remove(new QueryWrapper<RoleTemplateEntity>().eq(FieldConstants.OPERATOR_ID, userId)) ;
//        GitUtils.clone(gitUrl, path);
//        List<RoleTemplateEntity> es = new ArrayList<>() ;
//
//        for(File sub : Objects.requireNonNull(files.listFiles())) {
//
//            if(sub.isDirectory()) {
//
//                String directName = sub.getName() ;
//
//                for(File s : Objects.requireNonNull(sub.listFiles())) {
//
//                    if(s.getName().equals("info.yaml")) {
//
//                        YamlReader reader = new YamlReader(new FileReader(s.getAbsoluteFile()));
//                        JSONObject obj = reader.read(JSONObject.class) ;
//
//                        RoleTemplateEntity e = new RoleTemplateEntity() ;
//
//                        e.setScreen(obj.getString("screen"));
//                        e.setIndustry(obj.getString("industry"));
//                        e.setType(obj.getString("classes"));
//                        e.setTempScope("1");
//                        e.setOperatorId(userId);
//
//                        e.setTempZip(directName); // 插件目录名称
//                        e.setTempName(obj.getString("name"));
//                        e.setTempBanner("");
//                        e.setFieldProp(obj.getString("status"));
//                        e.setTempDesc(obj.getString("desc"));
//                        e.setTempTeam(obj.getString("team"));
//
//                        e.setInstallCount(0);
//                        e.setGradeCount(0);
//
//                        es.add(e) ;
//
//                        log.debug("e = {}" , e.toString());
//                    }
//
//                }
//            }
//
//        }

//        this.saveBatch(es) ;

        this.remove(new LambdaQueryWrapper<>());
        this.saveBatch(createDemoTemplate());
        FileUtils.forceDeleteOnExit(files);
    }

    public List<RoleTemplateEntity> createDemoTemplate() {

        // 研发部门
        RoleTemplateEntity architect = new RoleTemplateEntity();
        architect.setRoleName("系统架构师");
        architect.setIndustryCatalog(1L); // 假设信息技术对应ID为1
        architect.setRoleType("单角色");
        architect.setRoleAvatar("architect_avatar.jpg");
        architect.setBackstory("""
                负责整体系统的架构设计与规划，确保技术选型符合业务需求并具有前瞻性。
                通过引入新技术和优化现有架构，提升系统的性能、可扩展性和安全性。
                """);
        architect.setResponsibilities(architect.getBackstory());

        RoleTemplateEntity seniorDeveloper = new RoleTemplateEntity();
        seniorDeveloper.setRoleName("高级开发工程师");
        seniorDeveloper.setIndustryCatalog(1L);
        seniorDeveloper.setRoleType("单角色");
        seniorDeveloper.setRoleAvatar("senior_developer_avatar.jpg");
        seniorDeveloper.setBackstory("""
                主导核心模块的编码工作，解决复杂的技术难题，编写高质量的代码。
                指导初级开发人员，进行代码审查，确保项目按时交付且质量优良。
                """);
        seniorDeveloper.setResponsibilities(seniorDeveloper.getBackstory());

        RoleTemplateEntity tester = new RoleTemplateEntity();
        tester.setRoleName("测试工程师");
        tester.setIndustryCatalog(1L);
        tester.setRoleType("单角色");
        tester.setRoleAvatar("tester_avatar.jpg");
        tester.setBackstory("""
                制定测试计划，执行功能测试、性能测试等，确保产品质量符合预期标准。
                分析测试结果，跟踪缺陷修复进度，保障软件发布前的质量。
                """);
        tester.setResponsibilities(tester.getBackstory());

        RoleTemplateEntity devOps = new RoleTemplateEntity();
        devOps.setRoleName("DevOps工程师");
        devOps.setIndustryCatalog(1L);
        devOps.setRoleType("协作角色");
        devOps.setRoleAvatar("devops_avatar.jpg");
        devOps.setBackstory("""
                构建和维护CI/CD流水线，实现自动化部署，提高软件发布的效率和可靠性。
                协调开发和运维团队，确保开发环境和生产环境的一致性。
                """);
        devOps.setResponsibilities(devOps.getBackstory());

// 运维部门
        RoleTemplateEntity operationsEngineer = new RoleTemplateEntity();
        operationsEngineer.setRoleName("运维工程师");
        operationsEngineer.setIndustryCatalog(1L);
        operationsEngineer.setRoleType("单角色");
        operationsEngineer.setRoleAvatar("operations_engineer_avatar.jpg");
        operationsEngineer.setBackstory("""
                负责服务器配置管理、网络监控和故障排除，确保基础设施稳定运行。
                实施自动化运维工具，降低运维成本，提高工作效率。
                """);
        operationsEngineer.setResponsibilities(operationsEngineer.getBackstory());

        RoleTemplateEntity securityEngineer = new RoleTemplateEntity();
        securityEngineer.setRoleName("安全工程师");
        securityEngineer.setIndustryCatalog(1L);
        securityEngineer.setRoleType("单角色");
        securityEngineer.setRoleAvatar("security_engineer_avatar.jpg");
        securityEngineer.setBackstory("""
                设计并实施网络安全策略，保护公司信息资产免受外部威胁。
                定期进行安全审计和漏洞扫描，及时修补安全隐患。
                提供安全培训，增强员工的安全意识。
                """);
        securityEngineer.setResponsibilities(securityEngineer.getBackstory());

        RoleTemplateEntity dba = new RoleTemplateEntity();
        dba.setRoleName("数据库管理员");
        dba.setIndustryCatalog(1L);
        dba.setRoleType("单角色");
        dba.setRoleAvatar("dba_avatar.jpg");
        dba.setBackstory("""
                负责数据库的设计、管理和优化，确保数据存储的安全性和高效性。
                处理数据库备份和恢复任务，制定灾难恢复计划。
                监控数据库性能，提供优化建议，支持应用开发。
                """);
        dba.setResponsibilities(dba.getBackstory());

// 商务部门
        RoleTemplateEntity businessAnalyst = new RoleTemplateEntity();
        businessAnalyst.setRoleName("商务分析师");
        businessAnalyst.setIndustryCatalog(2L); // 假设商业服务对应ID为2
        businessAnalyst.setRoleType("单角色");
        businessAnalyst.setRoleAvatar("business_analyst_avatar.jpg");
        businessAnalyst.setBackstory("""
                收集和分析市场数据，为管理层提供决策支持，识别新的商业机会。
                协助制定销售策略，评估市场竞争态势，推动业务增长。
                """);
        businessAnalyst.setResponsibilities(businessAnalyst.getBackstory());

        RoleTemplateEntity salesRepresentative = new RoleTemplateEntity();
        salesRepresentative.setRoleName("销售代表");
        salesRepresentative.setIndustryCatalog(2L);
        salesRepresentative.setRoleType("单角色");
        salesRepresentative.setRoleAvatar("sales_representative_avatar.jpg");
        salesRepresentative.setBackstory("""
                负责开拓新客户，维护现有客户关系，促进产品和服务的销售。
                了解客户需求，提供个性化解决方案，达成销售目标。
                参与商务谈判，签订合同，确保交易顺利完成。
                """);
        salesRepresentative.setResponsibilities(salesRepresentative.getBackstory());

        RoleTemplateEntity partnershipManager = new RoleTemplateEntity();
        partnershipManager.setRoleName("合作伙伴经理");
        partnershipManager.setIndustryCatalog(2L);
        partnershipManager.setRoleType("协作角色");
        partnershipManager.setRoleAvatar("partnership_manager_avatar.jpg");
        partnershipManager.setBackstory("""
                建立和维护与关键合作伙伴的关系，寻找合作机会，拓展业务渠道。
                管理合作伙伴之间的沟通协调，确保合作关系顺畅。
                推动合作项目的落地实施，实现双方共赢。
                """);
        partnershipManager.setResponsibilities(partnershipManager.getBackstory());

// 产品营销部门
        RoleTemplateEntity productManager = new RoleTemplateEntity();
        productManager.setRoleName("产品经理");
        productManager.setIndustryCatalog(3L); // 假设市场营销对应ID为3
        productManager.setRoleType("单角色");
        productManager.setRoleAvatar("product_manager_avatar.jpg");
        productManager.setBackstory("""
                定义产品愿景和发展路线图，收集用户反馈，驱动产品迭代。
                协调跨部门资源，确保产品研发进度符合市场需求。
                制定产品上市策略，推动产品的成功推出。
                """);
        productManager.setResponsibilities(productManager.getBackstory());

        RoleTemplateEntity marketingSpecialist = new RoleTemplateEntity();
        marketingSpecialist.setRoleName("营销专员");
        marketingSpecialist.setIndustryCatalog(3L);
        marketingSpecialist.setRoleType("单角色");
        marketingSpecialist.setRoleAvatar("marketing_specialist_avatar.jpg");
        marketingSpecialist.setBackstory("""
                规划并执行市场推广活动，提升品牌知名度和产品曝光率。
                分析市场趋势，制定针对性的营销方案，吸引潜在客户。
                管理社交媒体平台，维护品牌形象，增加用户互动。
                """);
        marketingSpecialist.setResponsibilities(marketingSpecialist.getBackstory());

        RoleTemplateEntity copywriterEditor = new RoleTemplateEntity();
        copywriterEditor.setRoleName("文案编辑专员");
        copywriterEditor.setIndustryCatalog(4L); // 假设广告传媒对应ID为4
        copywriterEditor.setRoleType("单角色");
        copywriterEditor.setRoleAvatar("copywriter_editor_avatar.jpg");
        copywriterEditor.setBackstory("""
                擅长撰写引人入胜的品牌故事和广告文案，提升品牌影响力。
                策划和执行社交媒体营销活动，促进产品销售的增长。
                审核和优化现有文案，确保所有对外发布的文字内容都符合高质量标准。
                """);
        copywriterEditor.setResponsibilities(copywriterEditor.getBackstory());

        RoleTemplateEntity prCoordinator = new RoleTemplateEntity();
        prCoordinator.setRoleName("公关协调员");
        prCoordinator.setIndustryCatalog(3L);
        prCoordinator.setRoleType("单角色");
        prCoordinator.setRoleAvatar("pr_coordinator_avatar.jpg");
        prCoordinator.setBackstory("""
                维护和提升公司的公众形象，处理媒体关系，组织新闻发布会。
                管理危机公关事件，及时响应社会舆论，维护公司声誉。
                协调内外部沟通，确保信息传递准确无误。
                """);
        prCoordinator.setResponsibilities(prCoordinator.getBackstory());

// 添加额外的角色以达到16个
        RoleTemplateEntity uiUxDesigner = new RoleTemplateEntity();
        uiUxDesigner.setRoleName("UI/UX设计师");
        uiUxDesigner.setIndustryCatalog(1L);
        uiUxDesigner.setRoleType("单角色");
        uiUxDesigner.setRoleAvatar("ui_ux_designer_avatar.jpg");
        uiUxDesigner.setBackstory("""
                设计直观易用的产品界面，提升用户体验，增加用户满意度。
                开展用户研究，收集用户反馈，不断优化设计。
                协作开发团队，确保设计方案顺利实施。
                """);
        uiUxDesigner.setResponsibilities(uiUxDesigner.getBackstory());

        RoleTemplateEntity dataAnalyst = new RoleTemplateEntity();
        dataAnalyst.setRoleName("数据分析师");
        dataAnalyst.setIndustryCatalog(1L);
        dataAnalyst.setRoleType("单角色");
        dataAnalyst.setRoleAvatar("data_analyst_avatar.jpg");
        dataAnalyst.setBackstory("""
                分析业务数据，挖掘有价值的信息，为决策提供数据支持。
                构建数据模型，预测市场趋势，辅助产品优化。
                定期生成报表，监控关键指标，帮助管理层把握业务动态。
                """);
        dataAnalyst.setResponsibilities(dataAnalyst.getBackstory());


        return Arrays.asList(
                architect,
                seniorDeveloper,
                tester,
                devOps,
                operationsEngineer,
                securityEngineer,
                dba,
                businessAnalyst,
                salesRepresentative,
                partnershipManager,
                productManager,
                marketingSpecialist,
                copywriterEditor,
                prCoordinator,
                uiUxDesigner,
                dataAnalyst
        );
    }

}
