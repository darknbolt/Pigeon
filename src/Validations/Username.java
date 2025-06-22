package Validations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {
    int min() default 4;
    int max() default 10;

    String tooShortError() default "Username.TooShort";
    String tooLongError() default "Username.TooLong";
}
