package com.alinesno.infra.smart.assistant.api.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ConditionalValidJsonValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalValidJson {
    String message() default "无效的JSON对象";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}