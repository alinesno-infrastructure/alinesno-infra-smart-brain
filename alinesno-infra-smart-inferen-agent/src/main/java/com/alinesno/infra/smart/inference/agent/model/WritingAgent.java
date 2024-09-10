package com.alinesno.infra.smart.inference.agent.model;

import com.alinesno.infra.smart.inference.agent.model.base.LifeAgent;
import com.alinesno.infra.smart.inference.enums.Level;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * WritingAgent 类，继承自 LifeAgent，表示一个写作代理。
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class WritingAgent extends LifeAgent {

    private int writingSkill; // 写作技巧等级
    private int vocabularySize; // 词汇量大小
    private int creativity; // 创造力水平
    private int productivity; // 生产效率

    // 简化构造函数
    public WritingAgent(String name, int age, String jobDescription) {
        super(name); // 调用父类的构造函数
        this.setAge(age); // 设置年龄
        this.setJobDescription(jobDescription); // 职业描述作为提示信息

        // 初始化其他属性
        this.writingSkill = 0; // 初始写作技巧等级
        this.vocabularySize = 5000; // 初始词汇量大小
        this.creativity = 5; // 初始创造力水平
        this.productivity = 5; // 初始生产效率

        this.setLevel(Level.NONE); // 初始级别
    }

    // 更新写作技巧的方法
    public void improveWritingSkill(int skillPoints) {
        if (skillPoints > 0 && isHealthy()) {
            writingSkill += skillPoints;
            log.debug("The agent's writing skill has increased to " + writingSkill);
        }
    }

    // 扩展学习方法，增加写作技能
    @Override
    public void study(int expGain) {
        super.study(expGain);
        improveWritingSkill(expGain / 2); // 每次学习增加一半的经验值作为写作技巧
    }

    // 写作方法
    public void write(int words) {
        if (words > 0 && isHealthy()) {
            if (words <= productivity) {
                log.debug("The agent wrote " + words + " words.");
                setExperience(getExperience() + words); // 将写的字数作为经验值的一部分
                if (getExperience() > getMaxExperience()) {
                    setExperience(getMaxExperience());
                }
                updateLevel();
            } else {
                log.debug("The agent could not write " + words + " words due to low productivity.");
            }
        }
    }

}