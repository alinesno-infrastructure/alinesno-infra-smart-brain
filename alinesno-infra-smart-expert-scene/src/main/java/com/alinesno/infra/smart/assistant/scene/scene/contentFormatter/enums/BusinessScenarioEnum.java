package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum BusinessScenarioEnum {

    GOVERNMENT("government", "政务"),
    COMMERCE("commerce", "商务"),
    ECOMMERCE("ecommerce", "电商"),
    CAMPUS("campus", "校园"),
    MEDICAL("medical", "医疗"),
    EDUCATION("education", "教育"),
    FINANCE("finance", "金融"),
    MANUFACTURING("manufacturing", "制造"),
    MEDIA("media", "媒体"),
    ENTERTAINMENT("entertainment", "娱乐"),
    TRANSPORTATION("transportation", "交通"),
    AGRICULTURE("agriculture", "农业"),
    CONSTRUCTION("construction", "建筑"),
    ENERGY("energy", "能源"),
    TOURISM("tourism", "旅游"),
    SERVICE("service", "服务");

    private final String value;
    private final String label;

    BusinessScenarioEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabelByValue(String value) {
        for (BusinessScenarioEnum scenario : BusinessScenarioEnum.values()) {
            if (scenario.getValue().equals(value)) {
                return scenario.getLabel();
            }
        }
        return null;
    }

    /**
     * 根据多个业务场景值获取对应的标签
     * @param values
     * @return
     */
    public static String getLabelByValues(List<String> values) {
        StringBuilder labelBuilder = new StringBuilder();
        for (String value: values){
            if(getLabelByValue(value) != null){
                labelBuilder.append(getLabelByValue(value)).append(",");
            }
        }
        if (!labelBuilder.isEmpty()) {
            labelBuilder.deleteCharAt(labelBuilder.length() - 1);
        }
        return labelBuilder.toString();
    }
}