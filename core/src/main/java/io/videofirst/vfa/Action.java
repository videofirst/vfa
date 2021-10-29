package io.videofirst.vfa;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.Type;
import io.videofirst.vfa.aop.VfaActionInterceptor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
@Around
@Type(VfaActionInterceptor.class)
public @interface Action {

    boolean isAssert() default false;

    boolean screenshotBefore() default false;

    boolean screenshotAfter() default true;

}