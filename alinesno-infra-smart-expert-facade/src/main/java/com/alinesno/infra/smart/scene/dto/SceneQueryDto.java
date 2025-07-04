package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.common.facade.pageable.DatatablesPageBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SceneQueryDto类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneQueryDto extends DatatablesPageBean {

    /**
     * 场景名称
     */
    private String sceneName ;

    /**
     * 场景类型
     */
    private String sceneType ;

}
