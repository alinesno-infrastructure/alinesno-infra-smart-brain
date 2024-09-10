package com.alinesno.infra.smart.inference.agent.model.base;

import com.alinesno.infra.smart.inference.agent.event.Event;
import com.alinesno.infra.smart.inference.agent.goal.Goal;
import com.alinesno.infra.smart.inference.agent.model.bean.Reflection;
import com.alinesno.infra.smart.inference.agent.model.bean.SocialConnection;
import com.alinesno.infra.smart.inference.agent.plan.DailyPlan;
import com.alinesno.infra.smart.inference.agent.plan.Plan;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class LifeAgent extends BaseLifeAgent {

    private SocialConnection head;

    private Queue<DailyPlan> dailyPlans;
    private Goal goal;

    public LifeAgent(String name) {
        super(name);
        this.head = null;
        this.dailyPlans = new LinkedList<>();
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    /**
     * 角色思考，从知识库中获取知识内容
     */
    public void think() {
        log.debug("Thinking...");

        // 从知识库中获取知识内容
        String content = "This is some knowledge content.";
        log.debug(content);
    }

    /**
     * 反思并增加经验值
     * @param content
     * @param experienceGain
     */
    public void reflectAndGainExperience(String content, int experienceGain) {
        // 进行反思并增加经验值
        Reflection reflection = new Reflection(content, experienceGain);
        setExperience(getExperience() + experienceGain);
    }

    public void handleEvent(Event event, LocalDate today) {
        // 根据事件类型处理
        switch (event.getCode()) {
            case "EVT_001" -> { // 编写目录
                System.out.println("Handling event with code " + event.getCode() + ": " + event);
                // 创建一个新计划项
                Plan writeCatalog = new Plan("Write Catalog", "Create documentation for project", today.plusDays(1), 2, goal);
                updatePlanBasedOnEvent(LocalDateTime.now(), writeCatalog);
            }
            case "EVT_002" -> { // 提交目录
                System.out.println("Handling event with code " + event.getCode() + ": " + event);
                // 创建一个新计划项
                Plan submitCatalog = new Plan("Submit Catalog", "Submit documentation for review", today.plusDays(1), 2, goal);
                updatePlanBasedOnEvent(LocalDateTime.now(), submitCatalog);
            }
            case "EVT_003" -> { // 发布目录
                System.out.println("Handling event with code " + event.getCode() + ": " + event);
                // 创建一个新计划项
                Plan publishCatalog = new Plan("Publish Catalog", "Publish documentation to repository", today.plusDays(1), 2, goal);
                updatePlanBasedOnEvent(LocalDateTime.now(), publishCatalog);
            }
            case "EVT_004" -> { // 检查目录
                System.out.println("Handling event with code " + event.getCode() + ": " + event);
                // 创建一个新计划项
                Plan checkCatalog = new Plan("Check Catalog", "Review and verify documentation", today.plusDays(1), 2, goal);
                updatePlanBasedOnEvent(LocalDateTime.now(), checkCatalog);
            }
            case "EVT_005" -> { // 发送邮件
                System.out.println("Handling event with code " + event.getCode() + ": " + event);
                // 创建一个新计划项
                Plan sendEmail = new Plan("Send Email", "Notify team about updates", today.plusDays(1), 2, goal);
                updatePlanBasedOnEvent(LocalDateTime.now(), sendEmail);
            }
            default -> System.out.println("Unknown event code: " + event.getCode());
        }
    }

    public void makePlanForToday(LocalDate today) {
        DailyPlan planForToday = new DailyPlan();
        // 示例计划
        planForToday.addPlan(new Plan("Write code", "Implement feature X", today.plusDays(1), 3 , goal));
        planForToday.addPlan(new Plan("Review code", "Review team's pull requests", today, 2 , goal));

        dailyPlans.add(planForToday);
    }

    public void checkPlanProgress(LocalDate today) {
        if (!dailyPlans.isEmpty()) {
            DailyPlan planForToday = dailyPlans.peek();
            planForToday.checkCompletion(today);
            System.out.println("Today's plan completion status:");
            for (Plan plan : planForToday.getPlans()) {
                System.out.println(plan.getTitle() + " - " + (plan.isCompleted() ? "Completed" : "Not completed"));
            }
        }
    }


    /**
     * 添加社交关系
     * @param agent 要添加的社交对象
     * @param time 遇到的时间
     * @param location 遇到的地点
     * @param depth 关系的深度
     */
    public void addConnection(LifeAgent agent, LocalDateTime time, String location, int depth) {
        if (agent == null || agent == this) {
            throw new IllegalArgumentException("Agent cannot be null or itself.");
        }
        SocialConnection newNode = new SocialConnection(agent, time, location, depth);
        if (head == null) {
            head = newNode;
        } else {
            SocialConnection current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
    }

    /**
     * 查找所有与特定Agent相关的社交关系
     * @param agent 要查找的社交对象
     * @return 包含所有匹配的社交连接的列表
     */
    public List<SocialConnection> findAllConnections(LifeAgent agent) {
        List<SocialConnection> connections = new ArrayList<>();
        SocialConnection current = head;
        while (current != null) {
            if (current.getAgent().equals(agent)) {
                connections.add(current);
            }
            current = current.getNext();
        }
        return connections;
    }

    public void updatePlanBasedOnEvent(LocalDateTime now, Plan updatedPlan) {
        DailyPlan planForToday = dailyPlans.peek();

        assert planForToday != null;
        planForToday.updatePlan(updatedPlan);
    }

}