package com.enigwed.util;

import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ValidationUtil {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public <T> Set<ConstraintViolation<T>> validate(T obj) {
        return validator.validate(obj);
    }

    public <T> void validateAndThrow(T obj) {
        Set<ConstraintViolation<T>> violations = validate(obj);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<T> violation : violations) {
                message.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
            }
            throw new ValidationException(message.toString());
        }
    }
}
