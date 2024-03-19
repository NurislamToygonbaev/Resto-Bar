package restaurant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import restaurant.validation.dateOfBirth.DateOfBirthValidation;
import restaurant.validation.email.EmailValidation;
import restaurant.validation.experience.ExperienceValidation;
import restaurant.validation.password.PasswordValidation;
import restaurant.validation.phoneNumber.PhoneNumberValidation;

import java.time.LocalDate;

public record UpdateRequest(
        String lastName,
        String firstName,
        @DateOfBirthValidation
        LocalDate dateOfBirth,
        @Email @EmailValidation
        String email,
        @PasswordValidation
        String password,
        @PhoneNumberValidation
        String phoneNumber,
        @ExperienceValidation
        @NotNull
        Integer experience
) {
}
