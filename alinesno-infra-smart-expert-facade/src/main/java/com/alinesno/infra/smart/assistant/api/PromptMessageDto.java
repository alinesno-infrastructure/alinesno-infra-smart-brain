package com.alinesno.infra.smart.assistant.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class PromptMessageDto {

    private String role ;
    private String content ;

    public PromptMessageDto(String role , String content){
        this.role = role ;
        this.content = content ;
    }

}