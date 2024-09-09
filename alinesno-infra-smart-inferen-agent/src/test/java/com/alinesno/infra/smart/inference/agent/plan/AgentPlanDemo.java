package com.alinesno.infra.smart.inference.agent.plan;

import com.alinesno.infra.smart.inference.agent.event.Event;
import com.alinesno.infra.smart.inference.agent.goal.Goal;
import com.alinesno.infra.smart.inference.agent.model.base.LifeAgent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AgentPlanDemo {
    public static void main(String[] args) throws InterruptedException {
        LifeAgent agent = new LifeAgent("Alice");

        // 设置目标
        Goal goal = new Goal("Complete project", "Finish all tasks by the end of the week");
        agent.setGoal(goal);

        LocalDate today = LocalDate.now();

        // 制定今天的计划
        agent.makePlanForToday(today);

        // 模拟每分钟检查计划
        for (int i = 0; i < 1440; i++) { // 一天有1440分钟
            Thread.sleep(1000); // 模拟等待一分钟
            LocalDateTime now = LocalDateTime.now();

            // 检查计划进度
            agent.checkPlanProgress(today);

            // 模拟突发事件，更新计划
            if (i % 100 == 0) { // 每隔100分钟发生一次突发事件
                Event event = new Event("EVT_001", "Client", "requests", "documentation");
                agent.handleEvent(event, today);
            }
        }
    }
}