package org.jdna.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = java.lang.annotation.ElementType.TYPE)
public @interface Group {
    String path();
    String label();
    String description() default "";
}
