package io.videofirst.vfa.web.actions.selenide;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import io.micronaut.context.annotation.Context;
import io.videofirst.vfa.Action;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Move to its own module.
 */
@Context
public class WebActions extends BaseSelenideActions {

    @Action
    public WebActions open(String url) {
        Selenide.open(url);
        return this;
    }

    @Action
    public WebActions click(String target) {
        elm(target).exists();  // check exists first? should this be configurable?
        elm(target).click();
        return this;
    }

    @Action
    public WebActions type(String target, String value) {
        elm(target).exists();  // check exists first? should this be configurable?
        elm(target).setValue(value);
        return this;
    }

    @Action
    public WebActions text_contains(String target, String value) {
        elm(target).exists();  // check exists first? should this be configurable?
        String elmText = elm(target).text();
        assertThat(elmText).contains(value).withFailMessage(
            "Expecting element [ " + target + " ] with text [ " + elmText + " ] to contain [ "
                + value + " ]");
        return this;
    }

    @Action
    public WebActions exists(String target) {
        elm(target).should(Condition.exist);
        return this;
    }

    public void url_equals(String expectedUrl) {
        assertThat(getCurrentUrl()).isEqualTo(expectedUrl);
    }

    // Private methods

    private SelenideElement elm(String target) {
        return $(by(target));
    }

    private ElementsCollection elms(String target) {
        return $$(by(target));
    }

    private By by(String target) {
        if (target.startsWith("id=")) {
            return By.id(removePrefix("id=", target));
        } else if (target.startsWith("xpath=")) {
            return By.xpath(removePrefix("xpath=", target));
        } else if (target.startsWith("name=")) {
            return By.name(removePrefix("name=", target));
        } else if (target.startsWith("css=")) {
            return By.xpath(removePrefix("css", target));
        } else {
            // Assume that it's CSS ("css=" is an optional prefix)
            return By.cssSelector(target);
        }
    }

    private String removePrefix(String prefix, String target) {
        return target.replaceFirst("^" + prefix, "");
    }

    private WebDriver getWebDriver() {
        return WebDriverRunner.getWebDriver();
    }

    private String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }

}