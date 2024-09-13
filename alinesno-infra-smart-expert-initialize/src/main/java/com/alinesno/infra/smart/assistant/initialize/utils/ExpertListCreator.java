package com.alinesno.infra.smart.assistant.initialize.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class ExpertListCreator {

    public static List<IndustryRoleEntity> createExpertList() {
        List<IndustryRoleEntity> expertList = new ArrayList<>();

        // 示例团队专家演示
        addExpert(expertList, "1808349384961875969", "考核题目生成专家", 9527L, "用于一些面试、考核培训题目生成并导出成Word/Excel等或者Markdown等", "MNigoYFn"); //P
        addExpert(expertList, "1808349417669058562", "毕业论文大纲设计专家", 9527L, "用于毕业论文大纲的拟定，包括论文标题的规划", "mMcYgPEU" );
        addExpert(expertList, "1808349453119315970", "毕业论文内容编辑专家", 9527L, "毕业论文内容生成和集成，并导出成Word文档输出", "GMFJPycO");

        // 方案编写团队
        addExpert(expertList, "1808349924122877953", "解决方案大纲设计专家", 9528L, "解决方案标题大纲拟定和编写", "lUYQZGWP");
        addExpert(expertList, "1808349957312405505", "解决方案内容编写专家", 9528L, "解决方案内容生成并导出成Word/Excel等或者Markdown等", "HOkuqHBm");

        // 产品运维团队(数据库备份/代码定时同步/K8S状态查询检测)
        addExpert(expertList, "1808350041093627906", "数据库备份专家", 9528L, "用于AIP产品运营数据库备份管理和配置", "ensXkJnK");
        addExpert(expertList, "1808350152938938369", "Git代码备份专家", 9528L, "用于AIP基线git代码备份管理", "qoNUPQFN");
        addExpert(expertList, "1808350127865389057", "K8S运行状态专家", 9528L, "用于AIP产品运行K8S状态查询，生成每日运维报告并导出Word", "BHIWVhzr");

        // 产品运营团队(产品问答客服/业务咨询)
        addExpert(expertList, "1808349647059738625", "业务咨询客服", 9528L, "AIP业务咨询问题，用于做客服类的咨询服务，定义于产品业务对外咨询类", "pRFDrSJH");  //P
        addExpert(expertList, "1808350238808924161", "产品列表客服", 9528L, "AIP产品问答客服，用于产品有多少还有有哪些哪类型的产品资产", "OAbtlQYU"); //P
        addExpert(expertList, "1808350003370057729", "邮件发送专家", 9528L, "团队内部人员邮件发送和附近内容发送", "RmCXXzKU");

        List<PostBean> posts = createPromptPostEntity() ;

        expertList.forEach(entity -> {
            String promptContent = posts.stream().filter(post -> post.getId() == entity.getId()).findFirst().map(PostBean::getPrompt_content).orElse("");
            entity.setPromptContent(promptContent);
        });

        return expertList;
    }

    @SneakyThrows
    public static List<PostBean> createPromptPostEntity(){
        final String jsonArr = ResourceUtil.readUtf8Str("prompt-post.json");
        return JSONArray.parseArray(jsonArr , PostBean.class);
    }

    @Data
    public static class PostBean {
        private long id ;
        private String prompt_content ;
    }

    private static void addExpert(List<IndustryRoleEntity> list,
                                  String roleAvatar,
                                  String roleName,
                                  long industryCatalog,
                                  String responsibilities,
                                  String chainId) {
        extracted(list, roleAvatar, roleName, industryCatalog, responsibilities, chainId, null);
    }

    private static void extracted(List<IndustryRoleEntity> list, String roleAvatar, String roleName, long industryCatalog, String responsibilities, String chainId, String fileType) {
        IndustryRoleEntity expert = new IndustryRoleEntity();
        expert.setRoleAvatar(roleAvatar);
        expert.setId(Long.valueOf(roleAvatar));
        expert.setRoleName(roleName);
        expert.setIndustryCatalog(industryCatalog);
        expert.setResponsibilities(responsibilities);
        expert.setRoleLevel("高级工程师");

        if(fileType != null){
            expert.setFieldProp(fileType);
        }

        expert.setChainId(AssistantConstants.PREFIX_ASSISTANT + chainId);

        list.add(expert);
    }
}