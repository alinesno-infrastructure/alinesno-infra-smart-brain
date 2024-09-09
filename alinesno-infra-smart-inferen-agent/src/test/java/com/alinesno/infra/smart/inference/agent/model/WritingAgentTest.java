package com.alinesno.infra.smart.inference.agent.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WritingAgentTest {
    // 测试主函数
    public static void main(String[] args) {
        WritingAgent agent = new WritingAgent("Alice", 25, "Freelance writer");

        agent.write(500); // 写500字的文章
        agent.study(10); // 学习10点经验
        agent.grow(); // 智能体成长一年

        log.debug("After one year of growth, writing, and studying:");
        log.debug("Name - " + agent.getName());
        log.debug("Age - " + agent.getAge());
        log.debug("Experience - " + agent.getExperience());
        log.debug("Writing Skill - " + agent.getWritingSkill());
        log.debug("Vocabulary Size - " + agent.getVocabularySize());
        log.debug("Creativity - " + agent.getCreativity());
        log.debug("Productivity - " + agent.getProductivity());
        log.debug("Level - " + agent.getLevel().name());
        log.debug("Stage - " + agent.getCurrentStage());
    }
}
