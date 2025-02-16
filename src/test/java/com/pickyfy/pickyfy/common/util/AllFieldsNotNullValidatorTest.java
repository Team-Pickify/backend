package com.pickyfy.pickyfy.common.util;

import com.pickyfy.pickyfy.common.AllFieldsNotNull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AllFieldsNotNullValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AllFieldsNotNull
    record TestClass (String name, Integer age){}

    @Test
    void 모든_필드가_null이_아닐_때_성공(){
        TestClass validObject = new TestClass("피키파이", 20);
        Set<ConstraintViolation<TestClass>> violations = validator.validate(validObject);
        assertTrue(violations.isEmpty());
    }

    @Test
    void 하나의_필드가_null이면_실패() {
        TestClass invalidObject = new TestClass(null, 30);
        Set<ConstraintViolation<TestClass>> violations = validator.validate(invalidObject);
        assertFalse(violations.isEmpty());
    }

    @Test
    void 모든_필드가_null이면_실패() {
        TestClass invalidObject = new TestClass(null, null);
        Set<ConstraintViolation<TestClass>> violations = validator.validate(invalidObject);
        assertFalse(violations.isEmpty());
    }

    @Test
    void 객체가_null이면_실패() {
        TestClass nullObject = null;
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullObject));
    }

}
