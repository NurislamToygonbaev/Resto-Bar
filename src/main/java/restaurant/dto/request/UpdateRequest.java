package restaurant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import restaurant.validation.dateOfBirth.DateOfBirthValidation;
import restaurant.validation.email.EmailValidation;
import restaurant.validation.experience.ExperienceValidation;
import restaurant.validation.password.PasswordValidation;
import restaurant.validation.phoneNumber.PhoneNumberValidation;

import java.time.LocalDate;

public record UpdateRequest(
        @NotBlank
        String lastName,
        @NotBlank
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
        int experience
) {
}
