package com.alinesno.infra.smart.assistant.workflow.nodes.step.compare;

import java.util.Collection;

public class GTConditionVerifier implements ConditionVerifier {
    @Override
    public boolean isSupported(String compare) {
        return compare.equals(CompareTypeEnums.GT.getValue());
    }

    @Override
    public boolean verify(Object sourceValue, String targetValue) {
        if (sourceValue instanceof Collection){
            return ((Collection<?>) sourceValue).size()> Integer.parseInt(targetValue);
        }
        if (sourceValue instanceof Float){
            return (float) sourceValue> Float.parseFloat(targetValue);
        }
        if (sourceValue instanceof Double){
            return (double) sourceValue> Double.parseDouble(targetValue);
        }
        if (sourceValue instanceof Integer){
            return (int) sourceValue> Integer.parseInt(targetValue);
        }
        return false;
    }
}
