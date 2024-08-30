package com.alinesno.infra.smart.brain.api.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 自定义验证器注解
 * 用于验证PromptId的有效性
 */
@Documented
@Constraint(validatedBy = PromptIdValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPromptId {

    String message() default "Invalid promptId, systemContent or userContent";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
