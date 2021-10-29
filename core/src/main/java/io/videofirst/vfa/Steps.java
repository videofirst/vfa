package io.videofirst.vfa;

/**
 * Steps base class - add this to a Steps class so its methods can be chained together.
 */
public abstract class Steps<SELF extends Steps<?>> {

    // Given

    public SELF given() {
        Vfa.given();
        return self();
    }

    public SELF given(String text, Object... paramValues) {
        return given(text, null, paramValues);
    }

    public SELF given(String text, StepOptions options, Object... paramValues) {
        Vfa.given(text, options, paramValues);
        return self();
    }

    // When

    public SELF when() {
        Vfa.when();
        return self();
    }

    public SELF when(String text, Object... paramValues) {
        return when(text, null, paramValues);
    }

    public SELF when(String text, StepOptions options, Object... paramValues) {
        Vfa.when(text, options, paramValues);
        return self();
    }

    // Then

    public SELF then() {
        Vfa.then();
        return self();
    }

    public SELF then(String text, Object... paramValues) {
        return then(text, null, paramValues);
    }

    public SELF then(String text, StepOptions options, Object... paramValues) {
        Vfa.then(text, options, paramValues);
        return self();
    }

    // And

    public SELF and() {
        Vfa.and();
        return self();
    }

    public SELF and(String text, Object... paramValues) {
        return and(text, null, paramValues);
    }

    public SELF and(String text, StepOptions options, Object... paramValues) {
        Vfa.and(text, options, paramValues);
        return self();
    }

    // But

    public SELF but() {
        Vfa.but();
        return self();
    }

    public SELF but(String text, Object... paramValues) {
        return but(text, null, paramValues);
    }

    public SELF but(String text, StepOptions options, Object... paramValues) {
        Vfa.but(text, options, paramValues);
        return self();
    }

    // Private methods

    @SuppressWarnings("unchecked")
    private SELF self() {
        return (SELF) this;
    }

}