package com.alinesno.infra.smart.assistant.workflow.nodes.step.compare;

public class EqualConditionVerifier implements ConditionVerifier {
    @Override
    public boolean isSupported(String compare) {
        return compare.equals(CompareTypeEnums.EQ.getValue());
    }

    @Override
    public boolean verify(Object sourceValue, String targetValue) {
        return sourceValue.equals(targetValue);
    }
}
