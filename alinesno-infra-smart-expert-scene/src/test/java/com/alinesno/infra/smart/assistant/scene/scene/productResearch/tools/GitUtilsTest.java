//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;
//
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.GitCommitInfoDto;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.lib.Repository;
//import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//public class GitUtilsTest {
//
//    @Autowired
//    private GitUtils gitUtils;
//
//    @MockBean
//    private Git mockGit;
//
//    @BeforeEach
//    public void setUp() throws IOException {
//        // 设置测试仓库路径
//        FileRepositoryBuilder builder = new FileRepositoryBuilder();
//        Repository repository = builder.setGitDir(new File("src/test/resources/test-repo/.git"))
//                .readEnvironment()
//                .findGitDir()
//                .build();
//
//        // 模拟Git对象
//        when(mockGit.getRepository()).thenReturn(repository);
//    }
//
//    @Test
//    public void testCollectCommitInfo() throws Exception {
//        // 设置mock行为
//        Mockito.when(mockGit.log()).thenReturn(null); // 根据实际实现调整
//
//        // 执行测试
//        List<GitCommitInfoDto> result = gitUtils.collectCommitInfo("src/test/resources/test-repo");
//
//        // 验证结果
//        assertFalse(result.isEmpty(), "提交信息列表不应为空");
//
//        // 打印结果用于调试
//        result.forEach(commit -> {
//            System.out.println("Commit: " + commit.getCommitContent());
//            System.out.println("Author: " + commit.getCommitterName());
//            System.out.println("Files: " + commit.getCommittedFiles());
//            System.out.println("---");
//        });
//    }
//}