package com.alinesno.infra.smart.inference.agent.model.base;

import com.alinesno.infra.smart.inference.enums.Level;
import com.alinesno.infra.smart.inference.enums.Stage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class BaseLifeAgent implements Agent {

    private String name ; // 名称
    private int age; // 年龄
    private int experience; // 经验值
    private int memory; // 记忆能力值
    private int health; // 健康值
    private Level level; // 当前级别
    private int growthRate; // 成长速率，每轮增加的年龄值
    private int maxAge; // 最大年龄
    private String jobDescription ; // 工作描述
    private int maxExperience; // 经验值上限
    private boolean isHealthy; // 是否健康
    private Stage currentStage; // 当前生命阶段
    private String personality; // 性格特点
    private String familyEnvironment; // 家庭环境
    private String abilities; // 能力范围
    private String interests; // 喜好
    private String homeAddress; // 家庭住址
    private String educationSchool; // 教育学校
    private int startWorkAge; // 开始工作的年龄
    private int workYears; // 工作年限

    public BaseLifeAgent(String name) {
        this.name = name;
    }


    // 构造函数
    public BaseLifeAgent(int growthRate, int maxAge, int maxExperience, int initialMemory,
                         String personality, String familyEnvironment,
                         String abilities, String interests, String homeAddress, String educationSchool,
                         int startWorkAge) {
        this.age = 0;
        this.experience = 0;
        this.memory = initialMemory;
        this.growthRate = growthRate;
        this.maxAge = maxAge;
        this.maxExperience = maxExperience;
        this.health = 100; // 初始健康值设为100
        this.isHealthy = true;
        this.level = Level.NONE;
        this.currentStage = Stage.BABY;
        this.personality = personality;
        this.familyEnvironment = familyEnvironment;
        this.abilities = abilities;
        this.interests = interests;
        this.homeAddress = homeAddress;
        this.educationSchool = educationSchool;
        this.startWorkAge = startWorkAge;
        this.workYears = 0;
    }

    // 更新年龄的方法
    public void grow() {
        if (isHealthy) {
            age += growthRate;
            updateStage();
            adjustMemoryWithAge(); // 随年龄调整记忆能力
            updateWorkYears();
        }
    }

    // 根据年龄更新生命阶段
    private void updateStage() {
        if (age >= 0 && age < 5) {
            currentStage = Stage.BABY;
        } else if (age >= 5 && age < 18) {
            currentStage = Stage.CHILD;
        } else if (age >= 18 && age < maxAge) {
            currentStage = Stage.ADULT;
        } else {
            currentStage = Stage.ELDER;
        }
    }

    // 随年龄调整记忆能力
    private void adjustMemoryWithAge() {
        if (currentStage == Stage.BABY || currentStage == Stage.CHILD) {
            memory += 1; // 儿童时期记忆能力逐渐增强
        } else if (currentStage == Stage.ADULT) {
            // 成年后记忆能力保持稳定
        } else if (currentStage == Stage.ELDER) {
            memory -= 1; // 老年时期记忆能力逐渐减弱
        }
        if (memory < 0) memory = 0;
    }

    // 受伤
    public void getInjured(int damage) {
        if (damage > 0) {
            health -= damage;
            if (health <= 0) {
                health = 0;
                isHealthy = false;
                log.debug("The agent has been severely injured and is now unhealthy.");
            }
        }
    }

    // 学习提升经验，学习效果受记忆能力影响
    public void study(int expGain) {
        if (expGain > 0 && isHealthy) {
            int effectiveExpGain = expGain * memory / 10; // 假设记忆能力值范围为0-10
            experience += effectiveExpGain;
            if (experience > maxExperience) {
                experience = maxExperience;
            }
            updateLevel();
            log.debug("The agent's experience has increased to " + experience);
        }
    }

    // 更新级别
    public void updateLevel() {
        for (Level l : Level.values()) {
            if (l.getRequiredExperience() <= experience) {
                level = l;
            } else {
                break;
            }
        }
    }

    // 更新工作年限
    private void updateWorkYears() {
        if (age >= startWorkAge) {
            workYears = age - startWorkAge;
        }
    }
}