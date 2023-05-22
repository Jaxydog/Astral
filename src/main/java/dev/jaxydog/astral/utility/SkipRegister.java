package dev.jaxydog.astral.utility;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a field to be skipped during value registration */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SkipRegister {
	/** Whether the value's registration should be skipped on the client environment */
	public boolean client() default true;

	/** Whether the value's registration should be skipped on the main environment */
	public boolean main() default true;

	/** Whether the value's registration should be skipped on the server environment */
	public boolean server() default true;
}
