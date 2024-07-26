package com.alinesno.infra.smart.assistant.api.prompt;

import lombok.Data;

@Data
public class PromptMessage {

    private String role ;
    private String content ;

    public PromptMessage(String role , String content){
        this.role = role ;
        this.content = content ;
    }

}