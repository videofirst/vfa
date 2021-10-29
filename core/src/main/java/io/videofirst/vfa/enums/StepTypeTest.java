package io.videofirst.vfa.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Junit class to test the methods of StepType.
 */
public class StepTypeTest {

    @Test
    public void shouldLabel() {
        assertThat(StepType.given.label()).isEqualTo("Given");
        assertThat(StepType.when.label()).isEqualTo(" When");
        assertThat(StepType.then.label()).isEqualTo(" Then");
        assertThat(StepType.and.label()).isEqualTo("  And");
        assertThat(StepType.but.label()).isEqualTo("  But");
    }

}