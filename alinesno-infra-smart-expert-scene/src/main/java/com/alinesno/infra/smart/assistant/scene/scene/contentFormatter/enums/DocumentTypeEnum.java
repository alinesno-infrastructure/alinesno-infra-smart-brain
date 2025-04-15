package com.alinesno.infra.smart.assistant.scene.scene.contentFormatter.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum DocumentTypeEnum {

    NOTICE("notice", "通知"),
    GOVERNMENT_LETTER("government_letter", "政务函"),
    RED_HEAD_DOCUMENT("red_head_document", "红头文件"),
    REPORT("report", "报告"),
    MEMO("memo", "备忘录"),
    CONTRACT("contract", "合同"),
    AGREEMENT("agreement", "协议"),
    PROPOSAL("proposal", "提案"),
    ANNOUNCEMENT("announcement", "公告"),
    CIRCULAR("circular", "通报"),
    INVITATION("invitation", "邀请函"),
    DECLARATION("declaration", "声明"),
    APPLICATION("application", "申请书"),
    RESUME("resume", "简历");

    private final String value;
    private final String label;

    DocumentTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static String getLabelByValue(String value) {
        for (DocumentTypeEnum type : DocumentTypeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type.getLabel();
            }
        }
        return null;
    }

    public static String getLabelByValues(List<String> values) {
        StringBuilder labelBuilder = new StringBuilder();
        for (String value : values) {
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