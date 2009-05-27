package org.jdna.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = java.lang.annotation.ElementType.FIELD)
public @interface Field {
    public static final String USE_FIELD_NAME = "";
    public static final String USE_PARENT_GROUP = "";

    String name() default USE_FIELD_NAME;
    String label();
    String description() default "";
    String fullKey() default USE_PARENT_GROUP;
    boolean hidden() default false;
}
