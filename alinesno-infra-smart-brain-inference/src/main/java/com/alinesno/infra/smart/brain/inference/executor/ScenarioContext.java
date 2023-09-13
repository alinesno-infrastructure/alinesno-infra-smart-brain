package com.alinesno.infra.smart.brain.inference.executor;

import com.alinesno.infra.common.core.context.SpringContext;
import com.alinesno.infra.smart.brain.inference.scenario.ParentScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ScenarioContext {

    private static final Logger log = LoggerFactory.getLogger(ScenarioContext.class) ;

    private static final Map<String , String> screnMap = new HashMap<>() ;

    static {
        screnMap.put("FLOW_SOFT" , "") ;
        screnMap.put("FLOW_DATA" , "") ;
        screnMap.put("FLOW_PRODUCT" , "") ;
        screnMap.put("FLOW_ITOPS" , "") ;
    }

    public static ParentScenario getInstance(String type){
        String flowChain = screnMap.get(type) ;

        log.debug("flowChain = {}" , flowChain);

        return (ParentScenario) SpringContext.getBean(flowChain);
    }

}
