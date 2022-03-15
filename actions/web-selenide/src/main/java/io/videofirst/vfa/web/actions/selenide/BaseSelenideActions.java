package io.videofirst.vfa.web.actions.selenide;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.webdriver.ChromeDriverFactory;
import io.videofirst.vfa.Action;
import io.videofirst.vfa.AfterAction;
import io.videofirst.vfa.BeforeAction;
import io.videofirst.vfa.ErrorAction;
import io.videofirst.vfa.enums.VfaReportMedia;
import io.videofirst.vfa.exceptions.handlers.ThrowableConverter;
import io.videofirst.vfa.model.VfaAction;
import io.videofirst.vfa.model.VfaFeature;
import io.videofirst.vfa.service.VfaReportsService;
import io.videofirst.vfa.templates.VfaReportsProperties;
import io.videofirst.vfa.web.exception.VfaWebAssertionError;
import jakarta.inject.Inject;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;

/**
 * Base VFA UI actions class.
 *
 * @author Bob Marks
 */
public abstract class BaseSelenideActions implements BeforeAction, AfterAction, ErrorAction,
    ThrowableConverter {

    private String seleniumWebBrowser = "chrome";

    @Inject
    private VfaReportsProperties reportProperties;

    @Inject
    private VfaReportsService reportsService;

    private static final Pattern LINE_SEP_PATTERN = Pattern.compile("\\R");

    private AtomicInteger screenshotIndex = new AtomicInteger();

    public BaseSelenideActions() {
        // 1) This line gets rid of
        //     Aug 19, 2021 8:54:15 AM org.openqa.selenium.remote.ProtocolHandshake createSession
        //     INFO: Detected dialect: W3C
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
    }

    @PostConstruct
    public void setup() {
        // FIXME - this needs work
        if ("chrome".equals(seleniumWebBrowser)) {
            Configuration.browser = MyFactory.class.getName();
            Configuration.reportsFolder = reportProperties.getFolder();
            Configuration.screenshots = false;
            Configuration.savePageSource = false;
        }
    }

    @Override
    public void before(VfaAction action) {
        checkScreenshot(action, true);
    }

    @Override
    public void after(VfaAction action) {
        checkScreenshot(action, false);
    }

    @Override
    public void error(VfaAction action) {
        checkScreenshot(action, false);
    }

    @Override
    public Throwable convertThrowable(Throwable throwable) {
        if (throwable instanceof UIAssertionError) {
            // Selenide error messages are needlessly complex - Here we are splitting on first line and returning it
            // only.
            String[] parts = LINE_SEP_PATTERN.split(throwable.getMessage());
            if (parts != null && parts.length > 0) {
                return new VfaWebAssertionError(parts[0], throwable);
            }
        } else if (throwable instanceof org.opentest4j.AssertionFailedError) {
            String message = throwable.getMessage().trim();
            message = message.replaceAll("[\\n\\r]", " ") // new lines to space
                .replaceAll("\\s{2,}", " "); // 2 or more spaces to single space
            return new VfaWebAssertionError(message, throwable);
        }
        return throwable;
    }

    // Private methods

    private void checkScreenshot(VfaAction action, boolean isBefore) {
        if (reportProperties.getMedia() != VfaReportMedia.screenshots) {
            return; // nothing more to do
        }

        // Grab annotation and see if we take a screenshot or not
        Action actionAnnotation = getActionAnnotation(action);
        boolean beforeScreenshot = isBefore && actionAnnotation.screenshotBefore();
        boolean afterScreenshot = !isBefore && actionAnnotation.screenshotAfter();
        boolean takeScreenshot = beforeScreenshot || afterScreenshot;

        if (takeScreenshot) {
            // Generate screenshot id
            String screenshotFilename = getScreenshotFilename(action);

            // Add filename to scenario and add to action
            Selenide.screenshot(screenshotFilename);
            action.addScreenshot(screenshotFilename);
        }
    }

    /**
     * FIXME Might be better in a further base abstract class.
     */
    protected Action getActionAnnotation(VfaAction action) {
        Method method = action.getMethodContext().getTargetMethod();
        Action actionAnnotation = method.getAnnotation(Action.class);
        return actionAnnotation;
    }

    /**
     * Get screenshot filename - put in another file?
     */
    protected String getScreenshotFilename(VfaAction action) {
        VfaFeature feature = action.getStep().getScenario().getFeature();
        String id = System.currentTimeMillis() + "-" + screenshotIndex.getAndIncrement();
        String fullFilenameNoExtension = feature.getClassName() + File.separator + id;
        return fullFilenameNoExtension;
    }

    // FIXME where does this stuff live?  Configuration file maybe????
    // Ref: https://mbbaig.blog/selenide-webdriverfactory/
    private static class MyFactory extends ChromeDriverFactory {

        // REF [ https://stackoverflow.com/a/54301361 ]
        // NOTE: this needs requires much more work and should feed into any experiments in future around logging.
        @Override
        @CheckReturnValue
        @SuppressWarnings("deprecation")
        public WebDriver create(Config config, Browser browser, @Nullable Proxy proxy,
            File browserDownloadsFolder) {

            DriverService.Builder serviceBuilder = new ChromeDriverService.Builder().withSilent(
                true);
            ChromeOptions options = new ChromeOptions();
            ChromeDriverService chromeDriverService = (ChromeDriverService) serviceBuilder.build();
            chromeDriverService.sendOutputTo(new OutputStream() { // !!!!!!!! THIS WORKS !!!!!!!!
                @Override
                public void write(int b) {

                }
            });

            ChromeDriver driver = new ChromeDriver(chromeDriverService, options);
            return driver;
        }
    }

}
