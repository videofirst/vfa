package io.videofirst.vfa.examples.imdb;

import static io.videofirst.vfa.Vfa.and;
import static io.videofirst.vfa.Vfa.given;
import static io.videofirst.vfa.Vfa.then;
import static io.videofirst.vfa.Vfa.when;

import io.videofirst.vfa.Feature;
import io.videofirst.vfa.Scenario;
import io.videofirst.vfa.examples.imdb.steps.AdvNameSearch;
import io.videofirst.vfa.web.actions.selenide.WebActions;
import jakarta.inject.Inject;

/**
 * @author Bob Marks
 */
@Feature(description = "Tests the advanced name search feature in IMDB")
public class AdvNameSearchFeature {

    @Inject
    private WebActions web;

    @Scenario
    public void search_by_name_v1() {
        given("I am at the homepage");
        web.open("https://www.imdb.com/search/name");

        when("I search for name Brad Pitt");
        web.type("name=name", "Brad Pitt");

        and("I click the search button2");
        web.click("#main button.primary");

        then("I expect to see results with name 'Brad Pitt'");
        web.exists("xpath=//h1[@class='header' and contains(text(), 'Brad Pitt')]");
    }

    @Scenario
    public void search_by_name_v2() {
        String name = "Brad Pitt";

        given("I am at the homepage");
        web.open("https://www.imdb.com/search/name");

        when("I search for name $", name);
        web.type("name=name", name);

        and("I click the search button");
        web.click("#main button.primary");

        then("I expect to see results with name $", name);
        web.exists("xpath=//h1[@class='header' and contains(text(), '" + name + "')]");
    }

    @Inject
    private AdvNameSearch advNameSearch;

    @Scenario
    public void search_by_name_v3() {
        advNameSearch.given_I_am_at_the_Advanced_Name_Search_page();

        advNameSearch.when_I_type_name_Brad_Pitt();
        advNameSearch.and_I_click_the_search_button();

        advNameSearch.then_I_expect_to_see_results_with_name_Brad_Pitt();
    }

    @Scenario
    public void search_by_name_v4() {
        advNameSearch.given_I_am_at_the_Advanced_Name_Search_page();

        advNameSearch.when_I_type_name_$("Nicole Kidman");
        advNameSearch.and_I_click_the_search_button();

        advNameSearch.then_I_expect_to_see_results_with_name_$("Nicole Kidman");
    }

    @Scenario
    public void search_by_birthday() {
        advNameSearch.given_I_am_at_the_Advanced_Name_Search_page();

        advNameSearch.when_I_type_birthday_$("12-18");
        advNameSearch.and_I_click_the_search_button();

        advNameSearch.then_I_expect_to_see_results_with_name_$("12-18");
    }

    @Scenario
    public void search_by_name_and_birthday_v1() {
        advNameSearch.given_I_am_at_the_Advanced_Name_Search_page();

        advNameSearch.when_I_type_name_$("Brad Pitt");
        advNameSearch.when_I_type_birthday_$("12-18");
        advNameSearch.and_I_click_the_search_button();

        advNameSearch.then_I_expect_to_see_results_with_name_$("12-18");
        advNameSearch.then_I_expect_to_see_results_with_name_$("Brad Pitt");
    }

    @Scenario
    public void search_by_name_and_birthday_v2() {
        advNameSearch.given().I_am_at_the_Advanced_Name_Search_page();

        advNameSearch.when().I_type_name_$("Brad Pitt");
        advNameSearch.and().I_type_birthday_$("12-18");
        advNameSearch.and().and_I_click_the_search_button();

        advNameSearch.then().I_expect_to_see_results_with_name_$("12-18");
        advNameSearch.and().I_expect_to_see_results_with_name_$("Brad Pitt");
    }

    @Scenario
    public void search_by_name_and_birthday_v3() {

        advNameSearch.given().I_am_at_the_Advanced_Name_Search_page()

            .when().I_type_name_$("Brad Pitt")
            .and().I_type_birthday_$("12-18")
            .and().I_type_number_$(12)
            .and().I_click_the_search_button()

            .then().I_expect_to_see_results_with_name_$("Brad Pitt")
            .and().I_expect_to_see_results_with_name_$("12-18");
    }

}
