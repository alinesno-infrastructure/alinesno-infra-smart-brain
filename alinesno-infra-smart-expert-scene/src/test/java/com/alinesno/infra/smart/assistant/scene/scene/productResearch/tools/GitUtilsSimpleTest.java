//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;
//
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitCommitInfoDto;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
//
//public class GitUtilsSimpleTest {
//
//    private Path tempDir;
//
//    @BeforeEach
//    public void setUp() throws IOException {
//        // 创建唯一的临时目录
//        tempDir = Files.createTempDirectory("git-test-");
//        System.out.println("创建临时目录: " + tempDir);
//    }
//
//    @AfterEach
//    public void tearDown() throws IOException {
//        // 删除临时目录及其内容
//        if (tempDir != null && Files.exists(tempDir)) {
//            Files.walk(tempDir)
//                    .sorted((a, b) -> -a.compareTo(b))
//                    .map(Path::toFile)
//                    .forEach(file -> {
//                        try {
////                            System.out.println("删除文件: " + file);
//                            Files.deleteIfExists(file.toPath());
//                        } catch (IOException e) {
//                            System.err.println("删除失败: " + file + " - " + e.getMessage());
//                        }
//                    });
//            System.out.println("临时目录已删除");
//        }
//    }
//
//    @Test
//    public void testCollectPublicRepoCommits() {
//        String remoteUrl = "https://gitee.com/alinesno-infrastructure/alinesno-infra-smart-brain.git";
//        remoteUrl = "https://gitee.com/AIDotNet/OpenDeepWiki.git" ;
//
//        String tempLocalPath = tempDir.toString();
//
//        try {
//            // 克隆仓库
//            GitUtils.cloneRepository(remoteUrl, tempLocalPath, null, null);
//
//            // 采集提交信息
//            List<GitCommitInfoDto> commits = GitUtils.collectCommitInfo(tempLocalPath);
//
//            // 验证结果
//            System.out.println("成功获取 " + commits.size() + " 条提交记录");
//
//            // 打印第一条提交信息用于调试
//            if (!commits.isEmpty()) {
//                for( GitCommitInfoDto commit : commits.subList(0 , 4)){
//                    System.out.println("提交记录: " + commit.getCommitContent());
//                    System.out.println("提交分支: " + commit.getBranch());
//                    System.out.println("提交者: " + commit.getCommitter());
//                    System.out.println("提交时间: " + commit.getCommitTime());
//                    System.out.println("提交文件: " + commit.getCommittedFiles());
//                    System.out.println("提交详情: " + commit.getCommitDetails());
//                    System.out.println("提交差异: " + commit.getCommitDiff());
//                    System.out.println("--------------------------------------------------");
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Assertions.fail("测试失败: " + e.getMessage());
//        }
//    }
//}