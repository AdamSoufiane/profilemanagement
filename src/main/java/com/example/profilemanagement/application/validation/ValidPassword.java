package com.example.profilemanagement.application.validation;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = {})
public @interface ValidPassword {

    String message() default "Invalid password format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
