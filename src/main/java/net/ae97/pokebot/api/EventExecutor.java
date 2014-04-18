package net.ae97.pokebot.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventExecutor {

    Priority priority() default Priority.NORMAL;

    boolean ignoreCancel() default false;
}
