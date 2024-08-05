package com.alinesno.infra.smart.assistant.initialize.utils;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;

import java.util.ArrayList;
import java.util.List;

public class ExpertListCreator {

    public static List<IndustryRoleEntity> createExpertList() {
        List<IndustryRoleEntity> expertList = new ArrayList<>();

        // 定义专家列表中的每个专家
        addExpert(expertList, "1808076798197686273", "软文编辑(SEO专家)", "1742481532649570306", "用于文案的SEO描述", "高级工程师");
        addExpert(expertList, "1808349307635687426", "K8S报告生成Agent", "1742481439657656321", "生成K8S报告内容，与其它平台进行交互", "高级工程师");
        addExpert(expertList, "1808349384961875969", "K8S问题排查专家", "1742481439657656321", "针对于K8S问题排查并给出合理建议", "高级工程师");
        addExpert(expertList, "1808349417669058562", "软文编辑(集成内容平台)", "1742481532649570306", "集成CMS平台服务，集成内容平台", "高级工程师", "1747618610907394049");
        addExpert(expertList, "1808349453119315970", "测试插件角色", "1742481225890758657", "测试插件角色", "高级工程师", "1747032636625973250");
        addExpert(expertList, "1808349526427361282", "数据库设计(实体模型专家)", "1742481317049761794", "数据库设计(实体模型专家)", "高级工程师", "1746667911722803202");
        addExpert(expertList, "1808349483804844034", "数据库设计(实体设计专家)", "1742481317049761794", "数据库设计(实体设计专家)", "高级工程师", "1746667792004784130");
        addExpert(expertList, "1808349555338698754", "SpringBoot接口编写工程师", "1742481263224258561", "根据API接口使用SpringBoot编写Controller层接口代码", "高级工程师");
        addExpert(expertList, "1808349584682049538", "Ansible自动化工程师", "1742481439657656321", "实现Ansible自动化脚本处理", "高级工程师");
        addExpert(expertList, "1808349924122877953", "K8S脚本生成工程师", "1742481439657656321", "生成k8s脚本和部署服务脚本", "高级工程师");
        addExpert(expertList, "1808349978535583746", "测试工程师(用例配置)", "1742481263224258561", "测试工程师(用例配置)", "高级工程师");
        addExpert(expertList, "1808349957312405505", "测试工程师(接口生成)", "1742481263224258561", "测试工程师(接口生成)", "高级工程师");
        addExpert(expertList, "1808350003370057729", "开发编码规范专家", "1744655683264892929", "开发技术规范说明", "高级工程师");
        addExpert(expertList, "1808350041093627906", "产品客户服务专家", "1742481773473923074", "产品客户服务专家，用于产品问答", "高级工程师");
        addExpert(expertList, "1808350152938938369", "代码生成专家", "1744655683264892929", "根据数据库生成代码结构", "高级工程师");
        addExpert(expertList, "1808350127865389057", "学习内容推荐专家", "1742481140373094402", "学习内容推荐专家", "高级工程师", "1745421374942576641");
        addExpert(expertList, "1808350176838082561", "软文编辑(文案编写)", "1742481532649570306", "软文推广(文案编写)", "高级工程师", "1745079855849852929");
        addExpert(expertList, "1808350214041559041", "软文编辑(目录大纲)", "1742481532649570306", "软文推广(目录大纲)编写", "高级工程师", "1745079705807015937");
        addExpert(expertList, "1808350238808924161", "需求分析(项目非功能性需求)", "1742481225890758657", "需求分析_项目非功能性需求", "高级工程师", "1742415641032724482");
        addExpert(expertList, "1808349886575468546", "需求分析(项目功能细化分析专家)", "1742481225890758657", "需求分析_项目功能细化分析专家", "高级工程师", "1742415517179121666");
        addExpert(expertList, "1808349839242747906", "需求分析(项目功能分析专家)", "1742481225890758657", "需求分析_项目功能分析专家", "高级工程师", "1742415455845814273");
        addExpert(expertList, "1808349808288784386", "需求分析(项目介绍分析专家)", "1742481225890758657", "需求分析_项目介绍分析专家", "高级工程师", "1742415392373411841");
        addExpert(expertList, "1808349774163927042", "需求分析(需求文档分析专家)", "1742481225890758657", "需求分析_需求文档分析专家", "高级工程师", "1742415269484498945");
        addExpert(expertList, "1808349751040729090", "项目菜单生成专家", "1744655683264892929", "用于平台菜单生成专家", "高级工程师");
        addExpert(expertList, "1808349728060137473", "服务工程生成专家", "1744655683264892929", "用于服务工程生成专家", "高级工程师", "1741645177548931074");
        addExpert(expertList, "1808349692358221826", "数据库设计专家", "1744655683264892929", "数据库设计专家，用于数据库设计", "高级工程师", "1740826360738152450");
        addExpert(expertList, "1808349647059738625", "需求编写分析师", "1742481225890758657", "需求拆分和编写", "高级工程师", "1740826553479004162");
        addExpert(expertList, "1808349617330511874", "培训题目设计专家", "1742481140373094402", "培训题目设计专家，用于考核题目设计", "高级工程师", "1740826156165169153");

        return expertList;
    }

    private static void addExpert(List<IndustryRoleEntity> list, String roleAvatar, String roleName, String industryCatalog, String responsibilities, String roleLevel) {
        IndustryRoleEntity expert = new IndustryRoleEntity();
        expert.setRoleAvatar(roleAvatar);
        expert.setRoleName(roleName);
        expert.setIndustryCatalog(Long.parseLong(industryCatalog));
        expert.setResponsibilities(responsibilities);
        expert.setRoleLevel(roleLevel);

        // 如果有 chainId 字段，则添加
        if (list.size() > 0 && ((IndustryRoleEntity) list.get(list.size() - 1)).getChainId() != null) {
            expert.setChainId(((IndustryRoleEntity) list.get(list.size() - 1)).getChainId());
        }

        list.add(expert);
    }

    private static void addExpert(List<IndustryRoleEntity> list, String roleAvatar, String roleName, String industryCatalog, String responsibilities, String roleLevel, String chainId) {
        IndustryRoleEntity expert = new IndustryRoleEntity();
        expert.setRoleAvatar(roleAvatar);
        expert.setRoleName(roleName);
        expert.setIndustryCatalog(Long.parseLong(industryCatalog));
        expert.setResponsibilities(responsibilities);
        expert.setRoleLevel(roleLevel);
        expert.setChainId(Long.parseLong(chainId));

        list.add(expert);
    }
}