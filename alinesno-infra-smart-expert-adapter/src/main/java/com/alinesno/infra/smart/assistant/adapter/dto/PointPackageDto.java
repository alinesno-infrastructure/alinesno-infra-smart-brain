package com.alinesno.infra.smart.assistant.adapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class PointPackageDto {

    private String id;
    private String packageName;
    private String version;
    private Integer price;
    private Integer points;
    private Integer concurrentChats;
    private Integer concurrentTasks;
    private Integer contextLength;
    private String priority;
    private Integer isActive;
    private List<Feature> featureList;

    @Data
    public static class Feature {
        private String id;
        private String icon;
        private String featureText;
        private Integer sort;
    }
}