package dev.jaxydog.astral.utility;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a class as a type container that should be automatically registered by the mod */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoRegister {
	/** Whether values should be registered on the client environment */
	public boolean client() default true;

	/** Whether values should be registered on the main environment */
	public boolean main() default true;

	/** Whether values should be registered on the server environment */
	public boolean server() default true;
}
