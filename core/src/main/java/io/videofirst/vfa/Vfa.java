package io.videofirst.vfa;

import io.micronaut.context.annotation.Context;
import io.videofirst.vfa.enums.StepType;
import io.videofirst.vfa.model.VfaStep;
import io.videofirst.vfa.model.VfaTextParameters;
import io.videofirst.vfa.service.VfaService;
import jakarta.inject.Inject;
import java.util.Arrays;

/**
 * Collection of useful methods which can be called statically.
 * <p>
 */
@Context // load immediately
public class Vfa {

    private static VfaService vfaService; // can be accessed statically

    @Inject
    public Vfa(VfaService vfaService) {
        this.vfaService = vfaService;
    }

    // Given

    public static void given() {
        step(StepType.given);
    }

    public static void given(String text, Object... paramValues) {
        given(text, null, paramValues);
    }

    public static void given(String text, StepOptions options, Object... paramValues) {
        step(StepType.given, options, text, paramValues);
    }

    // When

    public static void when() {
        step(StepType.when);
    }

    public static void when(String text, Object... paramValues) {
        when(text, null, paramValues);
    }

    public static void when(String text, StepOptions options, Object... paramValues) {
        step(StepType.when, options, text, paramValues);
    }

    // Then

    public static void then() {
        step(StepType.then);
    }

    public static void then(String text, Object... paramValues) {
        then(text, null, paramValues);
    }

    public static void then(String text, StepOptions options, Object... paramValues) {
        step(StepType.then, options, text, paramValues);
    }

    // And

    public static void and() {
        step(StepType.and);
    }

    public static void and(String text, Object... paramValues) {
        and(text, null, paramValues);
    }

    public static void and(String text, StepOptions options, Object... paramValues) {
        step(StepType.and, options, text, paramValues);
    }

    // But

    public static void but() {
        step(StepType.but);
    }

    public static void but(String text, Object... paramValues) {
        but(text, null, paramValues);
    }

    public static void but(String text, StepOptions options, Object... paramValues) {
        step(StepType.but, options, text, paramValues);
    }

    // None

    public static void none() {
        step(StepType.none);
    }

    public static void none(String text, Object... paramValues) {
        none(text, null, paramValues);
    }

    public static void none(String text, StepOptions options, Object... paramValues) {
        step(StepType.none, options, text, paramValues);
    }

    // Step (note, type must already be set before)

    public static void step(String text, Object... paramValues) {
        vfaService.setStepText(text, null, paramValues);
    }

    public static void step(String text, StepOptions options, Object... paramValues) {
        vfaService.setStepText(text, options, paramValues);
    }

    // Action methods

    public static void action(String name) {
        vfaService.action(name);
    }

    // Private methods

    private static void step(StepType type) {
        vfaService.setStepType(type);
    }

    private static void step(StepType type, StepOptions options, String text, Object... paramValues) {
        VfaTextParameters textParameters = VfaTextParameters.parse(text, Arrays.asList(paramValues));
        VfaStep step = VfaStep.builder()
            .type(type)
            .options(options)
            .text(text)
            .textParameters(textParameters)
            .build();
        vfaService.before(step);
    }

}