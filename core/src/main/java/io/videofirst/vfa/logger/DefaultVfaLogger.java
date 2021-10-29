package io.videofirst.vfa.logger;

import static com.diogonunes.jcolor.Ansi.colorize;
import static io.videofirst.vfa.util.VfaUtils.repeat;

import com.diogonunes.jcolor.Attribute;
import io.videofirst.vfa.enums.StepType;
import io.videofirst.vfa.enums.VfaExceptionPosition;
import io.videofirst.vfa.enums.VfaStatus;
import io.videofirst.vfa.model.VfaAction;
import io.videofirst.vfa.model.VfaError;
import io.videofirst.vfa.model.VfaFeature;
import io.videofirst.vfa.model.VfaScenario;
import io.videofirst.vfa.model.VfaStep;
import io.videofirst.vfa.model.VfaTextParameters;
import io.videofirst.vfa.properties.VfaExceptionsProperties;
import io.videofirst.vfa.properties.VfaLoggerProperties;
import io.videofirst.vfa.properties.model.VfaTheme;
import io.videofirst.vfa.util.VfaUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;
import javax.annotation.PostConstruct;

/**
 * VFA Logger.
 * <p>
 * This class was built with extensibility in mind - all these methods are either public or protected.
 * <p>
 */
@Singleton
public class DefaultVfaLogger implements VfaLogger, VfaThemeColours {

    private static final int NONE = -1; // TODO move to a constants class ???

    // Text constants

    private static final String TEXT_FEATURE = "Feature";
    private static final String TEXT_SCENARIO = "Scenario";
    private static final String TEXT_SPACE = " ";
    private static final String TEXT_COLON = ": ";
    private static final String TEXT_DOT = ".";
    private static final String TEXT_BRACKET_OPEN = "(";
    private static final String TEXT_BRACKET_CLOSE = ")";
    private static final String TEXT_METHOD_COMMA = ", ";

    @Inject
    private VfaLoggerProperties loggerConfig;

    @Inject
    private VfaExceptionsProperties exceptionsProperties;

    // Other fields

    private VfaTheme theme;
    private String indentSpaces;
    private StringBuilder line = new StringBuilder();

    @PostConstruct
    public void init() {
        this.theme = loggerConfig.getCurrentTheme();
        this.indentSpaces = repeat(TEXT_SPACE, loggerConfig.getIndentChars());
    }

    // Override methods

    @Override
    public void before(VfaFeature feature) {
        printFeatureTextAndId(feature);
        printFeatureDescription(feature);
    }

    @Override
    public void before(VfaScenario scenario) {
        printScenarioTextAndId(scenario);
    }

    @Override
    public void before(VfaStep step) {
        printStepType(step);
        printStepText(step);
    }

    @Override
    public void before(VfaAction action) {
        if (isLogAction(action)) {
            printActionAlias(action);
            printActionName(action);
            printActionParameters(action);
        }
    }

    @Override
    public void after(VfaAction action) {
        if (isLogAction(action) && isLogActionStatus(action)) {
            printActionStatus(action);
        }
    }

    @Override
    public void after(VfaStep step) {
        // nothing at min
    }

    @Override
    public void after(VfaScenario scenario) {
        println();
        println();
        printScenarioError(scenario);
    }

    @Override
    public void after(VfaFeature feature) {
        // nothing at min  (link to report - configurable on/off)
    }

    @Override
    public void error(VfaScenario scenario) {
        // Top level errors are always errors (is this correct ???)
        VfaStatus status = VfaStatus.error;
        int indent = loggerConfig.getIndentChars() * 2;  // scenario indent

        printErrorMessage(status, scenario.getError(), indent);
        printStacktrace(status, scenario.getError(), indent);
    }

    // Protected methods

    /**
     * Print non action colon.
     */
    protected void printRightColon() {
        int numberOfSpaces = loggerConfig.getRightColumnChars() - line.length();
        printSpaces(numberOfSpaces);
        print(TEXT_COLON, COLOUR_RIGHT_COLON);
    }

    protected void printFeatureTextAndId(VfaFeature feature) {
        print(TEXT_FEATURE, COLOUR_FEATURE_LABEL);
        print(TEXT_COLON, COLOUR_FEATURE_COLON);
        print(feature.getText(), COLOUR_FEATURE_TEXT);

        if (feature.getId() != NONE) {
            printRightColon();
            print(String.valueOf(feature.getId()), COLOUR_FEATURE_ID);
        }
        println();
        println();
    }

    protected void printFeatureDescription(VfaFeature feature) {
        if (feature.getDescription() != null && !feature.getDescription().isEmpty()) {
            print(indentSpaces);
            println(feature.getDescription().trim(), COLOUR_FEATURE_DESCRIPTION);
            println();
        }
    }

    protected void printScenarioTextAndId(VfaScenario scenario) {
        resetLine(); // reset from a potential previous scenario
        print(indentSpaces);
        print(TEXT_SCENARIO, COLOUR_SCENARIO_LABEL);
        print(TEXT_COLON, COLOUR_SCENARIO_COLON);
        print(scenario.getText(), COLOUR_SCENARIO_TEXT);
        if (scenario.getId() != NONE) {
            printRightColon();
            print(String.valueOf(scenario.getId()), COLOUR_SCENARIO_ID);
        }
        println();
    }

    // Step methods

    protected void printStepType(VfaStep step) {
        println();

        // Step label e.g. "Given"
        StepType stepType = step.getType();
        String colorStepLabel = getStepTypeColor(step);
        print(indentSpaces + indentSpaces + stepType.label(), colorStepLabel);
    }

    protected void printStepText(VfaStep step) {

        // Step text e.g. "A user is at the homepage"
        print(TEXT_SPACE);

        boolean isFinished = step.isFinished();
        String stepText = step.getText();
        String color = isFinished ? COLOUR_ACTION_IGNORED : COLOUR_STEP_TEXT;

        if (step.hasParameters()) {
            VfaTextParameters textParameters = step.getTextParameters();
            for (int i = 0; i < textParameters.getTexts().size(); i++) {
                String text = textParameters.getTexts().get(i);
                print(text, color);
                if (i < textParameters.getIndexes().size()) {
                    int index = textParameters.getIndexes().get(i);
                    Object paramValue = textParameters.getValues().get(index);
                    printStepParameterValue(step, paramValue);
                }
            }
        } else {
            print(stepText, color);
        }
    }

    protected void printStepParameterValue(VfaStep step, Object paramValue) {
        boolean isFinished = step.isFinished();
        if (paramValue instanceof String) {
            String paramValueString = (String) paramValue;
            boolean addQuotes =
                step.getOptions() != null && step.getOptions().getAddQuotes() != null ?
                    step.getOptions().getAddQuotes() : loggerConfig.isStepAddQuotes();
            String quotedParamValue =
                addQuotes ? VfaUtils.quote(paramValueString) : paramValueString;
            print(quotedParamValue, isFinished ? COLOUR_ACTION_IGNORED : COLOUR_STEP_STRING_PARAM);
        } else {
            print(String.valueOf(paramValue),
                isFinished ? COLOUR_ACTION_IGNORED : COLOUR_STEP_OTHER_PARAM);
        }
    }

    protected String getStepTypeColor(VfaStep step) {
        boolean isFinished = step.isFinished();
        if (isFinished) {
            return COLOUR_ACTION_IGNORED;
        } else if (step.getType().isMain()) {
            return COLOUR_STEP_LABEL;
        }
        return COLOUR_STEP_LABEL_OTHER;
    }

    // Action methods

    protected void printActionAlias(VfaAction action) {
        boolean isFirstAction = action.getStep().getTotalActions() == 1;
        if (!isFirstAction) {
            println();
        }
        printActionColon(action);

        // Print alias (unless configured to be ignored) e.g. "web."
        String alias = action.getAlias();
        if (!isAliasIgnored(alias)) {
            boolean isFinished = action.isFinished();
            print(alias, isFinished ? COLOUR_ACTION_IGNORED : COLOUR_ACTION_ALIAS);
            print(TEXT_DOT, isFinished ? COLOUR_ACTION_IGNORED : COLOUR_ACTION_DOT);
        }
    }

    protected void printActionName(VfaAction action) {
        String methodName = action.getMethodName();
        boolean isFinished = action.isFinished();
        print(methodName, isFinished ? COLOUR_ACTION_IGNORED : COLOUR_ACTION_METHOD);
    }

    protected void printActionColon(VfaAction action) {
        printActionSpaces(action);
        print(TEXT_COLON, COLOUR_RIGHT_COLON);
    }

    protected void printActionSpaces(VfaAction action) {
        int numberOfSpaces = loggerConfig.getRightColumnChars() - line.length();
        numberOfSpaces +=
            action.countParents() * loggerConfig.getIndentChars();  // indent for each action level
        printSpaces(numberOfSpaces);
    }

    protected int getActionErrorSpaces(VfaAction action) {
        int numberOfSpaces = loggerConfig.getRightColumnChars() - line.length();
        numberOfSpaces += (action.countParents() + 2)
            * loggerConfig.getIndentChars();  // indent for each action level
        return numberOfSpaces;
    }

    protected void printActionParameters(VfaAction action) {
        // Print parameter
        boolean isFinished = action.isFinished();

        print(TEXT_SPACE);
        print(TEXT_BRACKET_OPEN, isFinished ? COLOUR_ACTION_IGNORED : COLOUR_ACTION_BRACKETS);
        int index = 0;
        for (Map.Entry<String, Object> param : action.getParams().entrySet()) {
            printActionParameterSeparator(action, index);

            Object paramValue = param.getValue();
            if (paramValue instanceof Object[]) { // Last parameter can be an array
                Object[] array = (Object[]) paramValue;
                for (Object arrayParamValue : array) {
                    printActionParameterSeparator(action, index);
                    printActionParameterValue(action, arrayParamValue);
                    index++;
                }
            } else {
                printActionParameterValue(action, paramValue);
                index++;
            }
        }
        print(TEXT_BRACKET_CLOSE, isFinished ? COLOUR_ACTION_IGNORED : COLOUR_ACTION_BRACKETS);
        print(TEXT_SPACE);
    }

    protected void printActionParameterSeparator(VfaAction action, int index) {
        boolean isFinished = action.isFinished();
        if (index > 0) {
            print(TEXT_METHOD_COMMA, isFinished ? COLOUR_ACTION_IGNORED : COLOUR_ACTION_COMMA);
        }
    }

    protected void printActionParameterValue(VfaAction action, Object paramValue) {
        boolean isFinished = action.isFinished();
        if (paramValue instanceof String) {
            String quotedParamValue = VfaUtils.quote((String) paramValue);
            print(quotedParamValue,
                isFinished ? COLOUR_ACTION_IGNORED : COLOUR_ACTION_STRING_PARAM);
        } else {
            print(String.valueOf(paramValue),
                isFinished ? COLOUR_ACTION_IGNORED : COLOUR_ACTION_OTHER_PARAM);
        }
    }

    protected void printActionStatus(VfaAction action) {
        VfaStatus status = action.getStatus();
        if (status != null) {
            printActionStatusSymbol(status);
            if (status.isErrorOrFail()) {
                printActionError(action);
            }
        }
    }

    protected void printActionStatusSymbol(VfaStatus status) {
        String statusSymbol = theme.getLabel(status);
        String statusThemeColour = getStatusColour(status);
        print(statusSymbol, statusThemeColour);
    }

    protected boolean isInlineException() {
        VfaExceptionPosition position = exceptionsProperties.getPosition();
        return position == VfaExceptionPosition.inline;
    }

    protected void printActionError(VfaAction action) {
        VfaScenario scenario = action.getScenario();
        VfaStatus status = action.getStatus();

        if (isInlineException()) {
            // If position is "inline" then display errors / failures on new line, followed by stacktrace
            println();
            int indent = getActionErrorSpaces(action);
            printErrorMessage(status, scenario.getError(), indent);
            printStacktrace(status, scenario.getError(), indent);
        } else {
            // Otherwise display error / failure on same line
            printErrorMessage(status, scenario.getError(), 1);
        }
    }

    protected void printScenarioError(VfaScenario scenario) {
        // If error (1) error is null OR (2) we are displaying exception inline -> just return
        VfaError error = scenario.getError();
        if (error == null || isInlineException()) {
            return;
        }
        VfaStatus status = scenario.getStatus();
        int indent = loggerConfig.getIndentStacktrace();

        println();
        println();
        printErrorMessage(status, scenario.getError(), indent);
        printStacktrace(status, scenario.getError(), indent);
    }

    protected void printErrorMessage(VfaStatus status, VfaError error, int indentSpaces) {
        String statusThemeColour = getStatusColour(status);
        printSpaces(indentSpaces);
        print(error.getMessage(), statusThemeColour);
    }

    protected void printStacktrace(VfaStatus status, VfaError error, int indentSpaces) {
        String statusThemeColour = getStatusColour(status);
        for (String line : error.getStackTrace()) {
            println();
            printSpaces(indentSpaces);
            print(line, statusThemeColour);
        }
    }

    protected boolean isLogAction(VfaAction action) {
        int actionParentCount = action.countParents();
        int actionDepth = loggerConfig.getActionDepth();
        return actionDepth > actionParentCount || actionDepth == NONE;
    }

    protected boolean isLogActionStatus(VfaAction action) {
        boolean hasNoChildActions = action.getActions() == null || action.getActions().isEmpty();
        return hasNoChildActions;
    }

    protected boolean isAliasIgnored(String alias) {
        if (!loggerConfig.getIgnoreAliases().isPresent()) {
            return false;
        }
        return loggerConfig.getIgnoreAliases().get().stream()
            .anyMatch(p -> p != null && p.trim().equalsIgnoreCase(alias));
    }

    protected String getStatusColour(VfaStatus status) {
        return STATUS_COLOUR_MAP.containsKey(status) ? STATUS_COLOUR_MAP.get(status) : null;
    }

    // Low level print methods

    protected void print(String input) {
        print(input, null);
    }

    protected void print(String input, String themeColour) {
        line.append(input);
        if (theme.isUseColours() && themeColour != null) {
            Attribute attribute = theme.getColourAttribute(themeColour);
            System.out.print(colorize(input, attribute));
        } else {
            System.out.print(input);
        }
        System.out.flush();
    }

    protected void println() {
        System.out.println();
        resetLine();
    }

    protected void println(String input, String themeColour) {
        print(input, themeColour);
        println();
        resetLine();
    }

    protected void printSpaces(int numberOfSpaces) {
        print(repeat(TEXT_SPACE, numberOfSpaces));
    }

    protected void resetLine() {
        line.setLength(0);
    }

}