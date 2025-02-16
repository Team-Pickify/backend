package com.pickyfy.pickyfy.common;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@NotNull
public @interface AllFieldsNotNull {
    String message() default "모든 필드는 Null이 될 수 없습니다.";
}
