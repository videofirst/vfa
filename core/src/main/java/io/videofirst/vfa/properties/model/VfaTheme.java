package io.videofirst.vfa.properties.model;

import com.diogonunes.jcolor.Attribute;
import io.videofirst.vfa.enums.VfaStatus;
import io.videofirst.vfa.exceptions.VfaException;
import io.videofirst.vfa.util.VfaMap;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Getter;

/**
 * Immutable VFA Theme.
 */
@Getter
@Builder
public class VfaTheme {

    // Constants

    private static final String USE_COLOURS = "use-colours";
    private static final String LABELS = "labels";
    private static final String COLOURS = "colours";

    private static final Map<String, String> DEFAULT_LABELS = VfaMap.of("status-passed", "√",
        "status-failed", "X",
        "status-error", "!",
        "status-ignored", "");

    private static final Pattern HEX_COLOR_VALIDATOR = Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$");

    // From properties

    private String name;
    private boolean useColours;
    private Map<String, String> labels;
    private Map<String, Attribute> attributeColours;   // Parsed colour

    // Public static methods

    /**
     * Parse a VfaTheme object from a raw map.  We're doing this because Micronaut has issues creating nested objects
     * (e.g. VfaTheme objects) which contains fields which are maps.
     */
    public static VfaTheme parse(String themeName, Map<String, Object> themeConfig) {
        boolean useColours = parseBooleanField(themeConfig, USE_COLOURS, false);
        Map<String, String> labels = parseMapStringField(themeConfig, LABELS, DEFAULT_LABELS);
        Map<String, Object> colours = themeConfig.containsKey(COLOURS) && themeConfig.get(COLOURS) instanceof Map ?
            (Map) themeConfig.get(COLOURS) : null;

        // Create new them and return
        VfaTheme theme = VfaTheme.builder()
            .name(themeName)
            .useColours(useColours)
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
        if (!attributeColours.containsKey(themeColour)) {
            throw new VfaException(
                "Theme colour [ " + themeColour + " ] - does not exist in theme [ " + this.name + " ]");
        }
        return attributeColours.get(themeColour);
    }

    public String getLabel(VfaStatus status) {
        String statusLabel = "status-" + status;
        return this.labels.containsKey(statusLabel) ? this.labels.get(statusLabel) : "";
    }

    // Private methods

    private static boolean parseBooleanField(Map<String, Object> themeConfig, String field, boolean defaultValue) {
        return themeConfig.containsKey(field) && themeConfig.get(field) instanceof Boolean ?
            (Boolean) themeConfig.get(field) : defaultValue;
    }

    private static Map<String, String> parseMapStringField(Map<String, Object> themeConfig, String field,
        Map<String, String> defaultValue) {
        return themeConfig.containsKey(field) && themeConfig.get(field) instanceof Map ?
            (Map) themeConfig.get(field) : defaultValue;
    }

}