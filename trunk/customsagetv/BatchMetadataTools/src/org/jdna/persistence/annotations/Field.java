package org.jdna.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = java.lang.annotation.ElementType.FIELD)
public @interface Field {
    public static final String USE_FIELD_NAME = "";

    boolean key() default false;

    String name() default USE_FIELD_NAME;
    String label();

    String description() default "";
    
    boolean map() default false;
}
