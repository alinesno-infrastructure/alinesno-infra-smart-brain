package com.alinesno.infra.smart.assistant.role.context;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解析数据
 */
@NoArgsConstructor
@Data
public class ParserDataBean {

    private int sort ;  // 解析顺序
    private String name ;   // 文件名称
    private String type ; // 类型(知识库/链接/记忆/附件)
    private String textLength ; // 文本长度

    public ParserDataBean(String name, int sort) {
        this.name = name;
        this.sort = sort;
    }
}
