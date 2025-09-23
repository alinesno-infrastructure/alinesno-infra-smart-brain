package com.alinesno.infra.smart.assistant.workflow.nodes.step.compare;

import java.util.Objects;

public class IsNullConditionVerifier implements ConditionVerifier {
    @Override
    public boolean isSupported(String compare) {
        return compare.equals(CompareTypeEnums.IS_NULL.getValue());
    }

    @Override
    public boolean verify(Object sourceValue, String targetValue) {
        return Objects.isNull(sourceValue)||sourceValue.equals("");
    }
}
