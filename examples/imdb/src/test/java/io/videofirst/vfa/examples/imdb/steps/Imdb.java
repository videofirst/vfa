package io.videofirst.vfa.examples.imdb.steps;

import io.micronaut.context.annotation.Value;
import io.videofirst.vfa.Action;
import io.videofirst.vfa.Step;
import io.videofirst.vfa.Steps;
import io.videofirst.vfa.web.actions.selenide.WebActions;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class Imdb extends Steps<Imdb> {

    @Value("${imdb.homepage}")
    private String homepage;

    @Inject
    private WebActions web;

    // v1

    @Step
    public void given_a_user_is_at_the_homepage() {
        web.open(homepage);
    }

    public void when_a_user_searches_for(String film) {
        when("I expect to see the results for film [ {0} ]", film);
        web.type("id=suggestion-search", "The Green Mile");
        web.click("#suggestion-search-button");
    }

    @Step
    public void then_I_expect_to_see_the_results_for_film_$(String film) {
        web.text_contains(".findHeader", "Results for");
        web.text_contains(".findSearchTerm", film);
    }

    // v2

    @Step
    public Imdb a_user_is_at_the_homepage() {
        open_homepage();
        return this;
    }

    @Step
    public Imdb a_user_searches_for_$(String input) {
        web.type("id=suggestion-search", "The Green Mile");
        web.click("#suggestion-search-button");
        return this;
    }

    @Step("I expect to see the results for film [ {0} ]")
    public Imdb I_expect_to_see_the_results_for_film(String film) {
        web.text_contains(".findHeader", "Results for");
        web.text_contains(".findSearchTerm", film);
        return this;
    }

    @Step
    public Imdb the_top_results_only_contains_$(String film) {
        web.exists("xpath=.//td[@class='result_text']/a[text() = 'The Green Mile']");
        return this;
    }

    @Action
    public Imdb open_homepage() {
        web.open(homepage);
        return this;
    }

}