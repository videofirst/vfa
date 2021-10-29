package io.videofirst.vfa.logger;

import io.videofirst.vfa.enums.VfaStatus;
import io.videofirst.vfa.util.VfaMap;
import java.util.Map;

/**
 * Default theme fields.
 */
public interface VfaThemeColours {

    String COLOUR_FEATURE_LABEL = "feature-label";
    String COLOUR_FEATURE_COLON = "feature-colon";
    String COLOUR_FEATURE_TEXT = "feature-text";
    String COLOUR_FEATURE_DESCRIPTION = "feature-description";
    String COLOUR_FEATURE_ID = "feature-id";

    String COLOUR_SCENARIO_LABEL = "scenario-label";
    String COLOUR_SCENARIO_COLON = "scenario-colon";
    String COLOUR_SCENARIO_TEXT = "scenario-text";
    String COLOUR_SCENARIO_ID = "scenario-id";

    String COLOUR_STEP_LABEL = "step-label";
    String COLOUR_STEP_LABEL_OTHER = "step-label-other"; // e.g. and, but,
    String COLOUR_STEP_TEXT = "step-text";
    String COLOUR_STEP_STRING_PARAM = "step-param-string";
    String COLOUR_STEP_OTHER_PARAM = "step-param-other";

    String COLOUR_ACTION_ALIAS = "action-alias";
    String COLOUR_ACTION_DOT = "action-dot";
    String COLOUR_ACTION_METHOD = "action-method";
    String COLOUR_ACTION_BRACKETS = "action-brackets";
    String COLOUR_ACTION_STRING_PARAM = "action-param-string";
    String COLOUR_ACTION_OTHER_PARAM = "action-param-other";
    String COLOUR_ACTION_COMMA = "action-comma";
    String COLOUR_ACTION_IGNORED = "action-ignored";

    String COLOUR_STATUS_PASSED = "status-passed";
    String COLOUR_STATUS_FAILED = "status-failed";
    String COLOUR_STATUS_ERROR = "status-error";
    String COLOUR_STATUS_IGNORED = "status-ignored";

    String COLOUR_RIGHT_COLON = "right-colon";

    // Status colour map

    Map<VfaStatus, String> STATUS_COLOUR_MAP = VfaMap.ofVfaStatusString(
        VfaStatus.passed, COLOUR_STATUS_PASSED,
        VfaStatus.failed, COLOUR_STATUS_FAILED,
        VfaStatus.error, COLOUR_STATUS_ERROR,
        VfaStatus.ignored, COLOUR_STATUS_IGNORED);


}