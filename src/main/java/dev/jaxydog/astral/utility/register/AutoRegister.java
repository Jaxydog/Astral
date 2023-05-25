package dev.jaxydog.astral.utility.register;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks the annotated type as a content container that may be automatically registered at runtime */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoRegister {
	/** Whether the class's fields should be registered on the client environment */
	public boolean client() default true;

	/** Whether the class's fields should be registered on the main environment */
	public boolean main() default true;

	/** Whether the class's fields should be registered on the server environment */
	public boolean server() default true;
}
