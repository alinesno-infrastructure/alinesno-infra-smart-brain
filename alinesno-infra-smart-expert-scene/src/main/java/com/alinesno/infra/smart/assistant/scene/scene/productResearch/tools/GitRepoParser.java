//package com.alinesno.infra.smart.assistant.scene.scene.productResearch.tools;
//
//import com.alinesno.infra.smart.assistant.scene.scene.productResearch.dto.RepoInfoDto;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Git仓库解析工具类
// */
//public class GitRepoParser {
//
//    /**
//     * 解析git信息，解析出git的基本信息，主要是描述信息
//     */
//    public static RepoInfoDto parseGitRepo(String gitUrl) {
//        if (gitUrl == null || gitUrl.isEmpty()) {
//            return null;
//        }
//
//        RepoInfoDto repoInfo = new RepoInfoDto();
//
//        // 处理HTTPS格式的URL
//        Pattern httpsPattern = Pattern.compile("https?://[^/]+/([^/]+)/([^/]+?)(?:\\.git)?$");
//        Matcher httpsMatcher = httpsPattern.matcher(gitUrl);
//
//        if (httpsMatcher.find()) {
//            String orgName = httpsMatcher.group(1);
//            String repoName = httpsMatcher.group(2);
//            repoInfo.setOrgName(orgName);
//            repoInfo.setRepoName(repoName);
//            repoInfo.setRepoFullName(orgName + "/" + repoName);
//            return repoInfo;
//        }
//
//        // 处理SSH格式的URL
//        Pattern sshPattern = Pattern.compile("git@[^:]+:([^/]+)/([^/]+?)(?:\\.git)?$");
//        Matcher sshMatcher = sshPattern.matcher(gitUrl);
//
//        if (sshMatcher.find()) {
//            String orgName = sshMatcher.group(1);
//            String repoName = sshMatcher.group(2);
//            repoInfo.setOrgName(orgName);
//            repoInfo.setRepoName(repoName);
//            repoInfo.setRepoFullName(orgName + "/" + repoName);
//            return repoInfo;
//        }
//
//        // 如果无法解析，返回null
//        return null;
//    }
//}