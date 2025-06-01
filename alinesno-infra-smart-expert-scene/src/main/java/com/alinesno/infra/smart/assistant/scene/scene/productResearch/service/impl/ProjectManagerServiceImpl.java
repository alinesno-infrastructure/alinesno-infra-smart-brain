package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.CommitterInfo;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitCommitInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitInfoDto;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectManagerDTO;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.mapper.ProjectManagerMapper;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.properties.GitImportConfig;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.scheme.SchemeService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectGitCommitService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectManagerService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectTeamMemberService;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools.AgentAnalysisTool;
import com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools.GitUtils;
import com.alinesno.infra.smart.scene.entity.ProjectGitCommitEntity;
import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
import com.alinesno.infra.smart.scene.entity.ProjectTeamMemberEntity;
import com.alinesno.infra.smart.scene.enums.ProjectStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Scope("prototype")
@Service
public class ProjectManagerServiceImpl extends IBaseServiceImpl<ProjectManagerEntity, ProjectManagerMapper> implements IProjectManagerService {

    private final GitImportConfig gitImportConfig;
    private final ScheduledExecutorService taskExecutor;

     @Autowired
    private IProjectTeamMemberService projectTeamConfigService;

    @Autowired
    private GitUtils gitUtils;

    @Autowired
    private SchemeService schemeService ;

    @Autowired
    private AgentAnalysisTool agentAnalysisTool;

    @Autowired
    private IProjectGitCommitService projectGitCommitService;

    // 记录用户当前正在处理的项目
    private final ConcurrentHashMap<Long, Long> userProcessingProjects = new ConcurrentHashMap<>();

    // 任务状态追踪：项目ID -> 重试次数
    private final ConcurrentHashMap<Long, Integer> taskRetryCount = new ConcurrentHashMap<>();

    // 最大重试次数
    private static final int MAX_RETRIES = 3;

    // 定时任务
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public ProjectManagerServiceImpl(GitImportConfig gitImportConfig) {
        this.gitImportConfig = gitImportConfig;
        // 创建支持定时任务的线程池
        this.taskExecutor = Executors.newScheduledThreadPool(
                Math.max(10, gitImportConfig.getMaxConcurrentProjects() * 2));
    }

    @Override
    public List<ProjectManagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query) {
        page.setPageNum(0);
        page.setPageSize(20);

        Page<ProjectManagerEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());

        LambdaQueryWrapper<ProjectManagerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectManagerEntity::getOrgId, query.getOrgId());
        wrapper.orderByDesc(ProjectManagerEntity::getAddTime);

        pageBean = page(pageBean, wrapper);

        return pageBean.getRecords();
    }

    @Override
    public ProjectManagerDTO getProjectDetail(Long id) {
        ProjectManagerEntity entity = baseMapper.selectById(id);
        return entity != null ? convertToDTO(entity) : null;
    }

    /**
     * 业务逻辑:<br/>
     * 1. 每个用户同时只能有1个项目处于导入中状态 <br/>
     * 2. 项目导入完成后会自动更新状态 <br/>
     * 3. 可以配置最大并发项目数(当前仍为每个用户1个)
     * @param gitInfoDto
     * @return
     */
    @Override
    @Transactional
    public ProjectManagerDTO importGitProject(GitInfoDto gitInfoDto) {
        Long operatorId = gitInfoDto.getOperatorId();
        if (operatorId == null) {
            throw new IllegalArgumentException("操作员ID不能为空");
        }

        // 检查用户是否有正在处理的项目
        checkUserHasProcessingProject(operatorId);

        // 创建项目实体并保存到数据库
        ProjectManagerEntity project = new ProjectManagerEntity();
        CopyOptions copyOptions = CopyOptions.create()
                .ignoreNullValue()
                .ignoreError();
        BeanUtil.copyProperties(gitInfoDto, project, copyOptions);

        project.setProjectRepoUrl(gitInfoDto.getGitUrl());
        project.setProjectName(gitInfoDto.getProjectName());
        project.setProjectType("git");
        project.setAuthUsername(gitInfoDto.getIsAuth() ? gitInfoDto.getGitUsername() : null);
        project.setAuthPassword(gitInfoDto.getIsAuth() ? gitInfoDto.getGitPassword() : null);
        project.setLastSyncTime(new Date(0));
        project.setStatus(ProjectStatus.PROCESSING.name()); // 直接设置为处理中
        project.setAddTime(new Date());
        project.setUpdateTime(new Date());
        project.setSceneId(gitInfoDto.getSceneId());

        project.setOrgScheme(String.valueOf(project.getOrgId()));

        save(project);
        schemeService.initSchemaAndTables(project.getOrgScheme() , project);

        // 记录用户正在处理此项目
        userProcessingProjects.put(operatorId, project.getId());

        // 执行异步采集任务
        executeTaskAsync(project, operatorId, gitInfoDto);

        // 返回项目信息
        return convertToDTO(project);
    }

    private void checkUserHasProcessingProject(Long operatorId) {
        Long processingProjectId = userProcessingProjects.get(operatorId);
        if (processingProjectId != null) {
            ProjectManagerEntity processingProject = baseMapper.selectById(processingProjectId);
            if (processingProject != null &&
                    (ProjectStatus.PROCESSING.name().equals(processingProject.getStatus()) ||
                            ProjectStatus.QUEUED.name().equals(processingProject.getStatus()))) {
                throw new RuntimeException("用户已有正在处理的项目，不能同时导入多个项目");
            } else {
                // 项目状态已变更，清除记录
                userProcessingProjects.remove(operatorId);
            }
        }
    }

    private void executeTaskAsync(ProjectManagerEntity project, Long operatorId, GitInfoDto gitInfoDto) {
        taskExecutor.submit(() -> {
            try {
                log.info("开始处理项目{}，仓库URL: {}", project.getId(), project.getProjectRepoUrl());

                gitUtils.setChannelStreamId(gitInfoDto.getLogChannelStreamId());

                // 克隆或拉取仓库
                String localPath = gitUtils.cloneRepository(
                        project.getProjectRepoUrl(),
                        getLocalStoragePath(project.getId()),
                        project.getAuthUsername(),
                        project.getAuthPassword()
                );

                // 获取到Git里面所有的committer信息，包括用户名和邮箱
                List<CommitterInfo> committers = gitUtils.getAllCommitters(localPath);
                List<ProjectTeamMemberEntity> teamMembers = saveCommittersToDatabase(committers, project);
                schemeService.saveCommitterToDatabase(teamMembers, project);

                // 执行全量或增量采集
                List<GitCommitInfoDto> commits;
                if (project.getLastSyncTime().getTime() == 0) {
                    commits = gitUtils.collectCommitInfo(localPath);
                    log.info("项目{}全量采集完成，提交记录数: {}", project.getId(), commits.size());
                } else {
                    commits = gitUtils.collectIncrementalCommitInfo(localPath, project.getLastSyncTime());
                    log.info("项目{}增量采集完成，新增提交记录数: {}", project.getId(), commits.size());
                }

                // 解析获取到的提交信息
                agentAnalysisTool.collectAnalysis(commits, project, gitInfoDto);

                // 保存提交信息到数据库
                List<ProjectGitCommitEntity> commitEntities = saveCommitsToDatabase(commits, project);
                schemeService.saveCommitsToDatabase(commitEntities, project);

                // 更新项目最后同步时间和状态
                project.setLastSyncTime(new Date());
                project.setStatus(ProjectStatus.COMPLETED.name());
                project.setUpdateTime(new Date());
                baseMapper.updateById(project);

                log.info("项目{}处理成功完成", project.getId());

                gitUtils.sendMessage(gitInfoDto.getLogChannelStreamId(), "finish");

                // 如果配置了同步间隔，自动开启定时增量采集
                if (project.getSyncInterval() > 0) {
                    scheduleNextIncrementalCollect(project);
                }

            } catch (Exception e) {
                handleTaskFailure(project, operatorId, gitInfoDto, e);
            } finally {
                // 清理本地仓库
                cleanLocalRepository(project.getId());
                // 从处理中集合移除
                userProcessingProjects.remove(operatorId);
            }
        });
    }

    /**
     * 保存提交者信息到数据库
     *
     * @param committers
     * @param project
     * @return
     */
    private List<ProjectTeamMemberEntity> saveCommittersToDatabase(List<CommitterInfo> committers, ProjectManagerEntity project) {

        List<ProjectTeamMemberEntity>  teams = new ArrayList<>();
         for (CommitterInfo committer : committers) {
            ProjectTeamMemberEntity team = new ProjectTeamMemberEntity();

            team.setOrgId(project.getOrgId());
            team.setDepartmentId(project.getDepartmentId());
            team.setOperatorId(project.getOperatorId());

            team.setCommitName(committer.getName());
            team.setUserEmail(committer.getEmail());

            teams.add(team) ;
        }

        return projectTeamConfigService.updateOrgTeam(teams , project) ;
    }

    private void handleTaskFailure(ProjectManagerEntity project, Long operatorId, GitInfoDto gitInfoDto, Exception e) {
        int retryCount = taskRetryCount.getOrDefault(project.getId(), 0);

        if (isRetriableException(e) && retryCount < MAX_RETRIES) {
            // 可重试异常，安排重试
            retryCount++;
            taskRetryCount.put(project.getId(), retryCount);

            project.setStatus(ProjectStatus.RETRYING.name());
            project.setUpdateTime(new Date());
            baseMapper.updateById(project);

            log.warn("项目{}处理失败，将在{}秒后重试 (第{}次/{}次): {}",
                    project.getId(), retryCount * 5, retryCount, MAX_RETRIES, e.getMessage());

            // 使用调度线程池安排重试
            taskExecutor.schedule(() -> {
                try {
                    // 直接重试执行任务
                    executeTaskAsync(project, operatorId, gitInfoDto);
                } catch (Exception ex) {
                    log.error("项目{}重试失败", project.getId(), ex);
                    markProjectFailed(project);
                }
            }, retryCount * 5L, TimeUnit.SECONDS);
        } else {
            // 不可重试或达到最大重试次数，标记为失败
            log.error("项目{}处理失败，已达到最大重试次数或不可重试: {}", project.getId(), e.getMessage());
            markProjectFailed(project);
        }
    }

    private boolean isRetriableException(Exception e) {
        // 判断是否为可重试的异常类型
        return e instanceof java.io.IOException ||
                e.getMessage() != null && e.getMessage().contains("timeout");
    }

    private void markProjectFailed(ProjectManagerEntity project) {
        project.setStatus(ProjectStatus.FAILED.name());
        project.setUpdateTime(new Date());
        baseMapper.updateById(project);
        taskRetryCount.remove(project.getId());
        // 从处理中集合移除
        userProcessingProjects.remove(project.getOperatorId());
    }

    private String getLocalStoragePath(Long projectId) {
        String basePath = gitImportConfig.getLocalRepoPath();

        if (basePath == null || basePath.trim().isEmpty()) {
            basePath = System.getProperty("java.io.tmpdir");
            basePath = Paths.get(basePath, ".alinesno-import-git").toString();
        }

        File baseDir = new File(basePath);
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            log.error("无法创建基础目录: {}", basePath);
            throw new RuntimeException("无法创建存储目录");
        }

        return Paths.get(basePath, String.valueOf(projectId)).toString();
    }

    private void cleanLocalRepository(Long projectId) {
        File localDir = new File(getLocalStoragePath(projectId));
        if (localDir.exists()) {
            try {
                deleteDirectory(localDir);
                log.info("成功清理项目{}的本地仓库", projectId);
            } catch (Exception e) {
                log.error("清理项目{}的本地仓库失败: {}", projectId, e.getMessage());
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        log.warn("无法删除文件: {}", file.getAbsolutePath());
                    }
                }
            }
        }
        if (!directory.delete()) {
            log.warn("无法删除目录: {}", directory.getAbsolutePath());
        }
    }

    private ProjectManagerDTO convertToDTO(ProjectManagerEntity entity) {
        ProjectManagerDTO dto = new ProjectManagerDTO();
        BeanUtil.copyProperties(entity, dto);
        return dto;
    }

    // 分批保存提交信息到数据库
    private List<ProjectGitCommitEntity> saveCommitsToDatabase(List<GitCommitInfoDto> commits, ProjectManagerEntity project) {
        final int batchSize = 500;

        for (int i = 0; i < commits.size(); i += batchSize) {
            List<GitCommitInfoDto> batch = commits.subList(i, Math.min(i + batchSize, commits.size()));

            List<ProjectGitCommitEntity> entities = batch.stream()
                    .map(commit -> {
                        ProjectGitCommitEntity entity = new ProjectGitCommitEntity();
                        BeanUtil.copyProperties(commit, entity);

                        entity.setProjectId(project.getId());
                        entity.setProjectName(project.getProjectName());

                        entity.setAddTime(new Date());
                        return entity;
                    })
                    .collect(Collectors.toList());

            try {
                projectGitCommitService.saveBatch(entities);
                return entities ;
            } catch (Exception e) {
                log.error("保存项目{}的提交信息失败，批次记录数: {}", project.getProjectName(), entities.size(), e);
                throw new RuntimeException("保存提交信息失败", e);
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        // 优雅关闭线程池
        if (taskExecutor != null && !taskExecutor.isShutdown()) {
            taskExecutor.shutdown();
            try {
                if (!taskExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    taskExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                taskExecutor.shutdownNow();
            }
        }
    }

    // 增量采集方法
    private void incrementalCollect(ProjectManagerEntity project) {
        if (project == null || project.getId() == null) {
            log.error("增量采集失败：项目信息不完整");
            return;
        }

        if (project.getSyncInterval() <= 0) {
            log.info("项目{}未配置有效同步间隔，跳过增量采集", project.getId());
            return;
        }

        long lastSyncTime = project.getLastSyncTime().getTime();
        long nextSyncTime = lastSyncTime + project.getSyncInterval() * 60 * 1000;
        long delay = Math.max(0, nextSyncTime - System.currentTimeMillis());

        ScheduledFuture<?> future = taskExecutor.schedule(() -> {
            try {
                log.info("开始执行项目{}的定时增量采集", project.getId());

                ProjectManagerEntity currentProject = baseMapper.selectById(project.getId());
                if (currentProject == null ||
                        !ProjectStatus.COMPLETED.name().equals(currentProject.getStatus()) ||
                        userProcessingProjects.getOrDefault(currentProject.getOperatorId(), null) != null) {
                    log.info("项目{}状态不允许增量采集，跳过本次任务", project.getId());
                    return;
                }

                // 检查用户是否有正在处理的项目
                checkUserHasProcessingProject(currentProject.getOperatorId());

                // 更新状态为处理中
                currentProject.setStatus(ProjectStatus.PROCESSING.name());
                currentProject.setUpdateTime(new Date());
                baseMapper.updateById(currentProject);

                // 记录用户正在处理此项目
                userProcessingProjects.put(currentProject.getOperatorId(), currentProject.getId());

                // 创建临时GitInfoDto对象
                GitInfoDto gitInfoDto = new GitInfoDto();
                gitInfoDto.setChannelStreamId(IdUtil.nanoId());

                // 执行异步采集任务
                executeTaskAsync(currentProject, currentProject.getOperatorId(), gitInfoDto);

            } catch (Exception e) {
                log.error("项目{}增量采集任务失败: {}", project.getId(), e.getMessage());
            } finally {
                // 重新注册下次定时任务
                scheduleNextIncrementalCollect(project);
            }
        }, delay, TimeUnit.MILLISECONDS);

        scheduledTasks.put(project.getId(), future);
    }

    // 调度下次增量采集任务
    @Override
    public void scheduleNextIncrementalCollect(ProjectManagerEntity project) {
        if (project.getSyncInterval() > 0) {
            incrementalCollect(project);
        } else {
            cancelIncrementalTask(project.getId());
        }
    }

    @Override
    public boolean isExistGitProject(Long orgId, String gitUrl) {
        LambdaQueryWrapper<ProjectManagerEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProjectManagerEntity::getOrgId, orgId)
                .eq(ProjectManagerEntity::getProjectRepoUrl, gitUrl);

        return count(query) > 0;
    }

    @Override
    public boolean isImportingGitProject(Long orgId, String gitUrl) {
        LambdaQueryWrapper<ProjectManagerEntity> query = new LambdaQueryWrapper<>();

        query.eq(ProjectManagerEntity::getOrgId, orgId)
                .eq(ProjectManagerEntity::getProjectRepoUrl, gitUrl)
                .and(qw -> qw
                        .eq(ProjectManagerEntity::getStatus, ProjectStatus.PROCESSING.name())
                        .or()
                        .eq(ProjectManagerEntity::getStatus, ProjectStatus.QUEUED.name())
                );

        return count(query) > 0;
    }

    // 取消定时任务
    public void cancelIncrementalTask(Long projectId) {
        ScheduledFuture<?> future = scheduledTasks.remove(projectId);
        if (future != null && !future.isDone()) {
            future.cancel(true);
            log.info("已取消项目{}的增量采集任务", projectId);
        }
    }

    public List<ProjectManagerEntity> listByStatus(String status) {
        LambdaQueryWrapper<ProjectManagerEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProjectManagerEntity::getStatus, status);
        return baseMapper.selectList(query);
    }
}