package io.videofirst.vfa;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.AliasFor;
import io.micronaut.context.annotation.Type;
import io.videofirst.vfa.aop.VfaStepInterceptor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
@Around
@Type(VfaStepInterceptor.class)
public @interface Step {

    /**
     * Step text e.g. "A user is at the homepage".
     */
    @AliasFor(member = "value")
    String text() default "";

    /**
     * Step text e.g. "A user is at the homepage".
     */
    String value() default "";

    /**
     * Boolean to denote if we add quotations around parameters (e.g. double quotes to String).  Defaults to true.
     */
    boolean[] addQuotes() default {};

}