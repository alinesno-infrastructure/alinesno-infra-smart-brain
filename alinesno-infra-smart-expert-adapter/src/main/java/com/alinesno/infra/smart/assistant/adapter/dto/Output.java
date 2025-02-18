package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class Output {

    // 结果列表，包含文档及其相关分数
    private List<Result> results;

}
