package com.alinesno.infra.smart.assistant.api.prompt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PromptMessage {

    private String role;
    private String content;
    private String name;

    public PromptMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static PromptMessage of(String content) {
        return new PromptMessage(Role.USER.getValue(), content);
    }

    public static PromptMessage ofSystem(String content) {
        return new PromptMessage(Role.SYSTEM.getValue(), content);
    }

    public static PromptMessage ofAssistant(String content) {
        return new PromptMessage(Role.ASSISTANT.getValue(), content);
    }

    public static PromptMessage ofFunction(String function) {
        return new PromptMessage(Role.FUNCTION.getValue(), function);
    }

    @Getter
    @AllArgsConstructor
    public static enum Role {

        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        FUNCTION("function");

        private final String value;
    }
}