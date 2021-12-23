- 1.6.3

    - Migrate to kotlinx.coroutines 1.6.0 
      https://blog.jetbrains.com/kotlin/2021/12/introducing-kotlinx-coroutines-1-6-0/#kotlinx-coroutines-test-update
      - Make module provided, i.e. must explicitly being added when used in tests

    - Allow annotation @BeforeTry on member variables of tests to reinitialize them before each try.
        - Alternative: New annotation @InitBeforeTry

    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - Arbitraries.recursive with depthMin and depthMax for better shrinking

- 1.6.x

    - JqwikSession:
        - setRandomSessionSeed(), getRandomSessionSeed()

    - Allow state machine / model specification with temporal logic.
      See https://wickstrom.tech/programming/2021/05/03/specifying-state-machines-with-temporal-logic.html

    - Kotlin:
        - Add ParameterResolver for combined contexts
          see https://www.47deg.com/blog/effects-contexts/

1.7.0

    - Make `AfterFailureMode.SAMPLE_FIRST` the default

    - Remove web and time from jqwik default dependencies