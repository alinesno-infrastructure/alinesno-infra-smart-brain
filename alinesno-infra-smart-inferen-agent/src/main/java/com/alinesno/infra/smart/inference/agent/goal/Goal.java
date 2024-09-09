package com.alinesno.infra.smart.inference.agent.goal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Goal {

    private String title;
    private String description;

    public Goal(String title, String description) {
        this.title = title;
        this.description = description;
    }
}