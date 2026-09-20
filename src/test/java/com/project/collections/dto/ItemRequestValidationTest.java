package com.project.collections.dto;

import com.project.collections.model.ItemType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestValidationTest {

    private ValidatorFactory validatorFactory;

    private Validator validator;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void validate_blankTitle_returnsViolationOnTitle() {
        ItemRequest request = new ItemRequest("", "A fantasy novel", ItemType.BOOK, 1937);

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(isViolatedProperty(violations, "title"));
    }

    @Test
    void validate_titleOf256Chars_returnsViolationOnTitle() {
        ItemRequest request = new ItemRequest("t".repeat(256), null, ItemType.BOOK, null);

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(isViolatedProperty(violations, "title"));
    }

    @Test
    void validate_descriptionOf2001Chars_returnsViolationOnDescription() {
        ItemRequest request = new ItemRequest("The Hobbit", "d".repeat(2001), ItemType.BOOK, 1937);

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(isViolatedProperty(violations, "description"));
    }

    @Test
    void validate_nullType_returnsViolationOnType() {
        ItemRequest request = new ItemRequest("The Hobbit", null, null, 1937);

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(isViolatedProperty(violations, "type"));
    }

    @Test
    void validate_fullyPopulatedRequest_returnsNoViolations() {
        ItemRequest request = new ItemRequest("The Hobbit", "A fantasy novel", ItemType.BOOK, 1937);

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(request);

        assertEquals(0, violations.size());
    }

    private boolean isViolatedProperty(Set<ConstraintViolation<ItemRequest>> violations, String property) {
        return violations.stream()
                .anyMatch(violation -> property.equals(violation.getPropertyPath().toString()));
    }

}
