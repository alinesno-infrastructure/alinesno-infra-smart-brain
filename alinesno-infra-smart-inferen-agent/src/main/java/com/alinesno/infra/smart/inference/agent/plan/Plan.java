package com.alinesno.infra.smart.inference.agent.plan;

import com.alinesno.infra.smart.inference.agent.goal.Goal;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Plan 类，表示一个计划。
 */
@Data
@ToString
public class Plan {

    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean isCompleted;
    private int priority;
    private Goal goal;

    public Plan(String title, String description, LocalDate dueDate, int priority, Goal goal) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isCompleted = false;
        this.priority = priority;
        this.goal = goal;
    }

    public void markAsCompleted() {
        isCompleted = true;
    }
}