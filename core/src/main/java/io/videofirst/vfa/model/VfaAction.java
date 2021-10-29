package io.videofirst.vfa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micronaut.aop.MethodInvocationContext;
import io.videofirst.vfa.enums.VfaStatus;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VfaAction {

    private String className;  // should we refactor to an object? e.g. ActionClass ???

    private String methodName;

    private String alias;

    private LinkedHashMap<String, Object> params;

    private VfaTime time;

    private List<String> screenshots;

    @Getter
    @Setter(AccessLevel.NONE)
    private VfaStatus status;

    @Getter
    @Setter(AccessLevel.NONE)
    private VfaError error;

    // children actions

    @ToString.Exclude
    private List<VfaAction> actions;

    // Parent / context objects

    @JsonIgnore
    @ToString.Exclude
    private VfaStep step; // link to parent scenario

    @JsonIgnore
    @ToString.Exclude // TODO exclude from JSON as well
    private VfaAction parent; // link to parent action (if applicable)

    @JsonIgnore
    @ToString.Exclude
    private MethodInvocationContext<Object, Object> methodContext;  // raw method context

    // Methods

    @JsonIgnore
    public VfaScenario getScenario() {
        return this.step != null ? this.step.getScenario() : null;
    }

    @JsonIgnore
    public int countParents() {
        int numOfParents = 0;  // TODO - Java8 stream might be nicer
        VfaAction curAction = this;
        while (curAction.getParent() != null) {
            numOfParents++;
            curAction = curAction.getParent();
        }
        return numOfParents;
    }

    @JsonIgnore
    public boolean isFinished() {
        if (step != null && step.getScenario() != null) {
            // we are in finished if the status of the scenario is set
            return step.getScenario().getStatus() != null;
        }
        return false;
    }

    public void addAction(VfaAction action, VfaStep step) {
        action.setParent(this);  // link to this
        action.setStep(step); // link to step
        if (this.actions == null) {
            this.actions = new ArrayList<>();
        }
        this.actions.add(action);
    }

    public void addScreenshot(String screenshot) {
        if (this.screenshots == null) {
            this.screenshots = new ArrayList<>();
            this.screenshots.add(screenshot);
        }
        if (this.step != null && this.step.getScenario() != null) {
            VfaScenario scenario = this.step.getScenario();
            scenario.addScreenshot(screenshot);
        }
    }

    public void setStatus(VfaStatus status) {
        if (this.status != null) {
            return;
        }
        this.status = status;

        if (this.parent != null) {
            this.parent.setStatus(status);
        }
        if (this.step != null && status != VfaStatus.passed) { // don't propagate pass statuses up
            this.step.setStatus(status);
        }
    }

    public void setError(VfaError error) {
        if (this.error != null) {
            return;
        }
        this.error = error;
        if (this.getScenario() != null) {
            this.getScenario().setError(error);
        }
    }

}