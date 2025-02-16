package com.pickyfy.pickyfy.common;

import com.pickyfy.pickyfy.common.util.AllFieldsNotNullValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {AllFieldsNotNullValidator.class})
@NotNull
public @interface AllFieldsNotNull {
    String message() default "모든 필드는 not Null";
    Class<?>[] groups() default {};  // 🚨 groups 추가 (필수)
    Class<? extends Payload>[] payload() default {};  // 🚨 payload 추가 (필수)
}
