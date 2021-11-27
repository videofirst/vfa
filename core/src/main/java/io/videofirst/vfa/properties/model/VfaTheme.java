package io.videofirst.vfa.properties.model;

import com.diogonunes.jcolor.Attribute;
import io.videofirst.vfa.enums.VfaStatus;
import io.videofirst.vfa.exceptions.VfaException;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * VFA Theme.
 */
@Builder
public class VfaTheme {

    // Constants

    private static final String EXTENDS = "extends";
    private static final String LABELS = "labels";
    private static final String COLOURS = "colours";

    private static final Pattern HEX_COLOR_VALIDATOR = Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$");

    // Getter

    @Getter
    private String name;
    @Getter
    private String parentName;
    @Getter
    private Map<String, String> labels;
    @Getter
    private Map<String, Attribute> attributeColours;   // Parsed colour

    // Setter + Getter

    @Getter
    @Setter
    private VfaTheme parent;   // parent instance (populated when full list of themes is initialised)

    // Public static methods

    /**
     * Parse a VfaTheme object from a raw map.  We're doing this because Micronaut has issues creating nested objects
     * (e.g. VfaTheme objects) which contains fields which are maps.
     */
    public static VfaTheme parse(String themeName, Map<String, Object> themeConfig) {
        String parentName = parseStringField(themeConfig, EXTENDS, null);
        Map<String, String> labels = parseMapStringField(themeConfig, LABELS, new HashMap<>());
        Map<String, Object> colours = themeConfig.containsKey(COLOURS) && themeConfig.get(COLOURS) instanceof Map ?
            (Map) themeConfig.get(COLOURS) : new HashMap<>();

        // Create new them and return
        VfaTheme theme = VfaTheme.builder()
            .name(themeName)
            .parentName(parentName)
            .labels(labels)
            .attributeColours(parseAttributeColours(colours))
            .build();

        return theme;
    }

    private static Map<String, Attribute> parseAttributeColours(Map<String, Object> colours) {
        Map<String, Attribute> attributeColours = new HashMap<>();
        if (colours != null) {
            for (Map.Entry<String, Object> colour : colours.entrySet()) {
                String themeColour = colour.getKey();
                Object value = colour.getValue();
                if (value == null || !(value instanceof String) || ((String) value).trim().isEmpty()) {
                    throw new VfaException("Invalid colour for key [ " + themeColour + " ]");
                }
                Attribute attributeColour = parseAttributeColour(themeColour, (String) value);
                attributeColours.put(colour.getKey(), attributeColour);
            }
        }

        return attributeColours;
    }

    // Private static methods

    /**
     * Convert a hexColour to an Attribute object.
     */
    private static Attribute parseAttributeColour(String themeColour, String hexColour) {
        Matcher matcher = HEX_COLOR_VALIDATOR.matcher(hexColour);
        if (!matcher.find()) {
            throw new VfaException("Invalid colour format [ " + hexColour + " ] for theme colour [ " + themeColour
                + " ]. A valid example is: #50b4b4");
        }
        Color c = Color.decode(hexColour); // convert to color
        Attribute attribute = Attribute.TEXT_COLOR(c.getRed(), c.getGreen(), c.getBlue());
        return attribute;
    }

    // Public methods

    public Attribute getColourAttribute(String themeColour) {
        return attributeColours.containsKey(themeColour) ? attributeColours.get(themeColour) : null;
    }

    public String getLabel(VfaStatus status) {
        String statusLabel = "status-" + status;
        return this.labels.containsKey(statusLabel) ? this.labels.get(statusLabel) : "";
    }

    // Private methods

    private static String parseStringField(Map<String, Object> themeConfig, String field, String defaultValue) {
        return themeConfig.containsKey(field) && themeConfig.get(field) instanceof String ?
            (String) themeConfig.get(field) : defaultValue;
    }

    private static Map<String, String> parseMapStringField(Map<String, Object> themeConfig, String field,
        Map<String, String> defaultValue) {
        return themeConfig.containsKey(field) && themeConfig.get(field) instanceof Map ?
            (Map) themeConfig.get(field) : defaultValue;
    }

    public void inheritFields(VfaTheme parent) {
        if (parent == null) {
            return;
        }

        // Inherit attribute colours from parent theme
        if (parent.getAttributeColours() != null) {
            parent.getAttributeColours().entrySet().stream().forEach(ac -> {
                if (this.attributeColours.get(ac.getKey()) == null) { // only set if null in theme
                    this.attributeColours.put(ac.getKey(), ac.getValue());
                }
            });
        }
        // Inherit labels from parent theme
        if (parent.getLabels() != null) {
            parent.getLabels().entrySet().stream().forEach(l -> {
                if (this.labels.get(l.getKey()) == null) { // only set if null in theme
                    this.labels.put(l.getKey(), l.getValue());
                }
            });
        }
    }

}