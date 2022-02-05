1.6.4

    - Kotlin convenience functions:
        - Collection<T>.anyValue() or Collection<T>.chooseAny() -> Arbitraries.of(..)
        - Collection<Arbitrary<T>>.chooseOne() -> Arbitraries.oneOf(..)

1.6.x

    - Allow annotation @BeforeTry on member variables of tests to reinitialize them before each try.
      - Alternative: New annotation @InitBeforeTry

    - JqwikSession:
      - setRandomSessionSeed(), getRandomSessionSeed()

    - Allow state machine / model specification with temporal logic.
      See https://wickstrom.tech/programming/2021/05/03/specifying-state-machines-with-temporal-logic.html

1.7.0

    - Make `AfterFailureMode.SAMPLE_FIRST` the default

    - Remove web and time from jqwik default dependencies

    - Introduce BOM

    - NullableArbitraryProvider should always be last to apply.
      This will probably require a new parameter based lifecycle hook, similar to:

      ```
      interface WrapArbitrary extends Lifecycle Hook {
        <T> Arbitrary<T> wrap(ForAllParameterContext parameterContext, Arbitrary<T> arbitrary);
      }
      ```

    - SharedArbitrary
      See https://github.com/jlink/jqwik/issues/294 & SharedArbitraryExperiments
      This probably requires major refactoring and change of structure.

        - Allow @ForAll on member variables of test container class

