package com.alinesno.infra.smart.inference.agent.model;

import com.alinesno.infra.smart.inference.agent.model.base.BaseLifeAgent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LifeAgentTest {
    // 主函数，用于测试
    public static void main(String[] args) {
        BaseLifeAgent agent = new BaseLifeAgent(
                1, // 成长速率
                80, // 最大年龄
                100, // 经验值上限
                5, // 初始记忆能力
                "Hardworking and determined", // 性格特点
                "Rural area with supportive parents", // 家庭环境
                "Programming, problem-solving", // 能力范围
                "Reading, coding, hiking", // 喜好
                "Rural village", // 家庭住址
                "Local public school", // 教育学校
                22 // 开始工作的年龄
        );

        for (int i = 0; i < 85; i++) { // 模拟85年
            if (i == 18) {
                agent.study(10); // 在18岁时开始学习
            }
            if (i >= 25 && i <= 50) {
                agent.study(15); // 在25至50岁期间持续学习
            }
            if (i >= 51 && i <= 60) {
                agent.study(10); // 在51至60岁期间学习
            }
            if (i >= 61) {
                agent.study(5); // 在61岁以后仍然学习
            }
            log.debug("Year " + i + ": Age - " + agent.getAge() +
                    ", Experience - " + agent.getExperience() +
                    ", Memory - " + agent.getMemory() +
                    ", Health - " + agent.getHealth() +
                    ", Level - " + agent.getLevel().name() +
                    ", Stage - " + agent.getCurrentStage() +
                    ", Personality - " + agent.getPersonality() +
                    ", Family Environment - " + agent.getFamilyEnvironment() +
                    ", Abilities - " + agent.getAbilities() +
                    ", Interests - " + agent.getInterests() +
                    ", Home Address - " + agent.getHomeAddress() +
                    ", Education School - " + agent.getEducationSchool() +
                    ", Start Work Age - " + agent.getStartWorkAge() +
                    ", Work Years - " + agent.getWorkYears());

            agent.grow(); // 智能体成长
        }
    }
}