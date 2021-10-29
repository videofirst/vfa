package io.videofirst.vfa.model;

import io.videofirst.vfa.exceptions.VfaException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Object which takes text and optional parameters.
 * <p>
 * Supports both (1) Basic and (2) Complex parameters.
 * <p>
 * Basic parameters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VfaTextParameters {

    // Constants

    public static final char PARAM_CHAR = '$';

    private static final Pattern COMPLEX_REGEX = Pattern.compile("(\\{\\d+\\})");

    private static final String BASIC_DOUBLE_CHAR_SEARCH = "\\$\\$";
    private static final String BASIC_DOUBLE_CHAR_REPLACE = " \\$";

    // Injected fields

    private List<String> names;
    private List<Object> values;

    // Other fields

    private List<String> texts;
    private List<Integer> indexes;

    private VfaTextParameters(String text, List<Object> paramValues, List<String> paramNames) {
        values = paramValues != null ? paramValues : new ArrayList<>();
        names = paramNames;

        if (!values.isEmpty()) {
            initTextsAndIndexes(text);
        }
        validateIndexes(text);
    }

    // Static methods

    public static VfaTextParameters parse(String text, List<Object> paramValues) {
        return parse(text, paramValues, null);
    }

    public static VfaTextParameters parse(String text, List<Object> paramValues, List<String> paramNames) {
        VfaTextParameters textParam = new VfaTextParameters(text, paramValues, paramNames);

        // We only return an instance if there are valid parameters
        if (textParam.values == null || textParam.values.isEmpty() || textParam.indexes == null
            || textParam.indexes.isEmpty()) {
            return null;
        }
        return textParam;
    }

    // Private methods

    private void initTextsAndIndexes(String text) {
        this.texts = new ArrayList<>();
        this.indexes = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return;
        }

        if (isComplex(text)) {
            initParamPairComplex(text);
        } else {
            initParamPairBasic(text);
        }
    }

    private boolean isComplex(String text) {
        return COMPLEX_REGEX.matcher(text).find();
    }

    private void initParamPairBasic(String text) {
        String unescapedParams = text.replaceAll(BASIC_DOUBLE_CHAR_SEARCH, BASIC_DOUBLE_CHAR_REPLACE);
        int index = unescapedParams.indexOf(PARAM_CHAR);
        int lastIndex = 0, paramIndex = 0;
        while (index >= 0) {
            texts.add(text.substring(lastIndex, index));
            indexes.add(paramIndex++);

            lastIndex = index + 1;
            index = unescapedParams.indexOf(PARAM_CHAR, index + 1);
        }
        // Add remaining text (if any exists)
        if (lastIndex < unescapedParams.length()) {
            texts.add(text.substring(lastIndex));
        }
    }

    private void initParamPairComplex(String text) {
        Matcher matcher = COMPLEX_REGEX.matcher(text);
        int lastIndex = 0;
        while (matcher.find()) {
            String sIndex = matcher.group().replaceAll("[{}]", "");
            Integer index = Integer.parseInt(sIndex); // convert to integer

            texts.add(text.substring(lastIndex, matcher.start()));
            indexes.add(index);
            lastIndex = matcher.end();
        }
        if (lastIndex < text.length()) {
            texts.add(text.substring(lastIndex));
        }
    }

    private void validateIndexes(String text) {
        if (text != null && this.indexes != null && this.values != null && !this.values.isEmpty()) {
            for (int index : this.indexes) {
                int paramCount = this.values.size();
                if (index < 0 || index >= paramCount) {
                    throw new VfaException(
                        "Invalid index " + index + " - must be between 0 and " + paramCount + " for text \"" + text
                            + "\"");
                }
            }
        }
    }

}
