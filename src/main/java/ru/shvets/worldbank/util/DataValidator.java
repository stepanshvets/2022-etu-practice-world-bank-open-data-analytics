package ru.shvets.worldbank.util;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataValidator<T> {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public Set<String> validate(T objToValidate) {
        Set<ConstraintViolation<T>> violations = validator.validate(objToValidate);
        if (!violations.isEmpty())
            return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
        return Collections.emptySet();
    }
}
