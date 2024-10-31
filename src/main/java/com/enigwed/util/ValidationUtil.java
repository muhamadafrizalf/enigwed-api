package com.enigwed.util;

import com.enigwed.constant.ErrorMessage;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.enigwed.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidationUtil {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    private  <T> Set<ConstraintViolation<T>> validate(T obj) {
        return validator.validate(obj);
    }

    public <T> void validateAndThrow(T obj) {
        Set<ConstraintViolation<T>> violations = validate(obj);
        if (!violations.isEmpty()) {
            List<String> errors = new ArrayList<>();
//            StringBuilder message = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<T> violation : violations) {
//                message.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
                errors.add(violation.getMessage());
            }
            throw new ValidationException(ErrorMessage.CONSTRAINT_VIOLATION, errors);
        }
    }
}
