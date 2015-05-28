package com.graphaware.importer.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation indicating that a field should have a named cache injected.
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface InjectCache {

    /**
     * @return Name of the cache to inject.
     */
    String name();

    /**
     * @return Whether the class whose field is annotated is the creator of the cache.
     */
    boolean creator() default false;
}
