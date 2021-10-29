package io.videofirst.vfa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StepOptions {

    public static final StepOptions ADD_QUOTES = StepOptions.builder().addQuotes(true).build();
    public static final StepOptions NO_QUOTES = StepOptions.builder().addQuotes(false).build();

    private Boolean addQuotes;

}
