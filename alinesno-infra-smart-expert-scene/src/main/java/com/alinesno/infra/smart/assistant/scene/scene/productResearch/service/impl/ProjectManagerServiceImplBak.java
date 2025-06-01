//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.bean.copier.CopyOptions;
//import cn.hutool.core.util.IdUtil;
//import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
//import com.alinesno.infra.common.facade.datascope.PermissionQuery;
//import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitCommitInfoDto;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitInfoDto;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.ProjectManagerDTO;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.mapper.ProjectManagerMapper;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.properties.GitImportConfig;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectGitCommitService;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.service.IProjectManagerService;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools.AgentAnalysisTool;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools.GitUtils;
//import com.alinesno.infra.smart.scene.entity.ProjectGitCommitEntity;
//import com.alinesno.infra.smart.scene.entity.ProjectManagerEntity;
//import com.alinesno.infra.smart.scene.enums.ProjectStatus;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.File;
//import java.nio.file.Paths;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Scope("prototype")
//@Service
//public class ProjectManagerServiceImplBak extends IBaseServiceImpl<ProjectManagerEntity, ProjectManagerMapper> implements IProjectManagerService {
//
//    private final GitImportConfig gitImportConfig;
//    private final ScheduledExecutorService retryExecutor;
//
//    @Autowired
//    private GitUtils gitUtils ;
//
//    @Autowired
//    private AgentAnalysisTool agentAnalysisTool;
//
//    @Autowired
//    private IProjectGitCommitService projectGitCommitService;
//
//    private final ScheduledExecutorService taskExecutor; // 修改类型为 ScheduledExecutorService
//
//    // 在类中新增定时任务相关成员变量
//    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
//
//    // 任务队列：用户ID -> 队列（按时间排序）
//    private final ConcurrentHashMap<Long, BlockingQueue<ProjectManagerEntity>> userTaskQueues = new ConcurrentHashMap<>();
//
//    // 正在处理的任务：用户ID -> 项目ID集合
//    private final ConcurrentHashMap<Long, Set<Long>> processingProjects = new ConcurrentHashMap<>();
//
//    // 任务状态追踪：项目ID -> 重试次数
//    private final ConcurrentHashMap<Long, Integer> taskRetryCount = new ConcurrentHashMap<>();
//
//    // 最大重试次数
//    private static final int MAX_RETRIES = 3;
//
//    public ProjectManagerServiceImplBak(GitImportConfig gitImportConfig) {
//        this.gitImportConfig = gitImportConfig;
//        // 创建支持定时任务的线程池
//        this.taskExecutor = Executors.newScheduledThreadPool(
//                Math.max(10, gitImportConfig.getMaxConcurrentProjects() * 2));
//        // 创建调度线程池用于重试机制
//        this.retryExecutor = Executors.newScheduledThreadPool(5);
//    }
//
//    @Override
//    public List<ProjectManagerEntity> pagerListByPage(DatatablesPageBean page, PermissionQuery query) {
//        page.setPageNum(0);
//        page.setPageSize(20);
//
//        Page<ProjectManagerEntity> pageBean = new Page<>(page.getPageNum(), page.getPageSize());
//
//        LambdaQueryWrapper<ProjectManagerEntity> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(ProjectManagerEntity::getOrgId, query.getOrgId()) ;
//        wrapper.orderByDesc(ProjectManagerEntity::getAddTime) ;
//
//        pageBean = page(pageBean, wrapper);
//
//        return pageBean.getRecords();
//    }
//
//    @Override
//    public ProjectManagerDTO getProjectDetail(Long id) {
//        ProjectManagerEntity entity = baseMapper.selectById(id);
//        return entity != null ? convertToDTO(entity) : null;
//    }
//
//    /**
//     * 业务逻辑:<br/>
//     * 1. 每个用户可以同时采集2个项目，如果超过2个，则进入排队，最多不能超过10个项目排队 <br/>
//     * 2. 如果用户当前项目采集完成之后，会自动的执行队列中的项目采集 <br/>
//     * 3. 项目的采集完成之后，会自动删除项目，并在项目中处理完成标识 <br/>
//     * 4. 可以采集多少个项目、队列多少个，这些都有限制，需要根据业务场景进行配置(提取出参数方便后期配置）
//     * @param gitInfoDto
//     * @return
//     */
//    @Override
//    @Transactional
//    public ProjectManagerDTO importGitProject(GitInfoDto gitInfoDto) {
//
//        Long operatorId = gitInfoDto.getOperatorId();
//        if (operatorId == null) {
//            throw new IllegalArgumentException("操作员ID不能为空");
//        }
//
//        // 1. 校验用户当前任务数（并发+排队）
//        checkTaskLimit(operatorId);
//
//        // 2. 创建项目实体并保存到数据库
//        ProjectManagerEntity project = new ProjectManagerEntity();
//        CopyOptions copyOptions = CopyOptions.create()
//                .ignoreNullValue()
//                .ignoreError() ;
//        BeanUtil.copyProperties(gitInfoDto, project , copyOptions);
//
//        project.setProjectRepoUrl(gitInfoDto.getGitUrl());
//
//        // 从git路径中解析出项目名称
//        project.setProjectName(gitInfoDto.getProjectName()) ; // GitRepoParser.parseGitRepo(gitInfoDto.getGitUrl()).getRepoFullName());
//        project.setProjectType("git"); // 固定为Git
//        project.setAuthUsername(gitInfoDto.getIsAuth() ? gitInfoDto.getGitUsername() : null);
//        project.setAuthPassword(gitInfoDto.getIsAuth() ? gitInfoDto.getGitPassword() : null);
//        project.setLastSyncTime(new Date(0)); // 初始化为0，表示未采集过
//        project.setStatus(ProjectStatus.QUEUED.name()); // 设置为排队状态
//        project.setAddTime(new Date());
//        project.setUpdateTime(new Date());
//        project.setSceneId(gitInfoDto.getSceneId());
//
//        save(project);
//
//        // 3. 将任务加入队列
//        addToQueue(operatorId, project);
//
//        // 4. 自动触发任务处理（若当前有空闲槽位）
//        triggerTaskProcessing(operatorId , gitInfoDto);
//
//        // 返回项目信息（需转换为DTO）
//        return convertToDTO(project);
//    }
//
//    private void checkTaskLimit(Long operatorId) {
//        int currentConcurrent = processingProjects.getOrDefault(operatorId, Collections.emptySet()).size();
//        BlockingQueue<ProjectManagerEntity> queue = getUserQueue(operatorId);
//
//        if (currentConcurrent >= gitImportConfig.getMaxConcurrentProjects() &&
//                queue.size() >= gitImportConfig.getMaxQueueSize()) {
//            throw new RuntimeException("用户任务数已达上限（并发" + gitImportConfig.getMaxConcurrentProjects() +
//                    "个，排队" + gitImportConfig.getMaxQueueSize() + "个）");
//        }
//    }
//
//    private BlockingQueue<ProjectManagerEntity> getUserQueue(Long operatorId) {
//        return userTaskQueues.computeIfAbsent(operatorId, k -> new LinkedBlockingQueue<>(
//                gitImportConfig.getMaxQueueSize()
//        ));
//    }
//
//    private void addToQueue(Long operatorId, ProjectManagerEntity project) {
//        BlockingQueue<ProjectManagerEntity> queue = getUserQueue(operatorId);
//        try {
//            if (!queue.offer(project, 10, TimeUnit.SECONDS)) {
//                throw new RuntimeException("任务队列已满，无法加入新任务");
//            }
//            log.info("用户{}的项目{}已加入排队队列", operatorId, project.getId());
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException("加入任务队列被中断", e);
//        }
//    }
//
//    private void triggerTaskProcessing(Long operatorId, GitInfoDto gitInfoDto) {
//        // 使用双重检查锁定确保线程安全
//        while (true) {
//            Set<Long> currentProcessing = processingProjects.computeIfAbsent(operatorId, k -> ConcurrentHashMap.newKeySet());
//            BlockingQueue<ProjectManagerEntity> queue = getUserQueue(operatorId);
//
//            // 检查是否有空闲槽位且队列中有任务
//            if (currentProcessing.size() >= gitImportConfig.getMaxConcurrentProjects() || queue.isEmpty()) {
//                break;
//            }
//
//            ProjectManagerEntity project = null;
//            try {
//                // 尝试获取任务，避免长时间阻塞
//                project = queue.poll(100, TimeUnit.MILLISECONDS);
//                if (project == null) {
//                    continue;
//                }
//
//                // 再次检查项目状态
//                ProjectManagerEntity updatedProject = baseMapper.selectById(project.getId());
//                if (updatedProject == null || !ProjectStatus.QUEUED.name().equals(updatedProject.getStatus())) {
//                    log.info("项目{}状态已变更，跳过处理", project.getId());
//                    continue;
//                }
//
//                // 更新状态为处理中
//                project.setStatus(ProjectStatus.PROCESSING.name());
//                project.setUpdateTime(new Date());
//                baseMapper.updateById(project);
//
//                // 执行异步采集任务
//                if (currentProcessing.add(project.getId())) {
//                    executeTaskAsync(project, operatorId , gitInfoDto);
//                } else {
//                    // 如果添加失败，说明已有其他线程在处理该项目
//                    queue.offer(project);
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                if (project != null) {
//                    queue.offer(project);
//                }
//                break;
//            } catch (Exception e) {
//                log.error("触发任务处理失败", e);
//                if (project != null) {
//                    queue.offer(project);
//                }
//                break;
//            }
//        }
//    }
//
//    private void executeTaskAsync(ProjectManagerEntity project, Long operatorId, GitInfoDto gitInfoDto) {
//        taskExecutor.submit(() -> {
//            try {
//                log.info("开始处理项目{}，仓库URL: {}", project.getId(), project.getProjectRepoUrl());
//
//                gitUtils.setChannelStreamId(gitInfoDto.getLogChannelStreamId());
//
//                // 克隆或拉取仓库（需处理已存在的本地仓库）
//                String localPath = gitUtils.cloneRepository(
//                        project.getProjectRepoUrl(),
//                        getLocalStoragePath(project.getId()),
//                        project.getAuthUsername(),
//                        project.getAuthPassword()
//                );
//
//                // 执行全量或增量采集
//                List<GitCommitInfoDto> commits;
//                if (project.getLastSyncTime().getTime() == 0) {
//                    commits = gitUtils.collectCommitInfo(localPath);
//                    log.info("项目{}全量采集完成，提交记录数: {}", project.getId(), commits.size());
//                } else {
//                    commits = gitUtils.collectIncrementalCommitInfo(localPath, project.getLastSyncTime());
//                    log.info("项目{}增量采集完成，新增提交记录数: {}", project.getId(), commits.size());
//                }
//
//                // 解析获取到的提交信息
//                agentAnalysisTool.collectAnalysis(commits , project , gitInfoDto);
//
//                // 保存提交信息到数据库
//                saveCommitsToDatabase(commits, project.getId());
//
//                // 更新项目最后同步时间和状态
//                project.setLastSyncTime(new Date());
//                project.setStatus(ProjectStatus.COMPLETED.name());
//                project.setUpdateTime(new Date());
//                baseMapper.updateById(project);
//
//                log.info("项目{}处理成功完成", project.getId());
//
//                gitUtils.sendMessage(gitInfoDto.getLogChannelStreamId(), "finish");
//
//                // 新增：如果配置了同步间隔，自动开启定时增量采集
//                if (project.getSyncInterval() > 0) {
//                    scheduleNextIncrementalCollect(project);
//                }
//
//            } catch (Exception e) {
//                handleTaskFailure(project, operatorId, gitInfoDto , e);
//            } finally {
//                // 清理本地仓库
//                cleanLocalRepository(project.getId());
//                // 从处理中集合移除，并触发下一个任务
//                processingProjects.get(operatorId).remove(project.getId());
//                triggerTaskProcessing(operatorId, gitInfoDto);
//            }
//        });
//    }
//
//    private void handleTaskFailure(ProjectManagerEntity project, Long operatorId, GitInfoDto gitInfoDto , Exception e) {
//        int retryCount = taskRetryCount.getOrDefault(project.getId(), 0);
//
//        if (isRetriableException(e) && retryCount < MAX_RETRIES) {
//            // 可重试异常，安排重试
//            retryCount++;
//            taskRetryCount.put(project.getId(), retryCount);
//
//            project.setStatus(ProjectStatus.RETRYING.name());
//            project.setUpdateTime(new Date());
//            baseMapper.updateById(project);
//
//            log.warn("项目{}处理失败，将在{}秒后重试 (第{}次/{}次): {}",
//                    project.getId(), retryCount * 5, retryCount, MAX_RETRIES, e.getMessage());
//
//            // 使用调度线程池安排重试
//            retryExecutor.schedule(() -> {
//                try {
//                    // 重新加入队列
//                    addToQueue(operatorId, project);
//                    // 触发任务处理
//                    triggerTaskProcessing(operatorId, gitInfoDto);
//                } catch (Exception ex) {
//                    log.error("项目{}重试失败", project.getId(), ex);
//                    markProjectFailed(project);
//                }
//            }, retryCount * 5L, TimeUnit.SECONDS);
//        } else {
//            // 不可重试或达到最大重试次数，标记为失败
//            log.error("项目{}处理失败，已达到最大重试次数或不可重试: {}", project.getId(), e.getMessage());
//            markProjectFailed(project);
//        }
//    }
//
//    private boolean isRetriableException(Exception e) {
//        // 判断是否为可重试的异常类型（如网络异常、临时文件锁定等）
//        return e instanceof java.io.IOException ||
//                e.getMessage() != null && e.getMessage().contains("timeout");
//    }
//
//    private void markProjectFailed(ProjectManagerEntity project) {
//        project.setStatus(ProjectStatus.FAILED.name());
//        project.setUpdateTime(new Date());
//        baseMapper.updateById(project);
//        taskRetryCount.remove(project.getId());
//    }
//
//    private String getLocalStoragePath(Long projectId) {
//        String basePath = gitImportConfig.getLocalRepoPath();
//
//        // 如果配置路径为空，使用系统临时目录
//        if (basePath == null || basePath.trim().isEmpty()) {
//            basePath = System.getProperty("java.io.tmpdir");
//            basePath = Paths.get(basePath, ".alinesno-import-git").toString();
//        }
//
//        // 创建基础目录（如果不存在）
//        File baseDir = new File(basePath);
//        if (!baseDir.exists() && !baseDir.mkdirs()) {
//            log.error("无法创建基础目录: {}", basePath);
//            throw new RuntimeException("无法创建存储目录");
//        }
//
//        return Paths.get(basePath, String.valueOf(projectId)).toString();
//    }
//
//    private void cleanLocalRepository(Long projectId) {
//        // 删除本地仓库文件
//        File localDir = new File(getLocalStoragePath(projectId));
//        if (localDir.exists()) {
//            try {
//                deleteDirectory(localDir);
//                log.info("成功清理项目{}的本地仓库", projectId);
//            } catch (Exception e) {
//                log.error("清理项目{}的本地仓库失败: {}", projectId, e.getMessage());
//            }
//        }
//    }
//
//    private void deleteDirectory(File directory) {
//        File[] files = directory.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    deleteDirectory(file);
//                } else {
//                    if (!file.delete()) {
//                        log.warn("无法删除文件: {}", file.getAbsolutePath());
//                    }
//                }
//            }
//        }
//        if (!directory.delete()) {
//            log.warn("无法删除目录: {}", directory.getAbsolutePath());
//        }
//    }
//
//    private ProjectManagerDTO convertToDTO(ProjectManagerEntity entity) {
//        ProjectManagerDTO dto = new ProjectManagerDTO();
//        // 字段映射
//        BeanUtil.copyProperties(entity, dto);
//        return dto;
//    }
//
//    // 分批保存提交信息到数据库
//    private void saveCommitsToDatabase(List<GitCommitInfoDto> commits, Long projectId) {
//        final int batchSize = 500; // 每批处理的记录数
//
//        for (int i = 0; i < commits.size(); i += batchSize) {
//            List<GitCommitInfoDto> batch = commits.subList(i, Math.min(i + batchSize, commits.size()));
//
//            // 分批保存
//            List<ProjectGitCommitEntity> entities = batch.stream()
//                    .map(commit -> {
//                        ProjectGitCommitEntity entity = new ProjectGitCommitEntity();
//                        BeanUtil.copyProperties(commit, entity);
//                        entity.setProjectId(projectId);
//                        entity.setAddTime(new Date());
//                        return entity;
//                    })
//                    .collect(Collectors.toList());
//
//            try {
//                projectGitCommitService.saveBatch(entities);
//            } catch (Exception e) {
//                log.error("保存项目{}的提交信息失败，批次记录数: {}", projectId, entities.size(), e);
//                // 可以考虑实现更复杂的错误处理策略，如记录失败批次并尝试单独保存每条记录
//                throw new RuntimeException("保存提交信息失败", e);
//            }
//        }
//    }
//
//    @Override
//    public void destroy() {
//        // 优雅关闭线程池
//        if (taskExecutor != null && !taskExecutor.isShutdown()) {
//            taskExecutor.shutdown();
//            try {
//                if (!taskExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
//                    taskExecutor.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                taskExecutor.shutdownNow();
//            }
//        }
//
//        if (retryExecutor != null && !retryExecutor.isShutdown()) {
//            retryExecutor.shutdown();
//            try {
//                if (!retryExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
//                    retryExecutor.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                retryExecutor.shutdownNow();
//            }
//        }
//    }
//
//    // 增量采集方法
//    private void incrementalCollect(ProjectManagerEntity project) {
//        if (project == null || project.getId() == null) {
//            log.error("增量采集失败：项目信息不完整");
//            return;
//        }
//
//        // 校验同步间隔配置
//        if (project.getSyncInterval() <= 0) {
//            log.info("项目{}未配置有效同步间隔，跳过增量采集", project.getId());
//            return;
//        }
//
//        // 计算距离下次采集的时间
//        long lastSyncTime = project.getLastSyncTime().getTime();
//        long nextSyncTime = lastSyncTime + project.getSyncInterval() * 60 * 1000; // 转换为毫秒
//        long delay = Math.max(0, nextSyncTime - System.currentTimeMillis());
//
//        // 提交定时任务
//        ScheduledFuture<?> future = taskExecutor.schedule(() -> {
//            try {
//                log.info("开始执行项目{}的定时增量采集", project.getId());
//
//                // 检查项目状态，确保未在处理中且未被删除
//                ProjectManagerEntity currentProject = baseMapper.selectById(project.getId());
//                if (currentProject == null ||
//                        !ProjectStatus.COMPLETED.name().equals(currentProject.getStatus()) ||
//                        processingProjects.getOrDefault(currentProject.getOperatorId(), Collections.emptySet()).contains(currentProject.getId())) {
//                    log.info("项目{}状态不允许增量采集，跳过本次任务", project.getId());
//                    return;
//                }
//
//                // 触发任务处理（模拟用户导入逻辑，但仅执行采集部分）
//                Long operatorId = currentProject.getOperatorId();
//                checkTaskLimit(operatorId); // 校验任务限制
//                currentProject.setStatus(ProjectStatus.QUEUED.name()); // 设置为排队状态
//                baseMapper.updateById(currentProject);
//
//                BlockingQueue<ProjectManagerEntity> queue = getUserQueue(operatorId);
//                addToQueue(operatorId, currentProject);
//
//                GitInfoDto gitInfoDto = new GitInfoDto() ;
//                gitInfoDto.setChannelStreamId(IdUtil.nanoId());
//
//                triggerTaskProcessing(operatorId, gitInfoDto);
//
//            } catch (Exception e) {
//                log.error("项目{}增量采集任务失败: {}", project.getId(), e.getMessage());
//                // 处理异常，可记录失败次数或重新调度
//            } finally {
//                // 重新注册下次定时任务
//                scheduleNextIncrementalCollect(project);
//            }
//        }, delay, TimeUnit.MILLISECONDS);
//
//        scheduledTasks.put(project.getId(), future);
//    }
//
//    // 调度下次增量采集任务
//    @Override
//    public void scheduleNextIncrementalCollect(ProjectManagerEntity project) {
//        if (project.getSyncInterval() > 0) {
//            incrementalCollect(project); // 递归调度下次任务
//        } else {
//            cancelIncrementalTask(project.getId()); // 清除无效任务
//        }
//    }
//
//    @Override
//    public boolean isExistGitProject(Long orgId, String gitUrl) {
//
//        LambdaQueryWrapper<ProjectManagerEntity> query = new LambdaQueryWrapper<>();
//         query.eq(ProjectManagerEntity::getOrgId, orgId)
//            .eq(ProjectManagerEntity::getProjectRepoUrl, gitUrl);
//
//         return count(query)  > 0;
//    }
//
//    @Override
//    public boolean isImportingGitProject(Long orgId, String gitUrl) {
//
//        LambdaQueryWrapper<ProjectManagerEntity> query = new LambdaQueryWrapper<>();
//
//        // 导入中的或者已经在队列中的项目
//        query.eq(ProjectManagerEntity::getOrgId, orgId)
//                .eq(ProjectManagerEntity::getProjectRepoUrl, gitUrl)
//                .and(qw -> qw
//                        .eq(ProjectManagerEntity::getStatus, ProjectStatus.PROCESSING.name()) // 导入中
//                        .or()
//                        .eq(ProjectManagerEntity::getStatus, ProjectStatus.QUEUED.name())    // 排队中
//                );
//
//        return count(query) > 0;
//    }
//
//    // 取消定时任务
//    public void cancelIncrementalTask(Long projectId) {
//        ScheduledFuture<?> future = scheduledTasks.remove(projectId);
//        if (future != null && !future.isDone()) {
//            future.cancel(true);
//            log.info("已取消项目{}的增量采集任务", projectId);
//        }
//    }
//
//    public List<ProjectManagerEntity> listByStatus( String status){
//        LambdaQueryWrapper<ProjectManagerEntity> query = new LambdaQueryWrapper<>();
//        query.eq(ProjectManagerEntity::getStatus, status);
//        return baseMapper.selectList(query);
//    }
//
//}