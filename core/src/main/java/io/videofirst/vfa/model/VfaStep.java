package io.videofirst.vfa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.videofirst.vfa.StepOptions;
import io.videofirst.vfa.enums.StepType;
import io.videofirst.vfa.enums.VfaStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO (1) - make this more advanced e.g. static create method with "type" + "text" + (optional "params" / "addQuotes").
 * <p>
 * TODO (2) - Add array of ints for parameters.  Makes down-stream code much easier.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VfaStep {

    // Mandatory

    private StepType type;
    private String text;

    // Optional

    private VfaTextParameters textParameters;

    private StepOptions options;

    // Internal

    private VfaTime time;

    @JsonIgnore
    private int totalActions;   // FIXME - think this would be better as method.

    @Getter
    @Setter(AccessLevel.NONE)
    private VfaStatus status;

    @ToString.Exclude
    private List<VfaAction> actions;

    // Parent References

    @JsonIgnore
    @ToString.Exclude
    private VfaScenario scenario; // link to parent scenario

    // Methods

    public boolean hasParameters() {
        return this.textParameters != null;
    }

    public void addAction(VfaAction action) {
        action.setStep(this); // link to parent
        if (this.actions == null) {
            this.actions = new ArrayList<>();
        }
        this.actions.add(action);
    }

    public void setStatus(VfaStatus status) {
        if (this.status != null) {
            return;
        }
        this.status = status;
        if (this.scenario != null && status != VfaStatus.passed) { // don't propagate pass statuses up
            this.scenario.setStatus(status);
        }
    }

    @JsonIgnore
    public boolean isFinished() {
        if (this.getScenario() != null) {
            // we are in finished if the status of the scenario is set
            return this.getScenario().getStatus() != null;
        }
        return false;
    }

}