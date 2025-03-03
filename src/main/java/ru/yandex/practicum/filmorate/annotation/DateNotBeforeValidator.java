package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateNotBeforeValidator implements ConstraintValidator<DateNotBefore, LocalDate> {
    private LocalDate notBeforeDate;

    @Override
    public void initialize(DateNotBefore constraintAnnotation) {
        notBeforeDate = LocalDate.parse(constraintAnnotation.date());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) {
            return true;
        }

        return !localDate.isBefore(notBeforeDate);
    }
}
