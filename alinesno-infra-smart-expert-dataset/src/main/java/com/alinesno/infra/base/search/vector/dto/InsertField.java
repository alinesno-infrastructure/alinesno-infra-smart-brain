package com.alinesno.infra.base.search.vector.dto;

import lombok.Data;

@Data
public class InsertField {

    private String fieldName;
    private Object fieldValue ;

    public InsertField(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
