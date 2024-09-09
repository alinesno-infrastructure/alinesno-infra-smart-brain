package com.alinesno.infra.smart.inference.agent.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GitAgentTest {

    // 测试主函数
    public static void main(String[] args) {
        GitAgent agent = new GitAgent("Dev Alpha", 25, "Software Developer", "https://github.com/example/repo", "main");

        agent.commitChanges("Initial commit");
        agent.pullFromRemote();
        agent.createPullRequest("Feature A", "Implemented feature A with unit tests.");

        log.debug("After some activities:");
        log.debug("Name - " + agent.getName());
        log.debug("Age - " + agent.getAge());
        log.debug("Experience - " + agent.getExperience());
        log.debug("Commits - " + agent.getCommits());
        log.debug("Pull Requests - " + agent.getPullRequests());
        log.debug("Contributions - " + agent.getContributions());
        log.debug("Level - " + agent.getLevel().name());
        log.debug("Stage - " + agent.getCurrentStage());
    }

}
