package io.videofirst.vfa.properties;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.videofirst.vfa.enums.VfaLogLevel;
import io.videofirst.vfa.exceptions.VfaException;
import io.videofirst.vfa.properties.model.VfaTheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Data;

/**
 * Configuration associated with logging.
 */
@Data
@ConfigurationProperties("vfa.logger")
@Context
public class VfaLoggerProperties {

    // Injected config

    private VfaLogLevel level;
    private String theme;
    private int actionDepth;
    private int rightColumnChars;
    private int indentChars;
    private int indentStacktrace;
    private boolean stepAddQuotes;
    private Optional<List<String>> ignoreAliases;
    private Map<String, Map<String, Object>> themes;

    // Other config

    private Map<String, VfaTheme> vfaThemes;
    private VfaTheme currentTheme;

    @PostConstruct
    public void init() {
        // Validate current theme
        if (!themes.containsKey(theme)) {
            throw new VfaException("Theme [ " + theme + " ] doesn't exist - please pick a valid theme!");
        }

        this.vfaThemes = themes.entrySet().stream()
            .map(rawTheme -> VfaTheme.parse(rawTheme.getKey(), rawTheme.getValue()))
            .collect(Collectors.toMap(e -> e.getName(), e -> e));
        this.currentTheme = vfaThemes.get(theme);
        updateExtendedThemes();
    }

    public VfaTheme getCurrentTheme() {
        return this.currentTheme;
    }

    // Private methods

    private void updateExtendedThemes() {
        if (this.vfaThemes == null) {
            return;
        }
        // 1) Create parent objects first (from parentName)
        for (VfaTheme theme : this.vfaThemes.values()) {
            if (theme.getParentName() != null) {
                VfaTheme vfaParentTheme = this.vfaThemes.get(theme.getParentName());
                if (vfaParentTheme == null) {
                    throw new VfaException("Theme [ " + theme.getName() + " ] is trying to inherit theme [ " +
                        theme.getParentName() + " ] but it does not exist!");
                }
                theme.setParent(vfaParentTheme);
            }
        }
        // 2) Validate (e.g. check for circular dependencies)
        for (VfaTheme theme : this.vfaThemes.values()) {
            checkCircularReference(theme, new ArrayList<>());
        }

        // 3) Populate fields in child objects
        updateExtendedFields(this.currentTheme, this.currentTheme.getParent());
        System.out.println();
    }


    private void updateExtendedFields(VfaTheme theme, VfaTheme parent) {
        if (parent != null) {
            // Copy all fields from parent to the theme
            theme.inheritFields(parent);
            updateExtendedFields(theme, parent.getParent()); // recursively call until no more themes
        }
    }

    private void checkCircularReference(VfaTheme theme, ArrayList<VfaTheme> themes) {
        if (theme.getParent() != null) {
            if (themes.contains(theme.getParent())) {
                // Add this theme and parent it's to show the circular link in error message
                themes.add(theme);
                themes.add(theme.getParent());
                String themeNames = themes.stream()
                    .map(t -> t.getName())
                    .collect(Collectors.joining(" -> "));
                throw new VfaException("Circular parent references detected  [ " + themeNames + " ]! " +
                    "To fix, check \"extends\" field in the \"themes\" in your properties and fix / remove circular reference!");
            }
            themes.add(theme);
            checkCircularReference(theme.getParent(), themes);
        }
    }

}