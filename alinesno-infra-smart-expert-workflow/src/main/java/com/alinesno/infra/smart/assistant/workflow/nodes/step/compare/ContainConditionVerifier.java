package com.alinesno.infra.smart.assistant.workflow.nodes.step.compare;

import java.util.List;

public class ContainConditionVerifier implements ConditionVerifier {
    @Override
    public boolean isSupported(String compare) {
        return compare.equals(CompareTypeEnums.CONTAIN.getValue());
    }

    @Override
    public boolean verify(Object sourceValue, String targetValue) {
        if(sourceValue instanceof List<?>){
            return ((List<?>) sourceValue).contains(targetValue);
        }
        if(sourceValue instanceof String){
            return ((String) sourceValue).contains(targetValue);
        }
        return false;
    }
}
