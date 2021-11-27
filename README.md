[![Apache License 2.0](https://img.shields.io/badge/license-apache2-red.svg?style=flat-square)](http://opensource.org/licenses/Apache-2.0)

## VFA (Video First Automation)

VFA is a Java based BDD (Behavior-Driven Development) framework from the same folk who created
[Video First](https://www.videofirst.io) (a UI testing platform which uses video to capture and share UI tests).

VFA enables developers and testers to _quickly_ and _easily_ create beautiful automated tests with zero loss of power or
flexibility. It focuses on E2E _(end-to-end)_ user interface tests but also supports testing e.g. API endpoints.

## Example Test

The best way to show VFA in action is with an example. The following test searching for the film
"The Green Mile" on the [IMDB](https://www.imdb.com) _(Internet Movie DataBase)_ website.

```java

import static io.Vfa.*;                                  // Import static step methods e.g. given()

import io.Feature;
import io.Scenario;
import io.WebActions;

import javax.inject.Inject;

@Feature                                                 //  @Feature marks class as a VFA feature 
public class SearchFilms {

    @Inject
    private WebActions web;

    @Scenario                                            // @Scenario marks method as a VFA scenario
    public void search_for_film_The_Green_Mile() {
        given("a user is at the homepage");              // Static method creates high-level VFA step
        web.open("https://www.imdb.com");                // This method runs a low-level VFA action 

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

}
```

This can be run from your IDE (we highly recommend [IntelliJ IDEA](https://www.jetbrains.com/idea/))
just like any JUnit test. The following will be displayed in the console: -

```
Feature: Search Films

  Scenario: Search for Film The Green Mile

    Given a user is at the homepage                             : open ("https://www.imdb.com") ✔
     When the user types the "The Green Mile" into search box   : type ("id=suggestion-search", "The Green Mile") ✔
      And the user clicks the search icon                       : click ("#suggestion-search-button") ✔
     Then I expect the see the results page                     : text_contains (".findHeader", "Results for") ✔
                                                                : text_contains (".findSearchTerm", "The Green Mile") ✔
      And the top result only contains "The Green Mile"         : exists ("xpath=.//td[@class='result_text']/a[text() = 'The Green Mile']") ✔

```

VFA outputs standard BDD format i.e. _Feature_ (e.g. `Search Films`) -> _Scenarios_
(e.g. `Search for Film The Green Mile`) -> _Steps_ (e.g. `Given a user is at the homepage`).

A unique feature of VFA are actions (e.g. `open ("https://www.imdb.com")`) and how they are displayed i.e. high level
steps are shown on the left (which anyone can understand) and lower level actions are displayed on the right-hand-side (
useful for engineers / testers).

The above test can be executed like any JUnit test. During the execution, JSON files are generated that can then be used
afterwards to generate test reports.

## What Problems does VFA Solve?

VFA is the result of many chats with lots of testers and engineers about their experiences creating and running E2E user
interface tests. These problems included: -

<table>
  <thead>
    <tr>
      <th>Problem</th>
      <th>Outline</th>
      <th>Solution</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td nowrap><b>1. Getting Started</b></td>
      <td>Creating a new UI test automation project can take a considerable amount of time.</td>
      <td>VFA provides a starter GitHub project to quickly get started (a project generator is also coming soon).</td>
    </tr>  
    <tr>
      <td nowrap><b>2. Complexity</b></td>
      <td>
        Some frameworks like Cucumber require you create multiple files - e.g. (1) a feature file, (2) a class with complex 
        regular expressions to link up the feature file (3) the actual test code. This can be off putting for new users.
      </td>
      <td>
        VFA you can write your first full E2E with a single method of a single class making it breeze for new users. 
      </td>
    </tr>
    <tr>
      <td nowrap><b>3. Structure</b></td>
      <td>
        Knowing how to properly structure your tests can be daunting and confusing for users, especially as the size of 
        the codebase starts to increase.
      </td>
      <td> 
        VFA enforces an opinionated structure (<i>feature -> scenario -> steps -> actions</i>) which ensures a high 
        quality structure with many advantages </i>(especially when the size of the codebase increases)</i>.
      </td>
    </tr>
    <tr>
      <td nowrap><b>4. Visibility</b></td>
      <td>
        If a user interface tests breaks, it can be very hard to determinate exactly why it broke.
      </td>
      <td>
        VFA automatically takes screenshots anytime an action is called.  These massively increases visibility and can 
        be invaluable when determining what went wrong in a test.
      </td>
    </tr>
    <tr>
      <td nowrap><b>5. Confusing Logs</b></td>
      <td>
        A common complaint are tests which produce messy logs where it isn't clear what is happening (at either a high or
        low level).
      </td>
      <td>
        VFA produces beautiful logs and shows high level contextual BDD (steps) on the left-hand-side and lower-level 
        action logs on the right-hand-side which saves users valuable time when understanding / fixing broken tests.
      </td>
    </tr>
    <tr>
      <td nowrap><b>6. Stack Traces</b></td>
      <td>
        Another complaint are logs from tests which contain huge Java exception stack traces.  
      </td>
      <td>
       VFA ensures stack traces are kept to a bare minimum which not only looks better and saves time when debugging and
       fixing broken tests.
      </td>
    </tr>
    <tr>
      <td nowrap><b>7. Configurability</b></td>
      <td>
        Configuring tests on e.g. different environments can be tricky. 
      </td>
      <td>
        VFA uses <a href="https://micronaut.io">Micronaut</a> which provides easy and powerful configurability.
      </td>
    </tr>
    <tr>
      <td nowrap><b>8. Metrics</b></td>
      <td>
        Knowing which tests are the slowest, especially over multiple tests can be tricky.  Also gaining a more granular
        view of which part of the test can be hard. 
      </td>
      <td>
        VFA takes timings at each level from scenario right down to individual actions (and even sub actions).  These
        metrics can be easily aggregated over time to determine the slow parts of the application or tests.
      </td>
    </tr>
  </tbody>
</table>

## What's Next

todo

## License

VFA is published under the Apache License 2.0, see https://www.apache.org/licenses/LICENSE-2.0
for the full licence.

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md)
