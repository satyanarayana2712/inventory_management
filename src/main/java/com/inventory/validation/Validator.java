package com.inventory.validation;

import com.inventory.exception.ValidationException;

/** Generic validator contract. */
@FunctionalInterface
public interface Validator<T> {
    /** Validates the provided value. */
    void validate(T value) throws ValidationException;
}
