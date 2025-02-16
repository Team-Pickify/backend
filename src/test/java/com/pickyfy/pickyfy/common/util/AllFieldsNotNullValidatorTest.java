package com.pickyfy.pickyfy.common.util;

import com.pickyfy.pickyfy.common.AllFieldsNotNull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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
    record TestClass (String name, Integer age, List<Integer> ids){}

    @Test
    void succeed_when_all_fields_are_not_null(){
        TestClass validObject = new TestClass("피키파이", 20, List.of(1,2,3));
        Set<ConstraintViolation<TestClass>> violations = validator.validate(validObject);
        assertTrue(violations.isEmpty());
    }

    @Test
    void fail_when_some_fields_are_null() {
        TestClass invalidObject = new TestClass(null, 30, null);
        Set<ConstraintViolation<TestClass>> violations = validator.validate(invalidObject);
        assertFalse(violations.isEmpty());
    }

    @Test
    void fail_when_all_fields_are_null() {
        TestClass invalidObject = new TestClass(null, null, null);
        Set<ConstraintViolation<TestClass>> violations = validator.validate(invalidObject);
        assertFalse(violations.isEmpty());
    }

    @Test
    void fail_when_object_is_null() {
        TestClass nullObject = null;
        assertThrows(IllegalArgumentException.class, () -> validator.validate(nullObject));
    }

}
