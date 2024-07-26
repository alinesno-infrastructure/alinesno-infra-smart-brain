package com.alinesno.infra.smart.assistant.api.adapter;

import lombok.Data;

import java.util.List;

@Data
public class TaskContentDto {

    /**
     * 业务 ID
     */
    private String businessId;

    /**
     * 任务状态
     */
    private int taskStatus ;

    /**
     * 生成内容
     */
    private String genContent;

    /**
     * 代码内容列表
     */
    private List<CodeContent> codeContent;

    @Data
    public static class CodeContent {

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

}