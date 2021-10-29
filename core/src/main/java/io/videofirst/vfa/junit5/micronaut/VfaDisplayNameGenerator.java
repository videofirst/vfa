package io.videofirst.vfa.junit5.micronaut;

import io.videofirst.vfa.Feature;
import io.videofirst.vfa.Scenario;
import io.videofirst.vfa.exceptions.VfaException;
import io.videofirst.vfa.util.VfaUtils;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * VFA Junit5 DisplayNameGenerator.
 *
 * Naming for classes support both camel-case and snake-case.  Snake-case gets precedence and supports lowercase
 * characters e.g. 'Book_homes' will become 'Book homes'.  Camel-Case is recommended most of the time e.g. 'BookHomes'
 * will become 'Book Homes'.  Note, any classes ending in 'Feature' or 'Test' will have the Feature / Text removed e.g.
 * 'ReservePropertyTest' will become 'Reserve Property'. The first letter is always capitalised for both camel-case and
 * snake-case.  If more complex naming is required then the 'text' field of '@Feature' annotation can be used. For
 * example '@Feature(text = "Book / Reserve Homes")' supports characters which Java class names do not support e.g. the
 * forward-slash character which could cause a compile error in Java.
 *
 * Naming for methods only supports snake-case as it is felt snake-case for scenarios is much more readable than
 * camel-case and supports both lower-case / upper-case characters (first letter is always capitalised).  For example,
 * if the scenario method is 'book_properties_by_city_Belfast' then the text will be 'Book properties by Belfast'.  If
 * more flexibility is required (e.g. using special characters) then the 'text' field of '@Scenario' annotation can be
 * used e.g. '@Scenario(text = "Book Properties by city [ Belfast ]")'.
 *
 * @author Bob Marks
 */
public class VfaDisplayNameGenerator implements DisplayNameGenerator {

    static final VfaDisplayNameGenerator INSTANCE = new VfaDisplayNameGenerator();

    private static final String REGEX_CLASS_NAME_REMOVE = "(Feature|Test)$";
    private static final String UNDERSCORE = "_";

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        final Feature featureAnnotation = testClass.getAnnotation(Feature.class);
        if (featureAnnotation == null) {
            new VfaException("Cannot run test without Feature annotation present");
        }
        String textFromAnnotation = featureAnnotation.text().trim();
        if (!textFromAnnotation.isEmpty()) {
            return textFromAnnotation;
        } else {
            // First of all remove Feature or Test from end of class name.
            String textFromClass = testClass.getSimpleName().replaceAll(REGEX_CLASS_NAME_REMOVE, "");

            // Class names with underscores get precedence
            if (textFromClass.contains(UNDERSCORE)) {
                textFromClass = VfaUtils.underScoresToSentence(textFromClass, true);
            } else {
                textFromClass = VfaUtils.camelCaseToTitleCase(textFromClass);
            }
            return textFromClass;
        }
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        return generateDisplayNameForClass(nestedClass); // needs tested
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        final Scenario scenarioAnnotation = testMethod.getAnnotation(Scenario.class);
        if (scenarioAnnotation == null) {
            new VfaException("Cannot run test without Scenario annotation present");
        }
        String textFromAnnotation = scenarioAnnotation.text().trim();
        if (!textFromAnnotation.isEmpty()) {
            return textFromAnnotation;
        } else {
            String methodName = testMethod.getName();
            return VfaUtils.underScoresToSentence(methodName, true);
        }
    }

}
