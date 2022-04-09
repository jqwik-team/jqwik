
new stateful:

    - Chains: Use TransformerProvider.precondition() to improve shrinking.
              Rework RegexChainExample.

    - Kotlin: Any obvious convenience methods or extensions for chains?

    - Document
        - Samples for
            - Plain Chain example
        - Release notes
        - User Guide
            Clarify that Chain and ActionChain cannot be reproduced in SAMPLE_FIRST/ONLY mode 

1.7.1

    - Introduce ModelChain. Should cover https://github.com/jlink/jqwik/issues/80.
        - ModelChain can be generated before it's provided as parameter!

    - Compose stateful actions: https://github.com/jlink/jqwik/issues/300

    - <T,S,U> SetArbitrary<E>.combineEach(Arbitrary<S>).as(BiFunction<T, E, U>): Arbitrary<Set<U>>
            - For all collection arbitraries

    - Make `AfterFailureMode.SAMPLE_FIRST` the default

    - Remove web and time from jqwik default dependencies

    - Introduce BOM

1.7.x

    - Kotlin convenience functions:
        - Collection<T>.anyValue() or Collection<T>.chooseAny() -> Arbitraries.of(..)
        - Collection<Arbitrary<T>>.chooseOne() -> Arbitraries.oneOf(..)

    - Allow annotation @BeforeTry on member variables of tests to reinitialize them before each try.
      - Alternative: New annotation @InitBeforeTry

    - JqwikSession:
      - setRandomSessionSeed(), getRandomSessionSeed()

    - Allow state machine / model specification with temporal logic.
      See https://wickstrom.tech/programming/2021/05/03/specifying-state-machines-with-temporal-logic.html

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

