package restaurant.validation.experience;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ExperienceValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExperienceValidation {
    String message() default "{cannot be zero or negative}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
