package io.videofirst.vfa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VfaError {

    private String message;

    private List<String> stackTrace;

    @JsonIgnore
    @ToString.Exclude
    private Throwable throwable;

}