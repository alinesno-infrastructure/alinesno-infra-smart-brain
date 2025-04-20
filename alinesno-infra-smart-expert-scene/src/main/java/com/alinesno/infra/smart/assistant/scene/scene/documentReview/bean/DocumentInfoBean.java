package com.alinesno.infra.smart.assistant.scene.scene.documentReview.bean;

import lombok.Data;

@Data
public class DocumentInfoBean {

    private String content ;

    public DocumentInfoBean(String content) {
        this.content = content;
    }
}
