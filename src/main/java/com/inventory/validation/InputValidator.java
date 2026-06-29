package com.inventory.validation;

import com.inventory.dto.ProductRequest;
import com.inventory.exception.ValidationException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Centralized input validation helpers using regex and business rules. */
public final class InputValidator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+()\\-\\s]{7,20}$");
    private static final Pattern BARCODE = Pattern.compile("^[0-9A-Za-z\\-]{6,32}$");

    private InputValidator() {
    }

    /** Validates an email address. */
    public static void email(String value) throws ValidationException {
        if (value == null || !EMAIL.matcher(value).matches()) {
            throw new ValidationException("Invalid email address");
        }
    }

    /** Validates a phone number. */
    public static void phone(String value) throws ValidationException {
        if (value == null || !PHONE.matcher(value).matches()) {
            throw new ValidationException("Invalid phone number");
        }
    }

    /** Validates a product request. */
    public static void product(ProductRequest request) throws ValidationException {
        if (request.name() == null || request.name().isBlank()) {
            throw new ValidationException("Product name is required");
        }
        if (request.purchasePrice().compareTo(BigDecimal.ZERO) < 0 || request.sellingPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Prices cannot be negative");
        }
        if (request.stock() < 0 || request.minimumStock() < 0) {
            throw new ValidationException("Stock values cannot be negative");
        }
        if (request.barcode() == null || !BARCODE.matcher(request.barcode()).matches()) {
            throw new ValidationException("Invalid barcode");
        }
    }
}
