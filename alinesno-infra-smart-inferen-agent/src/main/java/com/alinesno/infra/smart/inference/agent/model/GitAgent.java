package com.alinesno.infra.smart.inference.agent.model;

import com.alinesno.infra.smart.inference.agent.model.base.LifeAgent;
import com.alinesno.infra.smart.inference.enums.Level;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * GitAgent 类，继承自 LifeAgent，用于表示一个 Git 代理。
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class GitAgent extends LifeAgent {

    private String repositoryUrl; // 仓库地址
    private String branch; // 当前分支
    private int commits; // 提交次数
    private int pullRequests; // 拉取请求数量
    private int contributions; // 贡献度

    // 简化构造函数
    public GitAgent(String name, int age, String jobDescription, String repositoryUrl, String branch) {
        super(name); // 调用父类的构造函数
        this.repositoryUrl = repositoryUrl;
        this.branch = branch;
        this.commits = 0; // 初始提交次数
        this.pullRequests = 0; // 初始拉取请求数量
        this.contributions = 0; // 初始贡献度
    }

    // 提交更改
    public void commitChanges(String message) {
        if (message != null && !message.isEmpty()) {
            log.debug("Committing changes with message: " + message);
            commits++;
            contributions++; // 每次提交都增加贡献度
            updateLevel();
        }
    }

    // 拉取远程分支
    public void pullFromRemote() {
        log.debug("Pulling from remote repository...");
        contributions++; // 每次拉取也增加贡献度
        updateLevel();
    }

    // 创建拉取请求
    public void createPullRequest(String title, String description) {
        if (title != null && !title.isEmpty() && description != null && !description.isEmpty()) {
            log.debug("Creating a pull request titled: " + title);
            pullRequests++;
            contributions += 2; // 创建拉取请求增加更多的贡献度
            updateLevel();
        }
    }

    // 更新级别
    @Override
    public void updateLevel() {
        for (Level l : Level.values()) {
            if (l.getRequiredExperience() <= contributions) {
                setLevel(l);
            } else {
                break;
            }
        }
    }

}