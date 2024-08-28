package com.fluffytime.domain.board.validation.annotation;

import com.fluffytime.domain.board.validation.validator.TagsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TagsValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTags {
    String message() default "Invalid Tag";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
