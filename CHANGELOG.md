# v2021.1

* Create example project with tests against IDMB website.
* Initial model structure (FeatureModel, ScenarioModel, StepModel, ActionModel).
* Create main service class which is called from Micronaut Test runner and updates model.
* Create logger with theme supports using Micronaut configuration.
* Add Vfa class with a bunch of useful static methods.
* Create Junit5 naming from class and methods names.
* Create Selenide action library with screenshot support.
* Create step parameters using both simple (using dollar character) and complex (using curlys and numbers).
* Save models as JSON in build folder - this will enable future HTML reports.
* Advanced stacktrace handling which are configurable using both `core-ignores` + `ignores`.
* Abbreviate fully qualified class name so a configurable number of parts are displayed.
* If error occurs, ensure steps and actions are still displayed (ignored + display grey).
* Add quotations to String parameters to logs + different colour.
* Add parameter and options functionality to static `step` methods.
* Validation (1) @Scenario can't start with action, (2) @Scenario must ends with an action after last Step.
* Validation (3) Must have at least one step in a @Scenario method.
* Creation of vfa GitHub repository and module structure with `README.md`
