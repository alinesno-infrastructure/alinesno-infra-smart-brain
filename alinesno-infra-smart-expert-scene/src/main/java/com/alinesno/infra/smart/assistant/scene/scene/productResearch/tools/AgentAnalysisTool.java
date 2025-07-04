//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;
//
//import com.alinesno.infra.common.facade.datascope.PermissionQuery;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitCommitInfoDto;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitInfoDto;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.prompt.PromptHandle;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.properties.GitImportConfig;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectResearchSceneService;
//import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
//import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
//import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
//import com.alinesno.infra.smart.scene.entity.ProjectResearchSceneEntity;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// *  AgentAnalysisTool，解析工具
// */
//@Slf4j
//@Component
//public class AgentAnalysisTool {
//
//    @Autowired
//    private IProjectResearchSceneService projectResearchSceneService; ;
//
//    @Autowired
//    private GitImportConfig gitImportConfig;
//
//    @Autowired
//    private IIndustryRoleService roleService ;
//
//    /**
//     *  收集分析并解析出内容
//     * @param commits
//     * @param project
//     */
//    public void collectAnalysis(List<GitCommitInfoDto> commits, ProjectManagerEntity project , GitInfoDto gitInfoDto) {
//
//
//        Long sceneId = project.getSceneId() ;
//
//        PermissionQuery query = new PermissionQuery() ;
//        query.setOrgId(project.getOrgId());
//        query.setDepartmentId(project.getDepartmentId());
//        query.setOperatorId(project.getOperatorId());
//
//        ProjectResearchSceneEntity entity = projectResearchSceneService.getBySceneId(sceneId , query) ;
//        int count = 0 ;
//
//        for(GitCommitInfoDto commit : commits){
//            MessageTaskInfo taskInfo = new MessageTaskInfo() ;
//
//            String promptText = PromptHandle.getPrompt(commit) ;
//
//            taskInfo.setRoleId(Long.parseLong(entity.getProcessCollectorEngineer()));
//            taskInfo.setChannelStreamId(gitInfoDto.getChannelStreamId());
//            taskInfo.setChannelId(sceneId);
//            taskInfo.setSceneId(sceneId);
//            taskInfo.setText(promptText);
//
//            // 优先获取到结果内容
//            roleService.runRoleAgent(taskInfo) ;
//            log.debug("genContent = {}", taskInfo.getFullContent());
//
//            commit.setOrgId(project.getOrgId());
//            commit.setDepartmentId(project.getDepartmentId());
//            commit.setOperatorId(project.getOperatorId());
//
//            commit.setGitCommitMessage(taskInfo.getFullContent());
//
//            count ++ ;
//            log.debug( "count = {}" , count);
//        }
//
//    }
//
//}
