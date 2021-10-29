package io.videofirst.vfa;

import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.annotation.Executable;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.condition.TestActiveCondition;
import io.videofirst.vfa.junit5.micronaut.VfaDisplayNameGenerator;
import io.videofirst.vfa.junit5.micronaut.VfaMicronautJunit5Extension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@ExtendWith(VfaMicronautJunit5Extension.class)
@Factory
@Inherited
@Requires(condition = TestActiveCondition.class)
@Executable
@TestMethodOrder(MethodOrderer.MethodName.class) // TODO create custom order field in @Scenario - otherwise revert
@DisplayNameGeneration(VfaDisplayNameGenerator.class)
public @interface Feature {

    // Video First annotations

    long NONE = -1;

    /**
     * Video First ID.  Highly recommend and required if running in strict mode.
     */
    long id() default NONE;

    /**
     * Optional feature text - useful if the default text created from the classname isn't enough.
     */
    String text() default "";

    /**
     * Optional feature text - useful if the default text created from the classname isn't enough.
     */
    String description() default "";

    // Micronaut annotations

    /**
     * @return The application class of the application
     */
    Class<?> application() default void.class;

    /**
     * @return The environments to use.
     */
    String[] environments() default {};

    /**
     * @return The packages to consider for scanning.
     */
    String[] packages() default {};

    /**
     * One or many references to classpath. For example: "classpath:mytest.yml"
     *
     * @return The property sources
     */
    String[] propertySources() default {};

    /**
     * Whether to rollback (if possible) any data access code between each test execution.
     *
     * @return True if changes should be rolled back
     */
    boolean rollback() default true;

    /**
     * Allow disabling or enabling of automatic transaction wrapping.
     *
     * @return Whether to wrap a test in a transaction.
     */
    boolean transactional() default true;

    /**
     * Whether to rebuild the application context before each test method.
     *
     * @return true if the application context should be rebuilt for each test method
     */
    boolean rebuildContext() default false;

    /**
     * The application context builder to use to construct the context.
     *
     * @return The builder
     */
    Class<? extends ApplicationContextBuilder>[] contextBuilder() default {};

    /**
     * The transaction mode describing how transactions should be handled for each test.
     *
     * @return The transaction mode
     */
    TransactionMode transactionMode() default TransactionMode.SEPARATE_TRANSACTIONS;

    /**
     * <p>Whether to start io.micronaut.runtime.EmbeddedApplication.</p>
     *
     * <p>When false, only the application context will be started.
     * This can be used to disable o.micronaut.runtime.server.EmbeddedServer.</p>
     *
     * @return true if io.micronaut.runtime.EmbeddedApplication
     */
    boolean startApplication() default false;

}