package restaurant.dto.request;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import restaurant.entities.enums.RestType;
import restaurant.validation.dateOfBirth.DateOfBirthValidation;
import restaurant.validation.email.EmailValidation;
import restaurant.validation.experience.ExperienceValidation;
import restaurant.validation.password.PasswordValidation;
import restaurant.validation.phoneNumber.PhoneNumberValidation;

import java.time.LocalDate;

@Builder
public record SaveRestaurantRequest(
        @NotBlank
        String name,
        @NotBlank
        String location,
        @Enumerated(EnumType.STRING)
        RestType restType,
        @ExperienceValidation
        int service,
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
