package io.videofirst.vfa.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit test to test the methods of VfaUtils.
 */
public class VfaUtilsTest {

    @Test
    public void shouldRepeat() {
        assertThat(VfaUtils.repeat(" ", 3)).isEqualTo("   ");
        assertThat(VfaUtils.repeat("*", 2)).isEqualTo("**");
        assertThat(VfaUtils.repeat("*", 4)).isEqualTo("****");
        assertThat(VfaUtils.repeat("dave", 3)).isEqualTo("davedavedave");

        // null / empty
        assertThat(VfaUtils.repeat("*", 0)).isNull();
        assertThat(VfaUtils.repeat("*", -1)).isNull();
        assertThat(VfaUtils.repeat("", 23)).isNull();
        assertThat(VfaUtils.repeat("", 0)).isNull();
        assertThat(VfaUtils.repeat(null, 23)).isNull();
    }

    @Test
    public void shouldCapFirst() {
        assertThat(VfaUtils.capFirst("t")).isEqualTo("T");
        assertThat(VfaUtils.capFirst(" t ")).isEqualTo(" t ");
        assertThat(VfaUtils.capFirst("Dave")).isEqualTo("Dave");

        // null / empty
        assertThat(VfaUtils.capFirst("")).isEqualTo("");
        assertThat(VfaUtils.capFirst(null)).isNull();
    }

    @Test
    public void shouldCamelCaseToTitleCase() {
        assertThat(VfaUtils.camelCaseToTitleCase("lowercase")).isEqualTo("lowercase");
        assertThat(VfaUtils.camelCaseToTitleCase("Class")).isEqualTo("Class");
        assertThat(VfaUtils.camelCaseToTitleCase("MyClass")).isEqualTo("My Class");
        assertThat(VfaUtils.camelCaseToTitleCase("lowercase")).isEqualTo("lowercase");

        // null / empty
        assertThat(VfaUtils.camelCaseToTitleCase("")).isEqualTo("");
        assertThat(VfaUtils.camelCaseToTitleCase(null)).isNull();
    }

    @Test
    public void shouldUnderScoresToSentence() {
        assertThat(VfaUtils.underScoresToSentence("search_bla", false)).isEqualTo("search bla");
        assertThat(VfaUtils.underScoresToSentence("Search_Bla", false)).isEqualTo("Search Bla");
        assertThat(VfaUtils.underScoresToSentence("Search_bla", false)).isEqualTo("Search bla");
        assertThat(VfaUtils.underScoresToSentence("search_Bla", false)).isEqualTo("search Bla");
        assertThat(VfaUtils.underScoresToSentence("search_BlaDave", false)).isEqualTo("search BlaDave");

        assertThat(VfaUtils.underScoresToSentence("search_bla", true)).isEqualTo("Search bla");
        assertThat(VfaUtils.underScoresToSentence("Search_Bla", true)).isEqualTo("Search Bla");
        assertThat(VfaUtils.underScoresToSentence("Search_bla", true)).isEqualTo("Search bla");
        assertThat(VfaUtils.underScoresToSentence("search_Bla", true)).isEqualTo("Search Bla");
        assertThat(VfaUtils.underScoresToSentence("search_BlaDave", true)).isEqualTo("Search BlaDave");

        // null / empty
        assertThat(VfaUtils.underScoresToSentence("", true)).isEqualTo("");
        assertThat(VfaUtils.underScoresToSentence(null, true)).isNull();
        assertThat(VfaUtils.underScoresToSentence("", false)).isEqualTo("");
        assertThat(VfaUtils.underScoresToSentence(null, false)).isNull();
    }

}