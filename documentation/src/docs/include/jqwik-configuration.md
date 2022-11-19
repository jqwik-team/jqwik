_jqwik_ uses JUnit's [configuration parameters](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params) to configure itself.

The simplest form is a file `junit-platform.properties` in your classpath in which you can configure
a few basic parameters:

```
jqwik.database = .jqwik-database             # The database file in which to store data of previous runs.
                                             # Set to empty to fully disable test run recording.
jqwik.tries.default = 1000                   # The default number of tries for each property
jqwik.maxdiscardratio.default = 5            # The default ratio before assumption misses make a property fail
jqwik.reporting.onlyfailures = false         # Set to true if only falsified properties should be reported
jqwik.reporting.usejunitplatform = false     # Set to true if you want to use platform reporting
jqwik.failures.runfirst = false              # Set to true if you want to run the failing tests from the previous run first
jqwik.failures.after.default = SAMPLE_FIRST  # Set default behaviour for falsified properties:
                                             # PREVIOUS_SEED, SAMPLE_ONLY, SAMPLE_FIRST or RANDOM_SEED
jqwik.generation.default = AUTO              # Set default behaviour for generation:
                                             # AUTO, RANDOMIZED, or EXHAUSTIVE
jqwik.edgecases.default = MIXIN              # Set default behaviour for edge cases generation:
                                             # FIRST, MIXIN, or NONE
jqwik.shrinking.default = BOUNDED            # Set default shrinking behaviour:
                                             # BOUNDED, FULL, or OFF
jqwik.shrinking.bounded.seconds = 10         # The maximum number of seconds to shrink if
                                             # shrinking behaviour is set to BOUNDED
jqwik.seeds.whenfixed = ALLOW                # How a test should act when a seed is fixed. Can set to ALLOW, WARN or FAIL
                                             # Useful to prevent accidental commits of fixed seeds into source control.
jqwik.seeds.default = SEED_FROM_NAME         # Allows to generate a fixed seed from the property's name
                                             # Can be useful if you want your CI to have repeatable test results
```

Besides the properties file there is also the possibility to set properties
in [Gradle](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle-config-params) or 
[Maven Surefire](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven-config-params).

#### Legacy Configuration in `jqwik.properties` File

Prior releases of _jqwik_ used a custom `jqwik.properties` file.
Since version `1.6.0` this is no longer supported.