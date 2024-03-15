package restaurant.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import restaurant.validation.email.EmailValidation;
import restaurant.validation.password.PasswordValidation;

@Builder
public record SignInRequest(
        @EmailValidation @Email
        String email,
        @PasswordValidation
        String password
) {
}
