package com.alinesno.infra.smart.inference.agent.plan;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DailyPlan 类，表示每日计划。
 */
@Data
public class DailyPlan {

    private List<Plan> plans;

    public DailyPlan() {
        this.plans = new ArrayList<>();
    }

    public void addPlan(Plan plan) {
        plans.add(plan);
    }

    public void checkCompletion(LocalDate today) {
        for (Plan plan : plans) {
            if (plan.getDueDate().isEqual(today)) {
                plan.markAsCompleted();
            }
        }
    }

    public void updatePlan(Plan plan) {
        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i).getTitle().equals(plan.getTitle())) {
                plans.set(i, plan);
                break;
            }
        }
    }
}