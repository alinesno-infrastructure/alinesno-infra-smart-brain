//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;
//
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.CommitterInfo;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitCommitInfoDto;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitFileDiffDto;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.event.GitLogEvent;
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.properties.GitImportConfig;
//import lombok.Setter;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.diff.DiffEntry;
//import org.eclipse.jgit.diff.DiffFormatter;
//import org.eclipse.jgit.diff.RawTextComparator;
//import org.eclipse.jgit.lib.*;
//import org.eclipse.jgit.revwalk.RevCommit;
//import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
//import org.eclipse.jgit.treewalk.CanonicalTreeParser;
//import org.eclipse.jgit.treewalk.TreeWalk;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Component;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
///**
// * Git工具类，用于采集Git仓库的提交信息
// */
//@Slf4j
//@Component
//public class GitUtils {
//
//    // 单个差异内容最大长度
//    private static final int MAX_DIFF_LENGTH = 8192 * 2;
//
//    @Autowired
//    private GitImportConfig gitImportConfig;
//
//    @Autowired
//    private ApplicationEventPublisher eventPublisher;
//
//    @Setter
//    private String channelStreamId;
//
//    /**
//     * 获取Git仓库中所有提交者的信息（去重）
//     * @param localPath 本地仓库路径
//     * @return 提交者信息列表（已去重）
//     */
//    public List<CommitterInfo> getAllCommitters(String localPath) {
//        Set<String> committerKeys = new HashSet<>(); // 用于去重的集合，存储"name:email"格式的字符串
//        List<CommitterInfo> result = new ArrayList<>();
//
//        try (Repository repository = openRepository(localPath);
//             Git git = new Git(repository)) {
//
//            // 获取所有分支
//            List<Ref> branches = git.branchList().call();
//
//            // 遍历所有分支的提交记录
//            for (Ref branch : branches) {
//                Iterable<RevCommit> commits = git.log()
//                        .add(branch.getObjectId())
//                        .call();
//
//                for (RevCommit commit : commits) {
//                    // 获取提交者信息
//                    PersonIdent committer = commit.getCommitterIdent();
//                    String name = committer.getName();
//                    String email = committer.getEmailAddress();
//
//                    // 生成唯一键（name:email）用于去重
//                    String key = name + ":" + email;
//
//                    // 如果该提交者信息未记录过，则添加到结果列表
//                    if (committerKeys.add(key)) {
//                        // 过滤掉一些不规则的email邮箱，比如 users.noreply.github.com后缀的
//                        CommitterInfo committerInfo = new CommitterInfo(name, email) ;
//                        if (CommitterFilter.isValidCommitter(committerInfo)) {
//                            result.add(committerInfo);
//                        }
//                    }
//                }
//            }
//
//            publishLogEvent("成功获取所有提交者信息，共" + result.size() + "位提交者", "INFO");
//
//        } catch (IOException | GitAPIException e) {
//            publishLogEvent("获取Git提交者信息失败:" + e.getMessage(), "ERROR");
//            throw new RuntimeException("获取Git提交者信息失败", e);
//        }
//
//        return result;
//    }
//
//    /**
//     * 从本地Git仓库采集所有提交信息
//     * @param localPath 本地仓库路径
//     * @return 提交信息列表
//     */
//    public List<GitCommitInfoDto> collectCommitInfo(String localPath) {
//        List<GitCommitInfoDto> commitInfoList = new ArrayList<>();
//
//        try (Repository repository = openRepository(localPath);
//             Git git = new Git(repository)) {
//
//            // 获取所有分支
//            List<Ref> branches = git.branchList().call();
//
//            String logMsg = "找到 " + branches.size() + " 个分支";
//            publishLogEvent(logMsg, "INFO"); // 发布事件
//
//            for (Ref branch : branches) {
//                String branchName = branch.getName().replace("refs/heads/", "");
//
//                logMsg = "处理分支: " + branchName;
//                publishLogEvent(logMsg, "INFO");
//
//                // 获取分支的提交记录
//                Iterable<RevCommit> commits = git.log()
//                        .add(branch.getObjectId())
//                        .call();
//
//                int commitCount = 0;
//                for (RevCommit commit : commits) {
//
//                    // 超过最大采集数则退出循环
//                    if (commitCount >= gitImportConfig.getMaxCollect()) {
//                        break;
//                    }
//
//                    // 获取每个文件的差异内容并分批处理
//                    List<GitFileDiffDto> fileDiffs = getFileDiffs(repository, commit);
//                    if (!fileDiffs.isEmpty()) {
//                        for (GitFileDiffDto fileDiff : fileDiffs) {
//                            // 将大文件差异切割成多个部分
//                            List<String> diffChunks = splitLargeDiff(fileDiff.getDiffContent());
//
//                            for (int i = 0; i < diffChunks.size(); i++) {
//                                GitCommitInfoDto info = new GitCommitInfoDto();
//                                info.setBranch(branchName);
//                                info.setCommitter(commit.getCommitterIdent().getName());
//                                info.setCommitterEmail(commit.getCommitterIdent().getEmailAddress());
//                                info.setCommitterName(commit.getCommitterIdent().getName());
//                                info.setCommitTime(new Date(commit.getCommitTime() * 1000L));
//                                info.setCommitContent(commit.getShortMessage());
//                                info.setCommitDetails(commit.getFullMessage());
//
//                                // 添加分片信息
//                                info.setCommittedFiles(fileDiff.getFilePath());
//                                info.setCommitDiff(diffChunks.get(i));
//                                info.setChangeType(fileDiff.getChangeType());
//                                info.setDiffChunkIndex(i + 1);
//                                info.setDiffTotalChunks(diffChunks.size());
//
//                                commitInfoList.add(info);
//
//                                // 打印采集到的提交信息
////                                logMsg = "采集到提交 ["+commit.getName()+"] - 文件: "+fileDiff.getFilePath()+", 分片: " +  (i + 1) + "/" + diffChunks.size() ;
////                                publishLogEvent(logMsg, "DEBUG");
//
//                                // 打印采集到的提交信息
//                                log.info("采集到增量提交 [{}] - 文件: {}, 分片: {}/{}",
//                                        commit.getName(),
//                                        fileDiff.getFilePath(),
//                                        i + 1,
//                                        diffChunks.size());
//
//                                commitCount++;
//                            }
//                        }
//                    }
//
//
//                    // 如果获取到commit的次数达到100条，则发送event事件
//                    if (commitCount % 100 == 0) {
//                        publishLogEvent("已采集 " + commitCount + " 条提交记录", "INFO");
//                    }
//
//                }
//
//                publishLogEvent("分支 " + branchName + " 处理完成，共采集 " + commitCount + " 条提交记录", "INFO");
//            }
//
//            publishLogEvent("所有分支处理完成，共采集 " + commitInfoList.size() + " 条提交记录", "INFO");
//            publishEndLogEvent() ;
//
//        } catch (IOException | GitAPIException e) {
//            publishLogEvent("采集Git提交信息失败:" + e.getMessage(), "ERROR");
//            throw new RuntimeException("采集Git提交信息失败", e);
//        }
//
//        return commitInfoList;
//    }
//
//    /**
//     * 克隆远程仓库到本地
//     * @param gitUrl 仓库URL
//     * @param localPath 本地路径
//     * @param username 用户名（可为空）
//     * @param password 密码（可为空）
//     * @return 本地仓库路径
//     */
//    public String cloneRepository(String gitUrl, String localPath, String username, String password) {
//        String logMsg = "开始克隆仓库: " + gitUrl;
//
//        try {
//            publishLogEvent(logMsg, "INFO");
//
//            Git.cloneRepository()
//                    .setURI(gitUrl)
//                    .setDirectory(new File(localPath))
//                    .setCredentialsProvider(username != null ?
//                            new UsernamePasswordCredentialsProvider(username, password) : null)
//                    .call();
//
//             logMsg = "仓库克隆完成，保存路径: " + localPath;
//             publishLogEvent(logMsg, "INFO");
//
//            return localPath;
//        } catch (GitAPIException e) {
//
//            logMsg = "克隆Git仓库失败: " + e.getMessage();
//            publishLogEvent(logMsg, "ERROR");
//
//            throw new RuntimeException("克隆Git仓库失败", e);
//        }
//    }
//
//    /**
//     * 打开本地仓库
//     */
//    private Repository openRepository(String localPath) throws IOException {
//        FileRepositoryBuilder builder = new FileRepositoryBuilder();
//        return builder.setGitDir(new File(localPath, ".git"))
//                .readEnvironment()
//                .findGitDir()
//                .build();
//    }
//
//    /**
//     * 获取提交修改的文件列表
//     */
//    private List<String> getModifiedFiles(Repository repository, RevCommit commit) throws IOException {
//        List<String> files = new ArrayList<>();
//
//        if (commit.getParentCount() > 0) {
//            RevCommit parent = commit.getParent(0);
//            try (TreeWalk treeWalk = new TreeWalk(repository)) {
//                treeWalk.addTree(parent.getTree());
//                treeWalk.addTree(commit.getTree());
//                treeWalk.setRecursive(true);
//
//                while (treeWalk.next()) {
//                    if (treeWalk.getFileMode(0) != treeWalk.getFileMode(1)) {
//                        files.add(treeWalk.getPathString());
//                    }
//                }
//            }
//        }
//
//        return files;
//    }
//
//    /**
//     * 从本地Git仓库采集指定日期之后的增量提交信息
//     * @param localPath 本地仓库路径
//     * @param lastCollectDate 最后一次采集的日期，只采集此日期之后的提交
//     * @return 增量提交信息列表
//     */
//    public List<GitCommitInfoDto> collectIncrementalCommitInfo(String localPath, Date lastCollectDate) {
//        List<GitCommitInfoDto> commitInfoList = new ArrayList<>();
//        long lastCollectTime = lastCollectDate.getTime() / 1000; // 转换为秒，与Git时间格式一致
//
//        try (Repository repository = openRepository(localPath);
//             Git git = new Git(repository)) {
//
//            // 获取所有分支
//            List<Ref> branches = git.branchList().call();
//            log.info("找到 {} 个分支", branches.size());
//
//            for (Ref branch : branches) {
//                String branchName = branch.getName().replace("refs/heads/", "");
//                log.info("处理分支: {}", branchName);
//
//                // 获取分支的提交记录，按时间过滤
//                Iterable<RevCommit> commits = git.log()
//                        .add(branch.getObjectId())
//                        .call();
//
//                int commitCount = 0;
//                for (RevCommit commit : commits) {
//                    // 检查提交时间是否在最后采集时间之后
//                    if (commit.getCommitTime() <= lastCollectTime) {
//                        continue; // 跳过已采集的提交
//                    }
//
//                    // 超过最大采集数则退出循环
//                    if (commitCount >= gitImportConfig.getMaxCollect()) {
//                        break;
//                    }
//
//                    // 获取每个文件的差异内容并分批处理
//                    List<GitFileDiffDto> fileDiffs = getFileDiffs(repository, commit);
//                    if (!fileDiffs.isEmpty()) {
//                        for (GitFileDiffDto fileDiff : fileDiffs) {
//                            // 将大文件差异切割成多个部分
//                            List<String> diffChunks = splitLargeDiff(fileDiff.getDiffContent());
//
//                            for (int i = 0; i < diffChunks.size(); i++) {
//                                GitCommitInfoDto info = new GitCommitInfoDto();
//                                info.setBranch(branchName);
//                                info.setCommitter(commit.getCommitterIdent().getName());
//                                info.setCommitterEmail(commit.getCommitterIdent().getEmailAddress());
//                                info.setCommitterName(commit.getCommitterIdent().getName());
//                                info.setCommitTime(new Date(commit.getCommitTime() * 1000L));
//                                info.setCommitContent(commit.getShortMessage());
//                                info.setCommitDetails(commit.getFullMessage());
//
//                                // 添加分片信息
//                                info.setCommittedFiles(fileDiff.getFilePath());
//                                info.setCommitDiff(diffChunks.get(i));
//                                info.setChangeType(fileDiff.getChangeType());
//                                info.setDiffChunkIndex(i + 1);
//                                info.setDiffTotalChunks(diffChunks.size());
//
//                                commitInfoList.add(info);
//
//                                // 打印采集到的提交信息
//                                log.info("采集到增量提交 [{}] - 文件: {}, 分片: {}/{}",
//                                        commit.getName(),
//                                        fileDiff.getFilePath(),
//                                        i + 1,
//                                        diffChunks.size());
//
//                                commitCount++;
//                            }
//                        }
//                    }
//
//                }
//
//                log.info("分支 {} 增量处理完成，共采集 {} 条提交记录", branchName, commitCount);
//            }
//
//            log.info("所有分支增量处理完成，共采集 {} 条提交记录", commitInfoList.size());
//
//        } catch (IOException | GitAPIException e) {
//            log.error("采集Git增量提交信息失败", e);
//            throw new RuntimeException("采集Git增量提交信息失败", e);
//        }
//
//        return commitInfoList;
//    }
//
//    /**
//     * 获取提交中每个文件的差异内容
//     */
//    @SneakyThrows
//    private List<GitFileDiffDto> getFileDiffs(Repository repository, RevCommit commit) {
//        List<GitFileDiffDto> fileDiffs = new ArrayList<>();
//
//        if (commit.getParentCount() == 0) {
//            // 初始提交没有父节点，返回空列表
//            return fileDiffs;
//        }
//
//        RevCommit parent = commit.getParent(0);
//        ObjectId oldTreeId = parent.getTree();
//        ObjectId newTreeId = commit.getTree();
//
//        // 获取差异列表
//        List<DiffEntry> diffs;
//        try (ObjectReader reader = repository.newObjectReader()) {
//            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
//            oldTreeIter.reset(reader, oldTreeId);
//
//            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
//            newTreeIter.reset(reader, newTreeId);
//
//            diffs = new Git(repository).diff()
//                    .setNewTree(newTreeIter)
//                    .setOldTree(oldTreeIter)
//                    .call();
//        }
//
//        // 按文件生成差异内容
//        for (DiffEntry entry : diffs) {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            try (DiffFormatter df = new DiffFormatter(out)) {
//                df.setRepository(repository);
//                df.setDiffComparator(RawTextComparator.DEFAULT);
//                df.setDetectRenames(true);
//                df.format(entry);
//
//                GitFileDiffDto fileDiff = new GitFileDiffDto();
//                fileDiff.setFilePath(entry.getNewPath());
//                fileDiff.setDiffContent(out.toString(StandardCharsets.UTF_8));
//                fileDiff.setChangeType(entry.getChangeType().name());
//
//                fileDiffs.add(fileDiff);
//            }
//        }
//
//        return fileDiffs;
//    }
//
//    /**
//     * 将大的差异内容切割成多个较小的部分
//     */
//    private List<String> splitLargeDiff(String diffContent) {
//        List<String> chunks = new ArrayList<>();
//
//        if (diffContent.length() <= MAX_DIFF_LENGTH) {
//            chunks.add(diffContent);
//            return chunks;
//        }
//
//        log.debug( "差异内容过大，长度: " + diffContent.length());
//
//        int startIndex = 0;
//        while (startIndex < diffContent.length()) {
//            // 寻找最近的diff分隔符或行结束符
//            int endIndex = Math.min(startIndex + MAX_DIFF_LENGTH, diffContent.length());
//
//            // 尝试在分隔符或行结束符处切割，保证内容完整性
//            while (endIndex > startIndex &&
//                    !diffContent.regionMatches(endIndex - 4, "diff --", 0, 6) &&
//                    diffContent.charAt(endIndex - 1) != '\n') {
//                endIndex--;
//            }
//
//            // 如果找不到合适的分割点，就在最大长度处强制分割
//            if (endIndex == startIndex) {
//                endIndex = Math.min(startIndex + MAX_DIFF_LENGTH, diffContent.length());
//            }
//
//            String chunk = diffContent.substring(startIndex, endIndex);
//            chunks.add(chunk);
//            startIndex = endIndex;
//        }
//
//        log.debug( "差异内容切割完成，共生成 " + chunks.size() + " 个部分");
//
//        return chunks;
//    }
//
//    // 修改原有log.info调用为发布事件
//    private void publishLogEvent(String message, String level) {
//
//        log.info(message) ;
//
//        if (eventPublisher != null) {
//            GitLogEvent  event = new GitLogEvent(
//                    GitUtils.class,
//                    message,
//                    level,
//                    GitUtils.class.getSimpleName() ,
//                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//            );
//            event.setChannelStreamId(channelStreamId) ;
//            eventPublisher.publishEvent(event);
//        }
//    }
//
//    private void publishEndLogEvent() {
//        if (eventPublisher != null) {
//            GitLogEvent  event = new GitLogEvent(
//                    GitUtils.class,
//                    "[DONE]",
//                    "END",
//                    GitUtils.class.getSimpleName() ,
//                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//            );
//            event.setChannelStreamId(channelStreamId) ;
//            eventPublisher.publishEvent(event);
//        }
//    }
//
//    /**
//     *  发送消息
//     * @param logChannelStreamId
//     */
//    public void sendMessage(String logChannelStreamId , String status) {
//
//        setChannelStreamId(logChannelStreamId);
//
//        if (eventPublisher != null) {
//            GitLogEvent  event = new GitLogEvent(
//                    GitUtils.class,
//                    "[FINISH]",
//                    "FINISH",
//                    GitUtils.class.getSimpleName() ,
//                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//            );
//            event.setChannelStreamId(channelStreamId) ;
//            eventPublisher.publishEvent(event);
//        }
//    }
//
//}