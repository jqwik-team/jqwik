
1.7.0

    - Chains: Implement Fluent API for Chains and Action chains
        - See https://github.com/jlink/jqwik/issues/134#issuecomment-1127478094:
          Reimplement SequentialActionChain.transformations() without reinstantiating all shrinkables
          - Collect all transformer descriptions for Chain.transformers() as they go.
          - Remove all descriptionProviders in Transformer factory methods and re-introduce description strings.
        - Transformer.noop()
            - Optimize shrinking for that
        - Action -> Action.Dependent | Action.Independent
        - Action.builder() instead of Action factory methods?
        - ActionChainArbitrary
            - addAction(Function<Action.Builder, Action.Builder>)
            - infinite()
            - improveShrinkingWith(Supplier<ChangeDetector<T>> detectorSupplier)
        - User Guide
            Clarify that Chain and ActionChain cannot be reproduced in SAMPLE_FIRST/ONLY mode 

    - Check module-info.java files

    - Document
        - Release notes

1.7.1

    - Optimize memoization, e.g. through adding Arbitrary.generatorIsCacheable()
      See discussion in https://github.com/jlink/jqwik/issues/339 and https://github.com/jlink/jqwik/issues/354

    - Kotlin: Convenience methods and extensions for chains and action chains

    - Introduce ModelChain. Should cover https://github.com/jlink/jqwik/issues/80.
        - ModelChain can be generated before it's provided as parameter!

    - Compose stateful actions: https://github.com/jlink/jqwik/issues/300

    - <T,S,U> SetArbitrary<E>.combineEach(Arbitrary<S>).as(BiFunction<T, E, U>): Arbitrary<Set<U>>
            - For all collection arbitraries

    - Make `AfterFailureMode.SAMPLE_FIRST` the default

    - Introduce BOM

1.7.x

    - Kotlin convenience functions:
        - Collection<T>.anyValue() or Collection<T>.chooseAny() -> Arbitraries.of(..)
        - Collection<Arbitrary<T>>.chooseOne() -> Arbitraries.oneOf(..)

    - Combinators.combine(..).filter(..)
      See https://github.com/jlink/jqwik/issues/329.

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

