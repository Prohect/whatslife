package property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Mutable {
    double minValue() default 0;

    double maxValue() default 10;

    /**
     * @return the mute rate for enum or (max - min)
     */
    double step() default 1E-1;
}
