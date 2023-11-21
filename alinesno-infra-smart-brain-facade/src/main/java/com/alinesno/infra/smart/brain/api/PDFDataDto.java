package com.alinesno.infra.smart.brain.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PDFDataDto {

    private long id;
    private String content;
    private int contentWordCount;

}
