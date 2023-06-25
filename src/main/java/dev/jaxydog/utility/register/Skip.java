package dev.jaxydog.utility.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Tells the automatic registration process to ignore the marked value */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Skip {

    /** Whether to skip on the client and the server */
    public boolean main() default true;

    /** Whether to skip on the client */
    public boolean client() default true;

    /** Whether to skip on the server */
    public boolean server() default true;

    /** Whether to skip on the datagen client */
    public boolean datagen() default true;

}
