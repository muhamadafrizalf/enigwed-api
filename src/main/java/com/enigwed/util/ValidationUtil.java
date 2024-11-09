package com.enigwed.util;

import com.enigwed.constant.SErrorMessage;
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

    public <T> void validateAndThrow(T obj) throws ValidationException {
        Set<ConstraintViolation<T>> violations = validate(obj);
        if (!violations.isEmpty()) {
            List<String> errors = new ArrayList<>();
            for (ConstraintViolation<T> violation : violations) {
                errors.add(violation.getMessage());
            }
            throw new ValidationException(SErrorMessage.CONSTRAINT_VIOLATION, errors);
        }
    }
}
