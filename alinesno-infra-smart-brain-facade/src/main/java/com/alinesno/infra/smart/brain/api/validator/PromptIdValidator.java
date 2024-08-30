package com.alinesno.infra.smart.brain.api.validator;

import com.alinesno.infra.smart.brain.api.BrainTaskDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义验证器实现类
 * 用于验证PromptId的有效性
 */
public class PromptIdValidator implements ConstraintValidator<ValidPromptId, BrainTaskDto> {

    @Override
    public void initialize(ValidPromptId constraintAnnotation) {
    }

    @Override
    public boolean isValid(BrainTaskDto dto, ConstraintValidatorContext context) {
        return dto.getPromptId() != null ;
    }
}
