package com.pickyfy.pickyfy.common.util;

import com.pickyfy.pickyfy.common.AllFieldsNotNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class AllFieldsNotNullValidator implements ConstraintValidator<AllFieldsNotNull, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context){
        if (object == null) {
            return false; // 클래스 자체가 null이면 false 반환
        }

        // 클래스에 선언된 모든 필드 가져와서 순회하고 Null 검사함
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true); // private, protected 접근자 붙었더라도 필드에 강제 접근 가능
            try {
                if (field.get(object) == null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("필드 접근 실패", e);
            }
        }
        return true; // 모든 필드가 null이 아닐 때만 true 반환
    }
}
