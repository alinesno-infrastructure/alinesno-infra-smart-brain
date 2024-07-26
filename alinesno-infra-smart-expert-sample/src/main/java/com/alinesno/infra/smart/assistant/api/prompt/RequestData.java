package com.alinesno.infra.smart.assistant.api.prompt;

import com.google.gson.Gson;
import lombok.Data;

import java.util.List;

@Data
public class RequestData {

    private String model;
    private long temperature ;
    private List<PromptMessage> messages;

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}