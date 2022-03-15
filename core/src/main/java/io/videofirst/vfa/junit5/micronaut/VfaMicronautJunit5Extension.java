package io.videofirst.vfa.junit5.micronaut;

import static io.micronaut.context.env.Environment.BOOTSTRAP_CONTEXT_PROPERTY;
import static io.micronaut.context.env.Environment.BOOTSTRAP_NAME_PROPERTY;

import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.MicronautTestValue;
import io.micronaut.test.context.TestContext;
import io.micronaut.test.extensions.junit5.MicronautJunit5Extension;
import io.videofirst.vfa.Feature;
import io.videofirst.vfa.Scenario;
import io.videofirst.vfa.exceptions.VfaException;
import io.videofirst.vfa.model.VfaFeature;
import io.videofirst.vfa.model.VfaScenario;
import io.videofirst.vfa.service.VfaService;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Junit5 extension which builds on Micronaut Framework and Video First Automation annotations.
 * <p>
 */
public class VfaMicronautJunit5Extension extends MicronautJunit5Extension implements
    TestExecutionExceptionHandler {

    {
        // Enable Bootstrap configuration so shorter `vfa.yaml` can be used  
        System.setProperty(BOOTSTRAP_CONTEXT_PROPERTY, "true");
        System.setProperty(BOOTSTRAP_NAME_PROPERTY, "vfa");
    }

    private VfaService vfaService;
    private VfaDisplayNameGenerator vfaDisplayNameGenerator = VfaDisplayNameGenerator.INSTANCE; // not currently using Micronaut injection

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        super.beforeAll(extensionContext);

        VfaService vfaService = applicationContext.getBean(VfaService.class);
        if (vfaService == null) {
            new VfaException("Service not initialised - exiting");
        }
        this.vfaService = vfaService;

        // Extract class / Feature annotation
        final Class<?> testClass = extensionContext.getRequiredTestClass();
        final Feature featureAnnotation = AnnotationSupport.findAnnotation(testClass, Feature.class)
            .get();

        // Extract fields and create VfaFeature model (should this be done here or another service - to make more SRP?)
        long id = featureAnnotation.id();
        String className = testClass.getName();
        String text = vfaDisplayNameGenerator.generateDisplayNameForClass(testClass);
        String description = featureAnnotation.description().trim();

        VfaFeature feature = VfaFeature.builder()
            .id(id)
            .text(text)
            .description(description)
            .className(className)
            .build();

        this.vfaService.before(feature);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        injectEnclosingTestInstances(extensionContext);
        final Optional<Object> testInstance = extensionContext.getTestInstance();
        final Optional<Method> testMethod = extensionContext.getTestMethod();
        List<Property> propertyAnnotations = null;
        if (testMethod.isPresent()) {
            Property[] annotationsByType = testMethod.get().getAnnotationsByType(Property.class);
            propertyAnnotations = Arrays.asList(annotationsByType);
        }

        if (testMethod.isPresent()) {
            final Scenario scenarioAnnotation = testMethod.get().getAnnotation(Scenario.class);

            if (scenarioAnnotation == null) {
                new VfaException("Cannot run test without Scenario annotation present");
            }

            long id = scenarioAnnotation.id();
            String methodName = testMethod.get().getName();
            String text = vfaDisplayNameGenerator
                .generateDisplayNameForMethod(testInstance.getClass(), testMethod.get());

            VfaScenario scenario = VfaScenario.builder()
                .id(id)
                .text(text)
                .methodName(methodName)
                .build();
            this.vfaService.before(scenario);
        }

        beforeEach(extensionContext, testInstance.orElse(null), testMethod.orElse(null),
            propertyAnnotations);
        beforeTestMethod(buildContext(extensionContext));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        super.afterEach(extensionContext);

        VfaScenario scenario = this.vfaService.getCurrentScenario();
        if (scenario != null) {
            this.vfaService.after(scenario);
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        afterTestClass(buildContext(extensionContext));
        afterClass(extensionContext);

        VfaFeature feature = this.vfaService.getCurrentFeature();
        if (feature != null) {
            this.vfaService.after(feature);
        }
    }

    /**
     * Builds a {@link MicronautTestValue} object from the provided class (e.g. by scanning annotations).
     *
     * @param testClass the class to extract builder configuration from
     * @return a MicronautTestValue to configure the test application context
     */
    @Override
    protected MicronautTestValue buildMicronautTestValue(Class<?> testClass) {
        final Optional<Feature> featureAnnotation = AnnotationSupport.findAnnotation(testClass,
            Feature.class);
        return featureAnnotation
            .map(VfaMicronautJunit5Extension::buildValueObject)
            .orElseThrow(
                () -> new VfaException("Cannot run extension without Feature annotation present"));
    }

    /**
     * @param testClass the test class
     * @return true if the provided test class holds the expected test annotations
     */
    @Override
    protected boolean hasExpectedAnnotations(Class<?> testClass) {
        return AnnotationSupport.isAnnotated(testClass, Feature.class);
    }

    // Private methods

    /**
     * Copied from AnnotationUtils.
     */
    private static MicronautTestValue buildValueObject(Feature feature) {
        if (feature != null) {
            return new MicronautTestValue(
                feature.application(),
                feature.environments(),
                feature.packages(),
                feature.propertySources(),
                feature.rollback(),
                feature.transactional(),
                feature.rebuildContext(),
                feature.contextBuilder(),
                feature.transactionMode(),
                feature.startApplication());
        } else {
            return null;
        }
    }

    private TestContext buildContext(ExtensionContext context) {
        return new TestContext(
            applicationContext,
            context.getTestClass().orElse(null),
            context.getTestMethod().orElse(null),
            context.getTestInstance().orElse(null),
            context.getExecutionException().orElse(null));
    }

    private void injectEnclosingTestInstances(ExtensionContext extensionContext) {
        extensionContext.getTestInstances().ifPresent(testInstances -> {
            List<Object> allInstances = testInstances.getAllInstances();
            allInstances.stream().limit(allInstances.size() - 1)
                .forEach(applicationContext::inject);
        });
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) {
        vfaService.handleThrowable(throwable);
    }

}