package restaurant.validation.dateOfBirth;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateOfBirthValidator implements ConstraintValidator<DateOfBirthValidation, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        int year = localDate.getYear();
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - year;
        return age > 18 && age < 45;
    }
}
