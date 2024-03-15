package restaurant.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import restaurant.entities.enums.Role;
import restaurant.validation.dateOfBirth.DateOfBirthValidation;
import restaurant.validation.email.EmailValidation;
import restaurant.validation.experience.ExperienceValidation;
import restaurant.validation.password.PasswordValidation;
import restaurant.validation.phoneNumber.PhoneNumberValidation;

import java.time.LocalDate;

@Builder
public record SignUpRequest(
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
        int experience,
        @NotNull
        @Enumerated(EnumType.STRING)
        Role role
) {
}
