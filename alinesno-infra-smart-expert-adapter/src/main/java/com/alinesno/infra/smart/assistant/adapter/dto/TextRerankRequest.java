package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class TextRerankRequest {

    private String model;
    private Input input;
    private Parameters parameters;

    @Data
    public static class Input {
        private String query;
        private List<String> documents;
    }

    @Data
    public static class Parameters {
        private boolean return_documents;
        private int top_n;
    }
}