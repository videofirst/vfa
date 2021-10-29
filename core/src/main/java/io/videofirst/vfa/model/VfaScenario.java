package io.videofirst.vfa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VfaScenario {

    private Long id;

    private String text;

    private String description;

    private String methodName;

    private List<VfaStep> steps;

    @Getter
    @Setter(AccessLevel.NONE)
    private VfaStatus status;

    @Getter
    @Setter(AccessLevel.NONE)
    private VfaError error;

    // used with e.g. given().I_am_at_the_homepage(); // FIXME - move to service layer ????
    private StepType stepType;

    private VfaTime time;

    private List<String> screenshots; // all screenshots (currently Strings but might change in future).

    // Parent references

    @JsonIgnore
    @ToString.Exclude
    private VfaFeature feature; // link to parent feature

    // Methods

    public void addStep(VfaStep step) {
        step.setScenario(this); // link to parent
        if (this.steps == null) {
            this.steps = new ArrayList<>();
        }
        this.steps.add(step);
    }

    public void addScreenshot(String screenshot) {
        if (this.screenshots == null) {
            this.screenshots = (new ArrayList<>());
            this.screenshots.add(screenshot);
        }
    }

    public void setStatus(VfaStatus status) {
        if (this.status != null) {
            return;
        }
        this.status = status;
    }

    public void setError(VfaError error) {
        if (this.error != null) {
            return;
        }
        this.error = error;
    }

}