package io.videofirst.vfa.model;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.videofirst.vfa.exceptions.VfaException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VfaTextParametersTest {

    private List<Object> V1 = asList("v1");
    private List<Object> V2 = asList("v1", "v2");
    private List<Object> V3 = asList("v1", "v2", "v3");
    private List<Object> V4 = asList("v1", "v2", "v3", "v4");

    @Test
    public void shouldNotCreateSimple() {
        assertTextParametersIsNull(null, V2);
        assertTextParametersIsNull("", V2);
        assertTextParametersIsNull(" $ ", asList());
    }

    @Test
    public void shouldCreateSimplePairs() {
        assertTextParametersPairs("$", V1, "", 0);
        assertTextParametersPairs(" $ ", V1, " ", 0, " ");
        assertTextParametersPairs("  $", V2, "  ", 0);
        assertTextParametersPairs(" $ $ ", V2, " ", 0, " ", 1, " ");
        assertTextParametersPairs("$   $", V2, "", 0, "   ", 1);
        assertTextParametersPairs("$ $ $", V3, "", 0, " ", 1, " ", 2);
        assertTextParametersPairs("$ $$ $", V4, "", 0, " $", 1, " ", 2);
        assertTextParametersPairs("$ $$$", V3, "", 0, " $", 1, "", 2);
        assertTextParametersPairs(" $$ $$$ $$ ", V4, " $", 0, " $", 1, "", 2, " $", 3, " ");
    }

    // Complex

    @Test
    public void shouldNotCreateComplex() {
        assertTextParametersIsNull(" {0} ", null);
        assertTextParametersIsNull(" {0} ", asList());
        assertTextParametersIsNull(" - {-1} - ", V2); // minus numbers ignored
    }

    @Test
    public void shouldCreateComplexPairs() {
        assertTextParametersPairs(" {0} ", V1, " ", 0, " ");
        assertTextParametersPairs(" ${0}$ ", V2, " $", 0, "$ ");
        assertTextParametersPairs(" ${0}$$${}{1} ", V2, " $", 0, "$$${}", 1, " ");
    }

    @Test
    public void shouldErrorComplex() {
        assertTextParametersIsError(" {0} ", asList());
        assertTextParametersIsError(" {0} {1} ", V1);
        assertTextParametersIsError(" {0} {1} {2} ", V2);
    }

    // Private methods

    private void assertTextParametersIsError(String text, List<Object> paramValues) {
        assertThatThrownBy(() -> VfaTextParameters.parse(text, paramValues))
            .hasCauseInstanceOf(VfaException.class);
    }

    private void assertTextParametersIsNull(String text, List<Object> paramValues) {
        assertThat(VfaTextParameters.parse(text, paramValues)).isNull();
    }

    private void assertTextParametersPairs(String text, List<Object> paramValues, Object... expectedPairs) {
        VfaTextParameters param = VfaTextParameters.parse(text, paramValues);
        List<String> texts = new ArrayList<>();
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < expectedPairs.length; i += 2) {
            texts.add((String) expectedPairs[i]);
            if (i + 1 < expectedPairs.length) {
                indexes.add((Integer) expectedPairs[i + 1]);
            }
        }
        assertThat(param.getTexts()).isEqualTo(texts);
        assertThat(param.getIndexes()).isEqualTo(indexes);
    }

}
