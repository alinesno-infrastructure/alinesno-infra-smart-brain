package com.alinesno.infra.smart.assistant.api;

import lombok.Data;

@Data
public class CodeContent {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 代码内容
     */
    private String content;

    public CodeContent(String language , String content){
        this.language = language ;
        this.content = content ;
    }

}