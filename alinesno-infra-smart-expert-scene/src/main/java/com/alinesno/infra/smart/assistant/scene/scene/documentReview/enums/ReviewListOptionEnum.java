package com.alinesno.infra.smart.assistant.scene.scene.documentReview.enums;

import lombok.Getter;

@Getter
public enum ReviewListOptionEnum {

    // aigen/dataset
    AIGEN("aigen") , DATASET("dataset") ;

    private final String value ;

    ReviewListOptionEnum(String value){
        this.value = value ;
    }
}
