package com.alinesno.infra.smart.assistant.workflow.nodes.step.compare;

import java.util.List;

public class LengthLTConditionVerifier implements ConditionVerifier {
    @Override
    public boolean isSupported(String compare) {
        return compare.equals(CompareTypeEnums.LEN_LT.getValue());
    }

    @Override
    public boolean verify(Object sourceValue, String targetValue) {
        int target = Integer.parseInt(targetValue);
        if(sourceValue instanceof List<?>){
            return ((List<?>) sourceValue).size() < target;
        }
        if (sourceValue instanceof String){
            return ((String) sourceValue).length() < target;
        }
        return false;
    }
}
