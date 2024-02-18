package dev.jaxydog.astral.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a field as intentionally ignored during automatic registration. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreRegistration {

	/** Whether registration in the common environment should be ignored. */
	boolean common() default true;

	/** Whether registration in the client environment should be ignored. */
	boolean client() default true;

	/** Whether registration in the server environment should be ignored. */
	boolean server() default true;

	/** Whether registration in the data generation environment should be ignored. */
	boolean dataGen() default true;

}
