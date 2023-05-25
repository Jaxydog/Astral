package dev.jaxydog.astral.utility.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Tells the auto-registration system to skip over the annotated value */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SkipRegistration {
	/** Whether the value should be skipped on the client environment */
	public boolean client() default true;

	/** Whether the value should be skipped on the main environment */
	public boolean main() default true;

	/** Whether the value should be skipped on the server environment */
	public boolean server() default true;
}
