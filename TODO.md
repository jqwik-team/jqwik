- 1.6.3

    - Rebase and fix Time PR

    - Optimize flatMap(just(value)) and flatCombine(just(), just())

- 1.6.x

    - Arbitraries.recursive with depthMin and depthMax for better shrinking

    - Allow annotation @BeforeTry on member variables of tests to reinitialize them before each try.
      - Alternative: New annotation @InitBeforeTry

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