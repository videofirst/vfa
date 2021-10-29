package io.videofirst.vfa.examples.imdb;

import static io.videofirst.vfa.Vfa.and;
import static io.videofirst.vfa.Vfa.given;
import static io.videofirst.vfa.Vfa.then;
import static io.videofirst.vfa.Vfa.when;

import io.videofirst.vfa.Feature;
import io.videofirst.vfa.Scenario;
import io.videofirst.vfa.examples.imdb.steps.Imdb;
import io.videofirst.vfa.web.actions.selenide.WebActions;
import jakarta.inject.Inject;

@Feature(id = 11, description = "Search for films in a variety of different ways")
public class BasicSearchFilms {

    @Inject
    private Imdb imdb;

    @Inject
    private WebActions web;

    @Scenario(id = 23, text = "Search for Film \"The Green Mile\"")
    public void search_for_film_The_Green_Mile() {
        given("A user is at the homepage");
        web.open("https://www.imdb.com");

        when("the user types the \"The Green Mile\" into search box");
        web.type("id=suggestion-search", "The Green Mile");

        and("the user clicks the search icon");
        web.click("#suggestion-search-button");

        then("I expect the see the results page");
        web.text_contains(".findHeader", "Results for");
        web.text_contains(".findSearchTerm", "The Green Mile");

        and("the top result only contains \"The Green Mile\"");
        web.exists("xpath=.//td[@class='result_text']/a[text() = 'The Green Mile']");
    }

    @Scenario
    public void search_for_film_The_Green_Mile_v2() {
        imdb.given_a_user_is_at_the_homepage();

        imdb.when_a_user_searches_for("The Green Mile");

        imdb.then_I_expect_to_see_the_results_for_film_$("The Green Mile");

        and("the top result only contains \"The Green Mile\"");
        web.exists("xpath=.//td[@class='result_text']/a[text() = 'The Green Mile']");
    }

    @Scenario
    public void search_for_film_The_Green_Mile_v3() {
        String film = "The Green Mile";
        imdb.given().a_user_is_at_the_homepage()

            .when().a_user_searches_for_$(film)

            .then().I_expect_to_see_the_results_for_film(film)
            .and().the_top_results_only_contains_$(film);
    }

}