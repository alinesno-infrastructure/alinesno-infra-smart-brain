package com.alinesno.infra.base.search.vector.dto;

import lombok.Data;

/**
 * 业务对接
 */
@Data
public class EmbeddingBean {

    private Long id ;
    private float score ;
    private Long idScore ;
    private Long datasetId ;
    private String documentContent ;
    private String documentVector ;

}
