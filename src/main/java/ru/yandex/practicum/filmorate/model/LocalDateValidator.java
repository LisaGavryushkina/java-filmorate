package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.time.Month;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class LocalDateValidator implements ConstraintValidator<LocalDateConstraint, LocalDate> {
    @Override
    public void initialize(LocalDateConstraint localDate) {
    }

    @Override
    public boolean isValid(LocalDate localDate,
                           ConstraintValidatorContext cxt) {
        return localDate != null && localDate.isAfter(LocalDate.of(1895, Month.DECEMBER, 28));
    }
}
