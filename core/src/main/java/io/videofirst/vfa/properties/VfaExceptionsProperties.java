package io.videofirst.vfa.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.core.util.clhm.ConcurrentLinkedHashMap;
import io.videofirst.vfa.enums.VfaExceptionPosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Data;

/**
 * Configuration associated with exceptions.
 */
@Data
@ConfigurationProperties("vfa.exceptions")
@Context
public class VfaExceptionsProperties {

    // Injected config

    private VfaExceptionPosition position;
    private List<String> coreIgnores;
    private Optional<List<String>> ignores;
    private Integer linesTop;
    private Integer linesBottom;
    private Integer showParts;
    private boolean showFull;

    // Other fields

    private List<Pattern> patternIgnores;

    private ConcurrentMap<String, Boolean> ignoreExceptionLineCache = new ConcurrentLinkedHashMap.Builder<String, Boolean>()
        .maximumWeightedCapacity(10000)
        .build();

    @PostConstruct
    public void init() {
        this.patternIgnores = new ArrayList<>();

        addIgnores(coreIgnores);
        if (ignores.isPresent()) {
            addIgnores(ignores.get());
        }
    }

    /**
     * Return filtered stack-trace from an exception (as List of String).
     */
    public List<String> getFilteredStackTrace(Throwable throwable) {
        List<String> filteredStackTrace = Arrays.stream(throwable.getStackTrace())
            .map(elm -> elm.toString())
            .filter(line -> !isIgnoreExceptionLine(line))
            .map(line -> abbreviateLine(line))
            .collect(Collectors.toList());
        return filteredStackTrace;
    }

    /**
     * Return true/false if this exception line is to be ignored.  Currently cached using a programmatic cache - we
     * could improve in future with a Micronaut based annotation approach.
     */
    public boolean isIgnoreExceptionLine(String line) {
        return ignoreExceptionLineCache.computeIfAbsent(line, (key) -> checkIgnorePatternForMatch(line));
    }

    // Private method

    private void addIgnores(List<String> ignores) {
        if (ignores == null || ignores.isEmpty()) {
            return;
        }
        for (String ignore : ignores) {
            patternIgnores.add(Pattern.compile("^" + ignore + "$"));
        }
    }

    /**
     * Abbreviate a line. For example, if the `showParts` property is set to 2 and a strack trace line is:
     *
     * io.videofirst.google.GoogleSearch.search_for_token(GoogleSearch.java:21)
     *
     * ... then the abbreviated line will be ...
     *
     * io.v.g.GoogleSearch.search_for_token(GoogleSearch.java:21)
     *
     * (NOTE: the root package is always shown in full as these are generally short e.g. com, io, etc)
     */
    private String abbreviateLine(String line) {
        if (showParts != null && showParts != -1 && line.indexOf("(") != -1) {
            // Shorten line
            int index = line.indexOf("(");
            String[] parts = line.substring(0, index).split("\\.");
            if (parts.length < showParts) {
                return line; // no change
            }
            StringBuilder sb = new StringBuilder();
            String sep = "";
            for (int i = 0; i < parts.length; i++) {
                boolean abbreviate = i > 0 && i < parts.length - showParts;
                String part = abbreviate ? parts[i].substring(0, 1) : parts[i];
                sb.append(sep + part);
                sep = ".";
            }
            sb.append(line.substring(index));
            return sb.toString();

        }
        return line;
    }

    /**
     * Iterate over each pattern and see if there is match for this line.
     */
    private boolean checkIgnorePatternForMatch(String line) {
        for (Pattern ignorePrefix : patternIgnores) {
            Matcher matcher = ignorePrefix.matcher(line);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

}