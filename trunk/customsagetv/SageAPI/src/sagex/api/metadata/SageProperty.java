package sagex.api.metadata;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = java.lang.annotation.ElementType.METHOD)
public @interface SageProperty {
    /**
     * The Key for the Sage Metadata property, ie, "Title", "MediaTitle", etc.
     * @return
     */
    public String value();
    
    /**
     * If format is true, then MessageFormat.format() will be called on the property passing in the method args as
     * paramters to the key.
     * 
     * For example, if the value() is "/test/{0}/{1}/prop", and the method is called with values, "A", "B", then 
     * the actual name of the sage metadata key will be "/test/A/B/prop"
     * 
     * This can only be used on "get" properties... it does not work for "set" properties
     * 
     * @return
     */
    public boolean format() default false;
}
