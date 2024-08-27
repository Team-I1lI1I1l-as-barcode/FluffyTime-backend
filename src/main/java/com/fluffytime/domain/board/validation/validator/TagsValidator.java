package com.fluffytime.domain.board.validation.validator;

import com.fluffytime.domain.board.validation.annotation.ValidTags;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class TagsValidator implements ConstraintValidator<ValidTags, List<String>> {

    @Override
    public void initialize(ValidTags constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> tags,
        ConstraintValidatorContext constraintValidatorContext) {
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        for (String tag : tags) {
            if(tag == null || tag.length() > 20) {
                return false;
            }
            if(!tag.matches("^[\\p{L}\\p{N}_]+$")) {
                return false;
            }
        }
        return true;
    }
}
