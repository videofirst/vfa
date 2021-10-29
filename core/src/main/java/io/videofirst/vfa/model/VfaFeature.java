package io.videofirst.vfa.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VfaFeature {

    private Long id;

    private String text;

    private String description;

    private String className;

    private VfaTime time;

    private List<VfaScenario> scenarios;

    public void addScenario(VfaScenario scenario) {
        scenario.setFeature(this); // scenario to this object
        if (this.scenarios == null) {
            this.scenarios = new ArrayList<>();
        }
        this.scenarios.add(scenario);
    }

}