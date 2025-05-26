package mate.academy.booking.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapperImpl;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstField;
    private String secondField;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstField = constraintAnnotation.first();
        this.secondField = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        Object field = new BeanWrapperImpl(object).getPropertyValue(this.firstField);
        Object fieldMatch = new BeanWrapperImpl(object).getPropertyValue(this.secondField);
        return Objects.equals(field, fieldMatch);
    }
}
