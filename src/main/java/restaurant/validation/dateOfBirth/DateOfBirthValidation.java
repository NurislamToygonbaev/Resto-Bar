package restaurant.validation.dateOfBirth;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {DateOfBirthValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateOfBirthValidation {
    String message() default "{The age must be over 18}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
