//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.scheme;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.properties.GitImportConfig;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTeamMemberService;
//import com.alinesno.infra.smart.scene.entity.ProjectGitCommitEntity;
//import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
//import com.alinesno.infra.smart.scene.entity.ProjectTeamMemberEntity;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 操作不同组织租户信息，这里是通过mysql数据库的进一步清理梳理，可以理解成ODS(mysql) -> DWD(postgresql)表结构的变化
// * SchemeService
// */
//@Slf4j
//@Component
//public class SchemeService {
//
//    @Autowired
//    private IProjectTeamMemberService projectTeamMemberService ;
//
//    @Autowired
//    private GitImportConfig gitImportConfig;
//
//    private static SchemaUtils schemaUtils ;
//
//    public SchemaUtils getSchemaUtils() {
//        if(schemaUtils == null){
//            schemaUtils = new SchemaUtils(getDataSource());
//        }
//        return schemaUtils ;
//    }
//
//    /**
//     * 初始化方案和表结构
//     *
//     * @param orgScheme
//     * @param project
//     */
//    public void initSchemaAndTables(String orgScheme, ProjectManagerEntity project) {
//
//        getSchemaUtils().initSchemaAndTables(orgScheme);
//
//        // 插入项目信息
//        SchemaUtils.Project sP = new SchemaUtils.Project() ;
//
//        sP.setId(project.getId());
//        sP.setProjectName(project.getProjectName());
//
//        // 判断项目是否存在 projectExists
//         if (schemaUtils.projectExists(orgScheme, project.getId())) {
//            log.debug( "项目已存在，不进行插入");
//            return ;
//         }
//
//         int count = schemaUtils.insertProject(orgScheme, sP) ;
//         log.debug( "插入项目信息：{}", count);
//    }
//
//    private DataSource getDataSource() {
//
//        // 返回Druid数据源
//        DruidDataSource dataSource = new DruidDataSource();
//
//        dataSource.setUrl(gitImportConfig.getUrl()) ;
//        dataSource.setUsername(gitImportConfig.getUsername());
//        dataSource.setPassword(gitImportConfig.getPassword());
//        dataSource.setMaxWait(15000);
//        dataSource.setBreakAfterAcquireFailure(true);
//
//        return dataSource;
//    }
//
//    /**
//     * 保存贡献者信息到数据库
//     * @param committers
//     * @param project
//     */
//    public void saveCommitterToDatabase(List<ProjectTeamMemberEntity> committers, ProjectManagerEntity project) {
//
//        if (committers == null || committers.isEmpty()) {
//            return;
//        }
//
//        List<SchemaUtils.TeamMember> teamMembers = new ArrayList<>() ;
//
//        // 使用teamMemberExists判断是否存在角色，如果存在则不再插入
//         for (ProjectTeamMemberEntity committer : committers) {
//              if (schemaUtils.teamMemberExists(project.getOrgScheme(), committer.getUserEmail())) {
//                 log.debug( "角色已存在，不进行插入");
//                 continue;
//             }
//
//              if(committer.getId() == null){
//                  log.debug( "角色ID为空，不进行插入");
//                  continue;
//              }
//
//             SchemaUtils.TeamMember member = new SchemaUtils.TeamMember();
//
//             member.setId(committer.getId());
//             member.setChineseName(committer.getRealName() == null ? committer.getCommitName() : committer.getRealName());
//             member.setEmail(committer.getUserEmail());
//             member.setPosition(committer.getJobTitle() == null ? "开发工程师" : committer.getJobTitle());
//             member.setResponsibility(committer.getJobResponsibility() == null ?  "负责软件开发和代码能力编写" : committer.getJobResponsibility());
//
//             teamMembers.add(member) ;
//         }
//
//         if(!teamMembers.isEmpty()){
//             int count =  getSchemaUtils().batchInsertTeamMembers(project.getOrgScheme() , teamMembers);
//             log.debug( "保存贡献者信息到数据库：{}", count);
//         }
//
//    }
//
//    /**
//     *  保存提交信息到数据库
//     * @param commits
//     * @param project
//     */
//    public void saveCommitsToDatabase(List<ProjectGitCommitEntity> commits, ProjectManagerEntity project) {
//        getSchemaUtils() ;
//        if (commits == null || commits.isEmpty()) {
//            return;
//        }
//
//        // 查询出所有的成员
//        List<ProjectTeamMemberEntity>  members = projectTeamMemberService.
//                list(new LambdaQueryWrapper<ProjectTeamMemberEntity>().eq(ProjectTeamMemberEntity::getOrgId, project.getOrgId()));
//
//        List<SchemaUtils.GitCommitMessage> messages = new ArrayList<>();
//
//        for (ProjectGitCommitEntity commit : commits) {
//            SchemaUtils.GitCommitMessage message = new SchemaUtils.GitCommitMessage();
//
//            message.setId(commit.getId());
//            message.setCreateTime(commit.getCommitTime());
//            message.setGitFilePath(commit.getCommittedFiles());
//            message.setCommitMessage(commit.getGitCommitMessage() == null?commit.getCommitContent(): commit.getGitCommitMessage());
//            message.setRole(commit.getChangeType());
//            message.setMemberId(getMemberIdByEmail(commit.getCommitterEmail() , members));
//            message.setProjectId(commit.getProjectId());
//            message.setCommitDiff(commit.getCommitDiff());
//
//            messages.add(message);
//        }
//
//        int count = schemaUtils.batchInsertGitCommitMessages(project.getOrgScheme(), messages) ;
//        log.debug( "保存提交信息到数据库：{}", count);
//    }
//
//    /**
//     * 根据邮箱获取成员ID
//     *
//     * @param committerEmail
//     * @param orgId
//     * @return
//     */
//    private Long getMemberIdByEmail(String committerEmail, List<ProjectTeamMemberEntity> orgId) {
//        for (ProjectTeamMemberEntity member : orgId) {
//            if (member.getUserEmail().equals(committerEmail)) {
//                return member.getId();
//            }
//        }
//        return 0L ;
//    }
//
//}
