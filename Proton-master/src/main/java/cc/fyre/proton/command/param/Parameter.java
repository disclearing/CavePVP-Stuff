package cc.fyre.proton.command.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Parameter {

    String name();

    String defaultValue() default "";

    String[] tabCompleteFlags() default {};

    boolean wildcard() default false;

}
